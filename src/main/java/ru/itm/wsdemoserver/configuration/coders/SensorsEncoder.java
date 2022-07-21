package ru.itm.wsdemoserver.configuration.coders;

import com.google.gson.Gson;
import ru.itm.wsdemoserver.models.ModelJSON;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @class SensorsEncoder
 * JSON кодер от Google
 * Подсовываем ему родительский для всех наших классов-моделей ModelJSON.
 * В результате для всех моделей преобразование в JSON происходит корректно.
 */
public class SensorsEncoder implements Encoder.Text<ModelJSON>  {

    private static Gson gson = new Gson();

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
        // Close resources (if any used)
    }

    @Override
    public String encode(ModelJSON object) throws EncodeException {
        return gson.toJson(object);
    }

}