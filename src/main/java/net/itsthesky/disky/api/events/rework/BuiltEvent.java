package net.itsthesky.disky.api.events.rework;

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

import ch.njol.skript.lang.SkriptEvent;
import net.dv8tion.jda.api.events.Event;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event that has been built & registered as a Skript event.
 * @param <T>
 */
public class BuiltEvent<T extends Event> {

    private final Class<T> jdaEventClass;
    private final Class<? extends DiSkyEvent<T>> diskyEventClass;
    private final Class<? extends org.bukkit.event.Event> bukkitEventClass;

    BuiltEvent(Class<T> jdaEventClass, Class<? extends DiSkyEvent<T>> diskyEventClass, Class<? extends org.bukkit.event.Event> bukkitEventClass) {
        this.jdaEventClass = jdaEventClass;
        this.diskyEventClass = diskyEventClass;
        this.bukkitEventClass = bukkitEventClass;
    }

    public Class<T> getJdaEventClass() {
        return jdaEventClass;
    }

    /**
     * Get the DiSky event class that is used to represent this JDA event in Skript.
     * @return The DiSky event class.
     */
    public Class<? extends DiSkyEvent<T>> getDiSkyEventClass() {
        return diskyEventClass;
    }

    /**
     * Get the Bukkit event class that is used to represent this JDA event in Skript.
     * This should technically always return a {@link SimpleDiSkyEvent<T>} class.
     * @return The Bukkit event class.
     */
    public Class<? extends org.bukkit.event.Event> getBukkitEventClass() {
        return bukkitEventClass;
    }

    /**
     * Create an instance of that event to ue when running Skript code, or for context parsing.
     * @param jdaEvent The JDA event to create the Bukkit event from.
     * @return The Bukkit event instance, with its JDA event set.
     */
    public org.bukkit.event.Event createBukkitInstance(Event jdaEvent) {
        try {
            final var event = bukkitEventClass.getConstructor().newInstance();
            final var method = SimpleDiSkyEvent.class.getDeclaredMethod("setJDAEvent", Event.class);

            method.invoke(event, jdaEvent);
            return event;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Bukkit event instance", e);
        }
    }

    /**
     * Get (~= cast) the given Bukkit event into this JDA Event.
     * If it's not the same class, or a reflection error occurs, null will be returned.
     * @param bukkitEvent The Bukkit event to get the JDA event from.
     * @return The JDA event instance, or null if the class doesn't match.
     */
    public @Nullable T getJDAEvent(@NotNull org.bukkit.event.Event bukkitEvent) {
        if (bukkitEvent.getClass() != bukkitEventClass)
            return null;
        try {
            final var method = SimpleDiSkyEvent.class.getDeclaredMethod("getJDAEvent");
            return (T) method.invoke(bukkitEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get JDA event instance", e);
        }
    }

    /**
     * Create an instance of the DiSky event class.
     * @return The DiSky event instance.
     */
    public DiSkyEvent<T> createDiSkyEvent() {
        try {
            return diskyEventClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DiSky event instance", e);
        }
    }
}
