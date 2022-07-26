package info.itsthesky.disky.elements.commands;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.localization.Language;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.List;
import java.util.Locale;

public class CommandListener extends ListenerAdapter {
    public static MessageReceivedEvent lastCommandEvent;
    public static Guild LAST_USED_GUILD;

    public static void exe(MessageReceivedEvent e, String content) {
        if (e.getAuthor().isBot()) {
            return;
        }

        for (CommandData storage : CommandFactory.getInstance().getCommands()) {
            CommandObject command = storage.getCommand();
            for (Expression<String> prefix : command.getPrefixes()) {
                if (prefix == null) return;
                for (String alias : command.getUsableAliases()) {
                    Message message = e.getMessage();
                    GuildMessageChannel textChannel = null;
                    Guild guild = null;
                    if (message.isFromGuild() && e.isFromType(ChannelType.TEXT)) {
                        textChannel = e.getChannel().asGuildMessageChannel();
                        guild = e.getGuild();
                    }
                    CommandEvent event = new CommandEvent(e,
                            null, alias, command, null,
                            guild, e.getChannel(), textChannel, message,
                            e.getAuthor(), e.getMember(), e.getJDA());

                    String usedCommand = null;
                    String rawPrefix = prefix.getSingle(event);
                    boolean mentions = false;
                    List<User> mentionedUsers = e.getMessage().getMentions().getUsers();
                    if (!mentionedUsers.isEmpty()) {
                        if (rawPrefix.contains(mentionedUsers.get(0).getId())) {
                            rawPrefix = rawPrefix.replaceFirst("!", "");
                            mentions = true;
                        }
                    }

                    if (rawPrefix.endsWith(" ")) {
                        // TODO I'm now questioning the need for the regex replacing, check this out
                        String[] spacedCommand = content.split(" ");
                        String suspectedPrefix = mentions ? spacedCommand[0].replaceFirst("!", "") : spacedCommand[0];
                        if ((suspectedPrefix + " ").equalsIgnoreCase(rawPrefix)) {
                            usedCommand = rawPrefix + (spacedCommand.length == 1 ? "" : spacedCommand[1]);
                        }

                    } else {
                        usedCommand = mentions ? content.split(" ")[0].replaceFirst("!", "") : content.split(" ")[0];
                    }

                    try {
                        String usedPrefixes = usedCommand.split("")[0].toLowerCase(Locale.ROOT);
                    } catch (Exception ignored) {}
                    String rawCommand = "";

                    // Setting back the new (nullable) event guild
                    LAST_USED_GUILD = e.isFromGuild() ? e.getGuild() : null;

                    // TODO: 29/05/2021 Fix the global prefix system. Hard to interfere with original one :'(
                    if (nonNull(usedCommand)) {
                        if ((nonNull(rawPrefix) && usedCommand.equalsIgnoreCase(rawPrefix + alias))) {
                            event.setPrefix(rawPrefix);
                            try {
                                event.setArguments(content.substring((usedCommand).length() + 1));
                            } catch (StringIndexOutOfBoundsException e1) {
                                event.setArguments(null);
                            }
                            // Because most of bukkit's apis are sync only, make sure to run this on bukkit's thread
                            SkriptUtils.sync(() -> {

                                Event ev = new DiSkyCommand.EvtDiSkyCommand(command, e);
                                Bukkit.getPluginManager().callEvent(ev);
                                lastCommandEvent = e;
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled() && !((Cancellable) ev).isCancelled()) {
                                    command.execute(event);
                                }
                            });

                            return;

                        }
                    }

                }

            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        exe(e, e.getMessage().getContentRaw());
    }

    /**
     * Checks if a string is null or the localized string form of null in Skript (usually <none>)
     */
    public static boolean nonNull(String s) {
        if (s == null) {
            return false;
        }
        String localized = Language.get("none");
        if (localized == null) {
            // on old skript versions you couldn't change <none> so assume that if result is null
            return !s.equals("<none>");
        }
        return !s.equals(localized);
    }

}