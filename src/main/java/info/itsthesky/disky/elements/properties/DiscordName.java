package info.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Name of Discord Entity")
@Description({"This represent the current name of any discord entity that can hold one.",
        "You can change name of every entity except member and user by defining a new text.",
        "Check for 'nickname of member' if you want to check / change custom member's name."})
@Examples("discord name of event-guild")
public class DiscordName extends SimplePropertyExpression<Object, String> {

    static {
        register(
                DiscordName.class,
                String.class,
                "[the] discord name",
                "channel/user/member/emote/role/guild"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "discord name";
    }

    @Override
    public @Nullable Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? new Class[] {String.class} : new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        final Object entity = getExpr().getSingle(e);
        final @Nullable String name = delta.length == 0 ? null : (String) delta[0];
        if (name == null || entity == null)
            return;

        if (entity instanceof GuildChannel) {
            ((GuildChannel) entity).getManager().setName(name).queue();
        } else if (entity instanceof Role) {
            ((Role) entity).getManager().setName(name).queue();
        }
    }

    @Override
    public @Nullable String convert(Object entity) {
        if (entity instanceof Channel) {
            return ((Channel) entity).getName();
        } else if (entity instanceof Role) {
            return ((Role) entity).getName();
        } else if (entity instanceof Member) {
            return ((Member) entity).getUser().getName();
        } else if (entity instanceof User) {
            return ((User) entity).getName();
        } else if (entity instanceof Guild) {
            return ((Guild) entity).getName();
        } else if (entity instanceof Emote) {
            return ((Emote) entity).getName();
        } else {
            return null;
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
