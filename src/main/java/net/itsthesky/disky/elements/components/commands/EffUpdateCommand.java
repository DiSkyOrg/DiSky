package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseList;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

public class EffUpdateCommand extends AsyncEffect {

    static {
        Skript.registerEffect(
                EffUpdateCommand.class,
                "(update|register) [the] [command[s]] %slashcommands% [(1¦globally|2¦locally)] in [the] [(bot|guild)] %bot/guild%"
        );
    }

    private boolean isGlobal;
    private Expression<SlashCommandData> exprCommands;
    private Expression<Object> exprEntity;

    @Override
    public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprCommands = (Expression<SlashCommandData>) expressions[0];
        exprEntity = (Expression<Object>) expressions[1];
        isGlobal = (parseResult.mark & 1) != 0;
        return true;
    }

    @Override
    public void execute(Event e) {
        final SlashCommandData[] commands = parseList(exprCommands, e, new SlashCommandData[0]);
        final Object entity = parseSingle(exprEntity, e, null);
        if (commands.length == 0) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("Cannot update 0 commands!"), getNode());
            return;
        }

        if (entity == null) {
            DiSkyRuntimeHandler.error(new NullPointerException("The bot/guild provided is null!"), getNode());
            return;
        }

        if (isGlobal && !(entity instanceof Bot)) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The entity must be a bot to update commands globally!"), getNode());
            return;
        }

        if (!isGlobal && !(entity instanceof Guild)) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The entity must be a guild to update commands locally!"), getNode());
            return;
        }

        final CommandListUpdateAction updateAction;
        if (isGlobal)
            updateAction = ((Bot) entity).getInstance().updateCommands();
        else
            updateAction = ((Guild) entity).updateCommands();

        try {
            updateAction.addCommands(commands).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, getNode());
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "update commands " + exprCommands.toString(e, debug) + " in " +
                exprEntity.toString(e, debug);
    }

}
