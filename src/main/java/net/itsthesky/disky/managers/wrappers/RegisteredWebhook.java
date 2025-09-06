package net.itsthesky.disky.managers.wrappers;

import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.WebhookClient;
import org.jetbrains.annotations.Nullable;

/**
 * Pairs a {@link net.dv8tion.jda.api.entities.WebhookClient webhooks client} with a {@link Bot bot}.
 *
 * @param name The name the client is referenced by.
 * @param bot The bot this client will be paired with.
 * @param client The {@link WebhookClient} that is being paired with.
 * @param threadId An optional id of a thread, that you want this webhook to send to
 */
public record RegisteredWebhook(String name, Bot bot, WebhookClient<Message> client, @Nullable String threadId) { }
