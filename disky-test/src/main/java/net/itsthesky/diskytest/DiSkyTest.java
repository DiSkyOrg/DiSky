package net.itsthesky.diskytest;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import net.itsthesky.diskytest.framework.TestRunner;
import net.itsthesky.diskytest.skript.EffDiSkyAssert;
import net.itsthesky.diskytest.skript.EvtDiSkyTest;
import net.itsthesky.diskytest.skript.SecWaitForEvent;
import net.itsthesky.diskytest.skript.utilities.PropFakeHistory;
import net.itsthesky.diskytest.skript.utilities.PropFakeLastMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * DiSkyTest plugin entry point.
 *
 * <p>Registers the three core Skript syntaxes ({@code disky test},
 * {@code disky assert}, {@code wait for event}) through a Skript addon owned by
 * this plugin, and wires up the {@code /diskytest} command for running tests.
 */
public class DiSkyTest extends JavaPlugin {

    private static DiSkyTest instance;
    private SkriptAddon addon;

    public static DiSkyTest getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;

        // Ensure tests directory exists so users know where to drop .sk files.
        File testsDir = new File(getDataFolder(), "tests");
        if (!testsDir.exists() && !testsDir.mkdirs()) {
            getLogger().warning("Could not create tests directory at " + testsDir.getAbsolutePath());
        }

        // Force-load the Skript classes so their static blocks run (registering events/effects/sections).
        try {
            addon = Skript.registerAddon(this);
            // Trigger static initializers of the syntax classes.

            Class.forName(EvtDiSkyTest.class.getName());
            Class.forName(EffDiSkyAssert.class.getName());
            Class.forName(SecWaitForEvent.class.getName());
            Class.forName(PropFakeLastMessage.class.getName());
            Class.forName(PropFakeHistory.class.getName());

            getLogger().info("DiSkyTest syntaxes registered.");
        } catch (Throwable t) {
            getLogger().severe("Failed to register DiSkyTest syntaxes: " + t);
            t.printStackTrace();
        }

        getLogger().info("DiSkyTest enabled. Drop .sk files under "
                + testsDir.getAbsolutePath() + " and run '/diskytest run'.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("diskytest")) return false;

        if (args.length == 0 || !args[0].equalsIgnoreCase("run")) {
            sender.sendMessage("\u00A77/diskytest run [<filter>]");
            return true;
        }
        String filter = args.length >= 2 ? args[1] : null;
        TestRunner.runAll(sender, filter);
        return true;
    }

    public SkriptAddon getAddon() { return addon; }
}
