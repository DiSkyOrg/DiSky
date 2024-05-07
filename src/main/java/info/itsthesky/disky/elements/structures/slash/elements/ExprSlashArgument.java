package info.itsthesky.disky.elements.structures.slash.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import info.itsthesky.disky.elements.structures.slash.StructSlashCommand;
import info.itsthesky.disky.elements.structures.slash.models.ParsedArgument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprSlashArgument extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprSlashArgument.class, Object.class, ExpressionType.SIMPLE,
                "[][the] last arg[ument][s]",
                "[][the] arg[ument][s](-| )<(\\d+)>", "[][the] <(\\d*1)st|(\\d*2)nd|(\\d*3)rd|(\\d*[4-90])th> arg[ument][s]",
                "[][the] arg[ument][s]",
                "[][the] %*classinfo%( |-)arg[ument][( |-)<\\d+>]", "[][the] arg[ument]( |-)%*classinfo%[( |-)<\\d+>]");
    }

    @SuppressWarnings("null")
    private ParsedArgument arg;

    @Override
    public boolean init(final Expression<?> @NotNull [] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parser) {
        if (!getParser().isCurrentStructure(StructSlashCommand.class))
            return false;

        List<ParsedArgument> currentArguments = ((StructSlashCommand) getParser().getCurrentStructure())
                .getParsedCommand().getArguments();

        if (currentArguments.isEmpty()) {
            Skript.error("This command doesn't have any arguments", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }

        ParsedArgument arg = null;
        switch (matchedPattern) {
            case 0:
                arg = currentArguments.get(currentArguments.size() - 1);
                break;
            case 1:
            case 2:
                @SuppressWarnings("null") final int i = Utils.parseInt(parser.regexes.get(0).group(1));
                if (i > currentArguments.size()) {
                    Skript.error("The command doesn't have a " + StringUtils.fancyOrderNumber(i) + " argument", ErrorQuality.SEMANTIC_ERROR);
                    return false;
                }
                arg = currentArguments.get(i - 1);
                break;
            case 3:
                if (currentArguments.size() == 1) {
                    arg = currentArguments.get(0);
                } else {
                    Skript.error("'argument(s)' cannot be used if the command has multiple arguments. Use 'argument 1', 'argument 2', etc. instead", ErrorQuality.SEMANTIC_ERROR);
                    return false;
                }
                break;
            case 4:
            case 5:
                @SuppressWarnings("unchecked") final ClassInfo<?> c = ((Literal<ClassInfo<?>>) exprs[0]).getSingle();
                @SuppressWarnings("null") final int num = parser.regexes.size() > 0 ? Utils.parseInt(parser.regexes.get(0).group()) : -1;
                int j = 1;
                for (final ParsedArgument a : currentArguments) {
                    if (!c.getC().isAssignableFrom(a.getTypeInfo().getC()))
                        continue;
                    if (arg != null) {
                        Skript.error("There are multiple " + c + " arguments in this command", ErrorQuality.SEMANTIC_ERROR);
                        return false;
                    }
                    if (j < num) {
                        j++;
                        continue;
                    }
                    arg = a;
                    if (j == num)
                        break;
                }
                if (arg == null) {
                    j--;
                    if (num == -1 || j == 0)
                        Skript.error("There is no " + c + " argument in this command", ErrorQuality.SEMANTIC_ERROR);
                    else if (j == 1)
                        Skript.error("There is only one " + c + " argument in this command", ErrorQuality.SEMANTIC_ERROR);
                    else
                        Skript.error("There are only " + j + " " + c + " arguments in this command", ErrorQuality.SEMANTIC_ERROR);
                    return false;
                }
                break;
            default:
                assert false : matchedPattern;
                return false;
        }
        assert arg != null;
        this.arg = arg;
        return true;
    }

    @Override
    protected Object @NotNull [] get(final @NotNull Event e) {
        final Object value = arg.getValue();
        if (value == null)
            return new Object[0];

        return new Object[] {value};
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return arg.getTypeInfo().getC();
    }

    @Override
    public @NotNull String toString(final @Nullable Event e, final boolean debug) {
        if (e == null)
            return "the " + StringUtils.fancyOrderNumber(1) + " argument";
        return Classes.getDebugMessage(getArray(e));
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isLoopOf(final String s) {
        return s.equalsIgnoreCase("argument");
    }

}
