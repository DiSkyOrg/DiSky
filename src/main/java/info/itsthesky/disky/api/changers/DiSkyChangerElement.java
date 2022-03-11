package info.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;

public interface DiSkyChangerElement {

    void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode);

    default Bot findAny() {
        return DiSky.getManager().findAny();
    }

}
