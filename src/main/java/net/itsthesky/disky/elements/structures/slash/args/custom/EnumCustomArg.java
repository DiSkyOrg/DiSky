package net.itsthesky.disky.elements.structures.slash.args.custom;

import ch.njol.skript.classes.ClassInfo;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.itsthesky.disky.elements.structures.slash.args.CustomArgument;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

import static net.itsthesky.disky.elements.structures.slash.args.custom.SmallEnumCustomArg.beautifyEnumName;

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
public class EnumCustomArg extends CustomArgument<Enum> {

    public EnumCustomArg(Class<Enum> clazz) {
        super(clazz, OptionType.STRING, true, false);
    }

    @Override
    public Enum convert(SlashCommandInteractionEvent event, OptionMapping mapping) {
        final var enumName = mapping.getAsString();
        return Enum.valueOf(clazz, enumName);
    }

    @Override
    public Enum convert(CommandAutoCompleteInteractionEvent event, OptionMapping mapping) {
        final var enumName = mapping.getAsString();
        return Enum.valueOf(clazz, enumName);
    }

    @Override
    public List<Command.Choice> handleAutoCompletion(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull String input) {
        final var enumConstants = clazz.getEnumConstants();
        return Stream.of(enumConstants)
                .filter(enumConstant -> beautifyEnumName(enumConstant.name()).toLowerCase().startsWith(input.toLowerCase()))
                .map(enumConstant -> new Command.Choice(
                        beautifyEnumName(enumConstant.name()),
                        enumConstant.name()
                ))
                .limit(OptionData.MAX_CHOICES)
                .toList();
    }

    @Override
    public boolean supportsClass(@NotNull ClassInfo<?> classInfo) {
        return classInfo.getC().isEnum() && classInfo.getC().isAssignableFrom(clazz);
    }
}
