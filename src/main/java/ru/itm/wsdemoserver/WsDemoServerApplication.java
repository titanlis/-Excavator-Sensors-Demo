package ru.itm.wsdemoserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.itm.wsdemoserver.components.SensorInstallations;

/**
 * @brief Основной класс сервера
 * @class WsDemoServerApplication
 * @details Spring Boot приложение с разрешением на циклически повторяющиеся методы
 */
@EnableScheduling
@SpringBootApplication
public class WsDemoServerApplication {
	private static Logger logger = LoggerFactory.getLogger(WsDemoServerApplication.class);
	/**
	 * @brief java -jar wsdemoserver.jar <port>
	 * @param args аргумент - порт
	 * @details Если аргумент некорректный, не из диапазона [1024;65535],
	 * то ставится порт из application.properties
	 */
	public static void main(String[] args) {

		/** Установка порта из командной строки. Если он не корректный, то берем дефолтный из
		 * application.properties*/
		try{
			if(args.length>0){
				int port = Integer.parseInt(args[0]);
				if(port>=1024 && port<=65535){
					System.setProperty("server.port", args[0]); //установка порта из ком.строки
					logger.info("Port setting " + args[0]);
					SensorInstallations.setPort(port);
				}
			}
		}
		catch(NumberFormatException e){
			logger.error("Error port argument. Set port default.");
		}
		SpringApplication.run(WsDemoServerApplication.class, args);
	}

}
