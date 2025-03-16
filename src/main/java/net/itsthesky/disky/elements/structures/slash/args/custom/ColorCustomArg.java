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

import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.SkriptColor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.itsthesky.disky.elements.structures.slash.args.CustomArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ColorCustomArg extends CustomArgument<Color> {

    public ColorCustomArg() {
        super(Color.class, OptionType.STRING, true, false);
    }

    @Override
    public @Nullable Color convert(@NotNull SlashCommandInteractionEvent event, @NotNull OptionMapping mapping) {
        String colorString = mapping.getAsString();
        try {
            return colorFromHex(colorString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public @Nullable Color convert(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull OptionMapping mapping) {
        String colorString = mapping.getAsString();
        try {
            return colorFromHex(colorString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<Command.Choice> handleAutoCompletion(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull String input) {
        if (input.startsWith("#")) {
            final var color = input.substring(1);
            if (color.length() == 6 || color.length() == 8) {
                final var clr = colorFromHex(color);
                if (clr != null)
                    return List.of(new Command.Choice("Color: #" + color, "#" + color));
            }

            return List.of(new Command.Choice("Invalid hex color. Format: #RRGGBB or #RRGGBBAA", "#FF0000"));
        } else if (input.startsWith("rgb")) {
            try {
                final var firstParenthesis = input.indexOf('(');
                final var color = input.substring(firstParenthesis + 1, input.length() - 1).replace(" ", "");
                final var parts = color.split(",");

                if (parts.length == 3) {
                    try {
                        int r = Integer.parseInt(parts[0]);
                        int g = Integer.parseInt(parts[1]);
                        int b = Integer.parseInt(parts[2]);
                        if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                            return List.of(new Command.Choice("rgb(" + r + ", " + g + ", " + b + ")",
                                    rbgToHex(r, g, b)));
                        }
                    } catch (NumberFormatException ignored) {

                    }
                } else if (parts.length == 4) { // may be RGBa
                    try {
                        int r = Integer.parseInt(parts[0]);
                        int g = Integer.parseInt(parts[1]);
                        int b = Integer.parseInt(parts[2]);
                        int a = Integer.parseInt(parts[3]);
                        if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255 && a >= 0 && a <= 255) {
                            return List.of(new Command.Choice("rgb(" + r + ", " + g + ", " + b + ", " + a + ")",
                                    rbgToHex(r, g, b)));
                        }
                    } catch (NumberFormatException ignored) {

                    }
                }
            } catch (StringIndexOutOfBoundsException ignored) {

            }

            return List.of(new Command.Choice("Invalid rgb color. Format: rgb(R, G, B)", "#FF0000"));
        }

        // if the input doesn't start with "#" or "rgb", we try to match it with SkriptColor
        final var matchingColors = new ArrayList<Command.Choice>();
        for (SkriptColor color : SkriptColor.values()) {
            if (color.getName().toLowerCase().startsWith(input.toLowerCase())) {
                matchingColors.add(new Command.Choice(SmallEnumCustomArg.beautifyEnumName(color.getName()), colorToHex(color)));
            }
        }

        return matchingColors;
    }

    private static String rbgToHex(int r, int g, int b) {
        return "#" + String.format("%02X%02X%02X", r, g, b);
    }

    private static String colorToHex(Color color) {
        return rbgToHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    private static Color colorFromHex(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        if (hex.length() == 8) {
            int a = Integer.parseInt(hex.substring(6, 8), 16);
            return ColorRGB.fromRGBA(r, g, b, a);
        }

        return ColorRGB.fromRGB(r, g, b);
    }
}
