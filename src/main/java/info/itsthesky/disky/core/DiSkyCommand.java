package info.itsthesky.disky.core;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Date;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.modules.DiSkyModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

public class DiSkyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Help Page &b------"));
            sender.sendMessage(Utils.colored(""));
            sender.sendMessage(Utils.colored("&b/disky &7- &9Show this help page."));
            sender.sendMessage(Utils.colored("&b/disky docs [include time] [module name] &7- &9Generate the full documentation of DiSky, including or not event-value's time."));
            sender.sendMessage(Utils.colored("&b/disky modules &7- &9Show the list of modules."));
            sender.sendMessage(Utils.colored("&b/disky bots &7- &9Show the list of loaded bots."));
            sender.sendMessage(Utils.colored("&b/disky bot <bot> &7- &9Show info about a specific bot."));
            sender.sendMessage(Utils.colored("&b/disky debug &7- &9Store the debug information inside the 'plugins/DiSky/debug.txt' file."));
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

            sb.append("== | OS Information\n\n");
            sb.append("Time: ").append(Date.now()).append("\n");
            sb.append("Memory: ").append(Utils.formatBytes(Runtime.getRuntime().totalMemory())).append("\n");
            sb.append("Free Memory: ").append(Utils.formatBytes(Runtime.getRuntime().freeMemory())).append("\n");
            sb.append("Used Memory: ").append(Utils.formatBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())).append("\n");
            sb.append("Max Memory: ").append(Utils.formatBytes(Runtime.getRuntime().maxMemory())).append("\n");
            sb.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
            sb.append("\n");

            sb.append("== | Server Information\n\n");
            sb.append("Software: ").append(Bukkit.getVersion()).append("\n");
            sb.append("Bukkit Version: ").append(Bukkit.getBukkitVersion()).append("\n");
            sb.append("Installed Plugins: ").append(Bukkit.getPluginManager().getPlugins().length).append("\n");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
                sb.append("  - ").append(plugin.getName()).append(" v").append(plugin.getDescription().getVersion()).append("\n");
            sb.append("\n");

            sb.append("== | DiSky Information\n\n");
            sb.append("Version: ").append(DiSky.getInstance().getDescription().getVersion()).append("\n");
            sb.append("JDA Version: ").append(JDAInfo.VERSION).append("\n");
            sb.append("Main JDA Class: ").append(JDA.class.getName()).append("\n");
            sb.append("Loaded Bots: ").append(DiSky.getManager().getBots().size()).append("\n");
            for (Bot bot : DiSky.getManager().getBots())
                sb.append("  - ").append(bot.getName()).append(" login in as ").append(bot.getInstance().getSelfUser().getName()).append("#").append(bot.getInstance().getSelfUser().getDiscriminator()).append("\n");
            sb.append("\n");

            sb.append("== | Modules Information\n\n");
            sb.append("Loaded Modules: ").append(DiSky.getModuleManager().getModules().size()).append("\n");
            for (DiSkyModule module : DiSky.getModuleManager().getModules())
                sb.append("  - ").append(module.getName()).append(" v").append(module.getVersion()).append("\n");
            sb.append("\n");

            sb.append("== | Skript Information\n\n");
            sb.append("Version: ").append(Skript.getVersion()).append("\n");
            sb.append("Loaded Addons: ").append(Skript.getAddons().size()).append("\n");
            for (SkriptAddon addon : Skript.getAddons())
                sb.append("  - ").append(addon.getName()).append(" [").append(addon.getFile()).append("]\n");
            sb.append("\n");

            sb.append("\n---- End of the Debug File ----");

            try {
                final File output = new File(DiSky.getInstance().getDataFolder(), "debug.txt");
                if (output.exists())
                    output.delete();
                Files.write(output.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
                sender.sendMessage(Utils.colored("&b------ &aSuccess! &b------"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return true;
        } else if (args[0].equalsIgnoreCase("modules")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Modules (" + DiSky.getModuleManager().getModules().size() + ") &b------"));
            sender.sendMessage(Utils.colored(""));
            for (DiSkyModule module : DiSky.getModuleManager().getModules())
                sender.sendMessage(Utils.colored("  &7- &b" + module.getName() + " &3made by &b" + module.getAuthor() + " &3version &b" + module.getVersion()));
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
            sender.sendMessage(Utils.colored("  &7- &3Gateway Intents ("+bot.getInstance().getGatewayIntents().size()+"):"));
            for (GatewayIntent intent : bot.getInstance().getGatewayIntents())
                sender.sendMessage(Utils.colored("    &7- &b" + intent.name().toLowerCase().replace("_", " ")));
            /*sender.sendMessage(Utils.colored("  &7- &3Shards ("+bot.getInstance().getShardInfo().getShardTotal()+"):"));
            for (int i = 0; i < bot.getInstance().getShardInfo().getShardTotal(); i++)
                sender.sendMessage(Utils.colored("    &7- &b" + i + " &3ping: &b" + bot.getInstance().getShardManager().getShardById(i).getGatewayPing() + "ms"));*/
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            final @Nullable String moduleName = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : null;
            if (moduleName == null) {
                sender.sendMessage(Utils.colored("&cYou must specify a module name!"));
                return false;
            }

            if (moduleName.equalsIgnoreCase("all")) {
                sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Reloading All Modules &b------"));
                long before = System.currentTimeMillis();

                sender.sendMessage(Utils.colored("&4/!\\"));
                sender.sendMessage(Utils.colored("&4/!\\ &cYou are using a BETA feature!"));
                sender.sendMessage(Utils.colored("&4/!\\ &cDiSky will make illegal operation on Skript, &4DO NOT&c use this feature in production!"));
                sender.sendMessage(Utils.colored("&4/!\\"));

                for (DiSkyModule module : DiSky.getModuleManager().getModules()) {
                    try {
                        module.reload();
                    } catch (Exception ex) {
                        sender.sendMessage(Utils.colored("&cFailed to reload module &b" + module.getName() + "&c!"));
                        ex.printStackTrace();
                    }
                }

                sender.sendMessage(Utils.colored("&b------ &aSuccess! Took &2"+( System.currentTimeMillis() - before )+"ms! &b------"));
            } else {
                final @Nullable DiSkyModule module = DiSky.getModule(moduleName);

                if (module == null) {
                    sender.sendMessage(Utils.colored("&cNo module by that name found."));
                    return false;
                }

                sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Module Reloading &b------"));
                long before = System.currentTimeMillis();

                sender.sendMessage(Utils.colored("&4/!\\"));
                sender.sendMessage(Utils.colored("&4/!\\ &cYou are using a BETA feature!"));
                sender.sendMessage(Utils.colored("&4/!\\ &cDiSky will make illegal operation on Skript, &4DO NOT&c use this feature in production!"));
                sender.sendMessage(Utils.colored("&4/!\\"));

                try {
                    module.reload();
                } catch (Exception ex) {
                    sender.sendMessage(Utils.colored("&cFailed to reload module &b" + module.getName() + "&c!"));
                    ex.printStackTrace();
                }
                sender.sendMessage(Utils.colored("&b------ &aSuccess! Took &2"+( System.currentTimeMillis() - before )+"ms! &b------"));
            }
            return true;
        }
        return onCommand(sender, command, label, new String[0]);
    }
}
