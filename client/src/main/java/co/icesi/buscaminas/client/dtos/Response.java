package co.icesi.buscaminas.client.dtos;

import java.util.Map;

import com.google.gson.Gson;

import co.icesi.buscaminas.client.model.Cell;

public class Response {

    public String status;

    public Map<String, Object> data;

    public Cell[][] getBoard() {

        if (
                data == null
                        || !data.containsKey("board")
        ) {

            return null;
        }

        Gson gson = new Gson();

        return gson.fromJson(
                gson.toJson(
                        data.get("board")
                ),
                Cell[][].class
        );
    }

    public boolean getBoolean(
            String key,
            boolean defaultValue
    ) {

        if (
                data == null
                        || !data.containsKey(key)
        ) {

            return defaultValue;
        }

        Object value =
                data.get(key);

        return value instanceof Boolean
                ? (Boolean) value
                : defaultValue;
    }

    public String getMessage() {

        if (
                data == null
                        || !data.containsKey("message")
        ) {

            return null;
        }

        return String.valueOf(
                data.get("message")
        );
    }
}