package info.itsthesky.disky.elements.components.properties;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropOptions extends MultiplyPropertyExpression<Object, Object> {

	static {
		register(
				PropOptions.class,
				Object.class,
				"option[s] [mapping[s]]",
				"slashcommand/dropdown"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.ADD))
			return new Class[] {SelectOption.class,
					OptionData.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] rawValues, Changer.@NotNull ChangeMode mode) {
		if (!mode.equals(Changer.ChangeMode.ADD))
			return;
		final Object entity = getExpr().getSingle(e);
		if (entity == null)
			return;

		if (entity instanceof SlashCommandData) {
			final OptionData[] options = (OptionData[]) rawValues;
			((SlashCommandData) entity).addOptions(options);
		} else {
			final SelectOption[] options = (SelectOption[]) rawValues;
			((SelectMenu.Builder) entity).addOptions(options);
		}
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "options";
	}

	@Override
	public @Nullable Object[] convert(Object entity) {
		if (entity instanceof SelectMenu.Builder)
			return ((SelectMenu.Builder) entity).getOptions().toArray(new SelectOption[0]);
		if (entity instanceof SlashCommandData)
			return ((SlashCommandData) entity).getOptions().toArray(new OptionData[0]);
		return new Object[0];
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return Object.class;
	}
}
