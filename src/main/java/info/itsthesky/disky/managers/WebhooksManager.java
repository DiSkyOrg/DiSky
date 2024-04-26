package info.itsthesky.disky.managers;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.managers.wrappers.RegisteredWebhook;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.WebhookClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Main manager for webhooks client, register via an url/token/id, and linked
 * with a JDA instance, in order to use rest actions system.
 * @author ItsTheSky
 */
public final class WebhooksManager {

    private final DiSky instance;
    private final Map<String, RegisteredWebhook> webhooks;

    public WebhooksManager(DiSky instance) {
        this.instance = instance;

        this.webhooks = new HashMap<>();
    }

    public void registerWebhook(Bot bot, String name, String url) {
        final WebhookClient<Message> client = WebhookClient.createClient(bot.getInstance(), url);
        final RegisteredWebhook webhook = new RegisteredWebhook(name, bot, client);

        webhooks.put(name, webhook);
    }

    public void unregisterWebhook(String name) {
        if (!webhooks.containsKey(name))
            return;

        webhooks.remove(name);
    }

    public boolean isWebhookRegistered(String name) {
        return webhooks.containsKey(name);
    }

    public RegisteredWebhook getWebhook(String name) {
        return webhooks.get(name);
    }

}
