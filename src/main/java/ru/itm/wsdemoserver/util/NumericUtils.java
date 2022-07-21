package ru.itm.wsdemoserver.util;

/**
 * @brief Вспомогательные утилиты
 * @class NumericUtils
 * @details Разные утилиты и методы для проверки числовых данных.
 */
public class NumericUtils {
    /**
     * @brief Проверяет строку на double
     * @param str строка
     * @return true/false на основе регулярного выражения
     */
    public static boolean isDouble(String str)
    {
        return str.matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$");
    }

    public static double round(double d){
        return Double.parseDouble(String.format("%.0f", d));
    }

    public static double round1(double d){
        double dd = Math.round(d*100);
        dd/=100.0;
        dd = Math.round(d*10);
        dd/=10.0;
        return dd;
    }

    public static double round2(double d){
        return Double.parseDouble(String.format("%.2f", d));
    }

}
