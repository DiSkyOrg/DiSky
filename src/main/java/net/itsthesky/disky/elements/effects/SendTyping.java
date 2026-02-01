package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Send typing")
@Description({"Sends the typing status to discord. This is what is used to make the message \"X is typing...\" appear.",
        "Typing status lasts for 10 seconds."})
@Examples({"show typing status in event-channel"})
@Since("4.0.0")
@SeeAlso({Channel.class, MessageChannel.class})
public class SendTyping extends AsyncEffect {

    static {
        Skript.registerEffect(
                SendTyping.class,
                "[discord] (send|show) typing [status] (in|to) [[text[ |-]]channel] %channel%"
        );
    }

    private Expression<Channel> exprChannel;
    private Node node;

    @Override
    public boolean init(Expression[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        node = getParser().getNode();

        exprChannel = (Expression<Channel>) expr[0];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Channel channel = EasyElement.parseSingle(exprChannel, e, null);

        if (channel == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprChannel);
            return;
        }

        if (!channel.getType().isMessage()) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The given channel is a '" + channel.getType().name() + "' channel, not a message channel."),
                    node, false);
        }
        final var msgChannel = (MessageChannel) channel;

        msgChannel.sendTyping().complete();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "send typing to " + exprChannel.toString(e, debug);
    }
}
