package info.itsthesky.disky.elements.structures.slash.models;

import ch.njol.skript.config.Node;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SlashCommandInformation {

    private final ParsedCommand command;
    private final @Nullable Node node;

    public SlashCommandInformation(ParsedCommand command, @Nullable Node node) {
        this.command = command;
        this.node = node;
    }

    public ParsedCommand getCommand() {
        return command;
    }

    public @Nullable Node getNode() {
        return node;
    }

    @Override
    public int hashCode() {
        return Objects.hash(command.getName(), node == null ? null : node.getKey());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof SlashCommandInformation)) return false;
        SlashCommandInformation info = (SlashCommandInformation) obj;
        return info.getCommand().getName().equals(command.getName()) &&
                (info.getNode() == null || node == null || info.getNode().getKey().equals(node.getKey()));
    }

    @Override
    public String toString() {
        return "SlashCommandInformation{" +
                "command=" + command +
                ", node=" + node +
                '}';
    }
}
