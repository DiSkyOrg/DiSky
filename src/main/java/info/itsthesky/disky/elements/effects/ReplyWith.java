package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.NonNullPair;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.core.Utils;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Name("Reply With / To Message")
@Description({"Reply with a specific text, embed or message builder.",
		"A reference message can be provided, and therefore the mention optional part can be implemented.",
		"This effect will only work with events that implement 'message event' (aka every channel-related events)"})
@Examples({"reply with \"Hello world\"",
		"reply with \"yo guys :p\" and store the message in {_msg}"})
public class ReplyWith extends SpecificBotEffect<Message> {

	static {
		DEFERRED_EVENTS = new LinkedList<>();
		final String types = DiSky.isSkImageInstalled() ? "strings/images" : "strings";
		Skript.registerEffect(
				ReplyWith.class,
				"reply with [hidden] [the] [content] %string/embedbuilder/messagebuilder% " +
						"[with [the] (component|action)[s] [row] %-rows%] " +
						"[with reference[d] [message] %-message% [(1¦mentioning)]] " +
						"[with [the] file[s] %-"+ types +"% [with [the] option[s] %-attachmentoptions%]]" +
						"[and store (it|the message) in %-objects%]"
		);
	}

	private Expression<Object> exprMessage;
	private Expression<ComponentRow> exprComponents;
	private boolean isInInteraction;
	private boolean hidden;

	private Expression<Object> exprFiles;
	private Expression<AttachmentOption> exprOptions;

	private boolean mentioning;
	private Expression<Message> exprReference;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!containsInterfaces(MessageEvent.class) && !containsInterfaces(InteractionEvent.class)) {
			Skript.error("The reply effect can only be used in message / interaction events.");
			return false;
		}
		isInInteraction = EasyElement.containsInterfaces(InteractionEvent.class);
		hidden = parseResult.expr.contains("with hidden");
		exprMessage = (Expression<Object>) expressions[0];
		exprComponents = (Expression<ComponentRow>) expressions[1];

		exprReference = (Expression<Message>) expressions[2];
		mentioning = (parseResult.mark & 1) != 1;
		exprFiles = (Expression<Object>) expressions[3];
		exprOptions = (Expression<AttachmentOption>) expressions[4];

		return validateVariable(expressions[5], false, true);
	}

	public static final LinkedList<Event> DEFERRED_EVENTS;

	@Override
	public void runEffect(@NotNull Event e, Bot bot) {

		final Object[] rawFiles = EasyElement.parseList(exprFiles, e, new String[0]);
		final AttachmentOption[] options = EasyElement.parseList(exprOptions, e, new AttachmentOption[0]);

		final List<ComponentRow> rows = Arrays.asList(parseList(exprComponents, e, new ComponentRow[0]));
		final List<ActionRow> formatted = rows
				.stream()
				.map(ComponentRow::asActionRow)
				.collect(Collectors.toList());

		final List<NonNullPair<InputStream, String>> files;
		try {
			files = Utils.parseFiles(rawFiles);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			restart();
			return;
		}

		if (isInInteraction) {

			final IReplyCallback event = (IReplyCallback) ((InteractionEvent) e).getInteractionEvent();
			final Object rawMessage = parseSingle(exprMessage, e, null);
			final MessageBuilder message = JDAUtils.constructMessage(rawMessage);
			if (anyNull(rawMessage, message)) {
				restart();
				return;
			}

			if (!event.getHook().isExpired() && DEFERRED_EVENTS.contains(e)) {
				WebhookMessageUpdateAction<Message> action = event.getHook().editOriginal(message.build());
				action = action.setActionRows(formatted);
				for (NonNullPair<InputStream, String> file : files)
					action = action.addFile(file.getFirst(), file.getSecond(), options);
				action.queue(this::restart, ex -> {
					DiSky.getErrorHandler().exception(e, ex);
					restart();
				});
				DEFERRED_EVENTS.remove(e);
				return;
			}

			ReplyCallbackAction reply = event.reply(message.build())
					.addActionRows(formatted)
					.setEphemeral(hidden);

			for (NonNullPair<InputStream, String> file : files)
				reply = reply.addFile(file.getFirst(), file.getSecond(), options);

			reply.queue(v -> v.retrieveOriginal().queue(this::restart, ex -> {
				DiSky.getErrorHandler().exception(e, ex);
				restart();
			}), ex -> {
				DiSky.getErrorHandler().exception(e, ex);
				restart();
			});

		} else {

			final MessageEvent event = (MessageEvent) e;
			final Object rawMessage = parseSingle(exprMessage, e, null);
			final MessageBuilder message = JDAUtils.constructMessage(rawMessage);
			final @Nullable Message referenced = parseSingle(exprReference, e, null);
			final MessageChannel channel = bot.findMessageChannel(event.getMessageChannel());
			if (anyNull(channel, event, rawMessage, message)) {
				restart();
				return;
			}

			MessageAction action = channel.sendMessage(message.build());

			action = action.setActionRows(formatted);
			if (referenced != null)
				action = action.reference(referenced).mentionRepliedUser(mentioning);
			for (NonNullPair<InputStream, String> file : files)
				action = action.addFile(file.getFirst(), file.getSecond(), options);

			action.queue(this::restart, ex -> {
				DiSky.getErrorHandler().exception(e, ex);
				restart();
			});

		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "reply with " + exprMessage.toString(e, debug) + (getChangedVariable() == null ? "" :
				" and store the message in " + variableAsString(e, debug));
	}
}