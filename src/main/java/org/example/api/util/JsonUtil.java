package org.example.api.util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String success(Object data) {
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        obj.add("data", gson.toJsonTree(data));
        return gson.toJson(obj);
    }

    public static String error(int code, String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("success", false);
        obj.addProperty("code", code);
        obj.addProperty("message", message);
        return gson.toJson(obj);
    }
}