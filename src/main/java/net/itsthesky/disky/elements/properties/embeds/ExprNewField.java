package net.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.Node;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprNewField extends SimpleExpression<MessageEmbed.Field> {

    static {
        DiSkyRegistry.registerExpression(
                ExprNewField.class,
                MessageEmbed.Field.class,
                ExpressionType.COMBINED,
                "new [:inline] [embed] field (named|with name) %string% [and] with [the] value %string%"
        );
    }

    private Node node;

    private Expression<String> exprName;
    private Expression<String> exprDesc;
    private boolean isInline;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();

        exprName = (Expression<String>) expressions[0];
        exprDesc = (Expression<String>) expressions[1];
        isInline = parseResult.hasTag("inline");

        return true;
    }

    @Override
    protected MessageEmbed.Field @Nullable [] get(Event event) {
        final var name = exprName.getSingle(event);
        final var desc = exprDesc.getSingle(event);
        if (name == null || desc == null) return null;

        if (name.length() > MessageEmbed.TITLE_MAX_LENGTH) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The title of a field cannot be bigger than 256 characters. The one you're trying to set is '"+name.length()+"' length!"), node);
            return new MessageEmbed.Field[0];
        }
        if (desc.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The value of a field cannot be bigger than 1024 characters. The one you're trying to set is '"+desc.length()+"' length!"), node);
            return new MessageEmbed.Field[0];
        }

        return new MessageEmbed.Field[]{new MessageEmbed.Field(name, desc, isInline)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MessageEmbed.Field> getReturnType() {
        return MessageEmbed.Field.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new field with named " + exprName.toString(event, debug) + " with description " + exprDesc.toString(event, debug);
    }
}
