package net.itsthesky.disky.elements.sections;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Find Members")
@Description({"Find member filter! It's a section that lets you filter by yourself the members you want to retrieve.", "Wants only members with a role, or specific nickname, that are not connected to a channel? You've got it!"})
@Examples({"find members in event-guild and store them in {_members::*} with filter var {_m}:", "\t# {_m} now contains the member to apply the filter to. For instance:", "\t{_m} has discord role with id \"XXX\"", "\t{_m} is muted", "\treturn true", "reply with \"I have found %size of {_members::*}% that has the role and is muted!\""})
@Since("4.14.3")
@SeeAlso({Member.class, Guild.class})
public class FindMemberSection extends Section implements ReturnHandler<Boolean> {

    static {
        Skript.registerSection(FindMemberSection.class, "find [the] [discord] member[s] (in|from) [guild] %guild% and store (them|the members) in %~objects% with filter var[iable] %~objects%");
    }

    private Expression<Guild> exprGuild;
    private Expression<Object> exprResult;
    private Expression<Object> exprValue;

    private ReturnableTrigger<Boolean> trigger;
    private boolean iterationResult = false;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        exprGuild = (Expression<Guild>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];
        exprValue = (Expression<Object>) expressions[2];

        if (!Changer.ChangerUtils.acceptsChange(exprValue, Changer.ChangeMode.SET, Member.class) || !Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class))
            return false;

        trigger = loadReturnableSectionCode(sectionNode, "find members", getParser().getCurrentEvents());

        // TODO: Be sure we don't have any delay within the section
//        if (delayed.get()) {
//            Skript.error("Delays can't be used within a 'find member' section.");
//            return false;
//        }

        return true;
    }

    @Override
    public void returnValues(Event event, Expression<? extends Boolean> value) {
        Boolean returned = value.getSingle(event);
        iterationResult = returned != null && returned;
    }

    @Override
    public boolean isSingleReturnValue() {
        return true;
    }

    @Override
    public @Nullable Class<? extends Boolean> returnValueType() {
        return Boolean.class;
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event e) {
        if (trigger == null) return null;

        final Guild guild = exprGuild.getSingle(e);
        if (guild == null) return getNext();

        debug(e, true);

        Delay.addDelayedEvent(e);
        Object localVars = Variables.removeLocals(e);

        Bukkit.getScheduler().runTaskAsynchronously(DiSky.getInstance(), () -> {
            if (localVars != null) Variables.setLocalVariables(e, localVars);

            try {
                List<Member> members = guild.findMembers(member -> {
                    iterationResult = false;
                    exprValue.change(e, new Member[]{member}, Changer.ChangeMode.SET);
                    trigger.execute(e);
                    return iterationResult;
                }).get();

                exprResult.change(e, members.toArray(new Member[0]), Changer.ChangeMode.SET);
            } catch (Exception ex) {
                DiSkyRuntimeHandler.error((Exception) ex);
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
}
