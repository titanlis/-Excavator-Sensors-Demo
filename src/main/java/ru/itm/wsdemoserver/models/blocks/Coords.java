package ru.itm.wsdemoserver.models.blocks;

import lombok.Data;

/**
 * @class Coords Трехмерные координаты
 * Используется в списке координат внутри полигонов.
 */
@Data
public class Coords {
    private int numPoint;
    private double x;
    private double y;
    private double z;

    public Coords(int numPoint, double x, double y, double z) {
        this.numPoint = numPoint;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
