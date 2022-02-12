package info.itsthesky.disky.core;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.elements.events.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class SkriptUtils {

    @Deprecated
    public static <T> T verifyVar(Expression<T> expression, Event e, T defaultValue) {
        return expression == null ? defaultValue :
                (expression.getSingle(e) == null ? defaultValue : expression.getSingle(e));
    }

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(DiSky.getInstance(), runnable);
    }

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(DiSky.getInstance(), runnable);
    }

    public static <B extends Event, T> void registerValue(Class<B> bukkitClass, Class<T> entityClass, Function<B, T> function, int time) {
        EventValues.registerEventValue(bukkitClass, entityClass, new Getter<T, B>() {
            @Override
            public @Nullable T get(B arg) {
                return function.apply(arg);
            }
        }, time);
    }

    public static <B extends Event, T> void registerValue(Class<B> bukkitClass, Class<T> entityClass, Function<B, T> function) {
        registerValue(bukkitClass, entityClass, function, 0);
    }

}
