package info.itsthesky.disky.experimental;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Case extends Section {

	static {
		Skript.registerSection(
				Case.class,
				"case %objects%",
				"default"
		);
	}

	private Expression<Object> exprValidations;
	private boolean isDefault;
	private Trigger code;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
		final TriggerSection parent = getParent();
		if (!(parent instanceof Switch)) {
			Skript.error("The 'case' element can only be used in a switch section.");
			return false;
		}
		((Switch) parent).linkCase(this);
		isDefault = matchedPattern == 1;
		if (!isDefault)
			exprValidations = (Expression<Object>) exprs[0];
		code = loadCode(sectionNode, "case section", SkriptUtils.addEventClasses());
		return true;
	}

	public boolean validate(Object value, Event e) {
		if (isDefault)
			return true;
		final Object[] verified = EasyElement.parseList(exprValidations, e, new Object[0]);
		return Arrays.asList(verified).contains(value);
	}

	public Trigger getCode() {
		return code;
	}

	@Override
	protected @Nullable TriggerItem walk(@NotNull Event e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "case " + exprValidations.toString(e, debug);
	}
}
