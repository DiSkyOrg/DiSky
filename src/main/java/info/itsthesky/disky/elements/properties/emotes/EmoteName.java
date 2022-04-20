package info.itsthesky.disky.elements.properties.emotes;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.changers.ChangeableSimplePropertyExpression;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Emote Name")
@Description({"Get the name of this emote.",
        "This, instead of 'discord name of %emote%' will return the name of an emote, and not an emoji.",
        "You can change this property to change the emote's name itself."
})
@Examples({"emote name of event-emote",
        "set emote name of reaction \"disky\" to \"disky2\" # Will now be 'reaction \"disky2\"' to get it back"})
public class EmoteName extends ChangeableSimplePropertyExpression<Emote, String> {

    static {
        register(
                EmoteName.class,
                String.class,
                "emo(te|ji) name",
                "emote"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "emote name";
    }

    @Override
    public @Nullable String convert(Emote emote) {
        return emote.isEmote() ? emote.getEmote().getName() : null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }


    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        final @Nullable Emote emote = getExpr().getSingle(e);
        if (emote == null || delta == null || delta.length == 0 || delta[0] == null)
            return;
        final @Nullable String newName = (String) delta[0];
        if (newName == null)
            throw new UnsupportedOperationException();
        if (!emote.isEmote())
            return;

        emote.getEmote().getManager().setName(newName).queue(null);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return new Class[] {String.class};
        return new Class[0];
    }
}
