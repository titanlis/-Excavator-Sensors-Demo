/**
 * @file ShutdownManager.java
 * Корректная остановка сервиса. Реализуется с помощью Spring Actuator.
 */
package ru.itm.wsdemoserver.controllers;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ShutdownManager implements ApplicationContextAware {

    @Value("${management.server.port}")
    private String actuatorPort;

    private ApplicationContext context;


    /**
     * Остановка контекста. Быстрая остановка передачи jsons
     * @return Service is stopped : {"message":"Shutting down, bye..."}
     */
    @GetMapping("/exit")
    public String shutdownContext() {

        String e = stop(actuatorPort);
        ((ConfigurableApplicationContext) context).close();
        System.exit(0);
        return "Service is stopped : " + e;
    }

    public static String stop(String port){
        /*Endpoint для отключения */
        String url = "http://localhost:"+ port +"/actuator/shutdown";

        /* Для срабатывания выключения через Spring Actuator мы должны сформировать POST HTTP
           запрос и отправить его на endpoint */
        // Http Headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        // Data attached to the request.
        HttpEntity<String> requestBody = new HttpEntity<>("", headers);
        // Send request with POST method. Пост запрос на /actuator/shutdown
        return restTemplate.postForObject(url, requestBody, String.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
