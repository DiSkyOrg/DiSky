package net.itsthesky.disky.api.events;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

import java.util.HashMap;
import java.util.Map;

public class SimpleDiSkyEvent<D extends net.dv8tion.jda.api.events.Event> extends BukkitEvent {

    private D JDAEvent;
    private GuildAuditLogEntryCreateEvent logEvent;
    private Map<Class<?>, Object> valueMap = new HashMap<>();

    public SimpleDiSkyEvent(boolean async) {
        super(async);
    }

    public SimpleDiSkyEvent() {
        super(false);
    }

    public SimpleDiSkyEvent(DiSkyEvent<D> diSkyEvent) {
        super(false);
    }

    public D getJDAEvent() {
        return JDAEvent;
    }

    public void setJDAEvent(D JDAEvent) {
        this.JDAEvent = JDAEvent;
    }

    public GuildAuditLogEntryCreateEvent getLogEvent() {
        return logEvent;
    }

    public void setLogEvent(GuildAuditLogEntryCreateEvent logEvent) {
        this.logEvent = logEvent;
    }

    public Map<Class<?>, Object> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<Class<?>, Object> valueMap) {
        this.valueMap = valueMap;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> clazz) {
        return (T) valueMap.get(clazz);
    }
}