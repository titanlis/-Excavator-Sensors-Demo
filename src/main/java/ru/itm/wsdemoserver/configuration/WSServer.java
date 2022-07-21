package ru.itm.wsdemoserver.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itm.wsdemoserver.components.SensorInstallations;
import ru.itm.wsdemoserver.configuration.coders.SensorsDecoder;
import ru.itm.wsdemoserver.configuration.coders.SensorsEncoder;
import ru.itm.wsdemoserver.data.Polygons;
import ru.itm.wsdemoserver.models.ModelJSON;
import ru.itm.wsdemoserver.models.blocks.Block;
import ru.itm.wsdemoserver.models.bucketdump.BucketDump;
import ru.itm.wsdemoserver.models.sensors.Sensors;
import ru.itm.wsdemoserver.terminal.Terminal;
import ru.itm.wsdemoserver.util.NumericUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @class WSServer
 * Основной класс сервера.
 * Порождает терминал для управления, получает модели блоков, полигонов, ковша и сенсоров.
 * @ServerEndpoint
 * ( value = "/",                       ендпоинт для websocket
 *   encoders = SensorsEncoder.class,   json кодер и декодер данных
 *   decoders = SensorsDecoder.class)
 */
@Component
@ServerEndpoint(value = "/",
        encoders = SensorsEncoder.class,
        decoders = SensorsDecoder.class)
public class WSServer {
    private static Logger logger = LoggerFactory.getLogger(WSServer.class);

    private Sensors sensors;                //текущие значения сенсоров
    private static Block block;             //данные блока
    private static Polygons polygons;       //полигоны
    private static BucketDump bucketDump;   //ковш
    private Terminal terminal;              //терминал

    @Autowired
    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }

    @Autowired
    public void setBlock(Block block) {
        this.block = block;
    }

    @Autowired
    public void setPolygons(Polygons polygons) {
        this.polygons = polygons;
    }

    @Autowired
    public void setBucketDump(BucketDump bucketDump) {
        WSServer.bucketDump = bucketDump;
    }

    @Autowired
    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    /**Множество ws соединений*/
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

    private Thread threadSensors = null;        //поток для изменения значений сенсоров
    private Thread threadTerminal = null;       //поток для терминала


    /**Задержка передачи данных при старте сервиса. 10 файлов в секунду.*/
    @Value("${sensors.delay}")
    private int delay;

    public WSServer() {}

    /**
     * Периодический метод для обновления и отправки данных json ws клиентам
     * @throws IOException
     * @throws EncodeException
     */
    @Scheduled(fixedDelay = 1)
    private void sendData() throws IOException, EncodeException{
        if(SensorInstallations.isTerminalStart()){
            if(!SensorInstallations.isThreadsStart()){
                SensorInstallations.setThreadsStart(true);
                /**Задача - обновление данных. Начинает работу после запуска терминала и если не была еще запущена */
                Runnable taskSensors = () -> {
                    SensorInstallations.setSensorsDelay(delay);
                    if(SensorInstallations.isThreadsStart() && SensorInstallations.isTerminalStart()){
                        /**Задача обновления координат антенны*/
                        Runnable taskAntennaCoordinates = () -> {
                            /**У антенны обновляются координата X и азимут*/
                            double antennaStep;     //это шаг для азимута (и теоретически для других кроме X координат,
                                                    // но они статичны
                            double antennaXStep;    //шаг изменения X
                            int antennaDelay;       //общая задержка обновления всех данных антенны

                            /**Пока не получен сигнал о завершении обновлений и не закрыт терминал, обновляем данные*/
                            while(SensorInstallations.isThreadsStart() && SensorInstallations.isTerminalStart()){
                                /**Получаем данные из обменника SensorInstallations*/
                                antennaStep = SensorInstallations.getAntennaStep();
                                antennaXStep = SensorInstallations.getAntennaXStep();
                                /*Скорость обновления данных в антенне переводим в задержку*/
                                antennaDelay = (int) (1000.0/SensorInstallations.getAntennaSpeed());
                                /*Задержка отправки json берется из обменника*/
                                delay = SensorInstallations.getSensorsDelay();

                                /**Тормозим на уазанное время обновление координат*/
                                try {
                                    Thread.sleep(antennaDelay);
                                } catch (InterruptedException e) {
                                    logger.info("Delay aborting");
                                }

                                /**На 1 шаг увеличиваем координаты X и Азимута, берем данные о них в обменнике, инициализируем модель сенсоров.
                                 * Y и Z статичны, берутся из обменника.*/
                                sensors.getAntennaCoordinates().setX(NumericUtils.round1(sensors.getAntennaCoordinates().getX()+antennaXStep));
                                sensors.getAntennaCoordinates().setY(SensorInstallations.getaY());
                                sensors.getAntennaCoordinates().setZ(SensorInstallations.getaZ());
                                sensors.getAntennaCoordinates().setAzimuth(NumericUtils.round1(sensors.getAntennaCoordinates().getAzimuth()+antennaStep));

                                /**Когда текущая координата становится максимальной или минимальной шаг меняет знак и начинается изменение значений
                                 * в обратную сторону*/
                                if(sensors.getAntennaCoordinates().getAzimuth()>=SensorInstallations.getAntennaAzimuthMax()
                                        || sensors.getAntennaCoordinates().getAzimuth()<=SensorInstallations.getAntennaAzimuthMin()){
                                    antennaStep*=-1.0;

                                    /**Корректируем предельные значения, чтобы не пропустить максимум и минимум*/
                                    if(sensors.getAntennaCoordinates().getAzimuth()>SensorInstallations.getAntennaAzimuthMax()){
                                        sensors.getAntennaCoordinates().setAzimuth(SensorInstallations.getAntennaAzimuthMax());
                                    }

                                    if(sensors.getAntennaCoordinates().getAzimuth()<SensorInstallations.getAntennaAzimuthMin()){
                                        sensors.getAntennaCoordinates().setAzimuth(SensorInstallations.getAntennaAzimuthMin());
                                    }

                                    /**Обновляем значение шага антенны в обменнике*/
                                    SensorInstallations.setAntennaStep(antennaStep);
                                }

                                /**С шагом координаты X поступаем аналогично азимуту (антенне)*/
                                if(sensors.getAntennaCoordinates().getX()>=SensorInstallations.getAXMax()
                                        || sensors.getAntennaCoordinates().getX()<=SensorInstallations.getAXMin()){
                                    antennaXStep*=-1.0;

                                    if(sensors.getAntennaCoordinates().getX()>SensorInstallations.getAXMax()){
                                        sensors.getAntennaCoordinates().setX(SensorInstallations.getAXMax());
                                    }

                                    if(sensors.getAntennaCoordinates().getX()<SensorInstallations.getAXMin()){
                                        sensors.getAntennaCoordinates().setX(SensorInstallations.getAXMin());
                                    }

                                    SensorInstallations.setAntennaXStep(antennaXStep);
                                }

                            }
                        };

                        /**Задача обновления угла наклона мачты с ковшом*/
                        Runnable taskBoomAngle = () -> {
                            double boomAngleStep;
                            int boomAngleDelay;

                            /**Задача будет остановлена, кгда поступит команда или закроется терминал*/
                            while(SensorInstallations.isThreadsStart() && SensorInstallations.isTerminalStart()){
                                boomAngleStep = SensorInstallations.getBoomAngleStep();
                                boomAngleDelay = (int) (1000.0/SensorInstallations.getBoomAngleSpeed());

                                try {
                                    Thread.sleep(boomAngleDelay);
                                } catch (InterruptedException e) {
                                    logger.info("Delay aborting");
                                }

                                sensors.setBoomAngle(NumericUtils.round1(sensors.getBoomAngle()+boomAngleStep));

                                if(sensors.getBoomAngle()>=SensorInstallations.getBoomAngleMax()
                                        || sensors.getBoomAngle()<=SensorInstallations.getBoomAngleMin()){
                                    if(boomAngleStep!=0) {
                                        boomAngleStep *= -1.0;
                                        SensorInstallations.setBoomAngleStep(boomAngleStep);
                                    }

                                    if(sensors.getBoomAngle()>SensorInstallations.getBoomAngleMax()){
                                        sensors.setBoomAngle(SensorInstallations.getBoomAngleMax());
                                    }

                                    if(sensors.getBoomAngle()<SensorInstallations.getBoomAngleMin()){
                                        sensors.setBoomAngle(SensorInstallations.getBoomAngleMin());
                                    }

                                }
                            }
                        };

                        /**Задача обновления датчика углов наклона*/
                        Runnable taskTiltAngle = () -> {

                            while(SensorInstallations.isThreadsStart() && SensorInstallations.isTerminalStart()){
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    logger.info("Delay aborting");
                                }

                                sensors.getTiltAngles().setX(SensorInstallations.gettX());
                                sensors.getTiltAngles().setY(SensorInstallations.gettY());
                            }
                        };


                        /**Задача обновления глубины стрелы*/
                        Runnable taskBoomDeep = () -> {
                            double boomDeepStep;
                            int boomDeepDelay;

                            while(SensorInstallations.isThreadsStart() && SensorInstallations.isTerminalStart()){
                                boomDeepStep = SensorInstallations.getBoomDeepStep();
                                boomDeepDelay = (int) (1000.0/SensorInstallations.getBoomDeepSpeed());

                                try {
                                    Thread.sleep(boomDeepDelay);
                                } catch (InterruptedException e) {
                                    logger.info("Delay aborting");
                                }

                                sensors.setBoomDeep(NumericUtils.round1(sensors.getBoomDeep()+boomDeepStep));

                                if(sensors.getBoomDeep()>=SensorInstallations.getBoomDeepMax()
                                        || sensors.getBoomDeep()<=SensorInstallations.getBoomDeepMin()){
                                    if(boomDeepStep!=0) {
                                        boomDeepStep*=-1.0;
                                        SensorInstallations.setBoomDeepStep(boomDeepStep);
                                    }

                                    if(sensors.getBoomDeep()>SensorInstallations.getBoomDeepMax()){
                                        sensors.setBoomDeep(SensorInstallations.getBoomDeepMax());
                                    }

                                    if(sensors.getBoomDeep()<SensorInstallations.getBoomDeepMin()){
                                        sensors.setBoomDeep(SensorInstallations.getBoomDeepMin());
                                    }
                                }
                            }
                        };


                        /**Запускаем потоки обновления данных*/
                        Thread threadAntenna = new Thread(taskAntennaCoordinates);
                        threadAntenna.start();

                        Thread threadBoomAngle = new Thread(taskBoomAngle);
                        threadBoomAngle.start();

                        Thread threadTiltAngle = new Thread(taskTiltAngle);
                        threadTiltAngle.start();

                        Thread threadBoomDeep = new Thread(taskBoomDeep);
                        threadBoomDeep.start();
                    }
                };
                /**Запускаем поток обновления координат. Эта задача запустить 4 потока обновления каждой координаты.
                 * См выше.*/
                threadSensors = new Thread(taskSensors);
                threadSensors.start();
            }
        }


        /**
         * Если командой терминала будет установлена слишком большая пауза между отправкой json, то
         * для избежания блокировки выполнения других команд, раз в секунду будем проверять, не отменили
         * ли паузу в терминале.
         */
        while(delay>10000){
            try {
                Thread.sleep(1000);  //ждем следующего запроса данных
            } catch (InterruptedException e) {
                logger.info("Delay aborting");
            }
        }

        /**Пауза между json*/
        try {
            Thread.sleep(delay);  //ждем следующего запроса данных
        } catch (InterruptedException e) {
            logger.info("Delay aborting");
        }


        /**Если есть подключения к websocket, то отправляем всем json с сенсорами*/
        if (sessions.size()>0) {
            broadcast(sensors);
            /**Если при этом пора открыть ковш, то всем отправим еще и json c bucketDump=1*/
            if(sensors.getBoomAngle()==SensorInstallations.getBoomAngleOpen()){ //or getBoomAngleMax
                broadcast(bucketDump);
            }
        }

        /**Если терминал уже завершен, ставим в обменнике флаги завершения, что завершит запущенные потоки.*/
        if(terminal.isExit()){
            SensorInstallations.setThreadsStart(false);
            SensorInstallations.setTerminalStart(false);
        }
        else{
            /**Если терминал еще не был запущен, запускаем его в отдельном потоке*/
            if(!terminal.isActive()){
                Runnable taskTerminal = () -> {
                    terminal.start();
                };
                threadTerminal = new Thread(taskTerminal);
                /**Перед запуском подождем пару секунд, чтобы остальные потоки создались*/
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.info("Delay aborting");
                }
                threadTerminal.start();
            }
        }

        /**Если нужно кинуть самодельные json, то сбрасываем флаг мануала и кидаем всем
         * json как есть в виде строки. Можно конечно реализовать его разбор, проверку и прогон через
         * енкодер, но задача этого не требует, поэтому понадеемся на правильность ввода.*/
        if(SensorInstallations.isIsManualJson()){
           SensorInstallations.setIsManualJson(false);
           broadcastString(SensorInstallations.getManualJson());
        }


    }


    /**
     * При подключении клиента добавляем его сессию к множеству сессий и кидаем всем json с блоком,
     * за ним json с очередным полигоном
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        try {
            broadcast(block);
        } catch (IOException e) {
            System.out.println();
            logger.error("Block IO Exception");
            System.out.print("> ");

        } catch (EncodeException e) {
            System.out.println();
            logger.error("Block Encode Exception");
            System.out.print("> ");
        }


        try {
            broadcast(polygons.getNext());
        } catch (IOException e) {
            System.out.println();
            logger.error("Polygons IO Exception");
            System.out.print("> ");

        } catch (EncodeException e) {
            System.out.println();
            logger.error("Polygons Encode Exception");
            System.out.print("> ");
        }


    }

    /**
     * @brief Отключение клиента
     * @param session
     * Просто удаляем сессию из множества.
     */
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    /**
     * @brief При получении сообщения ничего не делаем, т.к. ни от кого ничего получать не планируем.
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * В случае ошибки просто выводим лог и восстанавливаем приглашение к вводу команд.
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println();
        logger.info("@OnError. Client stopped!!");
        System.out.print("> ");
    }

    /**
     * Отправка всем подключившимся нашу json модель данных
     * @param message
     * @throws IOException
     * @throws EncodeException
     */
    private static void broadcast(ModelJSON message)
            throws IOException, EncodeException {

        sessions.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Отправка всем подключившимся клиентам простой строки.
     * @param message
     * @throws IOException
     */
    private static void broadcastString(String message)
            throws IOException {

        sessions.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Sensors getSensors() {
        return sensors;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }


}