package de.byjoker.jfql.util;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

public class RelationalTableEntry implements TableEntry {

    private final JSONObject content;
    private final long createdAt;

    public RelationalTableEntry(JSONObject content, long createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    @Deprecated
    @Override
    public Object getObject(String key) {
        return content.get(key);
    }

    @Override
    public String getRawString(String key) {
        return content.getString(key);
    }

    @Override
    public String getString(String key) {
        final String content = this.content.getString(key);
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
        return content.has(key);
    }

    @Override
    public boolean isNull(String key) {
        return content.getString(key) == null || content.getString(key).equals("null");
    }

    @Override
    public long getCreation() {
        return createdAt;
    }
}
