package ru.itm.wsdemoserver.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import ru.itm.wsdemoserver.models.blocks.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Polygons
 * Создает 13 полигонов, формирует из них список, предоставляет доступ в порядке замкнутой очереди.
 * Имеет хардкод-зависимость от класса Polygon (плохо, но для демки пойдет).
 * В зависимости 13 возможных полигонов с привязанными через id значениями. Значения координат
 * зашиты в конструкторе полигона.
 */
public class Polygons {
    private int polygonsCount;          //число полигонов
    private int nextNumberPolygon;      //номер следующего полигона при очередном доступе
    private List<Polygon> list;         //список полигонов

    /**
     * По id от 1 до 13 создаются полигоны и добавляются в список.
     * Данные заполняет конструктор полигона. В него зашито 13 вариантов, которые подтягиваются
     * в зависимости от переданного порядкового номера id.
     */
    public Polygons() {
        nextNumberPolygon = 0;
        polygonsCount = 13;
        list = new ArrayList<Polygon>();
        for(int id=1; id<=polygonsCount; id++){
            list.add(new Polygon(id));
        }
    }

    /**
     * Доступ к следующему полигону (круговая очередь)
     * @return Polygon следующий полигон
     */
    public Polygon getNext(){
        Polygon p = list.get(nextNumberPolygon);
        nextNumberPolygon++;
        if(nextNumberPolygon>=polygonsCount){
            nextNumberPolygon = 0;
        }
        return p;
    }

    /**
     * Число полигонов в списке
     * @return size размер списка с полигонами
     */
    public int size(){
        return list.size();
    }
}
