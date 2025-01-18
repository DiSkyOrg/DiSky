package net.itsthesky.disky.elements.structures.slash.models;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.registrations.Classes;
import com.google.common.base.Objects;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ParsedArgument {

    // Parsed when defining the command itself
    private final OptionType type;
    private final String name;
    private final boolean required;

    // Parsed further in the section
    private String description;
    private Map<String, Object> choices;
    private @Nullable Trigger onCompletionRequest;

    // Set when the argument is executed
    private Object value;

    public ParsedArgument(OptionType type, String name, boolean required) {
        this.type = type;
        this.name = name;
        this.required = required;

        this.choices = new HashMap<>();
        this.description = "No description provided.";
        this.onCompletionRequest = null;
    }

    public OptionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addChoice(String name, Object arg) {
        choices.put(name, arg);
    }

    public Map<String, Object> getChoices() {
        return choices;
    }

    public void setOnCompletionRequest(@Nullable Trigger onCompletionRequest) {
        this.onCompletionRequest = onCompletionRequest;
    }

    public @Nullable Trigger getOnCompletionRequest() {
        return onCompletionRequest;
    }

    public boolean hasChoices() {
        return !choices.isEmpty();
    }

    public boolean isOptional() {
        return !required;
    }

    public boolean isAutoCompletion() {
        return onCompletionRequest != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedArgument that = (ParsedArgument) o;
        return required == that.required && type == that.type && Objects.equal(name, that.name) && Objects.equal(description, that.description) && Objects.equal(choices, that.choices);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, name, required, description, choices);
    }

    public ClassInfo<?> getTypeInfo() {
        switch (type) {
            case STRING:
                return Classes.getExactClassInfo(String.class);
            case INTEGER:
                return Classes.getExactClassInfo(Integer.class);
            case BOOLEAN:
                return Classes.getExactClassInfo(Boolean.class);
            case USER:
                return Classes.getExactClassInfo(User.class);
            case CHANNEL:
                return Classes.getExactClassInfo(MessageChannel.class);
            case ROLE:
                return Classes.getExactClassInfo(Role.class);
            case NUMBER:
                return Classes.getExactClassInfo(Number.class);
            case ATTACHMENT:
                return Classes.getExactClassInfo(Message.Attachment.class);
            case MENTIONABLE:
                return Classes.getExactClassInfo(IMentionable.class);
            default:
                return Classes.getExactClassInfo(Object.class);
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
