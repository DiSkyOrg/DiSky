package info.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("New Button")
@Description({"Create a new button with an ID and some optional options. It can be either enabled or disabled, and either link or action. If the button is a link-type, then the ID will be the URL that the user will be redirect to."})
@Examples("set {_btn} to new enabled danger button with id \"button-id\" named \"Hello world :p\"")
public class ExprNewButton extends SimpleExpression<Button> {
    
    static {
        Skript.registerExpression(ExprNewButton.class, Button.class, ExpressionType.SIMPLE,
                "[a] new [(enabled|disabled)] %buttonstyle% [link] button [with (id|url)] %string% [(named|with label) %-string%][,] [with [emoji] %-emote%]");
    }

    private Expression<String> exprIdOrURL;
    private Expression<ButtonStyle> exprStyle;
    private Expression<String> exprContent;
    private Expression<Emote> exprEmoji;
    private boolean isEnabled;
    private boolean isLink;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprStyle = (Expression<ButtonStyle>) exprs[0];
        exprIdOrURL = (Expression<String>) exprs[1];
        exprContent = (Expression<String>) exprs[2];
        exprEmoji = (Expression<Emote>) exprs[3];
        isEnabled = !parseResult.expr.contains("new disabled");
        isLink = parseResult.expr.contains("new link") ||
                parseResult.expr.contains("link button");
        return true;
    }

    @Override
    protected Button @NotNull [] get(@NotNull Event e) {
        String idOrURL = exprIdOrURL.getSingle(e);
        ButtonStyle style = exprStyle.getSingle(e);

        String content = exprContent == null ? null : exprContent.getSingle(e);
        Emote emoji = exprEmoji == null ? null : exprEmoji.getSingle(e);

        if (style == null || idOrURL == null) return new Button[0];
        if (emoji == null && content == null) return new Button[0];

        if (isLink)
            style = ButtonStyle.LINK;

        Button button = (content == null ? Button.of(style, idOrURL, "") : Button.of(style, idOrURL, content));

        if (emoji != null && content != null)
            button = Button.of(style, idOrURL, content).withEmoji(emoji.getEmoji());

        if (!isEnabled)
            button = button.asDisabled();

        return new Button[] {button};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Button> getReturnType() {
        return Button.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "new button with id or url " + exprIdOrURL.toString(e, debug);
    }
}