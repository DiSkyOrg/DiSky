package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.*;

@Name("Edit Message")
@Description({"Edit a specific message/interaction hook to show a new rich or simple message.",
"The interaction hook will only be editable for the next 15 minutes once it's sent!"})
@Examples(
		"# We are in a slash command event!\n" +
				"reply with hidden \"Wanna see a magic trick? ...\" and store it in {_msg}\n" +
				"wait a second\n" +
				"# The variable does not contains a 'real' message, it contains the interaction hook." +
				"edit {_msg} to show \"Abracadabra!\""
)
@Since("4.4.0")
public class EditMessage extends AsyncEffect {

	static {
		Skript.registerEffect(
				EditMessage.class,
				"edit [the] [message] %message% (with|to show) %string/messagecreatebuilder/embedbuilder%"
		);
	}

	private Expression<Object> exprTarget;
	private Expression<Object> exprMessage;

	@Override
	public boolean init(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		exprTarget = (Expression<Object>) expressions[0];
		exprMessage = (Expression<Object>) expressions[1];
		return true;
	}

	@Override
	public void execute(@NotNull Event e) {
		final Object target = parseSingle(exprTarget, e);
		final Object message = parseSingle(exprMessage, e);

		if (message == null || target == null)
			return;

		final MessageCreateBuilder builder;
		if (message instanceof MessageCreateBuilder)
			builder = (MessageCreateBuilder) message;
		else if (message instanceof EmbedBuilder)
			builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
		else
			builder = new MessageCreateBuilder().setContent((String) message);
		final MessageEditBuilder editBuilder = new MessageEditBuilder().applyCreateData(builder.build());

		try {
			// Basically, here we check if it's an interaction event, if that event holds a ComponentInteraction,
			// and also if the provided message's ID is the original message of the interaction.
			// ==> Why? In interactions, we have to edit the interaction itself, and not the message.
			if (e instanceof InteractionEvent
					&& ((InteractionEvent) e).getInteractionEvent().getInteraction() instanceof ComponentInteraction
					&&  ((ComponentInteraction) ((InteractionEvent) e).getInteractionEvent().getInteraction()).getMessageId().equals(((Message) target).getId()))
				((ComponentInteraction) ((InteractionEvent) e).getInteractionEvent().getInteraction()).editMessage(editBuilder.build()).complete();
			else if (target instanceof Message)
				((Message) target).editMessage(editBuilder.build()).complete();
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "edit the message/interaction " + exprTarget.toString(e, debug) + " with " + exprMessage.toString(e, debug);
	}
}
