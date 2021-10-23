package org.jokergames.jfql.repository;

public class TableData {

    private final String name;
    private final String primary;
    private final String structure;

    public TableData(String name, String primary, String structure) {
        this.name = name;
        this.primary = primary;
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public String getPrimary() {
        return primary;
    }

    public String getStructure() {
        return structure;
    }
}
