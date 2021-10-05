package org.jokergames.jfql.util;

import java.util.Objects;
import java.util.Random;

public class ID {

    private static final Random RANDOM = new Random();

    private String value;

    public ID(Type type, int length) {
        switch (type) {
            case NUMBER: {
                value = generateOf("0123456789", length);
                break;
            }
            case MIXED: {
                value = generateOf("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.,-+", length);
                break;
            }
            case STRING: {
                value = generateOf("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", length);
                break;
            }
        }
    }

    public ID(Type type) {
        switch (type) {
            case NUMBER: {
                value = generateOf("0123456789", 8);
                break;
            }
            case MIXED: {
                value = generateOf("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.,-+", 20);
                break;
            }
            case STRING: {
                value = generateOf("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 12);
                break;
            }
        }
    }

    public ID(String value) {
        this.value = value;
    }

    public static ID of(Object o) {
        return new ID(o.toString());
    }

    public static ID generateString() {
        return new ID(Type.STRING);
    }

    public static ID generateNumber() {
        return new ID(Type.NUMBER);
    }

    public static ID generateMixed() {
        return new ID(Type.MIXED);
    }

    public ID regenerateNumber() {
        return new ID(Type.NUMBER, 8);
    }

    public ID regenerateMixed() {
        return new ID(Type.MIXED, 20);
    }

    public ID regenerateString() {
        return new ID(Type.STRING, 12);
    }

    public ID regenerate(Type type, int length) {
        return new ID(type, length);
    }

    public ID regenerateNumber(int length) {
        return new ID(Type.NUMBER, length);
    }

    public ID regenerateMixed(int length) {
        return new ID(Type.MIXED, length);
    }

    public ID regenerateString(int length) {
        return new ID(Type.STRING, length);
    }

    private String generateOf(String chars, int length) {
        final char[] array = chars.toCharArray();
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(array[RANDOM.nextInt(array.length)]);
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ID id = (ID) o;
        return Objects.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public enum Type {
        NUMBER,
        STRING,
        MIXED
    }

}
