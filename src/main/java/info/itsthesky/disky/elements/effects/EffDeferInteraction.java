package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name(" ")
@Description({""})

public class EffDeferInteraction extends WaiterEffect {

    static {
        Skript.registerEffect(
                EffDeferIntreaction.class,
                "defer the interaction [and wait [(1¦silently)]]"
        );
    }

    private boolean isInInteraction;
    private boolean shouldwait;
    private boolean isEphemeral;


    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if ((!(Arrays.asList(info.itsthesky.disky3.api.skript.adapter.SkriptAdapter.getInstance().getCurrentEvents()[0].getInterfaces()).contains(InteractionEvent.class)))) {
            Skript.error("The defer interaction effect can only be used in interaction events.");
            return false;
        }
        isInInteraction = containsInterfaces(InteractionEvent.class);
        shouldwait = parseResult.expr.contains("and wait");
        boolean isEphemeral = parseResult.mark == 1;

    }

    @Override
    public void runEffect(Event e) {
        if (isInInteraction) {

            final IReplyCallback event = (IReplyCallback) ((InteractionEvent) e).getInteractionEvent();
            if (anyNull(event)) {
                restart();
                return;
            }

            if (shouldwait) {
                event.deferReply(isEphemeral).queue((v -> restart()), ex -> {
                    DiSky.getErrorHandler().exception(event, ex);
                    restart())
            else {
                event.deferEdit().queue((v -> restart(), ex -> {
                    DiSky.getErrorHandler().exception(event, ex);
                    restart(););
                    });
            }
                    });


        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "defer the interaction";
    }
}
