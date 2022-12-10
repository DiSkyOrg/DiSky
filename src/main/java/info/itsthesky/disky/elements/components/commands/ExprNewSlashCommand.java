package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.stream.Stream;

public class ExprNewSlashCommand extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(
				ExprNewSlashCommand.class,
				Object.class,
				ExpressionType.COMBINED,
				"[a] [new] [nsfw] slash[( |-)]command [with] [(the name|named)] %string% [and] with [the] desc[ription] %string%",
				"[a] [new] sub [slash][( |-)]command [with] [(the name|named)] %string% [and] with [the] desc[ription] %string%",
				"[a] [new] [slash][( |-)][command] group [with] [(the name|named)] %string% [and] with [the] desc[ription] %string%"
		);
	}

	enum Type {
		SLASH_COMMAND(0, SlashCommandData.class),
		SUB_COMMAND(1, SubcommandData.class),
		SUB_GROUP(2, SubcommandGroupData.class),
		;

		private final int pattern;
		private final Class<?> clazz;
		Type(int pattern, Class<?> clazz) {
			this.pattern = pattern;
			this.clazz = clazz;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public static Type fromPattern(int matchedPattern) {
			return Stream
					.of(values())
					.filter(v -> v.pattern == matchedPattern)
					.findAny()
					.orElse(null);
		}

		@Override
		public String toString() {
			return name().toLowerCase(Locale.ROOT).replace("_", " ");
		}
	}

	private Expression<String> exprName;
	private Expression<String> exprDesc;
	private Type type;
	private boolean isNSFW;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		exprDesc = (Expression<String>) exprs[1];
		type = Type.fromPattern(matchedPattern);
		return true;
	}

	@Override
	protected Object @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e, null);
		final String desc = EasyElement.parseSingle(exprDesc, e, null);
		if (EasyElement.anyNull(name, desc))
			return new Object[0];
		if (type == Type.SUB_GROUP)
			return new SubcommandGroupData[] {new SubcommandGroupData(name, desc)};
		else if (type == Type.SLASH_COMMAND)
			return new SlashCommandData[] {Commands.slash(name, desc)
					.setNSFW(isNSFW)};
		else return new SubcommandData[] {new SubcommandData(name, desc)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return type.getClazz();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new "+ type.toString() +" named " + exprName.toString(e, debug) + " with description " + exprDesc.toString(e, debug);
	}

}
