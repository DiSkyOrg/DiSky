package info.itsthesky.disky.elements.sections.message;

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
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Name("Inline Rich Message Builder")
@Description({"Create a new rich message in one line only.",
"WARNING: This could slow a lot the Skript's parsing time if used too many times!",
"We still recommend to use the create message section instead!"})
@Examples("reply with message \"hello world\" with embed last embed with components {_row}")
@Since("4.4.1")
public class InlineMessageBuilder extends SimpleExpression<MessageCreateBuilder> {

	static {
		Skript.registerExpression(
				InlineMessageBuilder.class,
				MessageCreateBuilder.class,
				ExpressionType.COMBINED,
				"[rich] message %string/embedbuilder% [with embed[s] %-embedbuilders%] [with (component[s]|row[s]) %-rows/buttons/dropdowns%] [with (file|attachment)[s] %-strings%]"
		);
	}

	private Expression<Object> exprBase;
	private Expression<EmbedBuilder> exprEmbeds;
	private Expression<Object> exprRows;
	private Expression<String> exprFiles;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprBase = (Expression<Object>) exprs[0];
		exprEmbeds = (Expression<EmbedBuilder>) exprs[1];
		exprRows = (Expression<Object>) exprs[2];
		exprFiles = (Expression<String>) exprs[3];
		return true;
	}

	@Override
	protected @Nullable MessageCreateBuilder[] get(@NotNull Event e) {
		final Object base = EasyElement.parseSingle(exprBase, e, null);
		final EmbedBuilder[] embeds = EasyElement.parseList(exprEmbeds, e, null);
		final Object[] rows = EasyElement.parseList(exprRows, e, null);
		final String[] files = EasyElement.parseList(exprFiles, e, null);
		if (base == null)
			return new MessageCreateBuilder[0];

		final MessageCreateBuilder builder = new MessageCreateBuilder();

		if (base instanceof String)
			builder.setContent((String) base);
		else if (base instanceof EmbedBuilder)
			builder.setEmbeds(((EmbedBuilder) base).build());

		if (embeds != null)
			for (EmbedBuilder embed : embeds)
				builder.addEmbeds(embed.build());

		if (files != null)
			for (String path : files)
				builder.addFiles(FileUpload.fromData(new File(path)));

		if (rows != null) {
			final List<ActionRow> actionRows = new ArrayList<>();
			for (Object obj : rows) {
				if (obj instanceof Button)
					actionRows.add(ActionRow.of((Button) obj));
				else if (obj instanceof ComponentRow)
					actionRows.add(((ComponentRow) obj).asActionRow());
				else if (obj instanceof SelectMenu.Builder)
					actionRows.add(ActionRow.of(((SelectMenu.Builder) obj).build()));
				else if (obj instanceof TextInput.Builder)
					actionRows.add(ActionRow.of(((TextInput.Builder) obj).build()));
			}
			builder.setComponents(actionRows);
		}

		return new MessageCreateBuilder[] { builder };
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends MessageCreateBuilder> getReturnType() {
		return MessageCreateBuilder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "rich message with content " + exprBase.toString(e, debug);
	}
}
