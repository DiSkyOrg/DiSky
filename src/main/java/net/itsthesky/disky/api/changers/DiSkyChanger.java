package net.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.changers.Change;
import org.jetbrains.annotations.NotNull;

public abstract class DiSkyChanger<T> implements Changer<T> {

    @Override
    public final void change(T @NotNull [] what, @NotNull Object[] delta, @NotNull ChangeMode mode) {
        if (Change.currentBot != null)
            change(what, delta, Change.currentBot, mode);
    }

    public abstract void change(T[] what, Object[] delta, Bot bot, ChangeMode mode);

}