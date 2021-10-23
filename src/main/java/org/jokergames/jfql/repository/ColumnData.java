package org.jokergames.jfql.repository;

public class ColumnData {

    private final String name;
    private final String content;
    private final boolean primary;
    private final boolean json;

    public ColumnData(String name, String content, boolean primary, boolean json) {
        this.name = name;
        this.content = content;
        this.primary = primary;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isJson() {
        return json;
    }
}
