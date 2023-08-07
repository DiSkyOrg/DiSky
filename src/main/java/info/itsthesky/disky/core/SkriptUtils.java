package info.itsthesky.disky.core;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.*;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.ReflectionUtils;
import info.itsthesky.disky.api.events.EventValue;
import info.itsthesky.disky.elements.events.ExprEventValues;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.elements.effects.RetrieveEventValue;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

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

    public static void nukeSectionNode(SectionNode sectionNode) {
        List<Node> nodes = new ArrayList<>();
        for (Node node : sectionNode) nodes.add(node);
        for (Node n : nodes) sectionNode.remove(n);
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
                    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
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
        final List<RetrieveEventValue.RetrieveValueInfo> current =
                RetrieveEventValue.VALUES.getOrDefault(bukkitClass, new ArrayList<>());
        current.add(new RetrieveEventValue.RetrieveValueInfo(bukkitClass, codeName, function, converter));
        RetrieveEventValue.VALUES.put(bukkitClass, current);
    }

    public static <B extends SimpleDiSkyEvent, T> void registerRestValue(String codeName,
                                                                         Class<B> bukkitClass,
                                                                         Function<B, RestAction<T>> function) {
        registerRestValue(codeName, bukkitClass, function, entity -> entity);
    }

    public static <B extends Event, T> void registerValues(Class<B> bukkitClass,
                                                           Class<T> entityClass,
                                                           String name,
                                                           Function<B, T[]> function) {
        ExprEventValues.registerEventValue(bukkitClass, new EventValue<>(entityClass, name, function));
    }

    public static <B extends Event, T> void registerValue(Class<B> bukkitClass,
                                                          Class<T> entityClass,
                                                          Function<B, T> function,
                                                          int time) {
        if (entityClass.isArray())
            Logger.getLogger("DiSky").severe("Class "+ ReflectionUtils.getCurrentClass().getName() + " still use the single value registration while providing an array value.");
        EventValues.registerEventValue(bukkitClass, entityClass, new Getter<T, B>() {
            @Override
            public @Nullable T get(B arg) {
                try {
                    return function.apply(arg);
                } catch (Exception ex) {
                    return null;
                }
            }
        }, time);
    }

    public static <B extends Event, T> void registerValue(Class<B> bukkitClass, Class<T> entityClass, Function<B, T> function) {
        registerValue(bukkitClass, entityClass, function, 0);
        if (entityClass.equals(GuildChannel.class)) // See https://github.com/DiSkyOrg/DiSky/issues/138
            registerValue(bukkitClass, Channel.class, b -> (Channel) function.apply(b));
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
        if (color == null)
            return null;
        return new ColorRGB(color.getRed(), color.getGreen(), color.getBlue());
	}

	@SafeVarargs
    public static Class<? extends Event>[] addEventClasses(Class<? extends Event>... classes) {
        final List<Class<? extends Event>> current = new ArrayList<>(Arrays.asList(ParserInstance.get().getCurrentEvents()));
        current.addAll(Arrays.asList(classes));
        return current.toArray(new Class[0]);
	}

    @SafeVarargs
    public static List<TriggerItem> loadCode(SectionNode sectionNode, Class<? extends Event>... classes) {
        if (classes.length > 0)
            ParserInstance.get().setCurrentEvent("custom section node", classes);
        return ScriptLoader.loadItems(sectionNode);
    }

    public static OffsetDateTime convertDate(Date date) {
        final long ms = date.getTimestamp();
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }
}
