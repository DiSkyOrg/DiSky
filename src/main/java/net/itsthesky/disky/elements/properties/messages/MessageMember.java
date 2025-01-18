package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message Member Author")
@Description("Get the member instance of the message's author. Can be null if it's in PM or not in guild!")
@Examples("member writer of event-message")
public class MessageMember extends SimplePropertyExpression<Message, Member> {

    static {
        register(
                MessageMember.class,
                Member.class,
                "[discord] [message] member (author|writer)",
                "message"
        );
    }


    @Override
    protected @NotNull String getPropertyName() {
        return "member author";
    }

    @Override
    public @Nullable Member convert(Message original) {
        if (original.isFromGuild())
            return original.getMember();
        return null;
    }

    @Override
    public @NotNull Class<? extends Member> getReturnType() {
        return Member.class;
    }
}
