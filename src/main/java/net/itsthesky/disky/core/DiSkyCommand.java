package net.itsthesky.disky.core;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Date;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.EventListener;
import net.itsthesky.disky.api.events.rework.EventBuilder;
import net.itsthesky.disky.api.modules.DiSkyModule;
import net.itsthesky.disky.managers.ConfigManager;
import net.itsthesky.disky.managers.CoreEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiSkyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Help Page &b------"));
            sender.sendMessage(Utils.colored(""));
            sender.sendMessage(Utils.colored("&b/disky &7- &9Show this help page."));
            sender.sendMessage(Utils.colored("&b/disky docs [include time] [module name] &7- &9Generate the full documentation of DiSky, including or not event-value's time."));
            sender.sendMessage(Utils.colored("&b/disky eventdocs &7- &9Generate the documentation for the events."));
            sender.sendMessage(Utils.colored("&b/disky modules &7- &9Show the list of modules."));
            sender.sendMessage(Utils.colored("&b/disky bots &7- &9Show the list of loaded bots."));
            sender.sendMessage(Utils.colored("&b/disky bot <bot> &7- &9Show info about a specific bot."));
            sender.sendMessage(Utils.colored("&b/disky debug [sections] &7- &9Store the debug information inside the 'plugins/DiSky/debug.txt' file."));
            sender.sendMessage(Utils.colored("&b/disky reloadconfig &7- &9Reload DiSky's configuration"));
            sender.sendMessage(Utils.colored("&b/disky reload <module name> &7- &4&lBETA &9Reload a specific module."));
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("docs")) {
            final boolean includeTime = args.length > 1 && args[1].equalsIgnoreCase("true");
            final String moduleName = args.length > 2 ? args[2] : null;

            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Documentation &b------"));
            long before = System.currentTimeMillis();
            DiSky.getDocBuilder().generate(includeTime, moduleName);
            sender.sendMessage(Utils.colored("&b------ &aSuccess! Took &2" + (System.currentTimeMillis() - before) + "ms! &b------"));
            return true;
        } else if (args[0].equalsIgnoreCase("debug")) {
            sender.sendMessage(Utils.colored("&b------ &6Generating file ... &b------"));

            final StringBuilder sb = new StringBuilder();
            sb.append("---- DiSky Debug File ----\n");
            sb.append("// I hopes you're all good my friend? :c\n\n");

            Map<String, Runnable> debugSections = new HashMap<>();
            debugSections.put("os", () -> {
                sb.append("== | OS Information\n\n");
                sb.append("Time: ").append(Date.now()).append("\n");
                sb.append("Memory: ").append(Utils.formatBytes(Runtime.getRuntime().totalMemory())).append("\n");
                sb.append("Free Memory: ").append(Utils.formatBytes(Runtime.getRuntime().freeMemory())).append("\n");
                sb.append("Used Memory: ").append(Utils.formatBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())).append("\n");
                sb.append("Max Memory: ").append(Utils.formatBytes(Runtime.getRuntime().maxMemory())).append("\n");
                sb.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
                sb.append("\n");
            });
            debugSections.put("server", () -> {
                sb.append("== | Server Information\n\n");
                sb.append("Software: ").append(Bukkit.getVersion()).append("\n");
                sb.append("Bukkit Version: ").append(Bukkit.getBukkitVersion()).append("\n");
                sb.append("Installed Plugins: ").append(Bukkit.getPluginManager().getPlugins().length).append("\n");
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
                    sb.append("  - ").append(plugin.getName()).append(" v").append(plugin.getDescription().getVersion()).append("\n");
                sb.append("\n");
            });
            debugSections.put("disky", () -> {
                sb.append("== | DiSky Information\n\n");
                sb.append("Version: ").append(DiSky.getInstance().getDescription().getVersion()).append("\n");
                sb.append("JDA Version: ").append(JDAInfo.VERSION).append("\n");
                sb.append("Main JDA Class: ").append(JDA.class.getName()).append("\n");
                sb.append("Loaded Bots: ").append(DiSky.getManager().getBots().size()).append("\n");
                for (Bot bot : DiSky.getManager().getBots())
                    sb.append("  - ").append(bot.getName()).append(" login in as ").append(bot.getInstance().getSelfUser().getName()).append("#").append(bot.getInstance().getSelfUser().getDiscriminator()).append("\n");
                sb.append("\n");
            });
            debugSections.put("modules", () -> {
                sb.append("== | Modules Information\n\n");
                sb.append("Loaded Modules: ").append(DiSky.getModuleManager().getModules().size()).append("\n");
                for (DiSkyModule module : DiSky.getModuleManager().getModules())
                    sb.append(" - ").append(module.getModuleInfo().name).append(" v").append(module.getModuleInfo().version).append("\n");
                sb.append("\n");
            });
            debugSections.put("skript", () -> {
                sb.append("== | Skript Information\n\n");
                sb.append("Version: ").append(Skript.getVersion()).append("\n");
                sb.append("Loaded Addons: ").append(Skript.getAddons().size()).append("\n");
                for (SkriptAddon addon : Skript.getAddons())
                    sb.append("  - ").append(addon.getName()).append(" [").append(addon.getFile()).append("]\n");
                sb.append("\n");
            });
            debugSections.put("listeners", () -> {
                sb.append("== | (DiSky) Listeners Information\n\n");
                sb.append("Loaded Listeners: ").append(CoreEventListener.AllRegisteredListeners.size()).append("\n");
                for (EventListener<?> listener : CoreEventListener.AllRegisteredListeners) {
                    sb.append("  - ").append(listener.getAttachedNode()).append(" / ").append(listener.isEnabled() ? "Enabled" : "Disabled").append("\n");
                    sb.append("    - Specific Bot Name: ").append(listener.getSpecificBotName()).append("\n");
                    sb.append("    - Waiting Log Event: ").append(listener.isWaitingLogEvent())
                            .append(" [LogType: ").append(listener.getLogType()).append("]").append("\n");
                    sb.append("    - Enabled: ").append(listener.enabled).append("\n");
                }
            });

            var desiredSections = args.length > 1 ? args[1].split(",") : debugSections.keySet().toArray(String[]::new);
            for (String section : desiredSections) {
                if (debugSections.containsKey(section)) {
                    debugSections.get(section).run();
                } else {
                    var closest = Utils.getClosest(section, debugSections.keySet());
                    sender.sendMessage(Utils.colored("&cUnknown debug section: &b" + section + " &7(Did you mean &b" + closest + "&7?)"));
                }
            }

            sb.append("\n---- End of the Debug File ----");

            try {
                final File output = new File(DiSky.getInstance().getDataFolder(), "debug.txt");
                if (output.exists())
                    output.delete();
                Files.write(output.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
                sender.sendMessage(Utils.colored("&b------ &aSuccess! Exported to &2" + output.getAbsolutePath() + " &b------"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return true;
        } else if (args[0].equalsIgnoreCase("modules")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Modules (" + DiSky.getModuleManager().getModules().size() + ") &b------"));
            sender.sendMessage(Utils.colored(""));
            for (DiSkyModule module : DiSky.getModuleManager().getModules())
                sender.sendMessage(Utils.colored(" &7- &b" + module.getModuleInfo().name + " &3made by &b" + module.getModuleInfo().author + " &3version &b" + module.getModuleInfo().version));
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("bots")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Bots (" + DiSky.getManager().getBots().size() + ") &b------"));
            sender.sendMessage(Utils.colored(""));
            for (Bot bot : DiSky.getManager().getBots())
                sender.sendMessage(Utils.colored("  &7- &b" + bot.getName() + " &3loaded as &b" + bot.getDiscordName() + "&3, ping:&b " + bot.getInstance().getGatewayPing() + "ms"));
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("bot")) {
            final String botName = args.length > 1 ? args[1] : null;
            final Bot bot = botName == null ? null : DiSky.getManager().getBotByName(botName);
            if (botName == null || bot == null) {
                sender.sendMessage(Utils.colored("&cYou must specify a valid bot name!"));
                return false;
            }

            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Bot &b------"));
            sender.sendMessage(Utils.colored(""));
            sender.sendMessage(Utils.colored("  &7- &3Name: &b" + bot.getName()));
            sender.sendMessage(Utils.colored("  &7- &3Discord Name: &b" + bot.getDiscordName()));
            sender.sendMessage(Utils.colored("  &7- &3Uptime: &b" + bot.getUptime()));
            sender.sendMessage(Utils.colored("  &7- &3Ping: &b" + bot.getInstance().getGatewayPing() + "ms"));
            sender.sendMessage(Utils.colored("  &7- &3Gateway Intents (" + bot.getInstance().getGatewayIntents().size() + "):"));
            for (GatewayIntent intent : bot.getInstance().getGatewayIntents())
                sender.sendMessage(Utils.colored("    &7- &b" + intent.name().toLowerCase().replace("_", " ")));
            /*sender.sendMessage(Utils.colored("  &7- &3Shards ("+bot.getInstance().getShardInfo().getShardTotal()+"):"));
            for (int i = 0; i < bot.getInstance().getShardInfo().getShardTotal(); i++)
                sender.sendMessage(Utils.colored("    &7- &b" + i + " &3ping: &b" + bot.getInstance().getShardManager().getShardById(i).getGatewayPing() + "ms"));*/
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("reloadconfig")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Configuration Reloading &b------"));
            boolean wasDebug = ConfigManager.get("debug", false);
            long before = System.currentTimeMillis();
            ConfigManager.reloadConfig(DiSky.getInstance());
            sender.sendMessage(Utils.colored("&b------ &aSuccess! Took &2" + (System.currentTimeMillis() - before) + "ms! &b------"));
            if (wasDebug != ConfigManager.get("debug", false) && ConfigManager.get("debug", false))
                sender.sendMessage(Utils.colored("&5--------> &dDebug mode has been enabled!"));
            return true;
        } else if (args[0].equalsIgnoreCase("eventdocs")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Events Documentation &b------"));
            long before = System.currentTimeMillis();
            final var file = new File(DiSky.getInstance().getDataFolder(), "events-docs.txt");
            if (file.exists())
                file.delete();

            try {
                final var builder = new StringBuilder();
                builder.append("""
                        ---
                        icon: material/check-all
                        ---
                        
                        # Events
                        
                        [[[% import 'macros.html' as macros %]]]
                        
                        ## Information: Retrieve-Values
                        
                        For some event, you can see a `retrieve values` section. Some values are given by Discord directly, and others needs another **request** to Discord to get the value (those are in as `retrieve values`).
                        
                        !!! example ""
                            For instance in the [Reaction Add Event](#reaction-add), Discord gives us the message ID only, so you can use its retrieve value to get the actual message:
                        
                            ```applescript
                            on reaction add:
                                # </>
                        
                                retrieve event value "message" and store it in {_message}
                                # now you can use {_message} as the message that was reacted to!
                            ```
                        
                        """);
                final var groupedEvents = EventBuilder.REGISTERED_EVENTS
                        .stream()
                        .filter(e -> e.getCategory() != null)
                        .collect(Collectors.groupingBy(EventBuilder::getCategory));
                final var uncategorizedEvents = EventBuilder.REGISTERED_EVENTS
                        .stream()
                        .filter(e -> e.getCategory() == null)
                        .toList();

                // first, the uncategorized events
                for (final var eventBuilder : uncategorizedEvents) {
                    final var doc = eventBuilder.createDocumentation();
                    if (doc == null)
                        continue;

                    builder.append(doc);
                }

                // then the categorized events
                for (final var entry : groupedEvents.entrySet()) {
                    builder.append("## ").append(entry.getKey().name()).append("\n\n");
                    builder.append(String.join("\n", entry.getKey().description())).append("\n\n");

                    for (final var eventBuilder : entry.getValue()) {
                        final var doc = eventBuilder.createDocumentation();
                        if (doc == null)
                            continue;

                        builder.append(doc);
                    }
                }

                Files.writeString(file.toPath(), builder.toString(), StandardCharsets.UTF_8);
            } catch (Exception ex) {
                ex.printStackTrace();
                sender.sendMessage(Utils.colored("&cAn error occurred while generating the events documentation!"));
                return false;
            }

            sender.sendMessage(Utils.colored("&b------ &aSuccess! Took &2" + (System.currentTimeMillis() - before) + "ms! &b------"));
            return true;
        }
        return onCommand(sender, command, label, new String[0]);
    }
}
