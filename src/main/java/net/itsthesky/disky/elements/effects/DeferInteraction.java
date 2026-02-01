package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.specific.InteractionEvent;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
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
@Since("4.0.0")
@SeeAlso(IReplyCallback.class)
public class DeferInteraction extends AsyncEffect {

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
    public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        if (!EasyElement.containsInterfaces(InteractionEvent.class)) {
            Skript.error("The defer interaction effect can only be used in interaction events!");
            return false;
        }

        isEphemeral = parseResult.mark == 1;
        shouldwait = parseResult.expr.contains("and wait");

        return true;
    }

    @Override
    public void execute(Event e) {
        GenericInteractionCreateEvent event = ((InteractionEvent) e).getInteractionEvent();

        try {
            if (shouldwait) {
                ((IReplyCallback) event).deferReply(isEphemeral).complete();
                WAITING_INTERACTIONS.add(event.getInteraction().getIdLong());
            } else {
                if (event instanceof GenericComponentInteractionCreateEvent)
                    ((ComponentInteraction) event).deferEdit().complete();
                if (event instanceof ModalInteractionEvent)
                    ((ModalInteractionEvent) event).deferEdit().complete();
            }
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "defer the interaction";
    }

}
