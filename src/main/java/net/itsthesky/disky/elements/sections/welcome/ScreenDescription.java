package net.itsthesky.disky.elements.sections.welcome;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Welcome Screen Description")
@Description({"Change the description of the welcome screen.",
		"Can only be used in a 'modify welcome screen' section."})
@Examples("discord command setup <guild>:\n" +
		"    trigger:\n" +
		"        modify welcome screen of arg-1:\n" +
		"            change the screen description to \"Welcome to the server! Please read the rules and get roles before chatting.\"\n" +
		"            add channel with id \"937001799896956991\" named \"Read our rules\" with reaction \"\uD83D\uDCDC\" to the screen\n" +
		"            add channel with id \"952199041335316520\" named \"Get roles\" with reaction \"\uD83C\uDF9F️\" to the screen")
@Since("4.10.0")
public class ScreenDescription extends Effect implements ScreenElement {

	static {
		Skript.registerEffect(ScreenDescription.class,
				"change [the] [welcome] screen description to %string%",
				"change [the] description of [the] [welcome] screen to %string%");
	}

	private Expression<String> exprDescription;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprDescription = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	public @NotNull GuildWelcomeScreenManager apply(@NotNull GuildWelcomeScreenManager manager, @NotNull Event event) {
		final String description = exprDescription.getSingle(event);
		if (description == null)
			return manager;
		return manager.setDescription(description);
	}

	@Override
	protected void execute(@NotNull Event e) {
		throw new UnsupportedOperationException("This effect is not supposed to be 'walked'!");
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "change welcome screen description to " + exprDescription.toString(e, debug);
	}

}
