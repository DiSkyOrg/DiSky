package net.itsthesky.disky.elements.commands;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.BukkitEvent;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiSkyCommand extends SkriptEvent {

    static {
        DiSkyEvent.register("Disky Command", DiSkyCommand.class, EvtDiSkyCommand.class,
                "disky command")
                .description("Fired when a disky/discord command is executed.")
                .examples("on disky command:");

        SkriptUtils.registerValue(EvtDiSkyCommand.class, CommandObject.class,
                event -> event.command);

        SkriptUtils.registerValue(EvtDiSkyCommand.class, Message.class,
                event -> event.jdaEvent.getMessage());

        SkriptUtils.registerValue(EvtDiSkyCommand.class, User.class,
                event -> event.jdaEvent.getAuthor());

        SkriptUtils.registerValue(EvtDiSkyCommand.class, Member.class,
                event -> event.jdaEvent.getMember());

        SkriptUtils.registerValue(EvtDiSkyCommand.class, GuildMessageChannel.class,
                event -> (GuildMessageChannel) event.jdaEvent.getChannel());

        SkriptUtils.registerValue(EvtDiSkyCommand.class, Guild.class,
                event -> event.jdaEvent.getGuild());

        SkriptUtils.registerValue(EvtDiSkyCommand.class, Bot.class,
                event -> DiSky.getManager().fromJDA(event.jdaEvent.getJDA()));
    }

    @Override
    public boolean init(Literal<?> @NotNull [] exprs, int i, @NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "on disky command";
    }

    public static class EvtDiSkyCommand extends BukkitEvent implements Cancellable {
        private final CommandObject command;
        private final MessageReceivedEvent jdaEvent;
        public EvtDiSkyCommand(CommandObject command,
                               MessageReceivedEvent e) {
            super(false);
            this.command = command;
            jdaEvent = e;
        }

        private boolean isCancelled = false;

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            isCancelled = cancel;
        }
    }
}