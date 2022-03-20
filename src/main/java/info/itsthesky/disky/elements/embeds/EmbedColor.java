package info.itsthesky.disky.elements.embeds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class EmbedColor extends EmbedProperty<Color> {

	static {
		register(EmbedColor.class,
				Color.class,
				"embed colo[u]r");
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		return new Class[] {Color.class};
	}

	@Override
	public void set(EmbedBuilder builder, Color value) {
		builder.setColor(new java.awt.Color(value.asBukkitColor().asRGB()));
	}

	@Override
	protected Color convert(MessageEmbed embed) {
		return SkriptUtils.convert(embed.getColor());
	}

	@Override
	public @NotNull Class<? extends Color> getReturnType() {
		return Color.class;
	}

}
