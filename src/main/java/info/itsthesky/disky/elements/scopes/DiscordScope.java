package info.itsthesky.disky.elements.scopes;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import info.itsthesky.disky.api.skript.BaseBukkitEvent;
import info.itsthesky.disky.api.skript.BaseScope;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiscordScope extends BaseScope<CommandData> {

	static {
		Skript.registerEvent("Discord Command", DiscordScope.class, DiscordCommandEvent.class, "discord command <([^\\s]+)( .+)?$>")
				.description("Custom DiSky discord command system. Arguments works like the normal skript's one and accept both optional and require arguments.")
				.examples("discord command move <member> <voicechannel>:\n" +
						"\tprefixes: !\n" +
						"\ttrigger:\n" +
						"\t\treply with mention tag of arg-2\n" +
						"\t\tmove arg-1 to arg-2")
				.since("3.0");
	}

	private String command;
	private String arguments;

	@Override
	public boolean init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull SkriptParser.ParseResult parser) {
		command = parser.regexes.get(0).group(1);
		arguments = parser.regexes.get(0).group(2);
		return super.init(args, matchedPattern, parser);
	}

	@Override
	public @Nullable CommandData parse(@NotNull SectionNode node) {
		return null;
	}

	@Override
	public boolean validate(@Nullable CommandData parsedEntity) {
		return false;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return null;
	}

	public static class DiscordCommandEvent extends BaseBukkitEvent { }
}
