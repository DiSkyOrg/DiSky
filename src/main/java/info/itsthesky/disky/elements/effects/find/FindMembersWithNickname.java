package info.itsthesky.disky.elements.effects.find;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FindMembersWithNickname extends AsyncEffect {

    static {
        Skript.registerEffect(
            FindMembersWithNickname.class,
            "find [the] [discord] member[s] with [the] nick[( |-)]name %string% [case:ignor(e|ing) [the] case] (from|in) [the] [guild] %guild% and store (them|the member[s]) in %~objects%"
        );
    }

    private Expression<String> exprNickname;
    private Expression<Guild> exprGuild;
    private Expression<Object> exprResult;
    private Node node;
    private boolean ignoreCase;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprNickname = (Expression<String>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];

        node = getParser().getNode();
        ignoreCase = parseResult.hasTag("case");

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final String nickname = exprNickname.getSingle(event);
        final Guild guild = exprGuild.getSingle(event);
        if (nickname == null || guild == null)
            return;

        final List<Member> members;
        try {
            members = guild.findMembers(member -> {
                if (ignoreCase) {
                    return member.getEffectiveName().equalsIgnoreCase(nickname);
                } else {
                    return member.getEffectiveName().equals(nickname);
                }
            }).get();
        } catch (Exception e) {
            DiSky.getErrorHandler().exception(event, e);
            return;
        }

        exprResult.change(event, members.toArray(new Member[0]), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "find members with nickname " + exprNickname.toString(event, debug)
                + " in guild " + exprGuild.toString(event, debug)
                + " and store them in " + exprResult.toString(event, debug);
    }

}
