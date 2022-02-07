package de.byjoker.jfql.statement;

public class LegacyConditionSetBuilder {

    private final String conditions;

    public LegacyConditionSetBuilder(String conditions) {
        this.conditions = conditions;
    }

    public ConditionSet build() {
        return () -> conditions;
    }

}
