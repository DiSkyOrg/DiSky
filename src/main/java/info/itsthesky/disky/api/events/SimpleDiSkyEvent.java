package info.itsthesky.disky.api.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Made by Blitz, minor edit by Sky for DiSky
 */
public class SimpleDiSkyEvent<D extends net.dv8tion.jda.api.events.Event> extends BukkitEvent {

    private D JDAEvent;
    private Map<Class<?>, Object> valueMap = new HashMap<>();

    public D getJDAEvent() {
        return JDAEvent;
    }

    public void setJDAEvent(D JDAEvent) {
        this.JDAEvent = JDAEvent;
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