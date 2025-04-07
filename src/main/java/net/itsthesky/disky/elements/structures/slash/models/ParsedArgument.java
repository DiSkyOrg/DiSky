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
import net.itsthesky.disky.elements.structures.slash.args.CustomArgument;
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

    // If applicable only
    private @Nullable CustomArgument<?> customArgument;

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
        return onCompletionRequest != null ||
                (customArgument != null && customArgument.isAutoCompletion());
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
        if (customArgument != null)
            return Classes.getExactClassInfo(customArgument.getClazz());

        return switch (type) {
            case STRING -> Classes.getExactClassInfo(String.class);
            case INTEGER -> Classes.getExactClassInfo(Integer.class);
            case BOOLEAN -> Classes.getExactClassInfo(Boolean.class);
            case USER -> Classes.getExactClassInfo(User.class);
            case CHANNEL -> Classes.getExactClassInfo(MessageChannel.class);
            case ROLE -> Classes.getExactClassInfo(Role.class);
            case NUMBER -> Classes.getExactClassInfo(Number.class);
            case ATTACHMENT -> Classes.getExactClassInfo(Message.Attachment.class);
            case MENTIONABLE -> Classes.getExactClassInfo(IMentionable.class);
            default -> Classes.getExactClassInfo(Object.class);
        };
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public @Nullable CustomArgument<?> getCustomArgument() {
        return customArgument;
    }

    public void setCustomArgument(@Nullable CustomArgument<?> customArgument) {
        this.customArgument = customArgument;
    }

    public boolean isCustomArgument() {
        return customArgument != null;
    }
}
