package ru.itm.wsdemoserver.configuration.coders;

import com.google.gson.Gson;
import ru.itm.wsdemoserver.models.ModelJSON;
import ru.itm.wsdemoserver.models.sensors.Sensors;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * @class SensorsDecoder
 * JSON декодер от Google
 * Подсовываем ему родительский для всех наших классов-моделей ModelJSON.
 * В результате для всех моделей преобразование из JSON в нужную модель происходит корректно.
 */
public class SensorsDecoder implements Decoder.Text<ModelJSON> {

    private static Gson gson = new Gson();

    @Override
    public ModelJSON decode(String s) throws DecodeException {
        return gson.fromJson(s, Sensors.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources (if any used)
    }
}