package de.jokergames.jfql.util;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Janick
 */

public class Column {

    private final JSONObject jsonObject;
    private final Object content;

    public Column(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.content = null;
    }

    public Column(Object content) {
        this.jsonObject = null;
        this.content = content;
    }

    public long getCreation() {
        if (jsonObject == null) {
            return -1;
        }

        return jsonObject.getLong("creation");
    }

    public Object getObject() {
        return content;
    }

    public String getString() {
        if (getObject() == null) {
            return null;
        }

        return getObject().toString();
    }

    public int getInteger() {
        if (getString() == null) {
            return -1;
        }

        return Integer.parseInt(getString());
    }

    public long getLong() {
        if (getString() == null) {
            return -1;
        }

        return Long.parseLong(getString());
    }

    public float getFloat() {
        if (getString() == null) {
            return -1;
        }

        return Float.parseFloat(getString());
    }

    public double getDouble() {
        if (getString() == null) {
            return -1;
        }

        return Double.parseDouble(getString());
    }

    public short getShort() {
        if (getString() == null) {
            return -1;
        }

        return Short.parseShort(getString());
    }

    public Object getObject(String key) {
        if (jsonObject == null) {
            return -1;
        }

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

        return Double.parseDouble(getString(key));
    }

    public short getShort(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Short.parseShort(getString(key));
    }

    public List<Object> getList(String key) {
        if (jsonObject == null) {
            return null;
        }

        return jsonObject.getJSONObject("content").getJSONArray(key).toList();
    }

    public boolean isEmpty() {
        if (jsonObject == null) {
            return content == null;
        }

        return jsonObject.isEmpty();
    }

    @Deprecated
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public String toString() {
        if (jsonObject == null) {
            return content.toString();
        } else {
            return jsonObject.toString();
        }
    }
}
