package info.itsthesky.disky.elements.changers;

/**
 * This file is part of Skript.
 * <p>
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * <p>
 * Copyright 2011-2017 Peter Güttinger and contributors
 */

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.log.CountingLogHandler;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Patterns;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.changers.DiSkyChangerElement;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.NodeInformation;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

/**
 * @author Peter Güttinger
 * edited minorly for DiSky
 */
public class Change extends Effect {

    public static Bot currentBot;
    private static boolean parsing;
    private static final String botPattern = "(with|using) %-bot%";
    public static Patterns<ChangeMode> patterns = new Patterns<>(new Object[][]{
            {"(add|give) %objects% to (%~objects%) " + botPattern, ChangeMode.ADD},
            {"increase %~objects% by (%objects%) " + botPattern, ChangeMode.ADD},
            {"give %~objects% (%objects%) " + botPattern, ChangeMode.ADD},

            {"set %~objects% to (%objects%) " + botPattern, ChangeMode.SET},

            {"remove (all|every) %objects% from (%~objects%) " + botPattern, ChangeMode.REMOVE_ALL},

            {"(remove|subtract) %objects% from (%~objects%) " + botPattern, ChangeMode.REMOVE},
            {"reduce %~objects% by (%objects%) " + botPattern, ChangeMode.REMOVE},

            {"(delete|clear) (%~objects%) " + botPattern, ChangeMode.DELETE},

            {"reset (%~objects%) " + botPattern, ChangeMode.RESET}
    });

    static {
        Skript.registerEffect(
                Change.class,
                patterns.getPatterns()
        );
    }

    @SuppressWarnings("null")
    private Expression<?> changed;
    @Nullable
    private Expression<?> changer = null;

    private Expression<Bot> bot;

    private NodeInformation node;

    @SuppressWarnings("null")
    private ChangeMode mode;

    private boolean single;

    public static String format(ChangeMode mode, String prop, Expression<?> changed, Bot bot) {
        return mode.name().toLowerCase(Locale.ENGLISH).replace("_", " ") + " " + prop + " " + changed.toString(null, false) + " with \"" + bot.toString() + "\"";
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        parsing = true;
        node = new NodeInformation();
        mode = patterns.getInfo(matchedPattern);
        switch (mode) {
            case ADD:
                bot = (Expression<Bot>) exprs[2];
                if (matchedPattern == 0) {
                    changer = exprs[0];
                    changed = exprs[1];
                } else {
                    changer = exprs[1];
                    changed = exprs[0];
                }
                break;
            case SET:
                bot = (Expression<Bot>) exprs[2];
                changer = exprs[1];
                changed = exprs[0];
                break;
            case REMOVE_ALL:
                bot = (Expression<Bot>) exprs[2];
                changer = exprs[0];
                changed = exprs[1];
                break;
            case REMOVE:
                bot = (Expression<Bot>) exprs[2];
                if (matchedPattern == 5) {
                    changer = exprs[0];
                    changed = exprs[1];
                } else {
                    changer = exprs[1];
                    changed = exprs[0];
                }
                break;
            case DELETE:
                bot = (Expression<Bot>) exprs[1];
                changed = exprs[0];
                break;
            case RESET:
                bot = (Expression<Bot>) exprs[1];
                changed = exprs[0];
        }
        //this.bot = SkriptUtils.defaultToEventValue(bot, Bot.class);
        if (bot == null) {
            parsing = false;
            return false;
        }

        try {
            DiSkyChangerElement e = ((DiSkyChangerElement) changed);
        } catch (ClassCastException ex) {
            if (!bot.isDefault()) {
                Skript.error(changed.toString(null, false) + " can't be changed with DiSky's changer effects");
            }
            parsing = false;
            return false;
        }

        final CountingLogHandler h = SkriptLogger.startLogHandler(new CountingLogHandler(Level.SEVERE));
        final Class<?>[] rs;
        final String what;
        try {
            rs = changed.acceptChange(mode);
            final ClassInfo<?> c = Classes.getSuperClassInfo(changed.getReturnType());
            final Changer<?> changer = c.getChanger();
            what = changer == null || !Arrays.equals(changer.acceptChange(mode), rs) ? changed.toString(null, false) : c.getName().withIndefiniteArticle();
        } finally {
            h.stop();
        }
        if (rs == null) {
            if (h.getCount() > 0) {
                parsing = false;
                return false;
            }
            switch (mode) {
                case SET:
                    Skript.error(what + " can't be set to anything", ErrorQuality.SEMANTIC_ERROR);
                    break;
                case DELETE:
                    if (changed.acceptChange(ChangeMode.RESET) != null)
                        Skript.error(what + " can't be deleted/cleared. It can however be reset which might result in the desired effect.", ErrorQuality.SEMANTIC_ERROR);
                    else
                        Skript.error(what + " can't be deleted/cleared", ErrorQuality.SEMANTIC_ERROR);
                    break;
                case REMOVE_ALL:
                    if (changed.acceptChange(ChangeMode.REMOVE) != null) {
                        Skript.error(what + " can't have 'all of something' removed from it. Use 'remove' instead of 'remove all' to fix this.", ErrorQuality.SEMANTIC_ERROR);
                        break;
                    }
                    //$FALL-THROUGH$
                case ADD:
                case REMOVE:
                    Skript.error(what + " can't have anything " + (mode == ChangeMode.ADD ? "added to" : "removed from") + " it", ErrorQuality.SEMANTIC_ERROR);
                    break;
                case RESET:
                    if (changed.acceptChange(ChangeMode.DELETE) != null)
                        Skript.error(what + " can't be reset. It can however be deleted which might result in the desired effect.", ErrorQuality.SEMANTIC_ERROR);
                    else
                        Skript.error(what + " can't be reset", ErrorQuality.SEMANTIC_ERROR);
            }
            parsing = false;
            return false;
        }

        final Class<?>[] rs2 = new Class<?>[rs.length];
        for (int i = 0; i < rs.length; i++)
            rs2[i] = rs[i].isArray() ? rs[i].getComponentType() : rs[i];
        final boolean allSingle = Arrays.equals(rs, rs2);

        Expression<?> ch = changer;
        if (ch != null) {
            Expression<?> v = null;
            final ParseLogHandler log = SkriptLogger.startParseLogHandler();
            try {
                for (final Class<?> r : rs) {
                    log.clear();
                    if ((r.isArray() ? r.getComponentType() : r).isAssignableFrom(ch.getReturnType())) {
                        v = ch.getConvertedExpression(Object.class);
                        break; // break even if v == null as it won't convert to Object apparently
                    }
                }
                if (v == null)
                    v = ch.getConvertedExpression((Class<Object>[]) rs2);
                if (v == null) {
                    if (log.hasError()) {
                        log.printError();
                        parsing = false;
                        return false;
                    }
                    log.clear();
                    log.printLog();
                    final Class<?>[] r = new Class[rs.length];
                    for (int i = 0; i < rs.length; i++)
                        r[i] = rs[i].isArray() ? rs[i].getComponentType() : rs[i];
                    if (rs.length == 1 && rs[0] == Object.class)
                        Skript.error("Can't understand this expression: " + changer, ErrorQuality.NOT_AN_EXPRESSION);
                    else if (mode == ChangeMode.SET)
                        Skript.error(what + " can't be set to " + changer + " because the latter is " + SkriptParser.notOfType(r), ErrorQuality.SEMANTIC_ERROR);
                    else
                        Skript.error(changer + " can't be " + (mode == ChangeMode.ADD ? "added to" : "removed from") + " " + what + " because the former is " + SkriptParser.notOfType(r), ErrorQuality.SEMANTIC_ERROR);
                    parsing = false;
                    return false;
                }
                log.printLog();
            } finally {
                log.stop();
            }

            Class<?> x = Utils.getSuperType(rs2);
            single = allSingle;
            for (int i = 0; i < rs.length; i++) {
                if (rs2[i].isAssignableFrom(v.getReturnType())) {
                    single = !rs[i].isArray();
                    x = rs2[i];
                    break;
                }
            }
            assert x != null;
            changer = ch = v;

            if (!ch.isSingle() && single) {
                if (mode == ChangeMode.SET)
                    Skript.error(changed + " can only be set to one " + Classes.getSuperClassInfo(x).getName() + ", not more", ErrorQuality.SEMANTIC_ERROR);
                else
                    Skript.error("only one " + Classes.getSuperClassInfo(x).getName() + " can be " + (mode == ChangeMode.ADD ? "added to" : "removed from") + " " + changed + ", not more", ErrorQuality.SEMANTIC_ERROR);
                parsing = false;
                return false;
            }

            if (changed instanceof Variable && !((Variable<?>) changed).isLocal() && (mode == ChangeMode.SET || ((Variable<?>) changed).isList() && mode == ChangeMode.ADD)) {
                final ClassInfo<?> ci = Classes.getSuperClassInfo(ch.getReturnType());
                if (ci.getC() != Object.class && ci.getSerializer() == null && ci.getSerializeAs() == null && !SkriptConfig.disableObjectCannotBeSavedWarnings.value())
                    Skript.warning(ci.getName().withIndefiniteArticle() + " cannot be saved, i.e. the contents of the variable " + changed + " will be lost when the server stops.");
            }
        }
        parsing = false;
        return true;
    }

    @Override
    public void execute(final @NotNull Event e) {
        final Expression<?> changer = this.changer;
        final Object[] delta = changer == null ? null : changer.getArray(e);
        final Bot bot = EasyElement.parseSingle(this.bot, e, null);
        if (delta != null && delta.length == 0) {
            return;
        }
        if (bot == null) {
            DiSky.getErrorHandler().exception(e, "DiSky tried to change \"" + changed.toString(e, false) + "\", but the bot wasn't found.");
            return;
        }

        try {
            currentBot = bot;
            try {
                ((DiSkyChangerElement) changed).change(e, delta, bot, mode);
            } catch (ClassCastException ex) {
                changed.change(e, delta, mode);
            }
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
        } finally {
            currentBot = null;
        }
    }

    @Override
    public @NotNull String toString(final @Nullable Event e, final boolean debug) {
        final Expression<?> changer = this.changer;
        switch (mode) {
            case ADD:
                assert changer != null;
                return "add " + changer.toString(e, debug) + " to " + changed.toString(e, debug) + " with " + bot.toString(e, debug);
            case SET:
                assert changer != null;
                return "set " + changed.toString(e, debug) + " to " + changer.toString(e, debug) + " with " + bot.toString(e, debug);
            case REMOVE:
                assert changer != null;
                return "remove " + changer.toString(e, debug) + " from " + changed.toString(e, debug) + " with " + bot.toString(e, debug);
            case REMOVE_ALL:
                assert changer != null;
                return "remove all " + changer.toString(e, debug) + " from " + changed.toString(e, debug) + " with " + bot.toString(e, debug);
            case DELETE:
                return "delete/clear " + changed.toString(e, debug) + " with " + bot.toString(e, debug);
            case RESET:
                return "reset " + changed.toString(e, debug) + " with " + bot.toString(e, debug);
        }
        assert false;
        return "";
    }

}