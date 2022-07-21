/**
 * @file Terminal.java
 * Содержит основной класс для ввода и обработки команд сервера.
 */
package ru.itm.wsdemoserver.terminal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.itm.wsdemoserver.components.SensorInstallations;
import ru.itm.wsdemoserver.controllers.ShutdownManager;

import java.util.Locale;
import java.util.Scanner;

/**
 * @class Terminal консольный терминал для управления сервером
 */
public class Terminal {

    @Value("${management.server.port}")
    private String actuatorPort;

    /**Значения сенсоров по умолчанию для ручного ввода из админки.*/
    @Value("${manual.aX}")
    private String startAX;
    @Value("${aX.min}")
    private String aXMin;
    @Value("${aX.max}")
    private String aXMax;
    @Value("${aX.step}")
    private String aXStep;


    @Value("${manual.aY}")
    private String startAY;
    @Value("${manual.aZ}")
    private String startAZ;
    @Value("${manual.azimuth}")
    private String startAAzimuth;
    @Value("${manual.bA}")
    private String startBA;
    @Value("${manual.tX}")
    private String startTX;
    @Value("${manual.tY}")
    private String startTY;
    @Value("${manual.bD}")
    private String startBD;

    @Value("${boom.angle.open}")
    private String boomAngleOpen;
    @Value("${sensors.Delay}")
    private String sensorsDelay;
    @Value("${antenna.Speed}")
    private String antennaSpeed;
    @Value("${antenna.Step}")
    private String antennaStep;
    @Value("${antenna.AzimuthMin}")
    private String antennaAzimuthMin;
    @Value("${antenna.AzimuthMax}")
    private String antennaAzimuthMax;
    @Value("${boomAngle.Speed}")
    private String boomAngleSpeed;
    @Value("${boomAngle.Step}")
    private String boomAngleStep;
    @Value("${boomAngle.Min}")
    private String boomAngleMin;
    @Value("${boomAngle.Max}")
    private String boomAngleMax;
    @Value("${boomDeep.Speed}")
    private String boomDeepSpeed;
    @Value("${boomDeep.Step}")
    private String boomDeepStep;
    @Value("${boomDeep.Min}")
    private String boomDeepMin;
    @Value("${boomDeep.Max}")
    private String boomDeepMax;


    private static boolean isActive = false;   /**активность терминала*/
    private static boolean isExit = false;     /**произведен ли выход из терминала*/

    private static Scanner scanner = new Scanner(System.in);

    /**+
     * Установить все значения в default
     */
    private String sensorsReset(){
        SensorInstallations.setaX(Double.parseDouble(startAX));
        SensorInstallations.setAXMin(Double.parseDouble(aXMin));
        SensorInstallations.setAXMax(Double.parseDouble(aXMax));
        SensorInstallations.setAntennaXStep(Double.parseDouble(aXStep));

        SensorInstallations.setaY(Double.parseDouble(startAY));
        SensorInstallations.setaZ(Double.parseDouble(startAZ));
        SensorInstallations.setaAz(Double.parseDouble(startAAzimuth));
        SensorInstallations.setbA(Double.parseDouble(startBA));
        SensorInstallations.settX(Double.parseDouble(startTX));
        SensorInstallations.settY(Double.parseDouble(startTY));
        SensorInstallations.setbD(Double.parseDouble(startBD));

        SensorInstallations.setBoomAngleOpen(Double.parseDouble(boomAngleOpen));
        SensorInstallations.setSensorsDelay(Integer.parseInt(sensorsDelay));

        SensorInstallations.setAntennaSpeed(Integer.parseInt(antennaSpeed));
        SensorInstallations.setAntennaStep(Double.parseDouble(antennaStep));
        SensorInstallations.setAntennaAzimuthMin(Double.parseDouble(antennaAzimuthMin));
        SensorInstallations.setAntennaAzimuthMax(Double.parseDouble(antennaAzimuthMax));

        SensorInstallations.setBoomAngleSpeed(Integer.parseInt(boomAngleSpeed));
        SensorInstallations.setBoomAngleStep(Double.parseDouble(boomAngleStep));
        SensorInstallations.setBoomAngleMin(Double.parseDouble(boomAngleMin));
        SensorInstallations.setBoomAngleMax(Double.parseDouble(boomAngleMax));

        SensorInstallations.setBoomDeepSpeed(Integer.parseInt(boomDeepSpeed));
        SensorInstallations.setBoomDeepStep(Double.parseDouble(boomDeepStep));
        SensorInstallations.setBoomDeepMin(Double.parseDouble(boomDeepMin));
        SensorInstallations.setBoomDeepMax(Double.parseDouble(boomDeepMax));

        SensorInstallations.setIsManualJson(false);
        SensorInstallations.setManualJson("{\"text\":\"example\"}");
        return "The default values are set.";
    }

    /**
     * Запуск командной строки терминала
     */
    public void start(){
        sensorsReset();                                 /**Все значения в default*/
        SensorInstallations.setTerminalStart(true);     /**Флаг запуска терминала в true*/
        String command = "";                            /**Команды для выполнения*/
        isActive = true;                                /**Терминал активен*/
        System.out.println("\n\tJSON terminal");

        /**Пока терминал активен, вводим и обрабатываем команды*/
        while(isActive) {
            System.out.print("> ");
            /**Все введенные команды переводятся в нижний регистр и разбираются по словам в массив строк*/
            command = scanner.nextLine();
            SensorInstallations.setConsoleCommand(true);    //отмечаем, что команда получена из консоли, а не из web
            runCommand(command);                            //выполняем команду
        }
        if(scanner!=null){
            scanner.close();
            scanner=null;
        }
        stop();                                             //остановка актуатором
    }

    /**
     * Остановка сервисов из консоли
     */
    public void stop(){
        isExit = true;
        SensorInstallations.setTerminalStart(false);        //отмечаем, что терминал отключен
        ShutdownManager.stop(actuatorPort);
    }


    public boolean isActive() {
        return isActive;
    }

    public static void setActive(boolean active) {
        isActive = active;
    }

    public static Scanner getScanner() {
        return scanner;
    }

    public static void setScanner(Scanner sc) {
        scanner = sc;
    }

    public boolean isExit() {
        return isExit;
    }

    public static void setExit(boolean exit) {
        isExit = exit;
    }

    /**
     * Выполнение команд терминала
     * @param command текст команды
     * @return строка сообщения о выполнении или ошибки
     */
    public String runCommand(String command){
        String returnMessage = "";
        command.toLowerCase(Locale.ROOT);
        String words[] = command.split(" ");
        /**Если что-то было введено, то проверяем что и выполняем*/
        if (words.length > 0) {
            switch (words[0]) {
                case "help" -> {
                    returnMessage = help(); //при выполнении команды, записываем ответ метода для вывода в web
                }
                case "info" -> {
                    returnMessage = info();
                }
                case "json" -> {
                    if (words.length >= 3) {
                        if (words.length==3 && words[1].equals("speed")) {
                            try {
                                int speed = Integer.parseInt(words[2]);
                                if(jsonSpeedDelay(speed)>=0){
                                    /** В returnMessage пишем ответ от метода, и если команда была послана из
                                     * консоли, то выводим сообщение в консоль, иначе не выводим, а пользуем
                                     * returnMessage в вебе.*/
                                    returnMessage = "New speed: " + speed + " files per second.";
                                    if(SensorInstallations.isConsoleCommand()){
                                        System.out.println(returnMessage);
                                    }
                                }
                                else{
                                    returnMessage = "Error input command";
                                }

                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) System.out.println("NumberFormatException");
                            }
                        }
                        else if(words[1].equals("manual")){
                            String jsonString = "";
                            for(int i=2; i<words.length; i++){
                                if(i>2){
                                    jsonString+=" ";
                                }
                                jsonString+=words[i];
                            }
                            SensorInstallations.setManualJson(jsonString);
                            SensorInstallations.setIsManualJson(true);
                            returnMessage = "Manual json: " + jsonString;
                            if(SensorInstallations.isConsoleCommand()){
                                System.out.println("Manual json: " + jsonString );
                            }
                        }

                    }
                    else{
                        returnMessage = "Error input command";
                    }
                }
                case "antenna" -> {
                    if (words.length == 3) {
                        if (words[1].equals("speed")) {
                            try {
                                int speed = Integer.parseInt(words[2]);
                                if (speed > 0 && speed <= 100) {
                                    SensorInstallations.setAntennaSpeed(speed);
                                    returnMessage="New antennaCoordinates speed: "  + speed +  " values per second.";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println("New antennaCoordinates speed: " + speed + " values per second.");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        } else if (words[1].equals("step")) {
                            try {
                                double step = Double.parseDouble(words[2]);
                                if (step >= 0 && step < SensorInstallations.getAntennaAzimuthMax()) {
                                    SensorInstallations.setAntennaStep(step);
                                    returnMessage = "New antennaCoordinates step: " + step
                                            + " (" + SensorInstallations.getAntennaSpeed() * step + " values per second)";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println(returnMessage);
                                    }

                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        } else if (words[1].equals("y") || words[1].equals("z")) {
                            try {
                                double num = Double.parseDouble(words[2]);
                                switch (words[1]) {
                                    case "y" -> SensorInstallations.setaY(num);
                                    case "z" -> SensorInstallations.setaZ(num);
                                }
                                returnMessage = "The new value of the " + words[1].toUpperCase()
                                        + " (" + num + ") antennaCoordinate is set.";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    }else if (words.length == 4) {
                        if (words[1].equals("x") && words[2].equals("step")) {
                            try {
                                double step = Double.parseDouble(words[3]);
                                if (step >= 0 && step < SensorInstallations.getAXMax()) {
                                    SensorInstallations.setAntennaXStep(step);
                                    returnMessage = "New antennaCoordinates X step: " + step
                                            + " (" + SensorInstallations.getAntennaXStep() * step + " values per second)";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println(returnMessage);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    }else if (words.length == 5) {
                        if (words[1].equals("azimuth") && words[2].equals("range")) {
                            try {
                                double minValue = Math.abs(Double.parseDouble(words[3]));
                                double maxValue = Math.abs(Double.parseDouble(words[4]));
                                if (maxValue - minValue < 0) {
                                    double tmp = maxValue;
                                    maxValue = minValue;
                                    minValue = tmp;
                                }
                                SensorInstallations.setAntennaAzimuthMin(minValue);
                                SensorInstallations.setAntennaAzimuthMax(maxValue);
                                returnMessage = "New antenna azimuth range [" + minValue + ", " + maxValue + "]";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }else if (words[1].equals("x") && words[2].equals("range")) {
                            try {
                                double minValue = Math.abs(Double.parseDouble(words[3]));
                                double maxValue = Math.abs(Double.parseDouble(words[4]));
                                if (maxValue - minValue < 0) {
                                    double tmp = maxValue;
                                    maxValue = minValue;
                                    minValue = tmp;
                                }
                                SensorInstallations.setAXMin(minValue);
                                SensorInstallations.setAXMax(maxValue);
                                returnMessage = "New antenna X range [" + minValue + ", " + maxValue + "]";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    }
                }
                case "boomangle" -> {
                    if (words.length == 3) {
                        if (words[1].equals("speed")) {
                            try {
                                int speed = Integer.parseInt(words[2]);
                                if (speed > 0 && speed <= 10) {
                                    SensorInstallations.setBoomAngleSpeed(speed);

                                    returnMessage = "New boomAngle speed: " + speed + " values per second.";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println(returnMessage);
                                    }

                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        } else if (words[1].equals("step")) {
                            try {
                                double step = Double.parseDouble(words[2]);
                                if (step >= 0 && step < SensorInstallations.getBoomAngleMax()) {
                                    SensorInstallations.setBoomAngleStep(step);

                                    returnMessage = "New BoomAngle step: " + SensorInstallations.getBoomAngleStep()
                                            + " (" + SensorInstallations.getAntennaSpeed() * SensorInstallations.getBoomAngleStep() + " values per second)";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println(returnMessage);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    } else if (words.length == 4) {
                        if (words[1].equals("range")) {
                            try {
                                double minValue = Math.abs(Double.parseDouble(words[2]));
                                double maxValue = Math.abs(Double.parseDouble(words[3]));
                                if (maxValue - minValue < 0) {
                                    double tmp = maxValue;
                                    maxValue = minValue;
                                    minValue = tmp;
                                }
                                SensorInstallations.setBoomAngleMin(minValue);
                                SensorInstallations.setBoomAngleMax(maxValue);

                                returnMessage = "New BoomAngle range [" + minValue + ", " + maxValue + "]";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }

                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    }
                }

                case "tiltangles" -> {
                    if (words.length == 3 && (words[1].equals("x") || words[1].equals("y"))) {
                        try {
                            double num = Double.parseDouble(words[2]);
                            switch (words[1]) {
                                case "x" -> SensorInstallations.settX(num);
                                case "y" -> SensorInstallations.settY(num);
                            }

                            returnMessage = "The new value of the " + words[1].toUpperCase()
                                    + " (" + num + ") tiltAngles is set.";
                            if(SensorInstallations.isConsoleCommand()) {
                                System.out.println(returnMessage);
                            }
                        } catch (NumberFormatException e) {
                            returnMessage = "NumberFormatException";
                            if(SensorInstallations.isConsoleCommand()) {
                                System.out.println(returnMessage);
                            }
                        }
                    }

                }

                case "boomdeep" -> {
                    if (words.length == 3) {
                        if (words[1].equals("speed")) {
                            try {
                                int speed = Integer.parseInt(words[2]);
                                if (speed > 0 && speed <= 100) {
                                    SensorInstallations.setBoomDeepSpeed(speed);

                                    returnMessage = "New boomDeep speed: " + speed + " values per second.";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println(returnMessage);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        } else if (words[1].equals("step")) {
                            try {
                                double step = Double.parseDouble(words[2]);
                                if (step >= 0 && step < SensorInstallations.getBoomDeepMax()) {
                                    SensorInstallations.setBoomDeepStep(step);

                                    returnMessage = "New BoomDeep step: " + step
                                            + " (" + SensorInstallations.getAntennaSpeed() * step + " values per second)";
                                    if(SensorInstallations.isConsoleCommand()) {
                                        System.out.println(returnMessage);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    } else if (words.length == 4) {
                        if (words[1].equals("range")) {
                            try {
                                double minValue = Math.abs(Double.parseDouble(words[2]));
                                double maxValue = Math.abs(Double.parseDouble(words[3]));
                                if (maxValue - minValue < 0) {
                                    double tmp = maxValue;
                                    maxValue = minValue;
                                    minValue = tmp;
                                }
                                SensorInstallations.setBoomDeepMin(minValue);
                                SensorInstallations.setBoomDeepMax(maxValue);
                                returnMessage = "New BoomDeep range [" + minValue + ", " + maxValue + "]";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }

                            } catch (NumberFormatException e) {
                                returnMessage = "NumberFormatException";
                                if(SensorInstallations.isConsoleCommand()) {
                                    System.out.println(returnMessage);
                                }
                            }
                        }
                    }

                }

                case "reset" -> {
                    returnMessage = sensorsReset();
                    if(SensorInstallations.isConsoleCommand()) {
                        System.out.println(returnMessage);
                    }
                }

                case "exit" -> {
                    if(SensorInstallations.isConsoleCommand()) {
                        isActive = false;
                        scanner.close(); //-------------------------------------------------
                        scanner=null;
                        stop();
                        System.out.println("Exit the terminal. Exits all processes.");
                    }
                    else{
                        returnMessage="Please, press exit button.";
                    }
                }

                case "boomangleopen" -> {
                    if (words.length == 2) {
                        try {
                            int valueForOpen = Integer.parseInt(words[1]);
                            SensorInstallations.setBoomAngleOpen(valueForOpen);
                            returnMessage = "The backhoe bucket will open when boomAngle will be " + valueForOpen + ".";
                            if(SensorInstallations.isConsoleCommand()) {
                                System.out.println(returnMessage);
                            }
                        } catch (NumberFormatException e) {
                            returnMessage = "NumberFormatException";
                            if(SensorInstallations.isConsoleCommand()) {
                                System.out.println(returnMessage);
                            }
                        }
                    }
                }
            }
        }
        return returnMessage;
    }


    /**
     * Формируем help сообщение и выводим его в консоль
     * если команда была консольная. В вебе этот текст выводится отдельно,
     * без задействия этого метода.
     * @return String строка для web
     */
    private String help(){
        if(!SensorInstallations.isConsoleCommand()){
            return "For help please press to up spoiler.";
        }
        System.out.println("\tTerminal commands : ");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("| help                                    | This information                                            |");
        System.out.println("| info                                    | Show current settings                                       |");
        System.out.println("| json speed <int>                        | Speed of sending json. Number json in 1 second (def 10).    |");
        System.out.println("| json manual <json string>               | Immediate sending of a manually created json.               |");
        System.out.println("| antenna speed <int>                     | The speed of change of antenna sensor per second (def 10).  |");
        System.out.println("| antenna step <double>                   | Step change azimuth values (def 0.1).                       |");
        System.out.println("| antenna x range <double> <double>       | Antenna x value range [min_value,max_value], def [0.0,90.0] |");
        System.out.println("| antenna x step <double>                 | Step change Antenna x values (def 0.1).                     |");
        System.out.println("| antenna y <double>                      | Constant antenna values y (def 1.0).                        |");
        System.out.println("| antenna z <double>                      | Constant values z (def 1.0).                                |");
        System.out.println("| antenna azimuth range <double> <double> | Azimuth value range [min_value,max_value], def [0.0,90.0]   |");
        System.out.println("| boomangle speed <int>                   | The speed of change of boomAngle sensor per second (def 10).|");
        System.out.println("| boomangle step <double>                 | Step change boomAngle values (def 0.1).                     |");
        System.out.println("| boomangle range <double> <double>       | boomAngle value range [min_value,max_value], def [45.0,84.0]|");
        System.out.println("| tiltangles x <double>                   | Constant tiltangles values x (def 0.0).                     |");
        System.out.println("| tiltangles y <double>                   | Constant tiltangles values y (def 0.0).                     |");
        System.out.println("| boomdeep speed <int>                    | The speed of change of boomDeep sensor per second (def 10). |");
        System.out.println("| boomdeep step <double>                  | Step change boomDeep values (def 0.1).                      |");
        System.out.println("| boomdeep range <double> <double>        | boomDeep value range [min_value,max_value], def [0.0,4.0]   |");
        System.out.println("| reset                                   | Set all default values.                                     |");
        System.out.println("| exit                                    | Stop the JSON transfer and terminate the program.           |");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        return "";
    }

    /**
     * Инфо строка для web.И вывод инфо в консоль, если команда консольная.
     * @return returnMessage
     */
    private String info(){
        String returnMessage = "server port = " + SensorInstallations.getPort() + "<br>"
                + "json speed = " + 1000/SensorInstallations.getSensorsDelay() + " per sec" + "<br>"
                + "antenna coordinates <br>&nbsp;&nbsp; X[" + SensorInstallations.getAXMin() + ", " + SensorInstallations.getAXMax()
                + "]. Speed " + SensorInstallations.getAntennaSpeed() + " per sec. Step "
                + SensorInstallations.getAntennaXStep() + "<br>&nbsp;&nbsp; Y = " + SensorInstallations.getaY()
                + "<br>&nbsp;&nbsp; Z = " + SensorInstallations.getaZ()
                + "<br>&nbsp;&nbsp; azimuth[" + SensorInstallations.getAntennaAzimuthMin()
                + ", " + SensorInstallations.getAntennaAzimuthMax() + "]. Speed " + SensorInstallations.getAntennaSpeed()
                + " per sec. Step " + SensorInstallations.getAntennaStep()
                + "<br> boomAngle[" + SensorInstallations.getBoomAngleMin() + ", "
                + SensorInstallations.getBoomAngleMax() + "]. Speed " + SensorInstallations.getBoomAngleSpeed()
                + " per sec. Step " + SensorInstallations.getBoomAngleStep() + "<br>tiltAngles"
                + "<br>&nbsp;&nbsp; X = " + SensorInstallations.gettX() + ",&nbsp;&nbsp; Y = " + SensorInstallations.gettY()
                + "<br>boomDeep              | range [" + SensorInstallations.getBoomDeepMin()
                + ", " + SensorInstallations.getBoomDeepMax() + "]. Speed "
                + SensorInstallations.getBoomDeepSpeed() + " per sec. Step "
                + SensorInstallations.getBoomDeepStep()
                ;

        if(SensorInstallations.isConsoleCommand()){
            System.out.println("\tInformation on sensor settings : ");
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" server port           | " + SensorInstallations.getPort());
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" json speed            | " + 1000/SensorInstallations.getSensorsDelay() + " per sec");
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" antenna coordinates:  | ");
            System.out.println("                    x  | range [" + SensorInstallations.getAXMin()
                    + ", " + SensorInstallations.getAXMax() + "]. Speed "
                    + SensorInstallations.getAntennaSpeed() + " per sec. Step "
                    + SensorInstallations.getAntennaXStep());
            System.out.println("                    y  | constant = " + SensorInstallations.getaY());
            System.out.println("                    z  | constant = " + SensorInstallations.getaZ());
            System.out.println("              azimuth  | range [" + SensorInstallations.getAntennaAzimuthMin()
                    + ", " + SensorInstallations.getAntennaAzimuthMax() + "]. Speed "
                    + SensorInstallations.getAntennaSpeed() + " per sec. Step "
                    + SensorInstallations.getAntennaStep());
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" boomAngle             | range [" + SensorInstallations.getBoomAngleMin()
                    + ", " + SensorInstallations.getBoomAngleMax() + "]. Speed "
                    + SensorInstallations.getBoomAngleSpeed() + " per sec. Step "
                    + SensorInstallations.getBoomAngleStep());
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" tiltAngles:           | ");
            System.out.println("                    x  | constant = " + SensorInstallations.gettX());
            System.out.println("                    y  | constant = " + SensorInstallations.gettY());
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println(" boomDeep              | range [" + SensorInstallations.getBoomDeepMin()
                    + ", " + SensorInstallations.getBoomDeepMax() + "]. Speed "
                    + SensorInstallations.getBoomDeepSpeed() + " per sec. Step "
                    + SensorInstallations.getBoomDeepStep());
            System.out.println("-------------------------------------------------------------------------------------");
        }
        return returnMessage;

    }

    /**
     * Установка скорости подачи json
     * @param speed число json в секунду
     * @return speed|-1 скорость или -1 если значение некорректное
     */
    public int jsonSpeedDelay(int speed){
        if (speed >= 0 && speed < 100) {
            int delay = Integer.MAX_VALUE;
            if (speed > 0) {
                delay = 1000 / speed;
            }
            SensorInstallations.setSensorsDelay(delay);
            return speed;
        }
        else{
            return -1;
        }
    }

}
