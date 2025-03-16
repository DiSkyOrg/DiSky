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
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.itsthesky.disky.elements.structures.slash.args.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PlayerCustomArg extends CustomArgument<Player> {

    public PlayerCustomArg() {
        super(Player.class, OptionType.STRING, true, false);
    }

    @Override
    public List<Command.Choice> handleAutoCompletion(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull String input) {
        final var onlinePLayers = Bukkit.getServer().getOnlinePlayers();
        return onlinePLayers.stream()
                .filter(player -> player.getName().toLowerCase().startsWith(input.toLowerCase()))
                .map(player -> new Command.Choice(player.getName(), player.getUniqueId().toString()))
                .toList();
    }

    @Override
    public @Nullable Player convert(@NotNull SlashCommandInteractionEvent event, @NotNull OptionMapping mapping) {
        final var playerName = mapping.getAsString();
        return Bukkit.getPlayer(UUID.fromString(playerName));
    }

    @Override
    public @Nullable Player convert(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull OptionMapping mapping) {
        final var playerName = mapping.getAsString();
        return Bukkit.getPlayer(UUID.fromString(playerName));
    }
}