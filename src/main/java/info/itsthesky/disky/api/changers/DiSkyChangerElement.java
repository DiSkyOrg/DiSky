package info.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import de.leonhard.storage.shaded.jetbrains.annotations.NotNull;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;

public interface DiSkyChangerElement {

    void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Bot bot, @NotNull Changer.ChangeMode mode);

    default Bot findAny() {
        return DiSky.getManager().findAny();
    }

}