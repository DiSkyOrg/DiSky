package info.itsthesky.disky.elements.sections.automod;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import info.itsthesky.disky.managers.wrappers.AutoModRuleBuilder;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AutomodResponse extends MultiplyPropertyExpression<AutoModRuleBuilder, AutoModResponse> {

	static {
		register(
				AutomodResponse.class,
				AutoModResponse.class,
				"(response|action)[s]",
				"automodrule"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!getParser().isCurrentSection(CreateAutoMod.class)) {
			Skript.error("You can only use the 'automod rule responses' expression inside a 'create auto mod rule' section");
			return false;
		}
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.ADD)
			return new Class[]{AutoModResponse.class, AutoModResponse[].class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final AutoModRuleBuilder builder = EasyElement.parseSingle(getExpr(), e, null);

		if (builder == null)
			return;

		final List<AutoModResponse> responses = new ArrayList<>();
		for (Object response : delta)
			responses.add((AutoModResponse) response);

		if (mode == Changer.ChangeMode.ADD) {
			builder.addResponses(responses.toArray(new AutoModResponse[0]));
		} else {
			Skript.warning("You can't remove a response from an automod rule!");
		}
	}

	@Override
	public @NotNull Class<? extends AutoModResponse> getReturnType() {
		return AutoModResponse.class;
	}

	@Override
	protected String getPropertyName() {
		return "attachments";
	}

	@Override
	protected AutoModResponse[] convert(AutoModRuleBuilder builder) {
		return new AutoModResponse[0];
	}

}
