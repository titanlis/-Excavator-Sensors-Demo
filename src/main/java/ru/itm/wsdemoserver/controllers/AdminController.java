/**
 * @file AdminController.java
 * Файл с классом-контроллером админки
 */
package ru.itm.wsdemoserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itm.wsdemoserver.components.SensorInstallations;
import ru.itm.wsdemoserver.terminal.Terminal;

import java.util.Scanner;

/**
 * @class AdminController
 * Контролер Web админки.
 * Все команды консоли можно отправить через url/admin
 */
@Controller
public class AdminController {

    private String terminalMessage = "";    //сообщение от исполняющих методов

    private Terminal terminal;              //инжектим терминал для выполнения команд

    @Autowired
    public AdminController(Terminal terminal) {
        this.terminal = terminal;
    }

    /**
     * @brief Инициализация данных в админке
     * @param model
     * @return возврат в админку
     */
    @GetMapping("/admin")
    public String adminGet(Model model){
        setFormData(model);
        return "admin";
    }

    /**
     * @brief Установка скорости
     * @param speedValue значение ползунка скорости
     * @param model
     * @return возврат в админку
     * @details Срабатывает при отправке данных ползунка скорости передачи jsons с сенсорами.
     */
    @PostMapping("/admin")
    public String adminPost(@RequestParam(required = true) String speedValue, Model model){

        /**Читаем числовое значение в виде строки от бегунка скорости, полученной из админки.*/
        Scanner scanner = new Scanner(speedValue);
        if(scanner.hasNextInt()){
            terminal.jsonSpeedDelay(scanner.nextInt()); //если число корректное, меняем скорость
        }
        scanner.close();
        setFormData(model);                 /**Обновляем данные в моделе*/
        return "admin";                     /**Возвращаемся в админку*/
    }


    @GetMapping("/terminal")
    public String terminalGet(@RequestParam(required = true) String terminal_message, Model model){
        setFormData(model);                 /**Обновляем данные в моделе*/
        return  "redirect:admin";           /**Возвращаемся в админку*/
    }

    /**
     * Выполняем команду терминала, полученую из админки
     * @param commands текст команды
     * @param model
     * @return редирект на админку
     */
    @PostMapping("/terminal")
    public String terminalPost(@RequestParam(required = true) String commands, Model model){
        SensorInstallations.setConsoleCommand(false);   //устанавливаем флаг команды админки
        /*Вызываем команду терминала на запуск и выводим в админку результат в виде html*/
        terminalMessage = "<h4><span class=\"mainText\">"+ commands + "</span></h4><span class=\"miniText\">"
                + terminal.runCommand(commands) + "</span>";
        setFormData(model);                 /**Обновляем данные в моделе*/
        return  "redirect:admin";           /**Возвращаемся в админку*/
    }


    /**
     * @brief Reset
     * @param model
     * @return возврат в админку
     * Сброс к значениям по умолчанию
     */
    @GetMapping("/reset")
    public String resetGet(Model model){
        SensorInstallations.setConsoleCommand(false);    //устанавливаем флаг команды админки
        /*Вызываем команду reset терминала и выводим в админку результат в виде html*/
        terminalMessage = "<h4><span class=\"mainText\">reset</span></h4><span class=\"miniText\">"
                + terminal.runCommand("reset") + "</span>";
        setFormData(model);                 /**Обновляем данные в моделе*/
        return  "redirect:admin";           /**Возвращаемся в админку*/
    }


    /**
     * @brief Инициализация модели manual для админки
     * @details Текущие аттрибуты данных сенсоров для ручной отправки добавляются в модель
     * для отражении в web админке. Задержка delay в миллисекундах переводится в скорость
     * передачи : количество файлов в секунду.
     * @param model
     */
    private void setFormData(Model model){
        model.addAttribute("speedValue", (int)(1000/SensorInstallations.getSensorsDelay()));
        model.addAttribute("aX", SensorInstallations.getaX());
        model.addAttribute("aY", SensorInstallations.getaY());
        model.addAttribute("aZ", SensorInstallations.getaZ());
        model.addAttribute("aAzimuth", SensorInstallations.getaAz());
        model.addAttribute("bA", SensorInstallations.getbA());
        model.addAttribute("tX", SensorInstallations.gettX());
        model.addAttribute("tY", SensorInstallations.gettY());
        model.addAttribute("bD", SensorInstallations.getbD());
        model.addAttribute("terminal_message", terminalMessage);
    }

}
