package net.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.managers.ConfigManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class BotStatus {

    static {
        if (ConfigManager.get("fix-skript-online-status", false)) {
            DiSky.debug("Fixing Skript online status");
            StringBotStatus.register(
                    StringBotStatus.class,
                    String.class,
                    "[discord] [online] status",
                    "bot/member"
            );
        } else {
            DiSky.debug("Registering normal enumeration for bot status");
            EnumBotStatus.register(
                    EnumBotStatus.class,
                    OnlineStatus.class,
                    "[discord] [online] status",
                    "bot/member"
            );
        }
    }

    public static class StringBotStatus extends PropertyExpression<Object, String> {

        @Override
        public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
            if (mode == Changer.ChangeMode.SET)
                return CollectionUtils.array(String.class);
            return CollectionUtils.array();
        }

        @Override
        public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
            if (delta == null || delta.length == 0 || delta[0] == null) return;
            Object entity = EasyElement.parseSingle(getExpr(), e, null);
            final String status = (String) delta[0];
            if (status == null || entity == null) return;

            if (entity instanceof Bot) {
                ((Bot) entity).getInstance().getPresence().setPresence(OnlineStatus.valueOf(
                        status.toUpperCase().replace(" ", "_")
                ), ((Bot) entity).getInstance().getPresence().getActivity());
            }
        }

        @Override
        protected String @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
            if (source.length == 0)
                return new String[0];
            return new String[] {
                    source[0] instanceof Bot
                            ? ((Bot) source[0]).getInstance().getPresence().getStatus().name().toLowerCase(Locale.ROOT).replace("_", " ")
                            : ((Member) source[0]).getOnlineStatus().name().toLowerCase(Locale.ROOT).replace("_", " ")
            };
        }

        @Override
        public @NotNull Class<? extends String> getReturnType() {
            return String.class;
        }

        @Override
        public @NotNull String toString(@Nullable Event e, boolean debug) {
            return "online status of " + getExpr().toString(e, debug);
        }

        @Override
        public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
            setExpr(exprs[0]);
            return true;
        }
    }

    public static class EnumBotStatus extends PropertyExpression<Object, OnlineStatus> {

        @Override
        public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
            if (mode == Changer.ChangeMode.SET)
                return CollectionUtils.array(OnlineStatus.class);
            return CollectionUtils.array();
        }

        @Override
        public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
            if (delta == null || delta.length == 0 || delta[0] == null) return;
            final Object entity = EasyElement.parseSingle(getExpr(), e, null);
            final OnlineStatus status = (OnlineStatus) delta[0];
            if (status == null || entity == null) return;

            if (entity instanceof Bot) {
                ((Bot) entity).getInstance().getPresence().setPresence(status, ((Bot) entity).getInstance().getPresence().getActivity());
            }
        }

        @Override
        protected OnlineStatus @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
            if (source.length == 0)
                return new OnlineStatus[0];
            return new OnlineStatus[] {
                    source[0] instanceof Bot
                            ? ((Bot) source[0]).getInstance().getPresence().getStatus()
                            : ((Member) source[0]).getOnlineStatus()
            };
        }

        @Override
        public @NotNull Class<? extends OnlineStatus> getReturnType() {
            return OnlineStatus.class;
        }

        @Override
        public @NotNull String toString(@Nullable Event e, boolean debug) {
            return "online status of " + getExpr().toString(e, debug);
        }

        @Override
        public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
            setExpr(exprs[0]);
            return true;
        }
    }

}
