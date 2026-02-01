package net.itsthesky.disky.elements.components.properties;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Name("Command Localization")
@Description({"Represents the localization of the name or the description of a slash/sub command.",
"You can add **Locale Data** (check for expression) to them.",
"Basically, the command's name & description will be according to the client's language code.",
"Documentation: https://docs.disky.me/advanced-stuff/slash-commands#using-localizations-v4.3.0+"})
public class PropLocalization extends MultiplyPropertyExpression<Object, PropLocalization.LocaleData> {

	static {
		register(
				PropLocalization.class,
				PropLocalization.LocaleData.class,
				"(name|description)['s] (localization[s]|locale[s])",
				"slashcommand/subslashcommand"
		);
	}

	private boolean isName;

	@Override
	public boolean init(Expression<?> @NotNull [] expr, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		this.expr = expr[0];
		isName = parseResult.expr.startsWith("name");
		return true;
	}

	@Override
	protected LocaleData[] convert(Object rawData) {
		if (rawData instanceof SlashCommandData)
			return LocaleData.convert(
					isName ? ((SlashCommandData) rawData).getNameLocalizations() : ((SlashCommandData) rawData).getDescriptionLocalizations()
			);
		else
			return LocaleData.convert(
					isName ? ((SubcommandData) rawData).getNameLocalizations() : ((SubcommandData) rawData).getDescriptionLocalizations()
			);
	}

	@Override
	public @Nullable Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.isChangerMode(mode))
			return new Class[] {LocaleData.class, LocaleData[].class};
		return new Class[0];
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;
		final Object rawData = EasyElement.parseSingle(getExpr(), e, null);
		if (rawData == null)
			return;

		final LocaleData[] locales = (LocaleData[]) delta;

		for (LocaleData locale : locales) {
			if (locale == null) return;

			if (isName)

				if (rawData instanceof SlashCommandData)
					((SlashCommandData) rawData).setNameLocalization(locale.getLocale(), locale.getValue());
				else if (rawData instanceof SubcommandData)
					((SubcommandData) rawData).setNameLocalization(locale.getLocale(), locale.getValue());
				else
					Skript.error("Unknown type of data: " + rawData.getClass().getName());

			else

				if (rawData instanceof SlashCommandData)
					((SlashCommandData) rawData).setDescriptionLocalization(locale.getLocale(), locale.getValue());
				else if (rawData instanceof SubcommandData)
					((SubcommandData) rawData).setDescriptionLocalization(locale.getLocale(), locale.getValue());
				else
					Skript.error("Unknown type of data: " + rawData.getClass().getName());

		}
	}

	@Override
	public @NotNull Class<? extends LocaleData> getReturnType() {
		return LocaleData.class;
	}

	@Override
	protected String getPropertyName() {
		return "localization";
	}

	public final static class LocaleData {

		private final DiscordLocale locale;
		private final String value;

		public LocaleData(DiscordLocale locale, String value) {
			this.locale = locale;
			this.value = value;
		}

		public static LocaleData[] convert(LocalizationMap localizationMap) {
			final Map<DiscordLocale, String> map = localizationMap.toMap();
			final LocaleData[] locales = new LocaleData[map.size()];
			int i = 0;
			for (DiscordLocale locale : map.keySet())
				locales[i++] = new LocaleData(locale, map.get(locale));
			return locales;
		}

		public DiscordLocale getLocale() {
			return locale;
		}

		public String getValue() {
			return value;
		}
	}

}
