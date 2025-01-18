package net.itsthesky.disky.elements.sections.automod;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.managers.wrappers.AutoModRuleBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateRule extends SpecificBotEffect {

	static {
		Skript.registerEffect(
				CreateRule.class,
				"create [a] [new] [automod] rule %automodrule% (in|to) [the] [guild] %guild%"
		);
	}

	private Expression<AutoModRuleBuilder> exprRule;
	private Expression<Guild> exprGuild;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		exprRule = (Expression<AutoModRuleBuilder>) expressions[0];
		exprGuild = (Expression<Guild>) expressions[1];
		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final AutoModRuleBuilder rule = parseSingle(exprRule, e);
		final Guild guild = parseSingle(exprGuild, e);

		if (anyNull(this, rule, guild)) {
			restart();
			return;
		}

		guild.createAutoModRule(rule.build()).queue(modrule -> restart());
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new automod rule " + exprRule.toString(e, debug) + " in guild " + exprGuild.toString(e, debug);
	}

}
