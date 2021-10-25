package de.byjoker.jfql.util;

import org.json.JSONArray;
import org.json.JSONObject;

public interface Column {

    String getString(String key);

    int getInteger(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    short getShort(String key);

    boolean getBoolean(String key);

    JSONObject getJsonObject(String key);

    JSONArray getJsonArray(String key);

    <T> T parse(String key, Class<T> clazz);

    boolean isNull(String key);

    long getCreation();

}
