package info.itsthesky.disky.elements.properties;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("User Locale")
@Description({"Get the language code defined as user-side client of Discord.",
"Basically, return the language user's client is loaded in.",
"This expression only works in interactions event, and cannot be used outside of them."})
@Examples("the user locale")
public class UserLocal extends SimpleExpression<String> {

	static {
		Skript.registerExpression(
				UserLocal.class,
				String.class,
				ExpressionType.COMBINED,
				"[the] user['s] local[e] [(code|language)]"
		);
	}

	@Override
	protected String @NotNull [] get(@NotNull Event e) {
		final GenericInteractionCreateEvent event = ((InteractionEvent) e).getInteractionEvent();
		return new String[] {event.getUserLocale().toString()};
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
		return "the user locale language";
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		if (!EasyElement.containsInterfaces(InteractionEvent.class)) {
			Skript.error("The 'user locale' expression can only be used in interaction events.");
			return false;
		}
		return true;
	}
}
