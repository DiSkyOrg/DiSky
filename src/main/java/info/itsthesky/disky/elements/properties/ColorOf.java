package info.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.changers.ChangeableSimplePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.sections.EmbedSection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Embed / Role Color")
@Description({"Get or change the color of an embed builder or a role.",
		"The color input must come from Skript, and will be converted by DiSky."})
@Examples({"set color of embed to red",
		"set color of role with id \"000\" to lime"})
public class ColorOf extends ChangeableSimplePropertyExpression<Object, Color> {

	static {
		register(ColorOf.class,
				Color.class,
				"(embed|role|discord) colo[u]r",
				"embedbuilder/role");
	}

	private boolean wasInScope;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		wasInScope = getParser().isCurrentSection(EmbedSection.class);
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Bot bot, Changer.@NotNull ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;
		final Color color = (Color) delta[0];
		final Object entity = EasyElement.parseSingle(getExpr(), e, null);
		if (entity == null)
			return;
		if (entity instanceof Role) {
			Role role = (Role) entity;
			if (!bot.coreIsEquals(role.getJDA()))
				role = bot.getInstance().getRoleById(role.getId());
			if (role != null)
				role.getManager().setColor(new java.awt.Color(color.asBukkitColor().asRGB())).queue();
		} else if (entity instanceof EmbedBuilder) {
			((EmbedBuilder) entity).setColor(new java.awt.Color(color.asBukkitColor().asRGB()));
			if (wasInScope)
				EmbedSection.lastEmbed.setColor(new java.awt.Color(color.asBukkitColor().asRGB()));
		}

	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		return new Class[] {Color.class};
	}

	@Override
	public @NotNull Class<? extends Color> getReturnType() {
		return Color.class;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "color";
	}

	@Override
	public @Nullable Color convert(Object entity) {
		if (entity instanceof EmbedBuilder)
			return ((EmbedBuilder) entity).isEmpty() ? null : SkriptUtils.convert(((EmbedBuilder) entity).build().getColor());
		if (entity instanceof Role)
			return SkriptUtils.convert(((Role) entity).getColor());
		return null;
	}
}
