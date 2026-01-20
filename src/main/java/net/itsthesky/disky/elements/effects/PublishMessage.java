package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.AsyncEffect;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Publish/Crosspost Message")
@Description("Publish/Crosspost a message from a news channel to all following guilds. Only works in news channels.")
@Examples({"publish event-message",
        "crosspost message with id \"000\" in channel with id \"123\""})
@Since("4.0.0")
@SeeAlso(Message.class)
public class PublishMessage extends AsyncEffect {

    static {
        Skript.registerEffect(
                PublishMessage.class,
                "[discord] (publish|crosspost) [message] %message%"
        );
    }

    private Expression<Message> exprMessage;

    @Override
    public boolean init(Expression @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMessage = (Expression<Message>) exprs[0];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final var message = exprMessage.getSingle(e);
        if (message == null) {
            DiSkyRuntimeHandler.error(new NullPointerException("Message to publish is null."), getNode());
            return;
        }

        try {
            message.crosspost().complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, getNode());
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "publish " + exprMessage.toString(e, debug);
    }
}
