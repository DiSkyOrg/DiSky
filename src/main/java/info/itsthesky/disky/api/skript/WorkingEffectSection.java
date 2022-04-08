package info.itsthesky.disky.api.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Config;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class WorkingEffectSection extends EffectSection {

    @SafeVarargs
    protected final Trigger loadWorkingCode(SectionNode sectionNode, String name, Class<? extends Event>... events) {
        ParserInstance parser = getParser();

        String previousName = parser.getCurrentEventName();
        Class<? extends Event>[] previousEvents = parser.getCurrentEvents();
        SkriptEvent previousSkriptEvent = parser.getCurrentSkriptEvent();
        List<TriggerSection> previousSections = parser.getCurrentSections();
        Kleenean previousDelay = parser.getHasDelayBefore();

        parser.setCurrentEvent(name, events);
        SkriptEvent skriptEvent = new SectionSkriptEvent(name, this);
        parser.setCurrentSkriptEvent(skriptEvent);
        parser.setHasDelayBefore(Kleenean.FALSE);
        List<TriggerItem> triggerItems = ScriptLoader.loadItems(sectionNode);

        //noinspection ConstantConditions - We are resetting it to what it was
        parser.setCurrentEvent(previousName, previousEvents);
        parser.setCurrentSkriptEvent(previousSkriptEvent);
        parser.setCurrentSections(previousSections);
        parser.setHasDelayBefore(previousDelay);

        Config script = parser.getCurrentScript();
        return new Trigger(script != null ? script.getFile() : null, name, skriptEvent, triggerItems);
    }

}
