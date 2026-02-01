package net.itsthesky.disky.elements.sections.message;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Name("Message Builder Embeds")
@Description({"Embeds of a message builder",
		"See also: 'Create (rich) Message'"})
public class MessageEmbeds extends MultiplyPropertyExpression<MessageCreateBuilder, EmbedBuilder> {

	static {
		register(
				MessageEmbeds.class,
				EmbedBuilder.class,
				"embed[s]",
				"messagecreatebuilder"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!getParser().isCurrentSection(CreateMessage.class)) {
			Skript.error("You can only use the 'message embeds' expression inside a 'create message' section");
			return false;
		}
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
			return new Class[]{EmbedBuilder.class, EmbedBuilder[].class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final MessageCreateBuilder builder = EasyElement.parseSingle(getExpr(), e, null);
		final EmbedBuilder[] builders = (EmbedBuilder[]) delta;

		if (builder == null || builders.length < 1)
			return;

		final List<MessageEmbed> embeds = Stream.of(builders)
				.map(EmbedBuilder::build)
				.collect(Collectors.toList());

		switch (mode) {
			case ADD:
				builder.addEmbeds(embeds);
				return;
			case SET:
				builder.setEmbeds(embeds);
		}
	}

	@Override
	public @NotNull Class<? extends EmbedBuilder> getReturnType() {
		return EmbedBuilder.class;
	}

	@Override
	protected String getPropertyName() {
		return "embeds";
	}

	@Override
	protected EmbedBuilder[] convert(MessageCreateBuilder messageCreateBuilder) {
		return messageCreateBuilder.getEmbeds().stream()
				.map(EmbedBuilder::new).toArray(EmbedBuilder[]::new);
	}

}
