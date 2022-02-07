package de.byjoker.jfql.util;

import org.json.JSONArray;
import org.json.JSONObject;

public interface TableEntry {

    Object getObject(String key);

    String getRawString(String key);

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

    boolean isPresent(String key);

    boolean isNull(String key);

    long getCreation();

}
