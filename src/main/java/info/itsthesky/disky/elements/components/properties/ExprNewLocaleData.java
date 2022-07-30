package info.itsthesky.disky.elements.components.properties;

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
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New Locale Data")
@Description({"Returns the a new locale data for the given locale and the given value.",
"You have to provide the locale using its code (list can be found here: https://discord.com/developers/docs/reference#locales) and the value to set.",
"Documentation: https://docs.disky.me/advanced-stuff/slash-commands#using-localizations-v4.3.0+"})
@Examples("new locale data for \"FR\" as \"niveau\"")
public class ExprNewLocaleData extends SimpleExpression<PropLocalization.LocaleData> {

	static {
		Skript.registerExpression(
				ExprNewLocaleData.class,
				PropLocalization.LocaleData.class,
				ExpressionType.COMBINED,
				"new local[e] [data] for %string% (as|with [value]) %string%"
		);
	}

	private Expression<String> exprLocale;
	private Expression<String> exprValue;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprLocale = (Expression<String>) exprs[0];
		exprValue = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected PropLocalization.LocaleData @NotNull [] get(@NotNull Event e) {
		final String locale = EasyElement.parseSingle(exprLocale, e, null);
		final String value = EasyElement.parseSingle(exprValue, e, null);
		if (EasyElement.anyNull(locale, value))
			return new PropLocalization.LocaleData[0];

		final DiscordLocale discordLocale = DiscordLocale.from(locale);
		if (discordLocale == DiscordLocale.UNKNOWN) {
			Skript.error("Unknown locale: " + locale);
			Skript.error("You can find a list of Discord Locales, with their code, here: https://discord.com/developers/docs/reference#locale");
			return new PropLocalization.LocaleData[0];
		}

		return new PropLocalization.LocaleData[] {new PropLocalization.LocaleData(discordLocale, value)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends PropLocalization.LocaleData> getReturnType() {
		return PropLocalization.LocaleData.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new locale data for " + exprLocale.toString(e, debug) + " with value " + exprValue.toString(e, debug);
	}
}
