package org.jokergames.jfql.util;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public String getString() {
        if (content == null) {
            return null;
        }

        final String stringContent = content.toString();
        return (stringContent != null && stringContent.equals("null")) ? null : stringContent;
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

    public boolean getBoolean() {
        if (getString() == null) {
            return false;
        }

        return Boolean.parseBoolean(getString());
    }

    public short getShort() {
        if (getString() == null) {
            return -1;
        }

        return Short.parseShort(getString());
    }

    public JSONObject getJSONObject() {
        return new JSONObject(getString());
    }

    public JSONArray getJSONArray() {
        return new JSONArray(getString());
    }

    public ID getID() {
        return new ID(getString());
    }

    public String getString(String key) {
        if (jsonObject == null) {
            return null;
        }

        final String content = jsonObject.getJSONObject("content").getString(key);
        return (content != null && content.equals("null")) ? null : content;
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

    public boolean getBoolean(String key) {
        if (getString(key) == null) {
            return false;
        }

        return Boolean.parseBoolean(getString(key));
    }

    public JSONObject getJSONObject(String key) {
        return new JSONObject(getString(key));
    }

    public JSONArray getJSONArray(String key) {
        return new JSONArray(getString(key));
    }

    public ID getID(String key) {
        return new ID(getString(key));
    }

    public <T> T formatJSON(String key, Class<T> t) {
        return new Gson().fromJson(getString(key), t);
    }

    public boolean isEmpty() {
        if (jsonObject == null) {
            return content == null;
        }

        return jsonObject.isEmpty();
    }

    public boolean isNull(String key) {
        return getString(key) == null || getString(key).equals("null");
    }

    @Deprecated
    public JSONObject toJSONObject() {
        return jsonObject;
    }

    @Override
    public String toString() {
        if (jsonObject == null) {
            return getString();
        } else {
            return jsonObject.getJSONObject("content").toString();
        }
    }

}
