package de.byjoker.jfql.util;

import com.google.gson.Gson;
import de.byjoker.jfql.exception.ConnectorException;
import org.json.JSONArray;
import org.json.JSONObject;

public class DocumentCollectionEntry implements TableEntry {

    private final JSONObject content;
    private final long createdAt;

    public DocumentCollectionEntry(JSONObject content, long createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    <T> T parse(Class<?> clazz) {
        return (T) new Gson().fromJson(content.toString(), clazz);
    }

    public JSONObject parseJsonObject() {
        return content;
    }

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
        return content.getString("key").equals("null") ? null : content.getString("key");
    }

    @Override
    public int getInteger(String key) {
        return content.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return content.getLong(key);
    }

    @Override
    public float getFloat(String key) {
        return content.getFloat(key);
    }

    @Override
    public double getDouble(String key) {
        return content.getDouble(key);
    }

    @Override
    public short getShort(String key) {
        return (Short) content.get(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return content.getBoolean(key);
    }

    @Override
    public JSONObject getJsonObject(String key) {
        return content.getJSONObject(key);
    }

    @Override
    public JSONArray getJsonArray(String key) {
        return content.getJSONArray(key);
    }

    @Override
    public <T> T parse(String key, Class<T> clazz) {
        throw new ConnectorException("Can't parse fields of document entry!");
    }

    @Override
    public boolean isPresent(String key) {
        return content.has(key);
    }

    @Override
    public boolean isNull(String key) {
        return content.isNull(key) || content.getString(key).equals("null");
    }

    @Override
    public long getCreation() {
        return createdAt;
    }

}
