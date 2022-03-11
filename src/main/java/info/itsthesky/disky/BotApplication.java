package info.itsthesky.disky;

import info.itsthesky.disky.core.Bot;

/**
 * Class that will store and handle information about a bot's application.
 */
public class BotApplication {

    private final String name;
    private final String clientID;
    private final String clientSecret;

    public BotApplication(String name, String clientID, String clientSecret) {
        this.name = name;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public String getName() {
        return name;
    }

    public Bot getBot() {
        return DiSky.getManager().fromName(getName());
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}