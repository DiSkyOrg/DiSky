package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Manage Command Permissions")
@Description({"This effect allows you to manage the permissions of slash commands, with the following rules:",
" - By default, the command is marked as ENABLED, and anyone can see & use it.",
" - You can DISABLE completely the command (first pattern), only admins will be able to use it.",
" - Or you can ENABLE the commands for specific PERMISSIONS (second pattern)."})
@Examples({
		"disable command{_cmd1} # disable the command for everyone, except the admins.",
		"enable command {_cmd2} for manage server # enable the command only for the users who have the 'manage server' permission."
})
public class EffEnableDisableCommand extends SpecificBotEffect {

	static {
		Skript.registerEffect(
				EffEnableDisableCommand.class,
				"disable [the] [command] %slashcommand%",
				"enable [the] [command] %slashcommands% (for|to) [the] [permissions] %permissions%"
		);
	}

	private Expression<SlashCommandData> exprCommand;
	private Expression<Permission> exprPermissions;
	private boolean isDisable;

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final SlashCommandData commandData = parseSingle(exprCommand, e, null);
		final Permission[] permissions = parseList(exprPermissions, e, new Permission[0]);
		if (commandData == null || (!isDisable && permissions.length == 0)) {
			restart();
			return;
		}

		if (isDisable)
			commandData.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
		else
			commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions));
		restart();
	}

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		isDisable = i == 0;
		exprCommand = (Expression<SlashCommandData>) expressions[0];
		if (!isDisable)
			exprPermissions = (Expression<Permission>) expressions[1];
		return true;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return isDisable ? "disable the command " + exprCommand.toString(e, debug) : "enable the command " + exprCommand.toString(e, debug) + " for the permissions " + exprPermissions.toString(e, debug);
	}
}
