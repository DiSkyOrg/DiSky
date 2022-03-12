package info.itsthesky.disky.core;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.log.*;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.elements.effects.EffRetrieveEventValue;
import info.itsthesky.disky.elements.events.MessageEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public final class SkriptUtils {

    @Deprecated
    public static <T> T verifyVar(Expression<T> expression, Event e, T defaultValue) {
        return EasyElement.parseSingle(expression, e, defaultValue);
    }

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(DiSky.getInstance(), runnable);
    }

    public static void stopLog(RetainingLogHandler logger) {
        //Stop the current one
        logger.stop();
        //Using reflection to access the iterator of handlers
        HandlerList handler = ParserInstance.get().getHandlers();
        if (handler == null)
            return;
        Iterator<LogHandler> it = handler.iterator();
        //A list containing the last handlers that will be stopped
        List<LogHandler> toStop = new ArrayList<>();
        while (it.hasNext()) {
            LogHandler l = it.next();
            if (l instanceof ParseLogHandler)
                toStop.add(l);
            else //We can only stop the lasts handler, this prevent in case the last is not what we want.
                break;
        }
        toStop.forEach(LogHandler::stop); //Stopping them
        SkriptLogger.logAll(logger.getLog()); //Sending the errors to Skript logger.
    }

    @SuppressWarnings("unchecked")
    public static <T> Expression<T> defaultToEventValue(Expression expr, Class<T> clazz) {
        if (expr != null)
            return (Expression<T>) expr;
        Class<? extends Event>[] events = ScriptLoader.getCurrentEvents();
        for (Class<? extends Event> e : events == null ? new Class[0] : events) {
            Getter getter = EventValues.getEventValueGetter(e, clazz, 0);
            if (getter != null) {
                return new SimpleExpression<T>() {
                    @Override
                    protected T @NotNull [] get(@NotNull Event e) {
                        T value = (T) getter.get(e);
                        if (value == null)
                            return null;
                        T[] arr = (T[]) Array.newInstance(clazz, 1);
                        arr[0] = value;
                        return arr;
                    }

                    @Override
                    public boolean isSingle() {
                        return true;
                    }

                    @Override
                    public @NotNull Class<? extends T> getReturnType() {
                        return clazz;
                    }

                    @Override
                    public boolean isDefault() {
                        return true;
                    }

                    @Override
                    public @NotNull String toString(Event e, boolean debug) {
                        return "defaulted event value expression";
                    }

                    @Override
                    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
                        return true;
                    }
                };
            }
        }
        return null;
    }

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(DiSky.getInstance(), runnable);
    }

    ////
    // Registering event-value / retrieved event-value
    ////

    public static <B extends SimpleDiSkyEvent<? extends GenericMessageEvent>> void registerAuthorValue(Class<B> clazz) {
        registerAuthorValue(clazz, event -> event.getJDAEvent().getGuild());
    }

    public static <B extends SimpleDiSkyEvent> void registerAuthorValue(Class<B> bukkitClass,
                                                                        Function<B, Guild> function) {
        registerRestValue("author",
                bukkitClass,
                event -> function.apply(event).retrieveAuditLogs(),
                logs -> logs.get(0).getUser());
    }

    public static <B extends SimpleDiSkyEvent, T, S> void registerRestValue(String codeName,
                                                                            Class<B> bukkitClass,
                                                                            Function<B, RestAction<S>> function,
                                                                            Function<S, T> converter) {
        final List<EffRetrieveEventValue.RetrieveValueInfo> current =
                EffRetrieveEventValue.VALUES.getOrDefault(bukkitClass, new ArrayList<>());
        current.add(new EffRetrieveEventValue.RetrieveValueInfo(bukkitClass, codeName, function, converter));
        EffRetrieveEventValue.VALUES.put(bukkitClass, current);
    }

    public static <B extends SimpleDiSkyEvent, T> void registerRestValue(String codeName,
                                                                         Class<B> bukkitClass,
                                                                         Function<B, RestAction<T>> function) {
        registerRestValue(codeName, bukkitClass, function, entity -> entity);
    }

    public static <B extends Event, T> void registerValue(Class<B> bukkitClass,
                                                          Class<T> entityClass,
                                                          Function<B, T> function,
                                                          int time) {
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

    public static <E extends net.dv8tion.jda.api.events.Event, B extends SimpleDiSkyEvent<E>> void registerBotValue(Class<B> bukkitClass) {
        registerValue(bukkitClass, Bot.class, e -> {
            final JDA jda = e.getJDAEvent().getJDA();
            return DiSky.getManager().fromJDA(jda);
        });
    }

	public static void createRegisteringSpace(Runnable code) {
        if (Skript.isAcceptRegistrations()) // Already accept it
            code.run();
        else {
            try {
                Field field = Skript.class.getDeclaredField("acceptRegistrations");
                field.setAccessible(true);
                field.set(null, true);
                code.run();
                field.setAccessible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
	}

	public static Color convert(java.awt.Color color) {
        return new ColorRGB(color.getRed(), color.getGreen(), color.getBlue());
	}
}
