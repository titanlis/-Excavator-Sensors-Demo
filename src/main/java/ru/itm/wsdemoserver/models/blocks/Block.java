package ru.itm.wsdemoserver.models.blocks;

import ru.itm.wsdemoserver.models.ModelJSON;

/**
 * @class Block модель данные json для блоков
 * Все данные - хардкод.
 * Ничего не меняется.
 */
public class Block extends ModelJSON {
    private String HPInit ="EQsize";
    private int EQid = 12;
    private String Name = "ЭКГ 12";
    private double BC = 9.0;
    private double BG = 8.2;
    private double L = 13.0;

    public Block() {
    }

    public String getHPInit() {
        return HPInit;
    }

    public void setHPInit(String HPInit) {
        this.HPInit = HPInit;
    }

    public int getEQid() {
        return EQid;
    }

    public void setEQid(int EQid) {
        this.EQid = EQid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getBC() {
        return BC;
    }

    public void setBC(double BC) {
        this.BC = BC;
    }

    public double getBG() {
        return BG;
    }

    public void setBG(double BG) {
        this.BG = BG;
    }

    public double getL() {
        return L;
    }

    public void setL(double l) {
        L = l;
    }

    @Override
    public String toString() {
        return "Block{" +
                "HPInit='" + HPInit + '\'' +
                ", EQid=" + EQid +
                ", Name='" + Name + '\'' +
                ", BC=" + BC +
                ", BG=" + BG +
                ", L=" + L +
                '}';
    }
}
