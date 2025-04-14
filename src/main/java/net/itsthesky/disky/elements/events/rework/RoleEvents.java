package net.itsthesky.disky.elements.events.rework;

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

import ch.njol.skript.util.Color;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.*;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.core.SkriptUtils;

@EventCategory(name = "Role Events", description = {
        "These events are triggered when a role is created, deleted, or updated in a Discord server.",
        "They provide access to the role and guild involved in the event.",
        "These events are useful for tracking changes to roles, managing permissions, and implementing role-based features."
})
public class RoleEvents {

    static {
        // Role Create Event
        // Fired when a new role is created in a guild
        EventRegistryFactory.builder(RoleCreateEvent.class)
                .name("Role Create Event")
                .patterns("[discord] [guild] role create[d]")
                .description("Fired when a new role is created in a guild.",
                        "This event provides access to the newly created role and the guild it belongs to.",
                        "It's useful for tracking administrative changes or implementing role management systems.")
                .example("on role create:\n    broadcast \"New role created: %event-role% in %event-guild%\"")
                .value(Guild.class, RoleCreateEvent::getGuild)
                .value(Role.class, RoleCreateEvent::getRole)
                .author(RoleCreateEvent::getGuild)
                .register();

        // Role Delete Event
        // Fired when a role is deleted from a guild
        EventRegistryFactory.builder(RoleDeleteEvent.class)
                .name("Role Delete Event")
                .patterns("[discord] [guild] role delete")
                .description("Fired when a role is deleted from a guild.",
                        "This event provides access to the deleted role and the guild it belonged to.",
                        "It can be used for auditing purposes or to trigger cleanup actions in your bot.")
                .example("on role delete:\n    broadcast \"Role %event-role% was deleted from %event-guild%\"")
                .value(Guild.class, RoleDeleteEvent::getGuild)
                .value(Role.class, RoleDeleteEvent::getRole)
                .author(RoleDeleteEvent::getGuild)
                .register();

        // Role Color Update Event
        // Fired when a role's color is changed
        EventRegistryFactory.builder(RoleUpdateColorEvent.class)
                .name("Role Color Update Event")
                .patterns("[discord] [guild] role color (update|change)")
                .description("Fired when the color of a role changes.",
                        "This event provides access to both the old and new colors of the role.",
                        "It can be used for tracking aesthetic changes to roles or for synchronization systems.")
                .example("on role color change:\n    broadcast \"Role %event-role% color changed from %previous role color% to %current role color%\"")
                .customTimedExpressions("role color", Color.class,
                        evt -> SkriptUtils.convert(evt.getNewColor()),
                        evt -> SkriptUtils.convert(evt.getOldColor()))
                .value(Guild.class, RoleUpdateColorEvent::getGuild)
                .value(Role.class, RoleUpdateColorEvent::getRole)
                .author(RoleUpdateColorEvent::getGuild)
                .register();

        // Role Name Update Event
        // Fired when a role's name is changed
        EventRegistryFactory.builder(RoleUpdateNameEvent.class)
                .name("Role Name Update Event")
                .patterns("[discord] [guild] role name (update|change)")
                .description("Fired when the name of a role changes.",
                        "This event provides access to both the old and new names of the role.",
                        "It's useful for tracking role identity changes or updating external systems that reference roles by name.")
                .example("on role name change:\n    broadcast \"Role name changed from '%previous role name%' to '%current role name%' in %event-guild%\"")
                .customTimedExpressions("role name", String.class,
                        RoleUpdateNameEvent::getNewValue,
                        RoleUpdateNameEvent::getOldValue)
                .value(Guild.class, RoleUpdateNameEvent::getGuild)
                .value(Role.class, RoleUpdateNameEvent::getRole)
                .author(RoleUpdateNameEvent::getGuild)
                .register();

        // Role Hoisted Update Event
        // Fired when a role's hoisted status changes
        EventRegistryFactory.builder(RoleUpdateHoistedEvent.class)
                .name("Role Hoisted Update Event")
                .patterns("[discord] [guild] role hoist[ed] (update|change)")
                .description("Fired when the hoisted status of a role changes.",
                        "Hoisted roles are displayed separately in the member list.",
                        "This event provides access to both the old and new hoisted states.",
                        "It's useful for tracking changes to role visibility in the member sidebar.")
                .example("on role hoisted change:\n    if current role hoisted state is true:\n        broadcast \"Role %event-role% is now shown separately in the member list\"\n    else:\n        broadcast \"Role %event-role% is no longer shown separately in the member list\"")
                .customTimedExpressions("role hoisted [state]", Boolean.class,
                        RoleUpdateHoistedEvent::getNewValue,
                        RoleUpdateHoistedEvent::getOldValue)
                .value(Guild.class, RoleUpdateHoistedEvent::getGuild)
                .value(Role.class, RoleUpdateHoistedEvent::getRole)
                .author(RoleUpdateHoistedEvent::getGuild)
                .register();

        // Role Icon Update Event
        // Fired when a role's icon is changed
        EventRegistryFactory.builder(RoleUpdateIconEvent.class)
                .name("Role Icon Update Event")
                .patterns("[discord] [guild] role icon (update|change)")
                .description("Fired when the icon of a role changes.",
                        "This event provides access to both the old and new icon URLs.",
                        "It can be used for tracking visual changes to roles or updating external systems.")
                .example("on role icon change:\n    broadcast \"Role %event-role% icon changed from %previous role icon% to %current role icon%\"")
                .customTimedExpressions("role icon", String.class,
                        evt -> evt.getNewValue() == null ? null : evt.getNewValue().getIconUrl(),
                        evt -> evt.getOldValue() == null ? null : evt.getOldValue().getIconUrl())
                .value(Guild.class, RoleUpdateIconEvent::getGuild)
                .value(Role.class, RoleUpdateIconEvent::getRole)
                .author(RoleUpdateIconEvent::getGuild)
                .register();

        // Role Position Update Event
        // Fired when a role's position in the hierarchy changes
        EventRegistryFactory.builder(RoleUpdatePositionEvent.class)
                .name("Role Position Update Event")
                .patterns("[discord] [guild] role position (update|change)")
                .description("Fired when the position of a role changes in the role hierarchy.",
                        "This event provides access to both the old and new positions.",
                        "It's useful for tracking changes to the role hierarchy that may affect permissions.")
                .example("on role position change:\n    broadcast \"Role %event-role% position changed from %previous role position% to %current role position%\"")
                .customTimedExpressions("role position", Integer.class,
                        RoleUpdatePositionEvent::getNewValue,
                        RoleUpdatePositionEvent::getOldValue)
                .value(Guild.class, RoleUpdatePositionEvent::getGuild)
                .value(Role.class, RoleUpdatePositionEvent::getRole)
                .author(RoleUpdatePositionEvent::getGuild)
                .register();

        // Role Permissions Update Event
        // Fired when a role's permissions are changed
        EventRegistryFactory.builder(RoleUpdatePermissionsEvent.class)
                .name("Role Permissions Update Event")
                .patterns("[discord] [guild] role permission[s] (update|change)")
                .description("Fired when the permissions of a role change.",
                        "This event provides access to both the old and new permission sets.",
                        "It's crucial for security monitoring, permission auditing, and tracking administrative changes.")
                .example("on role permissions change:\n    broadcast \"Permissions for role %event-role% have been updated in %event-guild%\"")
                .customTimedListExpressions("role permission[s]", Permission.class,
                        evt -> evt.getNewPermissions().toArray(Permission[]::new),
                        evt -> evt.getOldPermissions().toArray(Permission[]::new))
                .value(Guild.class, RoleUpdatePermissionsEvent::getGuild)
                .value(Role.class, RoleUpdatePermissionsEvent::getRole)
                .author(RoleUpdatePermissionsEvent::getGuild)
                .register();

        // Role Mentionable Update Event
        // Fired when a role's mentionable status changes
        EventRegistryFactory.builder(RoleUpdateMentionableEvent.class)
                .name("Role Mentionable Update Event")
                .patterns("[discord] [guild] role mentionable (update|change)")
                .description("Fired when the mentionable status of a role changes.",
                        "This event tracks whether a role can be mentioned by regular users.",
                        "It provides access to both the old and new mentionable states.",
                        "It's useful for tracking changes that affect role notifications and visibility.")
                .example("on role mentionable change:\n    if current role mentionable state is true:\n        broadcast \"Role %event-role% can now be mentioned by everyone\"\n    else:\n        broadcast \"Role %event-role% can no longer be mentioned by everyone\"")
                .customTimedExpressions("role mentionable [state]", Boolean.class,
                        RoleUpdateMentionableEvent::getNewValue,
                        RoleUpdateMentionableEvent::getOldValue)
                .value(Guild.class, RoleUpdateMentionableEvent::getGuild)
                .value(Role.class, RoleUpdateMentionableEvent::getRole)
                .author(RoleUpdateMentionableEvent::getGuild)
                .register();
    }
}