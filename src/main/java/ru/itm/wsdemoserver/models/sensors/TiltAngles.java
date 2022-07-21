package ru.itm.wsdemoserver.models.sensors;

import lombok.Data;

/**
 * @class TiltAngles
 * @ brief датчик углов наклона
 */
@Data
public class TiltAngles {
    private double x;
    private double y;

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

    public TiltAngles(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @brief String в формате json
     * @return json формат без фигурных скобок
     */
    @Override
    public String toString() {
        return "TiltAngles{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public TiltAngles() {
    }
}