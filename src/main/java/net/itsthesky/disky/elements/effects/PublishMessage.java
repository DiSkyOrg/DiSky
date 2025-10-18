package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
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

public class PublishMessage extends SpecificBotEffect {

    static {
        Skript.registerEffect(
                PublishMessage.class,
                "[discord] (publish|crosspost) [message] %message%"
        );
    }

    private Expression<Message> exprMessage;

    @Override
    public boolean initEffect(Expression @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprMessage = (Expression<Message>) exprs[0];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, final Bot bot) {
        Message message = exprMessage.getSingle(e);
        message.crosspost().queue(
                (v)-> restart(),
                ex -> {
                    DiSkyRuntimeHandler.error((Exception) ex);
                    restart();
                });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "publish " + exprMessage.toString(e, debug);
    }
}
