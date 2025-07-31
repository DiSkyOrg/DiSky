package net.itsthesky.disky.elements.componentsv2.base.sub;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.elements.componentsv2.base.INewComponentBuilder;
import net.itsthesky.disky.elements.componentsv2.base.ISectionAccessoryBuilder;

@Getter
@Setter
@NoArgsConstructor
public class ButtonBuilder implements ISectionAccessoryBuilder<Button> {

    private String label;
    private ButtonStyle style;
    private String url;
    private String customId;
    private Emote emote;
    private boolean disabled;
    private int uniqueId = -1;

    @Override
    public void loadFrom(Button component) {
        this.label = component.getLabel();
        this.style = component.getStyle();
        this.url = component.getUrl();
        this.customId = component.getCustomId();
        this.emote = component.getEmoji() == null ? null : new Emote(component.getEmoji());
        this.disabled = component.isDisabled();
        this.uniqueId = component.getUniqueId();
    }

    @Override
    public Button build() {
        final var idOrUrl = customId == null ? url : customId;
        Button button = label == null
                ? Button.of(style, idOrUrl, emote == null ? null : emote.getEmoji())
                : Button.of(style, idOrUrl, label, emote == null ? null : emote.getEmoji());

        if (disabled) button = button.asDisabled();

        return button;
    }
}
