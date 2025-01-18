package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Send typing")
@Description({"Sends the typing status to discord. This is what is used to make the message \"X is typing...\" appear.",
        "Typing status lasts for 10 seconds."})
@Examples({"show typing status in event-channel"})
public class SendTyping extends SpecificBotEffect {

    static {
        Skript.registerEffect(
                SendTyping.class,
                "[discord] (send|show) typing [status] (in|to) [[text[ |-]]channel] %channel%"
        );
    }

    private Expression<Channel> exprChannel;

    @Override
    public boolean initEffect(Expression[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        exprChannel = (Expression<Channel>) expr[0];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final Channel channel = parseSingle(exprChannel, e, null);

        if (bot == null || channel == null) {
            restart();
            return;
        }

        final MessageChannel textchannel = bot != null ?
                bot.findMessageChannel((MessageChannel) channel) : (MessageChannel) channel;
        if (textchannel == null) {
            restart();
            return;
        }

        textchannel.sendTyping().queue(
                v -> restart(),
                ex -> {
                    DiSky.getErrorHandler().exception(e, ex);
                    restart();
                }
        );
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "Sent typing to " + exprChannel.toString(e, debug);
    }
}
