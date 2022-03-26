package info.itsthesky.disky.elements.components;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.NodeInformation;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Defer Interaction")
@Description({"Only usable in interaction event, currently button click/dropdown update/modal event!",
"\nThis will force the interaction to be acknowledge, you have 3 seconds to do so, the effect will send a success message to Discord or hold the interaction to send a message later.",
"\nKeep in mind that replying in an interaction event will automatically defer the interaction, and therefore you don't need to defer it.",
"\nIf you need to wait more than 3 seconds use the and wait pattern",
"\nAn interaction can only be deferred once!"})
@Examples("defer the interaction [and wait]")
public class EffDeferInteraction extends Effect {

    static {
        Skript.registerEffect(
                EffDeferInteraction.class,
                "(acknowledge|defer) [the] interaction [and wait (1¦silently)]"
        );
    }

    private NodeInformation node;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {

        if (!EasyElement.containsInterfaces(InteractionEvent.class)) {
            Skript.error("The defer interaction effect can only be used in interaction events!");
            return false;
        }

        node = new NodeInformation();

        isEphemeral = parseResult.mark == 1;
        shouldwait = parseResult.expr.contains("and wait");

        return true;
    }

    private boolean isEphemeral;
    private boolean shouldwait;

    @Override
    protected void execute(@NotNull Event e) {
        GenericInteractionCreateEvent event = ((InteractionEvent) e).getInteractionEvent();

        if (event instanceof GenericComponentInteractionCreateEvent) {

            GenericComponentInteractionCreateEvent clickEvent = (GenericComponentInteractionCreateEvent) event;

        } else if (event instanceof ModalInteractionEvent) {

            ModalInteractionEvent clickEvent = (ModalInteractionEvent) event;

        if (shouldwait) {
            clickEvent.deferReply(isEphemeral).queue(null, ex -> DiSky.getErrorHandler().exception(e, ex));
                return;
            }
            clickEvent.deferEdit().queue(null, ex -> DiSky.getErrorHandler().exception(e, ex));

        }

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "defer the interaction";
    }

}
