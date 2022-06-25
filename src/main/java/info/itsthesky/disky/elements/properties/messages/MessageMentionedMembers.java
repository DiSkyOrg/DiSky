package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Message Mentioned Members")
@Description("Get every mentioned members in a message. If the message doesn't come from a guild it will return an empty array!")
@Examples("mentioned members of event-message")
public class MessageMentionedMembers extends MultiplyPropertyExpression<Message, Member> {

    static {
                register(
                MessageMentionedMembers.class,
                Member.class,
                "[discord] [message] mentioned members",
                        "message"
        );
    }

    @Override
    public @NotNull Class<? extends Member> getReturnType() {
        return Member.class;
    }

    @Override
    protected String getPropertyName() {
        return "mentioned members";
    }

    @Override
    protected Member[] convert(Message message) {
        return message.getMentions().getMembers().toArray(new Member[0]);
    }
}
