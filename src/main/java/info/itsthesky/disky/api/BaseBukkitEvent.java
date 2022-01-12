package info.itsthesky.disky.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBukkitEvent extends Event {

    private final HandlerList list = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return list;
    }
}
