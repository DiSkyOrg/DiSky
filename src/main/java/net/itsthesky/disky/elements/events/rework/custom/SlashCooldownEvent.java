package net.itsthesky.disky.elements.events.rework.custom;

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

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCooldownEvent extends SlashCommandInteractionEvent {

    private final long remainingTime;
    private boolean cancelled = false;
    public SlashCooldownEvent(SlashCommandInteractionEvent event, long remainingTime) {
        super(event.getJDA(), event.getResponseNumber(), event.getInteraction());
        this.remainingTime = remainingTime;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
