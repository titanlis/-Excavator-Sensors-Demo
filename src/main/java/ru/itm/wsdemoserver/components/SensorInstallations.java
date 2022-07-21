package ru.itm.wsdemoserver.components;

/**
 * @class SensorInstallations
 * Класс хранит все текущие настройки сенсоров и сервера.
 * Через изменение статических данных этого класса потоки сервера общаются друг с другом.
 * Состоит из данных и методов доступа.
 */
public class SensorInstallations {

    private static int port = 8080;                     //порт по умолчанию
    private static boolean terminalStart = false;       //флаг включения потока терминала
                                                        //включен ли терминал?
    private static boolean threadsStart = false;        //флаг включения основного потока json
                                                        //запущен ли поток обновления координат?
    private static double aX=1.0;                       //текущая координата антенны X
    private static double aY=1.0;                       //текущая координата антенны Y
    private static double aZ=1.0;                       //текущая координата антенны Z
    private static double aAz=0.0;                      //текущий азимут антенны
    private static double bA=45.0;                      //текущий датчик стрелы boomAngle в градусах
    private static double tX=0.0;                       //текущая координата tiltAngles X датчик углов наклона стрелы
    private static double tY=0.0;                       //текущая координата tiltAngles y датчик углов наклона стрелы
    private static double bD=0.0;                       //текущее значение boomDeep насколько выдвинута стрела

    private static double aXMin = 0.0;                  //минимальное значение координаты X антенны
    private static double aXMax = 90.0;                 //максимальное значение координаты X антенны
    private static double  antennaXStep=0.1;            //шаг изменения координаты X антенны


    private static double boomAngleOpen = 29;           //значение датчика стрелы когда нужно произвести открытие ковша
    private static int sensorsDelay = 100;              //задержка отправки json в миллисекундах (10 шт в сек)

    private static int antennaSpeed=10;                 //скорость обновления координат антенны 10 раз в секунду
                                                        //работает на все координаты кроме X (у X своя скорость)
    private static double  antennaStep=0.1;             //шаг обновления координат антенны (работает с азимутом),
                                                        //у X свой шаг, а Y и Z константы

    private static double  antennaAzimuthMin=0.0;       //минимальное значение азимута
    private static double  antennaAzimuthMax=90.0;      //максимальное значение азимута


    private static int boomAngleSpeed=10;               //скорость обновления boomAngle 10 раз в сек
    private static double  boomAngleStep=0.1;           //шаг обновления boomAngle

    private static double  boomAngleMin=29.0;           //минимальный угол наклона стрелы
    private static double  boomAngleMax=84.0;           //максимальный угол наклона стрелы


    private static int boomDeepSpeed=10;                //скорость обновления данных о выдвижении стрелы
    private static double  boomDeepStep=0.1;            //шаг изменения данных о выдвижении стрелы

    private static double  boomDeepMin=0.0;             //минимальное значение (задвинута стрела)
    private static double  boomDeepMax=4.0;             //максимальное значение (выдвинута стрела)


    private static boolean isManualJson = false;        //флаг необходимости отправки мануального json
    private static String manualJson = "{\"text\":\"example\"}";    //строка с мануальным json
                                                                    //может быть любой, все равно будет заменена

    private static boolean consoleCommand = true;       //флаг источника команды (консоль или веб)

    public static boolean isConsoleCommand() {
        return consoleCommand;
    }

    public static void setConsoleCommand(boolean consoleCommand) {
        SensorInstallations.consoleCommand = consoleCommand;
    }

    public static boolean isIsManualJson() {
        return isManualJson;
    }

    public static void setIsManualJson(boolean isManualJson) {
        SensorInstallations.isManualJson = isManualJson;
    }

    public static String getManualJson() {
        return manualJson;
    }

    public static void setManualJson(String manualJson) {
        SensorInstallations.manualJson = manualJson;
    }

    public static double getBoomAngleOpen() {
        return boomAngleOpen;
    }

    public static void setBoomAngleOpen(double boomAngleOpen) {
        SensorInstallations.boomAngleOpen = boomAngleOpen;
    }

    public static boolean isTerminalStart() {
        return terminalStart;
    }

    public static void setTerminalStart(boolean terminalStart) {
        SensorInstallations.terminalStart = terminalStart;
    }

    public static int getBoomDeepSpeed() {
        return boomDeepSpeed;
    }

    public static boolean isThreadsStart() {
        return threadsStart;
    }

    public static void setThreadsStart(boolean threadsStart) {
        SensorInstallations.threadsStart = threadsStart;
    }

    public static void setBoomDeepSpeed(int boomDeepSpeed) {
        SensorInstallations.boomDeepSpeed = boomDeepSpeed;
    }

    public static double getBoomDeepStep() {
        return boomDeepStep;
    }

    public static void setBoomDeepStep(double boomDeepStep) {
        SensorInstallations.boomDeepStep = boomDeepStep;
    }

    public static double getBoomDeepMin() {
        return boomDeepMin;
    }

    public static void setBoomDeepMin(double boomDeepMin) {
        SensorInstallations.boomDeepMin = boomDeepMin;
    }

    public static double getBoomDeepMax() {
        return boomDeepMax;
    }

    public static void setBoomDeepMax(double boomDeepMax) {
        SensorInstallations.boomDeepMax = boomDeepMax;
    }

    public static int getBoomAngleSpeed() {
        return boomAngleSpeed;
    }

    public static void setBoomAngleSpeed(int boomAngleSpeed) {
        SensorInstallations.boomAngleSpeed = boomAngleSpeed;
    }

    public static double getBoomAngleStep() {
        return boomAngleStep;
    }

    public static void setBoomAngleStep(double boomAngleStep) {
        SensorInstallations.boomAngleStep = boomAngleStep;
    }

    public static double getBoomAngleMin() {
        return boomAngleMin;
    }

    public static void setBoomAngleMin(double boomAngleMin) {
        SensorInstallations.boomAngleMin = boomAngleMin;
    }

    public static double getBoomAngleMax() {
        return boomAngleMax;
    }

    public static void setBoomAngleMax(double boomAngleMax) {
        SensorInstallations.boomAngleMax = boomAngleMax;
    }

    public static double getAntennaAzimuthMin() {
        return antennaAzimuthMin;
    }

    public static void setAntennaAzimuthMin(double antennaAzimuthMin) {
        SensorInstallations.antennaAzimuthMin = antennaAzimuthMin;
    }

    public static double getAntennaAzimuthMax() {
        return antennaAzimuthMax;
    }

    public static void setAntennaAzimuthMax(double antennaAzimuthMax) {
        SensorInstallations.antennaAzimuthMax = antennaAzimuthMax;
    }

    public static int getAntennaSpeed() {
        return antennaSpeed;
    }

    public static void setAntennaSpeed(int antennaSpeed) {
        SensorInstallations.antennaSpeed = antennaSpeed;
    }

    public static double getAntennaStep() {
        return antennaStep;
    }

    public static void setAntennaStep(double antennaStep) {
        SensorInstallations.antennaStep = antennaStep;
    }

    public static double getaX() {
        return aX;
    }

    public static void setaX(double aX) {
        SensorInstallations.aX = aX;
    }

    public static double getaY() {
        return aY;
    }

    public static void setaY(double aY) {
        SensorInstallations.aY = aY;
    }

    public static double getaZ() {
        return aZ;
    }

    public static void setaZ(double aZ) {
        SensorInstallations.aZ = aZ;
    }

    public static double getaAz() {
        return aAz;
    }

    public static void setaAz(double aAz) {
        SensorInstallations.aAz = aAz;
    }

    public static double getbA() {
        return bA;
    }

    public static void setbA(double bA) {
        SensorInstallations.bA = bA;
    }

    public static double gettX() {
        return tX;
    }

    public static void settX(double tX) {
        SensorInstallations.tX = tX;
    }

    public static double gettY() {
        return tY;
    }

    public static void settY(double tY) {
        SensorInstallations.tY = tY;
    }

    public static double getbD() {
        return bD;
    }

    public static void setbD(double bD) {
        SensorInstallations.bD = bD;
    }

    public static int getSensorsDelay() {
        return sensorsDelay;
    }

    public static void setSensorsDelay(int sensorsDelay) {
        SensorInstallations.sensorsDelay = sensorsDelay;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        SensorInstallations.port = port;
    }


    public static double getAXMin() {
        return aXMin;
    }

    public static void setAXMin(double aXMin) {
        SensorInstallations.aXMin = aXMin;
    }

    public static double getAXMax() {
        return aXMax;
    }

    public static void setAXMax(double aXMax) {
        SensorInstallations.aXMax = aXMax;
    }


    public static double getAntennaXStep() {
        return antennaXStep;
    }

    public static void setAntennaXStep(double antennaXStep) {
        SensorInstallations.antennaXStep = antennaXStep;
    }
}
