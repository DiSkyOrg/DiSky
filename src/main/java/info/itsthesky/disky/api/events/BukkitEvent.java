package info.itsthesky.disky.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Made by Blitz, minor edit by Sky for DiSky
 */
public class BukkitEvent extends org.bukkit.event.Event {

    private static final HandlerList handlerList = new HandlerList();

    public BukkitEvent(boolean async) {
        super(async);
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}