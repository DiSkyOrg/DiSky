package net.itsthesky.disky.elements.structures.slash.args;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import ch.njol.skript.classes.ClassInfo;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a custom argument for slash commands (slash structures)
 *
 * @author ItsTheSky
 */
public abstract class CustomArgument<T> {

    protected final Class<T> clazz;
    protected final OptionType type;
    protected final boolean autoCompletion;
    protected final boolean choices;

    protected CustomArgument(Class<T> clazz, OptionType type, boolean autoCompletion, boolean choices) {
        this.clazz = clazz;
        this.type = type;
        this.autoCompletion = autoCompletion;
        this.choices = choices;

        if (!type.canSupportChoices() && choices)
            throw new IllegalArgumentException("The type " + type + " does not support choices.");
    }

    /**
     * Check whether the argument supports the given class info.
     * @param classInfo The class info to check
     * @return True if the argument supports the class info, false otherwise
     */
    public boolean supportsClass(@NotNull ClassInfo<?> classInfo) {
        if (classInfo.getC() == null) return false;
        if (!clazz.isAssignableFrom(classInfo.getC())) return false;

        return true;
    }

    /**
     * Convert the given value (that will always be the type of {@link #getType()}) to the
     * expected type of this argument. The returned object's class should be the same as the
     * class info supported in {@link #supportsClass(ClassInfo)}.
     * @param mapping The option mapping of the argument
     * @return The converted value, or null if the conversion failed
     */
    public abstract @Nullable T convert(@NotNull SlashCommandInteractionEvent event, @NotNull OptionMapping mapping);

    /**
     * Convert the given value (that will always be the type of {@link #getType()}) to the
     * expected type of this argument. The returned object's class should be the same as the
     * class info supported in {@link #supportsClass(ClassInfo)}.
     * <br />
     * That method will be called if auto complete is enabled for another argument.
     * The running Skript code must pre-parse arguments, hence call this method.
     * @param event The event that triggered the auto-completion
     * @param mapping The option mapping of the argument
     * @return The converted value, or null if the conversion failed
     */
    public abstract @Nullable T convert(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull OptionMapping mapping);

    /**
     * Handle the auto-completion for this argument. This method will be called when the user
     * types in the argument and the auto-completion is enabled.
     * This will only be called if {@link #isAutoCompletion()} is true.
     * @param event The event that triggered the auto-completion
     * @param input The input that the user typed
     * @return A list of choices for the auto-completion, or null if no choices are available
     */
    public List<Command.Choice> handleAutoCompletion(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull String input) {
        return null;
    }

    /**
     * Get the list of possible choices for this argument.
     * This will only be called if {@link #isChoices()} is true.
     * @return The list of choices for this argument, or null if no choices are available
     */
    public List<Command.Choice> getChoices() {
        return null;
    }

    public OptionType getType() {
        return type;
    }

    public boolean isAutoCompletion() {
        return autoCompletion;
    }

    public boolean isChoices() {
        return choices;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "CustomArgument{" +
                "clazz=" + clazz +
                ", type=" + type +
                ", autoCompletion=" + autoCompletion +
                ", choices=" + choices +
                '}';
    }
}
