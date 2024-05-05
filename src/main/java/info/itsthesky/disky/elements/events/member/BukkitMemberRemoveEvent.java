package info.itsthesky.disky.elements.events.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BukkitMemberRemoveEvent extends Event {

    public final User target;
    public final Guild guild;
    public final JDA bot;
    public Member author;

    public BukkitMemberRemoveEvent(User target, Guild guild, JDA bot) {
        this.target = target;
        this.guild = guild;
        this.bot = bot;
    };

    private final static HandlerList handlers = new HandlerList();
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
