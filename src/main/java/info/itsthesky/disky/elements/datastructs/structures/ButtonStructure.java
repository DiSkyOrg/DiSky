package info.itsthesky.disky.elements.datastructs.structures;

import info.itsthesky.disky.api.datastruct.DataStructureEntry;
import info.itsthesky.disky.api.datastruct.base.BasicDS;
import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ButtonStructure implements BasicDS<Button> {

    @DataStructureEntry(value = "emote",
            description = "The emoji/emote of the button. If this is not set, the button MUST have a label.")
    public Emote emoji;

    @DataStructureEntry(value = "label",
            description = "The label of the button. If this is not set, the button MUST have an emoji.")
    public String label;

    @DataStructureEntry(value = "style", optional = false,
            description = "The style of the button.")
    public ButtonStyle style;

    @DataStructureEntry(value = "url",
            description = "The URL of the button. If this is set, the button will be a link button and the style will be ignored. If this is null, the button must have an ID.")
    public String url;

    @DataStructureEntry(value = "disabled",
            description = "Whether the button is disabled or not.")
    public boolean disabled;

    @DataStructureEntry(value = "id",
            description = "The ID of the button to represent it. Must be unique within all a message's components. If this is null, the URL must be set.")
    public String id;

    @Override
    public @Nullable String preValidate(List<String> presentKeys) {
        if (!presentKeys.contains("label") && !presentKeys.contains("emote"))
            return "The button must have a label or an emoji. None of them are present.";
        if (!presentKeys.contains("id") && !presentKeys.contains("url"))
            return "The button must have an ID or an URL. None of them are present.";

        return null;
    }

    @Override
    public Button build() {
        final var idOrUrl = id == null ? url : id;
        Button button = label == null
                ? Button.of(style, idOrUrl, emoji.getEmoji())
                : Button.of(style, idOrUrl, label);

        if (label != null && emoji != null)
            button = Button.of(style, idOrUrl, label, emoji.getEmoji());

        if (disabled)
            button = button.asDisabled();

        return button;
    }
}
