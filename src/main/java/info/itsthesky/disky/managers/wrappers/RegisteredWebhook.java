package info.itsthesky.disky.managers.wrappers;

import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.WebhookClient;

/**
 * Pair a {@link net.dv8tion.jda.api.entities.WebhookClient webhooks client} with a {@link info.itsthesky.disky.core.Bot bot}
 */
public class RegisteredWebhook {

    private final String name;
    private final Bot bot;
    private final WebhookClient<Message> client;

    public RegisteredWebhook(String name, Bot bot, WebhookClient<Message> client) {
        this.name = name;
        this.bot = bot;
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public Bot getBot() {
        return bot;
    }

    public WebhookClient<Message> getClient() {
        return client;
    }
}
