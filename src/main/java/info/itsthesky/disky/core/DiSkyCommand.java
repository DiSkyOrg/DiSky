package info.itsthesky.disky.core;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.modules.DiSkyModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiSkyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Help Page &b------"));
            sender.sendMessage(Utils.colored(""));
            sender.sendMessage(Utils.colored("&b/disky &7- &9Show this help page."));
            sender.sendMessage(Utils.colored("&b/disky docs <include time> &7- &9Generate the full documentation of DiSky, including or not event-value's time."));
            sender.sendMessage(Utils.colored("&b/disky modules &7- &9Show the list of modules."));
            sender.sendMessage(Utils.colored("&b/disky bots &7- &9Show the list of loaded bots."));
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("docs")) {
            final boolean includeTime = args.length > 1 && args[1].equalsIgnoreCase("true");
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Documentation &b------"));
            long before = System.currentTimeMillis();
            DiSky.getDocBuilder().generate(includeTime);
            sender.sendMessage(Utils.colored("&b------ &aSuccess! Took &2"+( System.currentTimeMillis() - before )+"ms! &b------"));
            return true;
        } else if (args[0].equalsIgnoreCase("modules")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Modules (" + DiSky.getModuleManager().getModules().size() + ") &b------"));
            for (DiSkyModule module : DiSky.getModuleManager().getModules())
                sender.sendMessage(Utils.colored("  &7- &b" + module.getName() + " &3made by &b" + module.getAuthor() + " &3version &b" + module.getVersion()));
            sender.sendMessage(Utils.colored(""));
            return true;
        } else if (args[0].equalsIgnoreCase("bots")) {
            sender.sendMessage(Utils.colored("&b------ &9DiSky v" + DiSky.getInstance().getDescription().getVersion() + " Bots (" + DiSky.getManager().getBots().size() + ") &b------"));
            for (Bot bot : DiSky.getManager().getBots())
                sender.sendMessage(Utils.colored("  &7- &b" + bot.getName() + " &3loaded as &b" + bot.getDiscordName() + ", &ping:&b " + bot.getInstance().getGatewayPing() + "ms"));
            sender.sendMessage(Utils.colored(""));
            return true;
        }
        return onCommand(sender, command, label, new String[0]);
    }
}
