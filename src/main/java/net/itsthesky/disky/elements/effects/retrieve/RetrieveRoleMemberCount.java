package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleMemberCount;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.getters.GetRole;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Role Member Count")
@Description("Retrieve the member count for each role in a guild and store it in a list variable. The variable will be filled with entries where the key is the role ID and the value is the member count for that role.")
@Examples({
        """
        retrieve role member counts of guild (event-guild) and store them in {_counts::*}
        loop {_counts::*}:
            send "%loop-index%: %loop-value%" to console
        
        # Will send something like:
        # 728740599108468756: 1
        # 1022582162186326026: 0
        # 1022582162731585556: 0
        # 1022592015080370216: 1
        # 1212795395487305831: 1
        # 1495775870558797896: 1
        """
})
@Since("4.28.0")
@SeeAlso({Role.class, GetRole.class, Guild.class})
public class RetrieveRoleMemberCount extends AsyncEffect {

    static {
        DiSkyRegistry.registerEffect(
                RetrieveRoleMemberCount.class,
                "retrieve role member count[s] (of|from) guild %guild% and store (it|them|the counts) in %-objects%"
                );
    }

    private Expression<Guild> exprGuild;
    private Variable<?> listVariable;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprGuild = (Expression<Guild>) exprs[0];

        if (!(exprs[1] instanceof Variable<?> var) || !var.isList()) {
            Skript.error("'" + exprs[1] + "' is not a list variable (ex: {_counts::*})");
            return false;
        }

        listVariable = var;
        return true;
    }

    @Override
    protected void execute(Event event) {
        final var guild = exprGuild.getSingle(event);
        if (guild == null)
            return;

        final var varName = listVariable.getName().toString(event);
        Variables.setVariable(varName, null, event, listVariable.isLocal());

        final var roleMemberCount = guild.retrieveRoleMemberCounts().complete();
        for (RoleMemberCount roleCount : roleMemberCount.asList()) {
            final var key = roleCount.getRoleId();
            final var amount = roleCount.getMemberCount();

            String fullName = varName.substring(0, varName.length() - 1) + key;
            Variables.setVariable(fullName, amount, event, listVariable.isLocal());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "retrieve role member counts of guild " + exprGuild.toString(event, debug) + " and store them in " + listVariable.toString(event, debug);
    }
}
