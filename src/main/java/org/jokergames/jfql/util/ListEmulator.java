package org.jokergames.jfql.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Janick
 */

public class ListEmulator {

    private List<Object> components;

    public ListEmulator(String list) {
        list = list
                .replace("[", "")
                .replace("]", "");
        components = Arrays.asList(list.split(", "));
    }

    public ListEmulator() {
        this(new ArrayList<>());
    }

    public ListEmulator(Object... objects) {
        this(Arrays.asList(objects));
    }

    public ListEmulator(List<Object> components) {
        this.components = components;
    }

    public List<Object> getComponents() {
        return components;
    }

    public void setComponents(List<Object> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return components.toString();
    }
}
