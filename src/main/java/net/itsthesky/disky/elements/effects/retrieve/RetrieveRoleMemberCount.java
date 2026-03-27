package net.itsthesky.disky.elements.effects.retrieve;


import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleMemberCounts;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Role Member Counts")
@Description("Retrieve the member count for a specific role in a guild.")
@Examples({
    "retrieve role member count of {_role} and store it in {_count}"
})
public class RetrieveRoleMemberCount extends AsyncEffect {

    static {
        DiSkyRegistry.registerEffect(
                RetrieveRoleMemberCount.class,
                "retrieve role member count of %role% and store (it|the count) in %-objects%"
        );
    }

    private Expression<Role> exprRole;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);
        exprRole = (Expression<Role>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        try {
            final Role role = EasyElement.parseSingle(exprRole, e, null);
            if (EasyElement.anyNull(this, role))
                return;

            final RoleMemberCounts counts = role.getGuild().retrieveRoleMemberCounts().complete();
            final int count = counts.get(role);
            exprResult.change(e, new Object[] {count}, Changer.ChangeMode.SET);
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "retrieve role member count of " + exprRole.toString(e, debug);
    }
}
