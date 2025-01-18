package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Sub-command Groups")
@Description({"Represent every sub-slash-command groups a slash command have.",
"You can add sub-slash-commands to a group, then add this group into the base slash command."})
public class ExprCommandGroup extends MultiplyPropertyExpression<SlashCommandData, SubcommandGroupData> {

	static {
		register(
				ExprCommandGroup.class,
				SubcommandGroupData.class,
				"sub[[( |-)]command[s]] group[s]",
				"slashcommand"
		);
	}

	@Override
	public @NotNull Class<? extends SubcommandGroupData> getReturnType() {
		return SubcommandGroupData.class;
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (delta == null)
			return;
		final SlashCommandData command = EasyElement.parseSingle(getExpr(), e, null);
		final SubcommandGroupData[] datas = (SubcommandGroupData[]) delta;
		if (EasyElement.anyNull(this, command, datas))
			return;

		final List<SubcommandGroupData> news = new ArrayList<>(Arrays.asList(datas));
		final List<SubcommandGroupData> current = new ArrayList<>(command.getSubcommandGroups());

		switch (mode) {
			case ADD:
				current.addAll(news);
				break;
			case SET:
				current.clear();
				current.addAll(news);
				break;
			case RESET:
			case REMOVE_ALL:
				current.clear();
				break;
		}

		try {
			command.addSubcommandGroups(current);
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
		}
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.isChangerMode(mode))
			return new Class[] {SubcommandGroupData[].class, SubcommandGroupData.class};
		return new Class[0];
	}

	@Override
	protected String getPropertyName() {
		return "groups";
	}

	@Override
	protected SubcommandGroupData[] convert(SlashCommandData data) {
		return data.getSubcommandGroups().toArray(new SubcommandGroupData[0]);
	}
}
