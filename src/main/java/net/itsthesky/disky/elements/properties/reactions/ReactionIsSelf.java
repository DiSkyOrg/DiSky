package net.itsthesky.disky.elements.properties.reactions;

import ch.njol.skript.conditions.base.PropertyCondition;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;

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
        return "super emoji";
    }
}
