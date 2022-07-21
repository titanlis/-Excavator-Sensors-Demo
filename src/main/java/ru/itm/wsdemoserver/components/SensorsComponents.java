package ru.itm.wsdemoserver.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.itm.wsdemoserver.data.Polygons;
import ru.itm.wsdemoserver.models.blocks.Block;
import ru.itm.wsdemoserver.models.bucketdump.BucketDump;
import ru.itm.wsdemoserver.models.sensors.AntennaCoordinates;
import ru.itm.wsdemoserver.models.sensors.Sensors;
import ru.itm.wsdemoserver.models.sensors.TiltAngles;
import ru.itm.wsdemoserver.terminal.Terminal;

/**
 * @class SensorsComponents
 * Компоненты сервера. Здесь сенсоры, полигоны, блоки, открывашка ковша и сам терминал.
 * Все они инжектятся в wsserver
 */
@Component
public class SensorsComponents {

    /**Значения сенсоров по умолчанию. Берем из application.properties*/
    @Value("${manual.aX}")
    private String startAX;
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

    /**
     * Создание сенсоров для сервера
     * @return Sensors сенсоры со стартовыми значениями из application.properties
     */
    @Bean
    public Sensors currentSensors(){
        return new Sensors(
                new AntennaCoordinates(
                        Double.parseDouble(startAX),
                        Double.parseDouble(startAY),
                        Double.parseDouble(startAZ), Double.parseDouble(startAAzimuth)),
                Double.parseDouble(startBA),
                new TiltAngles(
                        Double.parseDouble(startTX),
                        Double.parseDouble(startTY)),
                        Double.parseDouble(startBD));
    }

    /**
     * Создание терминала
     * @return Terminal используется в WSServer
     */
    @Bean
    public Terminal makeTerminal(){
        return new Terminal();
    }

    /**
     * Создание полигонов
     * @return Polygons полигоны создаются и инициализируются в конструкторе
     */
    @Bean
    public Polygons makePolygons(){
        return new Polygons();
    }

    /**
     * Создание блоков
     * @return Block блоки имеют статические значения и формируются в конструкторе
     */
    @Bean
    public Block makeBlock(){
        return new Block();
    }

    /**
     * Данные об открытии ковша
     * @return BucketDump одно значение bucketDump=1
     */
    @Bean
    public BucketDump makeBucketDump(){
        return new BucketDump();
    }

}
