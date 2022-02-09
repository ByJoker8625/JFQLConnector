package de.byjoker.jfql.util;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

public class DeprecatedTableEntry implements TableEntry {

    private final JSONObject jsonContent;
    private final Object content;
    private final long creation;

    public DeprecatedTableEntry(JSONObject jsonContent, long creation) {
        this.jsonContent = jsonContent;
        this.creation = creation;
        this.content = null;
    }

    public DeprecatedTableEntry(Object content, long creation) {
        this.creation = creation;
        this.jsonContent = null;
        this.content = content;
    }

    @Override
    public Object getObject(String key) {
        if (jsonContent == null) {
            return null;
        }

        return jsonContent.get(key);
    }

    @Override
    public String getRawString(String key) {
        if (jsonContent == null) {
            return null;
        }

        return jsonContent.getString(key);
    }

    @Override
    public String getString(String key) {
        if (jsonContent == null) {
            return null;
        }

        final String content = jsonContent.getString(key);
        return (content != null && content.equals("null")) ? null : content;
    }

    @Override
    public int getInteger(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Integer.parseInt(getString(key));
    }

    @Override
    public long getLong(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Long.parseLong(getString(key));
    }

    @Override
    public float getFloat(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Float.parseFloat(getString(key));
    }

    @Override
    public double getDouble(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Double.parseDouble(getString(key));
    }

    @Override
    public short getShort(String key) {
        if (getString(key) == null) {
            return -1;
        }

        return Short.parseShort(getString(key));
    }

    @Override
    public boolean getBoolean(String key) {
        if (getString(key) == null) {
            return false;
        }

        return Boolean.parseBoolean(getString(key));
    }

    @Override
    public JSONObject getJsonObject(String key) {
        return new JSONObject(getString(key));
    }

    @Override
    public JSONArray getJsonArray(String key) {
        return new JSONArray(getString(key));
    }

    @Override
    public <T> T parse(String key, Class<T> clazz) {
        return new Gson().fromJson(getString(key), clazz);
    }

    @Override
    public boolean isPresent(String key) {
        if (jsonContent == null) {
            return false;
        }

        return jsonContent.has(key);
    }

    @Override
    public boolean isNull(String key) {
        return getString(key) == null || getString(key).equals("null");
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

    public short getShort() {
        if (getString() == null) {
            return -1;
        }

        return Short.parseShort(getString());
    }

    public boolean getBoolean() {
        if (getString() == null) {
            return false;
        }

        return Boolean.parseBoolean(getString());
    }

    public JSONObject getJsonObject() {
        return new JSONObject(getString());
    }

    public JSONArray getJsonArray() {
        return new JSONArray(getString());
    }

    public <T> T parse(Class<T> clazz) {
        return new Gson().fromJson(getString(), clazz);
    }

    public boolean isNull() {
        return getString() == null || getString().equals("null");
    }

    public long getCreation() {
        if (jsonContent == null) {
            return -1;
        }

        return jsonContent.getLong("creation");
    }

    public String toString() {
        if (content != null)
            return content.toString();
        else if (jsonContent != null)
            return jsonContent.toString();
        return null;
    }

}
