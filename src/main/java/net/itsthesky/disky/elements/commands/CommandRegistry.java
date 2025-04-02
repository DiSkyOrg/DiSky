package net.itsthesky.disky.elements.commands;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.validate.SectionValidator;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandRegistry extends SelfRegisteringSkriptEvent {

    private static final Pattern MATCHER_PATTERN = Pattern.compile("discord command (\\S+)( .+)?$");

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
                "    prefixes: !\n" +
                "    trigger:\n" +
                "        reply with mention tag of arg-2\n" +
                "        move arg-1 to arg-2")
        .since("3.0");

        SkriptUtils.registerValue(CommandEvent.class, CommandObject.class, CommandEvent::getCommand);
        SkriptUtils.registerValue(CommandEvent.class, Member.class, CommandEvent::getMember);
        SkriptUtils.registerValue(CommandEvent.class, Message.class, CommandEvent::getMessage);
        SkriptUtils.registerValue(CommandEvent.class, User.class, CommandEvent::getUser);
        SkriptUtils.registerValue(CommandEvent.class, Guild.class, CommandEvent::getGuild);
        SkriptUtils.registerValue(CommandEvent.class, MessageChannel.class, CommandEvent::getMessageChannel);
        SkriptUtils.registerValue(CommandEvent.class, GuildChannel.class, CommandEvent::getTxtChannel);
        SkriptUtils.registerValue(CommandEvent.class, String.class, CommandEvent::getPrefix);

        SkriptUtils.registerValue(CommandEvent.class, Bot.class, event -> DiSky.getManager().fromJDA(event.getBot()));
    }

    private String arguments;
    private String command;

    @Override
    public boolean init(final Literal<?> @NotNull [] args, final int matchedPattern, final @NotNull ParseResult parser) {
        return true;
    }

    @Override
    public boolean load() {
        EntryContainer entryContainer = getEntryContainer();

        String fullCommand = entryContainer.getSource().getKey();
        assert fullCommand != null;
        fullCommand = ScriptLoader.replaceOptions(fullCommand);

        Matcher matcher = MATCHER_PATTERN.matcher(fullCommand);
        boolean matches = matcher.matches();
        if (!matches) {
            Skript.error("Invalid command structure pattern");
            return false;
        }

        command = matcher.group(1);
        arguments = matcher.group(2);

        // discord command test:
        SectionNode sectionNode = (SectionNode) getParser().getNode();

        String originalName = getParser().getCurrentEventName();
        Class<? extends Event>[] originalEvents = getParser().getCurrentEvents();
        Kleenean originalDelay = getParser().getHasDelayBefore();
        getParser().setCurrentEvent("discord command", CommandEvent.class);
        CommandObject cmd = CommandFactory.getInstance().add(sectionNode);
        command = cmd == null ? command : cmd.getName();

        getParser().setCurrentEvent(originalName, originalEvents);
        getParser().setHasDelayBefore(originalDelay);
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