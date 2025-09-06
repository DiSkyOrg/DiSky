package net.itsthesky.disky.elements.effects.webhooks;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.managers.wrappers.RegisteredWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MakeClientSpeak extends AsyncEffect {

    static {
        Skript.registerEffect(
                MakeClientSpeak.class,
                "make [the] [webhook] client %string% (post|send) [the] [message] %string/messagecreatebuilder/embedbuilder/messagepollbuilder% [with [the] username %-string%] [[and] [with] [the] avatar [url] %-string%] [and store (it|the message) in %-~objects%]"
        );
    }

    private Node node;
    private Expression<String> exprName;
    private Expression<Object> exprMessage;
    private Expression<String> exprAvatar;
    private Expression<String> exprUsername;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprName = (Expression<String>) expressions[0];
        exprMessage = (Expression<Object>) expressions[1];
        exprUsername = (Expression<String>) expressions[2];
        exprAvatar = (Expression<String>) expressions[3];
        exprResult = (Expression<Object>) expressions[4];
        node = getParser().getNode();
        return exprResult == null || Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final String name = exprName.getSingle(event);
        final Object message = exprMessage.getSingle(event);
        final String avatar = exprAvatar == null ? null : exprAvatar.getSingle(event);
        final String username = exprUsername == null ? null : exprUsername.getSingle(event);
        if (name == null || message == null)
            return;

        if (!DiSky.getWebhooksManager().isWebhookRegistered(name)) {
            SkriptUtils.error(node, "The webhook client named " + name + " isn't registered!");
            return;
        }

        final MessageCreateBuilder builder;
        if (message instanceof MessageCreateBuilder)
            builder = (MessageCreateBuilder) message;
        else if (message instanceof EmbedBuilder)
            builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
        else if (message instanceof MessagePollBuilder)
            builder = new MessageCreateBuilder().setPoll(((MessagePollBuilder) message).build());
        else
            builder = new MessageCreateBuilder().setContent((String) message);

        final RegisteredWebhook registerClient = DiSky.getWebhooksManager().getWebhook(name);
        final WebhookClient<Message> client = registerClient.client();

        final Message resultMessage;
        try {

            resultMessage = client.sendMessage(builder.build())
                    .setUsername(username)
                    .setAvatarUrl(avatar)
                    .setThreadId(registerClient.threadId())
                    .complete();

        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        if (exprResult == null)
            return;
        exprResult.change(event, new Message[] {resultMessage}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "make client " + exprName.toString(event, debug) + " send message " + exprMessage.toString(event, debug)
                + (exprAvatar == null ? "" : " with avatar " + exprAvatar.toString(event, debug))
                + (exprUsername == null ? "" : " with username " + exprUsername.toString(event, debug))
                + (exprResult == null ? "" : " and store it in " + exprResult.toString(event, debug));
    }
}
