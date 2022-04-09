package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New Command Restriction")
@Description({"Create a new slash command restriction, that will allow or disallow a role or a user to use the specified command."})
@Examples({"update restriction of \"command_id\" in event-guild with new disabled user restriction with id \"user_id\"",
"update restriction of \"command_id\" in event-guild with new enabled role restriction with id \"role_id\""})
public class ExprNewRestriction extends SimpleExpression<CommandPrivilege> {

    static {
        Skript.registerExpression(
                ExprNewRestriction.class,
                CommandPrivilege.class,
                ExpressionType.COMBINED,
                "[a] [new] (1¦enabled|2¦disabled) user [slash] [command] restriction with [the] id %string%",
                "[a] [new] (1¦enabled|2¦disabled) role [slash] [command] restriction with [the] id %string%"
        );
    }

    private boolean isRole;
    private boolean enabled;
    private Expression<String> exprId;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprId = (Expression<String>) exprs[0];
        isRole = matchedPattern == 1;
        enabled = (parseResult.mark & 1) != 0;
        return true;
    }

    @Override
    protected CommandPrivilege @NotNull [] get(@NotNull Event e) {
        final String id = EasyElement.parseSingle(exprId, e, null);
        if (id == null)
            return new CommandPrivilege[0];
        return new CommandPrivilege[] {
                new CommandPrivilege(isRole ? CommandPrivilege.Type.ROLE : CommandPrivilege.Type.USER,
                        enabled, MiscUtil.parseSnowflake(id))
        };
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends CommandPrivilege> getReturnType() {
        return CommandPrivilege.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "new " + (isRole ? "role" : "user") + " restriction with id " + exprId.toString(e, debug);
    }
}
