package ru.itm.wsdemoserver.models.bucketdump;

import ru.itm.wsdemoserver.models.ModelJSON;

/**
 * @class BucketDump
 * Модель данных для json с единственным используемым здесь значением 1 для открытия ковша.
 * open -статус открытия ковша: 0 закрыт, 1 открыт, -1 нет данных.
 */
public class BucketDump extends ModelJSON {
    private int bucketDump = 1;
    public BucketDump() {}

    public int getBucketDump() {
        return bucketDump;
    }

    public void setBucketDump(int bucketDump) {
        this.bucketDump = bucketDump;
    }
}
