package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.JDAImpl;
import net.itsthesky.disky.managers.CoreEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory fake of {@link JDA}.
 *
 * <p>Captures every {@link #addEventListener(Object...)} call so that
 * {@link #dispatchEvent(GenericEvent)} can route inbound events through both
 * the JDA-level listeners (notably DiSky's {@link CoreEventListener}) and any
 * test-only listeners attached directly via DiSky's
 * {@link CoreEventListener#AllRegisteredListeners} list.
 *
 * <p>Holds a {@link FakeSelfUser} and a map of {@link FakeGuild guilds}.
 */
public class FakeJDA extends FakeEntity<JDA> {

    private final FakeSelfUser selfUser;
    private final Map<Long, FakeGuild> guilds = new LinkedHashMap<>();
    private final List<EventListener> jdaListeners = new ArrayList<>();
    private final AtomicLong responseNumber = new AtomicLong(0);
    private JDA.Status status = JDA.Status.CONNECTED;

    public FakeJDA(String botName) {
        super(JDA.class, allInterfacesOf(JDAImpl.class));
        this.selfUser = new FakeSelfUser(this, botName);
    }

    public FakeSelfUser getFakeSelfUser() { return selfUser; }

    public void registerFakeGuild(FakeGuild guild) {
        guilds.put(guild.getIdLong(), guild);
    }

    public long nextResponseNumber() { return responseNumber.incrementAndGet(); }

    /**
     * Convenience: build and dispatch a {@link MessageReceivedEvent} for a fake message.
     * Production listeners (registered through DiSky) and test listeners both fire.
     */
    public void dispatchMessageReceived(FakeMessage message) {
        MessageReceivedEvent event = new MessageReceivedEvent(
                asProxy(),
                nextResponseNumber(),
                message.asProxy());
        dispatchEvent(event);
    }

    /**
     * Routes the given JDA event through every registered JDA-level listener
     * attached to this fake. Synchronous and ordered.
     */
    public void dispatchEvent(GenericEvent event) {
        // Snapshot the list to avoid ConcurrentModificationException if a handler
        // adds/removes listeners during dispatch.
        List<EventListener> snapshot = new ArrayList<>(jdaListeners);
        for (EventListener listener : snapshot) {
            try {
                listener.onEvent(event);
            } catch (Throwable t) {
                System.err.println("[FakeJDA] listener " + listener.getClass().getName()
                        + " threw during dispatch: " + t);
                t.printStackTrace();
            }
        }
    }

    // ===== JDA surface required by DiSky =====

    @NotNull public SelfUser getSelfUser() { return selfUser.asProxy(); }
    @NotNull public JDA.Status getStatus() { return status; }

    @NotNull
    public JDA addEventListener(@NotNull Object... listeners) {
        for (Object l : listeners) {
            if (l instanceof EventListener)
                jdaListeners.add((EventListener) l);
        }
        return asProxy();
    }

    @NotNull
    public JDA removeEventListener(@NotNull Object... listeners) {
        for (Object l : listeners) {
            if (l instanceof EventListener)
                jdaListeners.remove(l);
        }
        return asProxy();
    }

    @NotNull
    public List<Object> getRegisteredListeners() {
        return new ArrayList<>(jdaListeners);
    }

    @NotNull
    public List<Guild> getGuilds() {
        if (guilds.isEmpty()) return Collections.emptyList();
        List<Guild> out = new ArrayList<>(guilds.size());
        for (FakeGuild g : guilds.values()) out.add(g.asProxy());
        return out;
    }

    @Nullable
    public Guild getGuildById(long id) {
        FakeGuild g = guilds.get(id);
        return g == null ? null : g.asProxy();
    }

    @Nullable
    public User getUserById(long id) {
        if (id == selfUser.getIdLong()) return selfUser.asProxy();
        for (FakeGuild g : guilds.values()) {
            net.dv8tion.jda.api.entities.Member m = g.getMemberById(id);
            if (m != null) return m.getUser();
        }
        return null;
    }

    public void shutdown() { status = JDA.Status.SHUTDOWN; }
    public void shutdownNow() { status = JDA.Status.SHUTDOWN; }

    public boolean awaitStatus(@NotNull JDA.Status status) { return true; }
    public boolean awaitReady() { return true; }

    @NotNull public java.util.EnumSet<GatewayIntent> getGatewayIntents() {
        return java.util.EnumSet.noneOf(GatewayIntent.class);
    }

    public long getResponseTotal() { return responseNumber.get(); }
}
