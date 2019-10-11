package com.mrl.i_note.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2017/11/2.
 */

public class JsonUtil {

    public static String objectToJson(Object obj){
        String json;
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                .create();
        json = gson.toJson(obj);
        return json;
    }

    public static JSONObject objectToJSONObject(String name, Object object) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(name,object);
        return jsonObject;
    }

    public static Object jsonToObject(String json, Class<?> cls){

        Gson gson = new Gson();
        Object obj;
        obj = gson.fromJson(json, cls);
        return obj;

    }

    public static <T> List<T> jsonsToObjects(String jsons, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsons).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<String, Object> jsonToMap(String jsons){
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(jsons, map.getClass());
        return map;
    }

}
