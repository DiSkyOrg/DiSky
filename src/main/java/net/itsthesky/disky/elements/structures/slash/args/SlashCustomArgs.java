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

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.structures.slash.args.custom.EnumCustomArg;
import net.itsthesky.disky.elements.structures.slash.args.custom.SmallEnumCustomArg;
import net.itsthesky.disky.elements.structures.slash.args.custom.MemberCustomArg;
import net.itsthesky.disky.elements.structures.slash.args.custom.PlayerCustomArg;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SlashCustomArgs {

    private static final Set<CustomArgument<?>> AVAILABLE_ARGUMENTS = new HashSet<>();

    static {
        AVAILABLE_ARGUMENTS.add(new MemberCustomArg());
        AVAILABLE_ARGUMENTS.add(new PlayerCustomArg());

        final var normalArgs = AVAILABLE_ARGUMENTS.size();
        Thread thread = new Thread(() -> {
            waitForClasses();

            Bukkit.getScheduler().runTask(DiSky.getInstance(), () -> {
                int count = registerEnums();

                DiSky.getInstance().getLogger().info("Registered " + AVAILABLE_ARGUMENTS.size() + " custom arguments (including " + count + " enums and " + normalArgs + " custom ones)");
            });
        });
        thread.setName("DiSky - Slash Custom Args Registration");
        thread.setDaemon(true);
        thread.start();
    }

    private static void waitForClasses() {
        while (Skript.isAcceptRegistrations()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        registerEnums();
    }

    private static int registerEnums() {
        int count = 0;
        for (ClassInfo<?> clazz : Classes.getClassInfos()) {
            if (clazz.getC() == null || !Enum.class.isAssignableFrom(clazz.getC()))
                continue;

            final var enumClass = (Class<Enum>) clazz.getC();
            if (enumClass.getEnumConstants() == null)
                continue;
            if (enumClass.getEnumConstants().length == 0)
                continue;
            if (enumClass.getEnumConstants().length <= OptionData.MAX_CHOICES)
                register(new SmallEnumCustomArg(enumClass));
            else
                register(new EnumCustomArg(enumClass));

            count++;
        }

        return count;
    }

    public static void register(CustomArgument<?> argument) {
        AVAILABLE_ARGUMENTS.add(argument);
    }

    public static Set<CustomArgument<?>> getAvailableArguments() {
        return Set.copyOf(AVAILABLE_ARGUMENTS);
    }

    public static @Nullable CustomArgument<?> tryParseCustomArgument(String input) {
        final var classInfo = Classes.getClassInfo(input);
        if (classInfo == null)
            return null;

        for (CustomArgument<?> argument : AVAILABLE_ARGUMENTS) {
            DiSky.debug(" --- Checking " + argument.toString() + " for " + classInfo.getName());
            if (argument.supportsClass(classInfo)) {
                return argument;
            }
        }

        return null;
    }

}
