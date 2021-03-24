package org.jokergames.jfql.util;

import org.jokergames.jfql.encryption.Encryption;
import org.json.JSONObject;

/**
 * @author Janick
 */

public class Column {

    private final JSONObject jsonObject;
    private final Object content;
    private final Encryption encryption;

    public Column(Encryption encryption, JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.content = null;
        this.encryption = encryption;
    }

    public Column(Encryption encryption, Object content) {
        this.jsonObject = null;
        this.content = content;
        this.encryption = encryption;
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

        return encryption.getProtocol().decrypt(content.toString(), encryption.getKey());
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

    public String getString(String key) {
        key = encryption.getProtocol().decrypt(key, encryption.getKey());

        if (jsonObject == null) {
            return null;
        }

        if (jsonObject.isNull(key)) {
            return null;
        }

        return encryption.getProtocol().decrypt(jsonObject.get(key).toString(), encryption.getKey());
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
            return getString();
        } else {
            JSONObject jsonObject = new JSONObject();

            for (String key : this.jsonObject.keySet()) {
                jsonObject.put(encryption.getProtocol().decrypt(key, encryption.getKey()), getString(key));
            }

            return jsonObject.toString();
        }
    }

    public Encryption getEncryption() {
        return encryption;
    }
}
