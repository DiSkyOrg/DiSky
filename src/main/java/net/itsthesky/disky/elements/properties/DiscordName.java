package net.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.itsthesky.disky.elements.componentsv2.base.sub.FileDisplayBuilder;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Name of Discord Entity")
@Description({"This represent the current name of any discord entity that can hold one.",
        "You can change name of every entity except member and user by defining a new text.",
        "Check for 'nickname of member' if you want to check / change custom member's name."})
@Examples("discord name of event-guild")
public class DiscordName extends SimplePropertyExpression<Object, String> implements IAsyncChangeableExpression {

    static {
        register(
                DiscordName.class,
                String.class,
                "discord name",
                "channel/user/member/sticker/scheduledevent/emote/threadchannel/role/guild/embedfield/applicationinfo/webhook/newcomponent"
        );
    }

    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "discord name";
    }

    @Override
    public @Nullable Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? new Class[] {String.class} : new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @Nullable @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        change(e, delta, mode, false);
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        change(e, delta, mode, true);
    }

    private void change(Event event, Object[] delta, Changer.ChangeMode mode, boolean async) {
        final Object entity = getExpr().getSingle(event);
        final @Nullable String name = delta.length == 0 ? null : (String) delta[0];
        if (name == null || entity == null)
            return;

        final RestAction<Void> action;
        if (entity instanceof GuildChannel) {
            action = ((GuildChannel) entity).getManager().setName(name);
        } else if (entity instanceof Role) {
            action = ((Role) entity).getManager().setName(name);
        } else if (entity instanceof Bot) {
            action = ((Bot) entity).getInstance().getSelfUser().getManager().setName(name);
        } else {
            action = null;
        }

        if (action != null) {
            try {
                if (async) action.complete();
                else action.queue();
            } catch (Exception ex) {
                DiSkyRuntimeHandler.error(ex, node);
            }
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
        } else if (entity instanceof Sticker) {
            return ((Sticker) entity).getName();
        } else if (entity instanceof ScheduledEvent) {
            return ((ScheduledEvent) entity).getName();
        } else if (entity instanceof MessageEmbed.Field) {
            return ((MessageEmbed.Field) entity).getName();
        } else if (entity instanceof ApplicationInfo) {
            return ((ApplicationInfo) entity).getName();
        } else if (entity instanceof Webhook) {
            return ((Webhook) entity).getName();
        } else if (entity instanceof FileDisplayBuilder) {
            return ((FileDisplayBuilder) entity).getFileName();
        } else {
            return null;
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
