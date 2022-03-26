package info.itsthesky.disky.elements.commands;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.validate.SectionValidator;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry extends SelfRegisteringSkriptEvent {

    public static final SectionValidator commandStructure = new SectionValidator()
            .addEntry("usage", true)
            .addEntry("description", true)
            .addEntry("roles", true)
            .addEntry("aliases", true)
            .addEntry("prefixes", true)
            .addEntry("category", true)
            .addEntry("bots", true)
            .addEntry("executable in", true)
            .addEntry("permissions", true)
            .addEntry("permission message", true)
            .addSection("trigger", false);

    static {
        Skript.registerEvent("Discord Command", CommandRegistry.class, CommandEvent.class, "discord command <([^\\s]+)( .+)?$>")
        .description("Custom DiSky discord command system. Arguments works like the normal skript's one and accept both optional and require arguments.")
        .examples("discord command move <member> <voicechannel>:\n" +
                "\tprefixes: !\n" +
                "\ttrigger:\n" +
                "\t\treply with mention tag of arg-2\n" +
                "\t\tmove arg-1 to arg-2")
        .since("3.0");

        SkriptUtils.registerValue(CommandEvent.class, CommandObject.class, CommandEvent::getCommand);
        SkriptUtils.registerValue(CommandEvent.class, Member.class, CommandEvent::getMember);
        SkriptUtils.registerValue(CommandEvent.class, Message.class, CommandEvent::getMessage);
        SkriptUtils.registerValue(CommandEvent.class, User.class, CommandEvent::getUser);
        SkriptUtils.registerValue(CommandEvent.class, Guild.class, CommandEvent::getGuild);
        SkriptUtils.registerValue(CommandEvent.class, MessageChannel.class, CommandEvent::getMessageChannel);
        SkriptUtils.registerValue(CommandEvent.class, GuildChannel.class, CommandEvent::getTxtChannel);
        SkriptUtils.registerValue(CommandEvent.class, String.class, CommandEvent::getPrefix);

        EventValues.registerEventValue(CommandEvent.class, Bot.class, new Getter<Bot, CommandEvent>() {
            @Override
            public Bot get(@NotNull CommandEvent event) {
                return DiSky.getManager().fromJDA(event.getBot());
            }
        }, 0);
    }

    private String arguments;
    private String command;

    @Override
    public boolean init(final Literal<?> @NotNull [] args, final int matchedPattern, final ParseResult parser) {
        command = parser.regexes.get(0).group(1);
        arguments = parser.regexes.get(0).group(2);
        // discord command test:
        SectionNode sectionNode = (SectionNode) SkriptLogger.getNode();

        String originalName = ParserInstance.get().getCurrentEventName();
        Class<? extends Event>[] originalEvents = ParserInstance.get().getCurrentEvents();
        Kleenean originalDelay = ParserInstance.get().getHasDelayBefore();
        ParserInstance.get().setCurrentEvent("discord command", CommandEvent.class);

        CommandObject cmd = CommandFactory.getInstance().add(sectionNode);
        command = cmd == null ? command : cmd.getName();

        ParserInstance.get().setCurrentEvent(originalName, originalEvents);
        ParserInstance.get().setHasDelayBefore(originalDelay);
        nukeSectionNode(sectionNode);

        return cmd != null;
    }

    @Override
    public void register(@NotNull Trigger t) {
    }

    @Override
    public void unregister(@NotNull Trigger t) {
        CommandFactory.getInstance().remove(command);
    }

    @Override
    public void unregisterAll() {
        CommandFactory.getInstance().commandMap.clear();
    }


    @Override
    public @NotNull String toString(final Event e, final boolean debug) {
        return "discord command " + command + (arguments == null ? "" : arguments);
    }

    public void nukeSectionNode(SectionNode sectionNode) {
        List<Node> nodes = new ArrayList<>();
        for (Node node : sectionNode) nodes.add(node);
        for (Node n : nodes) sectionNode.remove(n);
    }

}