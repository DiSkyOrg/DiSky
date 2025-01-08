package info.itsthesky.disky.elements.properties.reactions;

import ch.njol.skript.conditions.base.PropertyCondition;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;

public class ReactionIsSuper extends PropertyCondition<MessageReaction> {

    static {
        register(
                ReactionIsSuper.class,
                PropertyType.BE,
                "[discord] [reaction] super [emoji]",
                "reaction"
        );
    }

    @Override
    public boolean check(MessageReaction messageReaction) {
        return messageReaction.getCount(MessageReaction.ReactionType.SUPER) > 0;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "super emoji";
    }
}
