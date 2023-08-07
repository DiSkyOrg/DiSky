package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Name("Defer Interaction")
@Description({"Only usable in interaction event, currently button click/dropdown update/modal event!",
"This will force the interaction to be acknowledge, you have 3 seconds to do so, the effect will send a success message to Discord or hold the interaction to send a message later.",
"Keep in mind that replying in an interaction event will automatically defer the interaction, and therefore you don't need to defer it.",
"If you need to wait more than 3 seconds use the and wait pattern",
"An interaction can only be deferred once!"})
@Examples({"defer the interaction",
"defer the interaction and wait",
"defer the interaction and wait silently"})
public class DeferInteraction extends WaiterEffect {

    public static final Set<Long> WAITING_INTERACTIONS = new HashSet<>();

    static {
        Skript.registerEffect(
                DeferInteraction.class,
                "(acknowledge|defer) [the] interaction [and wait [(1¦silently)]]"
        );
    }

    private boolean isEphemeral;
    private boolean shouldwait;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!EasyElement.containsInterfaces(InteractionEvent.class)) {
            Skript.error("The defer interaction effect can only be used in interaction events!");
            return false;
        }

        isEphemeral = parseResult.mark == 1;
        shouldwait = parseResult.expr.contains("and wait");

        return true;
    }

    @Override
    public void runEffect(Event e) {
        GenericInteractionCreateEvent event = ((InteractionEvent) e).getInteractionEvent();

        if (shouldwait) {
            ((IReplyCallback) event).deferReply(isEphemeral).queue(reply -> {
                restart();

                WAITING_INTERACTIONS.add(event.getInteraction().getIdLong());
            }, ex -> DiSky.getErrorHandler().exception(e, ex));
        } else {
            if (event instanceof GenericComponentInteractionCreateEvent)
                ((ComponentInteraction) event).deferEdit().queue(this::restart, ex -> DiSky.getErrorHandler().exception(e, ex));
            if (event instanceof ModalInteractionEvent)
                ((ModalInteractionEvent) event).deferEdit().queue(this::restart, ex -> DiSky.getErrorHandler().exception(e, ex));
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "defer the interaction";
    }

}
