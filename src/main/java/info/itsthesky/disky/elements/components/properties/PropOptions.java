package info.itsthesky.disky.elements.components.properties;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.data.SerializableData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropOptions extends MultiplyPropertyExpression<Object, Object> {

	static {
		register(
				PropOptions.class,
				Object.class,
				"option[s] [mapping[s]]",
				"slashcommand/subslashcommand/dropdown"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.ADD))
			return new Class[] {OptionData.class, SelectOption.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] rawValues, @NotNull Changer.ChangeMode mode) {
		if (!mode.equals(Changer.ChangeMode.ADD))
			return;
		final Object entity = getExpr().getSingle(e);
		if (entity == null)
			return;

		if (entity instanceof SlashCommandData) {
			for (Object value : rawValues)
				((SlashCommandData) entity).addOptions((OptionData) value);
		} if (entity instanceof SubcommandData) {
			for (Object value : rawValues)
				((SubcommandData) entity).addOptions((OptionData) value);
		} else if (entity instanceof StringSelectMenu.Builder) {
			for (Object value : rawValues)
				((StringSelectMenu.Builder) entity).addOptions((SelectOption) value);
		}
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "options";
	}

	@Override
	public @Nullable Object[] convert(Object entity) {
		if (entity instanceof StringSelectMenu.Builder)
			return ((StringSelectMenu.Builder) entity).getOptions().toArray(new SelectOption[0]);
		if (entity instanceof SlashCommandData)
			return ((SlashCommandData) entity).getOptions().toArray(new OptionData[0]);
		return new Object[0];
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return Object.class;
	}
}
