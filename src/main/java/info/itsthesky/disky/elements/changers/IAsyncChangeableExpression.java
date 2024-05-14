package info.itsthesky.disky.elements.changers;

import ch.njol.skript.classes.Changer;
import org.bukkit.event.Event;

public interface IAsyncChangeableExpression {

    void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode);

}
