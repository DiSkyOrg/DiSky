package net.itsthesky.disky.elements.structures.slash.args.custom;

import ch.njol.skript.classes.ClassInfo;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.itsthesky.disky.elements.structures.slash.args.CustomArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

/**
 * Custom argument for small (< {@link net.dv8tion.jda.api.interactions.commands.build.OptionData#MAX_CHOICES} values) enums.
 * This class is used to create a custom argument for enums that are small enough to be used as choices in a slash command.
 * For bigger enums, use {@link EnumCustomArg} instead that will use auto-completion instead of choices.
 */
public class SmallEnumCustomArg extends CustomArgument<Enum> {

    public SmallEnumCustomArg(Class<Enum> clazz) {
        super(clazz, OptionType.STRING, false, true);
    }

    @Override
    public List<Command.Choice> getChoices() {
        final var enumConstants = clazz.getEnumConstants();
        return List.of(enumConstants).stream()
                .map(enumConstant -> new Command.Choice(
                        beautifyEnumName(enumConstant.name()),
                        enumConstant.name()
                )).toList();
    }

    @Override
    public @Nullable Enum convert(@NotNull SlashCommandInteractionEvent event, @NotNull OptionMapping mapping) {
        final var enumName = mapping.getAsString();
        return Enum.valueOf(clazz, enumName);
    }

    @Override
    public @Nullable Enum convert(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull OptionMapping mapping) {
        final var enumName = mapping.getAsString();
        return Enum.valueOf(clazz, enumName);
    }

    public static String beautifyEnumName(String name) {
        final var lower = name.replaceAll("_", " ").toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    @Override
    public boolean supportsClass(@NotNull ClassInfo<?> classInfo) {
        return classInfo.getC().isEnum() && classInfo.getC().isAssignableFrom(clazz);
    }
}
