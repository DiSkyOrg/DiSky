package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.Debug;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Name("Unregister Command")
@Description({"Unregister a specific slash command from local or global context of a bot.",
"You must provide the command's name. Keep in mind this **SHOULD NOT** be used!",
"The best way remains to update bot's commands without the command you want to delete!"})
@Examples("unregister command \"test\" locally in guild with id \"000\"")
public class UnregisterCommand extends SpecificBotEffect {

    static {
        Skript.registerEffect(UnregisterCommand.class,
                "unregister [the] [command[s]] %strings% [(1¦globally|2¦locally)] (in|from|of) [the] [(bot|guild)] %bot/guild%");
    }

    private Expression<String> exprNames;
    private boolean isGlobal;
    private Expression<Object> exprEntity;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        exprNames = (Expression<String>) expressions[0];
        exprEntity = (Expression<Object>) expressions[1];
        isGlobal = (parseResult.mark & 1) != 0;
        return true;
    }


    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final String[] names = parseList(exprNames, e, new String[0]);
        final Object entity = parseSingle(exprEntity, e, null);
        if (names.length == 0) {
            Debug.debug(this, Debug.Type.EMPTY_LIST, "No names found.");
            restart();
            return;
        }
        if (anyNull(this, entity)) {
            restart();
            return;
        }

        final @Nullable Guild guild = isGlobal ? null : (Guild) entity;

        final RestAction<List<Command>> restAction = isGlobal ? bot.getInstance().retrieveCommands() : guild.retrieveCommands();

        restAction.queue(commands -> {
            final List<Command> toDelete = commands.stream()
                    .filter(command -> Stream.of(names).anyMatch(name -> name.equalsIgnoreCase(command.getName())))
                    .collect(Collectors.toList());

            final RestAction<?> action = RestAction.allOf(toDelete.stream()
                    .map(Command::delete)
                    .collect(Collectors.toList()));

            action.queue(this::restart, ex -> {
                restart();
                DiSkyRuntimeHandler.error((Exception) ex);
            });
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "unregister commands " + exprNames.toString(e, debug) + " in " + (isGlobal ? "global" : "local") + " in " + exprEntity.toString(e, debug);
    }
}
