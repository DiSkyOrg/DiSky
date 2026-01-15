package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Suppress Embeds")
@Description("Suppress/hide link embeds from a specific message.")
@Examples("suppress embeds from event-message")
@Since("4.0.0")
@SeeAlso(Message.class)
public class SuppressEmbed extends AsyncEffect {

    static {
        Skript.registerEffect(
                SuppressEmbed.class,
                "(suppress|hide) [the] [discord] [link] embed[s] [message] (of|from) [the] [discord] [message] %message%"
        );
    }

    private Expression<Message> exprMessage;

    @Override
    public boolean init(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Message>) expressions[0];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Message message = parseSingle(exprMessage, e, null);

        if (message == null) {
            DiSkyRuntimeHandler.error(new NullPointerException("Message to suppress embeds from is null"), getNode());
            return;
        }

        try {
            message.suppressEmbeds(true).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, getNode());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "suppress embeds" + " from " + exprMessage.toString(event, debug);
    }
}
