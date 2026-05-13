package net.itsthesky.disky.core;

import moe.kyokobot.libdave.DaveFactory;
import moe.kyokobot.libdave.NativeDaveFactory;
import moe.kyokobot.libdave.jda.LDJDADaveSessionFactory;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.audio.dave.DaveSessionFactory;
import net.itsthesky.disky.DiSky;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public final class DaveSupport {

    private static @Nullable DaveSessionFactory cachedFactory;
    private static boolean initialized;
    private static boolean failed;

    private DaveSupport() {}

    public static @Nullable DaveSessionFactory get() {
        if (initialized)
            return cachedFactory;
        initialized = true;
        try {
            final DaveFactory factory = new NativeDaveFactory();
            cachedFactory = new LDJDADaveSessionFactory(factory);
        } catch (Throwable t) {
            failed = true;
            DiSky.getInstance().getLogger().log(Level.WARNING,
                    "Failed to initialize the native DAVE factory; audio connections will keep using JDA's "
                            + "passthrough implementation and will stop working on 2026-03-01. "
                            + "Cause: " + t.getClass().getSimpleName() + ": " + t.getMessage(), t);
            cachedFactory = null;
        }
        return cachedFactory;
    }

    public static AudioModuleConfig audioModuleConfig() {
        final AudioModuleConfig config = new AudioModuleConfig();
        final DaveSessionFactory factory = get();
        return factory == null ? config : config.withDaveSessionFactory(factory);
    }

    public static boolean isAvailable() {
        get();
        return !failed && cachedFactory != null;
    }
}
