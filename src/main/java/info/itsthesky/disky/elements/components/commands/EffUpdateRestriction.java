package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;


@Name("Update Command Restriction")
@Description({"Update restrictions of a specific slash command.",
"Here, the input is the **NAME** of the command, and not the command itself.",
"You can add enabled/disabled role/user restriction, see 'new command restriction' for more information about that."})
@Examples("update restriction of \"command_name\" in event-guild with new disabled user restriction with id \"user_id\"")
public class EffUpdateRestriction extends WaiterEffect {

    static {
        /* Skript.registerEffect(
                EffUpdateRestriction.class,
                "update [the] restriction[s] of [command] %string% (in|of) [the] [guild] %guild% (as|with) %restrictions%"
        ); */
    }

    private Expression<String> exprCommand;
    private Expression<Guild> exprTarget;
    private Expression<CommandPrivilege> exprPrivileges;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprCommand = (Expression<String>) expressions[0];
        exprTarget = (Expression<Guild>) expressions[1];
        exprPrivileges = (Expression<CommandPrivilege>) expressions[2];
        return true;
    }

    @Override
    public void runEffect(Event e) {
        final String command = parseSingle(exprCommand, e, null);
        final Guild target = parseSingle(exprTarget, e, null);
        final CommandPrivilege[] privileges = parseList(exprPrivileges, e, new CommandPrivilege[0]);
        if (anyNull(command, target) || privileges.length <= 0) {
            restart();
            return;
        }

        target.retrieveCommands().queue(commands -> {
            for (Command slash : commands) {
                if (!slash.getName().equalsIgnoreCase(command))
                    continue;
                if (slash.isDefaultEnabled())
                    slash.editCommand().setDefaultEnabled(false).queue(v -> {
                        target.updateCommandPrivilegesById(slash.getId(), privileges).queue(this::restart, ex -> {
                            exception(e, ex);
                            restart();
                        });
                    }, ex -> {
                        exception(e, ex);
                        restart();
                    });
                else
                    target.updateCommandPrivilegesById(slash.getId(), privileges).queue(this::restart, ex -> {
                        exception(e, ex);
                        restart();
                    });
            }
        }, ex -> {
            exception(e, ex);
            restart();
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "update command restriction of " + exprCommand.toString(e, debug) + " in guild " + exprTarget.toString(e, debug) + " with restrictions " + exprPrivileges.toString(e, debug);
    }
}
