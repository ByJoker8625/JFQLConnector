package de.jokergames.connector.util;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Janick
 */

public class Column {

    private final JSONObject jsonObject;

    public Column(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public long getCreation() {
        return jsonObject.getLong("creation");
    }

    public Object getObject(String key) {
        return jsonObject.getJSONObject("content").get(key);
    }

    public String getString(String key) {
        if (getObject(key) == null) {
            return null;
        }

        return getObject(key).toString();
    }

    public int getInteger(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Integer.parseInt(getString(key));
    }

    public long getLong(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Long.parseLong(getString(key));
    }

    public float getFloat(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Float.parseFloat(getString(key));
    }

    public double getDouble(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Double.parseDouble(key);
    }

    public short getShort(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Short.parseShort(getString(key));
    }

    public List<Object> getList(String key) {
        return jsonObject.getJSONObject("content").getJSONArray(key).toList();
    }

    @Deprecated
    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
