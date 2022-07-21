package ru.itm.wsdemoserver.models.sensors;

import ru.itm.wsdemoserver.components.SensorInstallations;
import ru.itm.wsdemoserver.models.ModelJSON;

/**
 * @class Sensors
 * Данные для передачи сообщения на фронт.
 * Сенсоры экскаватора.
 * <ul>
 *     <li>Координаты антенны x, y, z, и азимут.</li>
 *     <li>Угол наклона мачты с ковшем boomAngle.</li>
 *     <li>Датчик углов наклона x, y.</li>
 *     <li>Глубина стрелы boomDeep.</li>
 * </ul>
 */
public class Sensors extends ModelJSON {
    private AntennaCoordinates antennaCoordinates;  //Координаты антенны x, y, z, и азимут.
    private double boomAngle;                       //Угол наклона мачты с ковшем.
    private TiltAngles tiltAngles;                  //Датчик углов наклона x, y.
    private double boomDeep;                        //глубина стрелы

    public Sensors(AntennaCoordinates antennaCoordinates, double boomAngle, TiltAngles tiltAngles, double boomDeep) {
        this.antennaCoordinates = antennaCoordinates;
        this.boomAngle = boomAngle;
        this.tiltAngles = tiltAngles;
        this.boomDeep = boomDeep;
    }

    public AntennaCoordinates getAntennaCoordinates() {
        return antennaCoordinates;
    }

    public void setAntennaCoordinates(AntennaCoordinates antennaCoordinates) {
        this.antennaCoordinates = antennaCoordinates;
    }

    public double getBoomAngle() {
        return boomAngle;
    }

    public void setBoomAngle(double boomAngle) {
        this.boomAngle = boomAngle;
    }

    public TiltAngles getTiltAngles() {
        return tiltAngles;
    }

    public void setTiltAngles(TiltAngles tiltAngles) {
        this.tiltAngles = tiltAngles;
    }

    public double getBoomDeep() {
        return boomDeep;
    }

    public void setBoomDeep(double boomDeep) {
        this.boomDeep = boomDeep;
    }


    public void loadData(){
        antennaCoordinates.setCoordinates(
                SensorInstallations.getaX(),
                SensorInstallations.getaY(),
                SensorInstallations.getaZ(),
                SensorInstallations.getaAz()
        );
        boomAngle = SensorInstallations.getbD();
        tiltAngles.setX(SensorInstallations.gettX());
        tiltAngles.setY(SensorInstallations.gettY());
        boomDeep = SensorInstallations.getbD();
    }


    public void saveData(){
        SensorInstallations.setaX(antennaCoordinates.getX());
        SensorInstallations.setaY(antennaCoordinates.getY());
        SensorInstallations.setaZ(antennaCoordinates.getZ());
        SensorInstallations.setaAz(antennaCoordinates.getAzimuth());

        SensorInstallations.setbA(boomAngle);

        SensorInstallations.settX(tiltAngles.getX());
        SensorInstallations.settY(tiltAngles.getY());

        SensorInstallations.setbD(boomDeep);
    }

}
