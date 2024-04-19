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
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Name("Inline Rich Message Builder")
@Description({"Create a new rich message in one line only.",
"WARNING: This could slow a lot the Skript's parsing time if used too many times!",
"We still recommend to use the create message section instead!",
"You can also use the second pattern to send component-only messages."})
@Examples({"reply with message \"hello world\" with embed last embed with components {_row}",
"post components new danger button with id \"id\" named \"Hey\" to event-channel"})
@Since("4.4.1, 4.4.3 (component-only)")
public class InlineMessageBuilder extends SimpleExpression<MessageCreateBuilder> {

	static {
		Skript.registerExpression(
				InlineMessageBuilder.class,
				MessageCreateBuilder.class,
				ExpressionType.COMBINED,
				"[rich] [:silent] message %string/embedbuilder% [with embed[s] %-embedbuilders%] [with (component[s]|row[s]) %-rows/buttons/dropdowns%] [with (file|attachment)[s] %-strings%] [with poll %-messagepoll%]",
				"rich [:silent] component[s] %rows/buttons/dropdowns%"
		);
	}

	private Expression<Object> exprBase;
	private Expression<EmbedBuilder> exprEmbeds;
	private Expression<Object> exprRows;
	private Expression<String> exprFiles;
	private Expression<MessagePollBuilder> exprPoll;

	private boolean isComponentOnly;
	private boolean isSilent;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		isComponentOnly = matchedPattern == 1;
		isSilent = parseResult.hasTag("silent");
		exprBase = (Expression<Object>) exprs[0];
		if (!isComponentOnly) {
			exprEmbeds = (Expression<EmbedBuilder>) exprs[1];
			exprRows = (Expression<Object>) exprs[2];
			exprFiles = (Expression<String>) exprs[3];
			exprPoll = (Expression<MessagePollBuilder>) exprs[4];
		}
		return true;
	}

	@Override
	protected @Nullable MessageCreateBuilder[] get(@NotNull Event e) {
		final EmbedBuilder[] embeds = EasyElement.parseList(exprEmbeds, e, null);
		final Object[] rows = EasyElement.parseList(exprRows, e, null);
		final String[] files = EasyElement.parseList(exprFiles, e, null);
		final MessagePollBuilder poll = EasyElement.parseSingle(exprPoll, e, null);
		final MessageCreateBuilder builder = new MessageCreateBuilder()
				.setSuppressedNotifications(isSilent);

		if (isComponentOnly) {
			final Object[] baseRows = EasyElement.parseList(exprBase, e, new Object[0]);
			final List<ActionRow> actionRows = new ArrayList<>();
			for (Object row : baseRows) {
				if (row instanceof ComponentRow) {
					actionRows.add(((ComponentRow) row).asActionRow());
				} else if (row instanceof Button) {
					actionRows.add(ActionRow.of((Button) row));
				} else if (row instanceof SelectMenu) {
					actionRows.add(ActionRow.of((SelectMenu) row));
				} else if (row instanceof TextInput) {
					actionRows.add(ActionRow.of((TextInput) row));
				}
			}

			return new MessageCreateBuilder[] {builder.setComponents(actionRows)};
		} else {
			final Object base = EasyElement.parseSingle(exprBase, e, null);
			if (base instanceof String)
				builder.setContent((String) base);
			else if (base instanceof EmbedBuilder)
				builder.setEmbeds(((EmbedBuilder) base).build());
		}

		if (embeds != null)
			for (EmbedBuilder embed : embeds)
				builder.addEmbeds(embed.build());

		if (files != null)
			for (String path : files)
				builder.addFiles(FileUpload.fromData(new File(path)));

		if (poll != null)
			builder.setPoll(poll.build());

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
