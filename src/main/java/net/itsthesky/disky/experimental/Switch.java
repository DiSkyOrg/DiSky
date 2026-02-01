package net.itsthesky.disky.experimental;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class Switch extends Section {

	static {
		Skript.registerSection(
				Switch.class,
				"switch %objects%"
		);
	}

	private LinkedList<Case> cases = new LinkedList<>();
	private Trigger trigger;
	private Expression<Object> exprObjects;

	public void linkCase(Case caseEffect) {
		caseEffect.setParent(this);
		cases.add(caseEffect);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs,
						int matchedPattern,
						@NotNull Kleenean isDelayed,
						@NotNull SkriptParser.ParseResult parseResult,
						@NotNull SectionNode node,
						@NotNull List<TriggerItem> items) {
		exprObjects = (Expression<Object>) exprs[0];
		trigger = loadCode(node, "switch section", SkriptUtils.addEventClasses());
		return false;
	}

	@Override
	protected @Nullable TriggerItem walk(@NotNull Event e) {
		final Object[] values = EasyElement.parseList(exprObjects, e, new Object[0]);
		if (values.length == 0)
			return getNext();
		for (Object value : values) {
			for (Case caze : cases) {
				if (caze.validate(value, e))
					caze.getCode().execute(e);
			}
		}
		return getNext();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "switch " + exprObjects.toString(e, debug);
	}
}
