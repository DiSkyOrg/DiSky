package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Suppress Embeds")
@Description("Suppress/hide link embeds from a specific message.")
@Examples("suppress embeds from event-message")
@Since("4.0.0")
@SeeAlso(Message.class)
public class SuppressEmbed extends SpecificBotEffect {

    static {
        Skript.registerEffect(
                SuppressEmbed.class,
                "(suppress|hide) [the] [discord] [link] embed[s] [message] (of|from) [the] [discord] [message] %message%"
        );
    }

    private Expression<Message> exprMessage;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Message>) expressions[0];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, @NotNull Bot bot) {
        final Message message = parseSingle(exprMessage, e, null);

        if (anyNull(this, message)) {
            restart();
            return;
        }

        message.suppressEmbeds(true).queue(this::restart, ex -> {
            restart();
            DiSkyRuntimeHandler.error((Exception) ex);
        });
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "suppress embeds" + " from " + exprMessage.toString(event, debug);
    }
}
