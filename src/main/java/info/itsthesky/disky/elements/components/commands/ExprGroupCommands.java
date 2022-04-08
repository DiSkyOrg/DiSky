package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Group / Command Sub-Commands")
@Description({"Represent every sub-slash-command a slash-command or a group have.",
"You can add sub-slash-commands to a group or a core slash-command, then add this group into the base slash command."})
public class ExprGroupCommands extends MultiplyPropertyExpression<Object, SubcommandData> {

	static {
		register(
				ExprGroupCommands.class,
				SubcommandData.class,
				"sub[( |-)]command[s]",
				"slashcommandgroup/slashcommand"
		);
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		if (delta == null)
			return;
		final Object group = EasyElement.parseSingle(getExpr(), e, null);
		final SubcommandData[] datas = (SubcommandData[]) delta;
		if (EasyElement.anyNull(group, datas))
			return;

		final List<SubcommandData> news = new ArrayList<>(Arrays.asList(datas));
		final List<SubcommandData> current = new ArrayList<>(Arrays.asList(convert(group)));

		if(!current.isEmpty())
			current.clear(); //the whole thing iterates twice, this fixes the bug where one subcommand tries to register twice

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
			if (group instanceof SubcommandGroupData)
				((SubcommandGroupData) group).addSubcommands(current);
			else
				((SlashCommandData) group).addSubcommands(current); //.getSubcommands method returns an empty array, so can't check it here
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
		}
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (EasyElement.isChangerMode(mode))
			return new Class[] {SubcommandData[].class, SubcommandData.class};
		return new Class[0];
	}

	@Override
	public @NotNull Class<? extends SubcommandData> getReturnType() {
		return SubcommandData.class;
	}

	@Override
	protected String getPropertyName() {
		return "sub commands";
	}

	@Override
	protected SubcommandData[] convert(Object data) {
		if (data instanceof SubcommandGroupData)
			return ((SubcommandGroupData) data).getSubcommands().toArray(new SubcommandData[0]);
		return ((SlashCommandData) data).getSubcommands().toArray(new SubcommandData[0]);
	}
}
