package net.itsthesky.disky.elements.properties.reactions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;

@Name("Reaction Is Self")
@Description("Check whether a reaction was added by the bot itself.")
@Examples({"if reaction \"smile\" of event-message is self emoji:",
        "    reply with \"That's a reaction I added!\""})
@Since("4.0.0")
public class ReactionIsSelf extends PropertyCondition<MessageReaction> {

    static {
        register(
                ReactionIsSelf.class,
                PropertyType.BE,
                "[discord] [reaction] self [emoji]",
                "reaction"
        );
    }

    @Override
    public boolean check(MessageReaction messageReaction) {
        return messageReaction.isSelf();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "self emoji";
    }
}
