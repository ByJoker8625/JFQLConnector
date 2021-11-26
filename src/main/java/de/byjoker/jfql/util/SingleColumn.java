package de.byjoker.jfql.util;

import org.json.JSONArray;
import org.json.JSONObject;

public interface SingleColumn {

    String getString();

    int getInteger();

    long getLong();

    float getFloat();

    double getDouble();

    short getShort();

    boolean getBoolean();

    JSONObject getJsonObject();

    JSONArray getJsonArray();

    <T> T parse(Class<T> clazz);

    boolean isNull();
}
