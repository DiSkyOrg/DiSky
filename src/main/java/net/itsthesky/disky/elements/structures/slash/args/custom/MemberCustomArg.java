package net.itsthesky.disky.elements.structures.slash.args.custom;

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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.itsthesky.disky.elements.structures.slash.args.CustomArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MemberCustomArg extends CustomArgument<Member> {

    public MemberCustomArg() {
        super(Member.class, OptionType.USER, false, false);
    }

    @Override
    public @Nullable Member convert(@NotNull SlashCommandInteractionEvent event, @NotNull OptionMapping mapping) {
        final var user = mapping.getAsUser();
        if (!event.getChannel().getType().isGuild() || event.getGuild() == null)
            return null;

        return event.getGuild().retrieveMember(user).complete();
    }

    @Override
    public @Nullable Member convert(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull OptionMapping mapping) {
        final var user = mapping.getAsUser();
        if (!event.getChannel().getType().isGuild() || event.getGuild() == null)
            return null;

        return event.getGuild().retrieveMember(user).complete();
    }
}
