package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.INodeHolder;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@Name("Guild Verification Level")
@Description({"Represent the verification level of the guild. It can either be:",
        "- None",
        "- Low",
        "- Medium",
        "- High",
        "- Very High"})
@Examples("reply with verification level of event-guild")
public class GuildVerificationLevel extends GuildProperty<String>
        implements IAsyncChangeableExpression, INodeHolder {

    static {
        register(
                GuildVerificationLevel.class,
                String.class,
                "verification level[s]"
        );
    }

    @Override
    public String converting(Guild guild) {
        return guild.getVerificationLevel().name().toLowerCase(Locale.ROOT).replace("_", " ");
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET)
            return new Class[] {String.class};
        return null;
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        change(event, delta, mode, false);
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        change(e, delta, mode, true);
    }

    private void change(Event e, Object[] delta, Changer.ChangeMode mode, boolean async) {
        if (delta == null || delta.length == 0 || delta[0] == null)
            return;
        Guild guild = getExpr().getSingle(e);
        final String value = (String) delta[0];
        if (value == null || guild == null)
            return;

        Guild.VerificationLevel level;
        try {
            level = Guild.VerificationLevel.valueOf(value.toUpperCase(Locale.ROOT).replace(" ", "_"));
        } catch (IllegalArgumentException ex) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The verification level specified is not valid!"), node);
            return;
        }

        var action = guild.getManager().setVerificationLevel(level);

        if (async) action.complete();
        else action.queue();
    }

    private Node node;
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @NotNull Node getNode() {
        return node;
    }
}

