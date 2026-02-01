package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Retrieve Logs")
@Description("Retrieve the audit logs of a guild.")
@Examples("retrieve audit logs from event-guild and store it in {_logs::*}")
@Since("4.11.0")
public class RetrieveLogs extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveLogs.class,
                "retrieve [(all|every)] [audit] log[s] [entries] (from|with|of|in) %guild% [(with|using) [the] [bot] %-bot%] and store (them|the [audit] log[s] [entries]) in %~objects%"
        );
    }


    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprGuild = (Expression<Guild>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, AuditLogEntry[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        Guild guild = exprGuild.getSingle(event);
        Bot bot = Bot.fromContext(exprBot, event);
        if (guild == null || bot == null)
            return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null)
            return;

        final AuditLogEntry[] logs;
        try {
            logs = guild.retrieveAuditLogs().complete().toArray(new AuditLogEntry[0]);
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(event, logs, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "retrieve audit logs from guild " + exprGuild.toString(e, debug)
                + (exprBot == null ? "" : " with bot " + exprBot.toString(e, debug))
                + " and store them in " + exprResult.toString(e, debug);
    }
}
