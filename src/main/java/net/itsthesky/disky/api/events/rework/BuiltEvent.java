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

import net.dv8tion.jda.api.events.Event;
import net.itsthesky.disky.api.events.DiSkyEvent;

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

    public Class<? extends DiSkyEvent<T>> getDiSkyEventClass() {
        return diskyEventClass;
    }

    public Class<?> getBukkitEventClass() {
        return bukkitEventClass;
    }

    public org.bukkit.event.Event createBukkitInstance(Object... args) {
        try {
            return (org.bukkit.event.Event) bukkitEventClass.getConstructor(args.getClass()).newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Bukkit event instance", e);
        }
    }
}
