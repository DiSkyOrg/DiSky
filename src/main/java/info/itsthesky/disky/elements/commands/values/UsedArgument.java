package info.itsthesky.disky.elements.commands.values;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.elements.commands.CommandEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Used Argument")
@Description({"Return the plain formatter of the discord command's argument",
        "You should however use (arg-1) for example which will return the argument value directly.",
        "This is intended to be for test purpose only, and therefore only return a String formatted containing every used arguments."})
public class UsedArgument extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
                UsedArgument.class,
                String.class,
                ExpressionType.SIMPLE,
                "[the] use[d]( |-)arg[ument][s]"
        );
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e) {
        return new String[] {((CommandEvent) e).getArguments()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "the used arguments";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(CommandEvent.class)) {
            Skript.error("The used arguments can only used in a discord command trigger section.");
            return false;
        }
        return true;
    }
}
