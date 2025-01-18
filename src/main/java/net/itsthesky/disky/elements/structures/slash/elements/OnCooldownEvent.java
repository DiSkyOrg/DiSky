package net.itsthesky.disky.elements.structures.slash.elements;

import net.itsthesky.disky.api.events.specific.InteractionEvent;
import net.itsthesky.disky.api.events.specific.ModalEvent;
import net.itsthesky.disky.elements.events.interactions.SlashCommandReceiveEvent;
import org.bukkit.event.Cancellable;

public class OnCooldownEvent extends SlashCommandReceiveEvent {

    public static class BukkitCooldownEvent extends BukkitSlashCommandReceiveEvent implements Cancellable,
            ModalEvent, InteractionEvent {

        public BukkitCooldownEvent(SlashCommandReceiveEvent event,
                                   long remainingTime) {
            super(event);
            this.remainingTime = remainingTime;
        }

        private long remainingTime;
        private boolean cancelled = false;
        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            cancelled = cancel;
        }

        public long getRemainingTime() {
            return remainingTime;
        }
    }

}
