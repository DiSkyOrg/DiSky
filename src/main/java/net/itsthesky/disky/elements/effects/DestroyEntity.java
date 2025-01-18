package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.events.specific.InteractionEvent;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.managers.wrappers.RegisteredWebhook;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Destroy Discord Entity")
@Description("Destroy on Discord the wanted entity.")
@Examples({"destroy event-channel",
"destroy event-message"})
public class DestroyEntity extends AsyncEffect {

	static {
		Skript.registerEffect(
				DestroyEntity.class,
				"destroy %guild/message/role/channel/emote/webhook%"
		);
	}

	private Expression<Object> exprEntity;
	private Node node;

	@Override
	public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);
		node = getParser().getNode();

		exprEntity = (Expression<Object>) expressions[0];
		return true;
	}

	@Override
	public void execute(Event e) {
		final Object entity = parseSingle(exprEntity, e, null);
		if (anyNull(this, entity))
			return;

		final RestAction<Void> action;
		if (entity instanceof Message) {
			final Message message = (Message) entity;
			final @Nullable RegisteredWebhook webhook =
					DiSky.getWebhooksManager().getWebhookById(message.getAuthor().getId());
			if (webhook != null) {
				DiSky.debug("Deleting message with webhook");
				action = webhook.getClient().deleteMessageById(message.getId());
			} else {
				DiSky.debug("Deleting message without webhook");
				action = message.delete();
			}
		} else if (entity instanceof Guild)
			action = ((Guild) entity).delete();
		else if (entity instanceof Role)
			action = ((Role) entity).delete();
		else if (entity instanceof Channel)
			action = ((Channel) entity).delete();
		else if (entity instanceof Emote && ((Emote) entity).isCustom())
			action = ((Emote) entity).getEmote().delete();
		else if (entity instanceof Webhook)
			action = ((Webhook) entity).delete();
		else
			action = null;
		if (anyNull(this, action))
			return;

		try {
			action.complete();
		} catch (Exception ex) {
			if (e instanceof InteractionEvent && entity instanceof Message && ex instanceof IllegalStateException) {
				DiSkyRuntimeHandler.error(new Exception("When deleting a message in an interaction, you must first DEFER the interaction!"), node);
				return;
			}
			DiSkyRuntimeHandler.error(ex, node);
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "destroy " + exprEntity.toString(e, debug);
	}
}
