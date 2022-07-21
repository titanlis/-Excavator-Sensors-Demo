package ru.itm.wsdemoserver.models.sensors;

import lombok.Data;

/**
 * @class AntennaCoordinates
 * @brief Координаты антенны x, y, z, и азимут
 */
public class AntennaCoordinates {
    private double x;
    private double y;
    private double z;
    private double azimuth;

    public AntennaCoordinates(double x, double y, double z, double azimuth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.azimuth = azimuth;
    }

    public void setCoordinates(double x, double y, double z, double azimuth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.azimuth = azimuth;
    }

    /**
     * @brief String в формате json
     * @return json формат без фигурных скобок
     */
    @Override
    public String toString() {
        return "AntennaCoordinates{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", azimuth=" + azimuth +
                '}';
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }


}
