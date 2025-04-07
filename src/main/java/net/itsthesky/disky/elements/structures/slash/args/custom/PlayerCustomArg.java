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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerCustomArg extends CustomArgument<OfflinePlayer> {

    private final boolean offline;

    public PlayerCustomArg(boolean offline) {
        super(OfflinePlayer.class, OptionType.STRING, true, false);
        this.offline = offline;
    }

    @Override
    public boolean supportsClass(@NotNull ClassInfo<?> classInfo) {
        if (offline)
            return OfflinePlayer.class.isAssignableFrom(classInfo.getC());

        return Player.class.isAssignableFrom(classInfo.getC());
    }

    @Override
    public List<Command.Choice> handleAutoCompletion(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull String input) {
        final List<OfflinePlayer> players;
        if (offline) {
            players = List.of(Bukkit.getOfflinePlayers());
        } else {
            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray(new OfflinePlayer[0]));
        }

        return players.stream()
                .filter(player -> player.getName() != null)
                .filter(player -> player.getName().toLowerCase().startsWith(input.toLowerCase()))
                .map(player -> new Command.Choice(player.getName(), player.getUniqueId().toString()))
                .toList();
    }

    @Override
    public @Nullable OfflinePlayer convert(@NotNull SlashCommandInteractionEvent event, @NotNull OptionMapping mapping) {
        final var playerName = mapping.getAsString();
        final var uuid = UUID.fromString(playerName);

        return offline ? Bukkit.getOfflinePlayer(uuid) : Bukkit.getPlayer(uuid);
    }

    @Override
    public @Nullable OfflinePlayer convert(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull OptionMapping mapping) {
        final var playerName = mapping.getAsString();
        final var uuid = UUID.fromString(playerName);

        return offline ? Bukkit.getOfflinePlayer(uuid) : Bukkit.getPlayer(uuid);
    }
}