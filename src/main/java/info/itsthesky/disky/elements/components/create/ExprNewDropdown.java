package info.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("New Dropdown")
@Description({"Create a new dropdown menu with different properties.",
		"There's two type of dropdown available:",
		"- String, only text values are accepted & pre-defined",
		"- Entity, only the specified entity type (role, channel and/or user) are accepted",
		"Therefore, you cannot add user, channel or role to a string dropdown and vice-versa.",
		"For entity dropdown, you can accept each type independently, or mix roles & users.",
		":warning: YOU CANNOT MIX CHANNELS WITH ROLES OR USERS!"
})
@Since("4.6.0")
@Examples({"new dropdown with id \"string\" # Default string dropdown",
		"new entity dropdown with id \"entities\" targeting \"user\" and \"role\" # Only user and role are accepted",
		"new entity dropdown with id \"channels\" targeting \"channel\" # Only channel are accepted"
})
public class ExprNewDropdown extends SimpleExpression<SelectMenu.Builder> {

	static {
		Skript.registerExpression(
				ExprNewDropdown.class,
				SelectMenu.Builder.class,
				ExpressionType.COMBINED,
				"[a] [new] [string] drop[( |-)]down [with] [the] [id] %string%",
				"[a] [new] entit(y|ies) drop[( |-)]down [with] [the] [id] %string% targeting %strings%"
		);
	}

	private Expression<String> exprId;
	private boolean isEntity;
	private Expression<String> exprTarget;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprId = (Expression<String>) exprs[0];
		isEntity = matchedPattern == 1;
		if (isEntity)
			exprTarget = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected SelectMenu.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(id))
			return new SelectMenu.Builder[0];
		if (isEntity) {
			final String[] rawTargets = EasyElement.parseList(exprTarget, e, new String[0]);
			final List<EntitySelectMenu.SelectTarget> targets = new ArrayList<>();

			for (String raw : rawTargets) {
				if (raw.equalsIgnoreCase("user") || raw.equalsIgnoreCase("users"))
					targets.add(EntitySelectMenu.SelectTarget.USER);
				else if (raw.equalsIgnoreCase("role") || raw.equalsIgnoreCase("roles"))
					targets.add(EntitySelectMenu.SelectTarget.ROLE);
				else if (raw.equalsIgnoreCase("channel") || raw.equalsIgnoreCase("channels"))
					targets.add(EntitySelectMenu.SelectTarget.CHANNEL);
				else Skript.warning("Unknown target type '" + raw + "' for entity dropdown, skipping it.");
			}

			if (targets.isEmpty()) {
				Skript.error("You must specify at least one target type for entity dropdown!");
				return new SelectMenu.Builder[0];
			}

			return new SelectMenu.Builder[] {EntitySelectMenu.create(id, targets)};
		} else
			return new SelectMenu.Builder[] {StringSelectMenu.create(id)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends SelectMenu.Builder> getReturnType() {
		return SelectMenu.Builder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new dropdown with id " + exprId.toString(e, debug);
	}

}
