package info.itsthesky.disky.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.effects.EffReturn;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.ReflectionUtils;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FindMemberSection extends Section {

    static {
        Skript.registerSection(
                FindMemberSection.class,
                "find [the] [discord] member[s] (in|from) [guild] %guild% and store (them|the members) in %~objects% with filter var[iable] %~objects%"
        );

        try {
            ReflectionUtils.removeElement("ch.njol.skript.effects.EffReturn",
                    "expressions");
        } catch (Exception e) {
            Skript.error("DiSky were unable to remove the original 'return' effect, please report this error to the developer, with the following error.");
            e.printStackTrace();
        }

        Skript.registerEffect(
                EffReturn.class,
                "return %objects%"
        );
    }

    public static @Nullable FindMemberSection instance;

    private Expression<Guild> exprGuild;
    private Expression<Object> exprResult;
    private Expression<Object> exprValue;
    private Trigger trigger;

    private boolean iterationResult = false;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        exprGuild = (Expression<Guild>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];
        exprValue = (Expression<Object>) expressions[2];

        if (!Changer.ChangerUtils.acceptsChange(exprValue, Changer.ChangeMode.SET, Member.class)
                || !Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class))
            return false;

        AtomicBoolean delayed = new AtomicBoolean(false);
        Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());

        instance = this;
        trigger = loadCode(sectionNode, "find members",
                afterLoading, getParser().getCurrentEvents());
        instance = null;

        if (delayed.get()) {
            Skript.error("Delays can't be used within a 'find member' section.");
            return false;
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event e) {
        if (trigger == null)
            return null;

        final Guild guild = exprGuild.getSingle(e);
        if (guild == null)
            return getNext();

        debug(e, true);

        Delay.addDelayedEvent(e);
        Object localVars = Variables.removeLocals(e);

        Bukkit.getScheduler().runTaskAsynchronously(DiSky.getInstance(), () -> {
            if (localVars != null)
                Variables.setLocalVariables(e, localVars);

            try {

                List<Member> members = guild.findMembers(member -> {

                    iterationResult = false;
                    exprValue.change(e, new Member[] {member}, Changer.ChangeMode.SET);

                    TriggerItem.walk(trigger, e);
                    return iterationResult;
                }).get();

                exprResult.change(e, members.toArray(new Member[0]), Changer.ChangeMode.SET);
            } catch (Exception ex) {
                DiSky.getErrorHandler().exception(e, ex);
            }

            // Restarts the following code
            if (getNext() != null) {
                Bukkit.getScheduler().runTask(Skript.getInstance(), () -> {
                    Object timing = null;
                    if (SkriptTimings.enabled()) {
                        Trigger trigger = getTrigger();
                        if (trigger != null) {
                            timing = SkriptTimings.start(trigger.getDebugLabel());
                        }
                    }

                    TriggerItem.walk(getNext(), e);
                    Variables.removeLocals(e);
                    SkriptTimings.stop(timing);
                });
            } else {
                Variables.removeLocals(e);
            }
        });

        return null;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "find members in guild " + exprGuild.toString(event, debug) + " and store them in " + exprResult.toString(event, debug) + " with filter " + exprValue.toString(event, debug);
    }

    // ####################################################################################################

    public static class EffReturn extends Effect {

        @SuppressWarnings("NotNullFieldNotInitialized")
        private ScriptFunction<?> function;

        @SuppressWarnings("NotNullFieldNotInitialized")
        private Expression<?> value;
        private @Nullable FindMemberSection section;

        @SuppressWarnings("unchecked")
        @Override
        public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
            if (instance != null) {
                section = instance;
                value = exprs[0];
                return true;
            }

            ScriptFunction<?> f = Functions.currentFunction;
            if (f == null) {
                Skript.error("The return statement can only be used in a function");
                return false;
            }

            if (!isDelayed.isFalse()) {
                Skript.error("A return statement after a delay is useless, as the calling trigger will resume when the delay starts (and won't get any returned value)");
                return false;
            }

            function = f;
            ClassInfo<?> returnType = function.getReturnType();
            if (returnType == null) {
                Skript.error("This function doesn't return any value. Please use 'stop' or 'exit' if you want to stop the function.");
                return false;
            }

            RetainingLogHandler log = SkriptLogger.startRetainingLog();
            Expression<?> convertedExpr;
            try {
                convertedExpr = exprs[0].getConvertedExpression(returnType.getC());
                if (convertedExpr == null) {
                    log.printErrors("This function is declared to return " + returnType.getName().withIndefiniteArticle() + ", but " + exprs[0].toString(null, false) + " is not of that type.");
                    return false;
                }
                log.printLog();
            } finally {
                log.stop();
            }

            if (f.isSingle() && !convertedExpr.isSingle()) {
                Skript.error("This function is defined to only return a single " + returnType.toString() + ", but this return statement can return multiple values.");
                return false;
            }
            value = convertedExpr;

            return true;
        }

        @Override
        @Nullable
        @SuppressWarnings({"unchecked", "rawtypes"})
        protected TriggerItem walk(Event event) {
            debug(event, false);

            if (section != null) {
                try {
                    section.iterationResult = value.getSingle(event) != null && Boolean.TRUE.equals(value.getSingle(event));
                } catch (Exception e) {
                    Expression<?> converted = value.getConvertedExpression(Boolean.class);
                    if (converted == null) {
                        Skript.error("Cannot convert the value to a boolean: " + value.toString(event, false));
                        return null;
                    }

                    section.iterationResult = converted.getSingle(event) != null && Boolean.TRUE.equals(converted.getSingle(event));
                }
                return null;
            }

            if (event instanceof FunctionEvent) {
                ((ScriptFunction) function).setReturnValue(value.getArray(event));
            } else {
                assert false : event;
            }

            TriggerSection parent = getParent();
            while (parent != null) {
                if (parent instanceof LoopSection)
                    ((LoopSection) parent).exit(event);

                parent = parent.getParent();
            }

            return null;
        }

        @Override
        protected void execute(Event event) {
            assert false;
        }

        @Override
        public String toString(@Nullable Event event, boolean debug) {
            return "return " + value.toString(event, debug);
        }

    }
}
