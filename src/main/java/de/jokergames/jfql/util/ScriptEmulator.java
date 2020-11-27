package de.jokergames.jfql.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Janick
 */

public class ScriptEmulator {

    private final List<String> commands;

    public ScriptEmulator() {
        commands = new ArrayList<>();
    }

    public ScriptEmulator(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addQuery(String query, Object... replacers) {
        for (Object replace : replacers) {
            query = query.replaceFirst("%", replace.toString());
        }

        commands.add(query);
    }

    public void removeQuery(String query) {
        commands.remove(query);
    }

    @Override
    public String toString() {
        return "'" + commands.stream().map(s -> s + "; ").collect(Collectors.joining()) + "'";
    }
}
