package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IInviteContainer;
import net.dv8tion.jda.api.entities.Invite;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateInvite extends SpecificBotEffect<Invite> {

    static {
        Skript.registerEffect(
                CreateInvite.class,
                "(make|create) [the] [new] invite in [the] [(guild|channel)] %guild/channel% [with max us(e|age)[s] %-number%] [with max (time|age) %-number%] and store (it|the invite) in %object%"
        );
    }

    private Expression<Object> exprEntity;
    private Expression<Number> exprMaxUses;
    private Expression<Number> exprMaxAge;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create invite in "+ exprEntity.toString(e, debug) +" using bot ";
    }

    @Override
    public boolean initEffect(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprEntity = (Expression<Object>) exprs[0];

        exprMaxUses = (Expression<Number>) exprs[1];
        exprMaxAge = (Expression<Number>) exprs[2];

        setChangedVariable((Variable<Invite>) exprs[3]);
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        Object entity = exprEntity.getSingle(e);

        Number maxUses = parseSingle(exprMaxUses, e, null);
        Number maxAge = parseSingle(exprMaxAge, e, null);

        if (entity == null || bot == null) return;

        final IInviteContainer channel;
        if (entity instanceof Guild) {
            Guild guild = bot.getInstance().getGuildById(((Guild) entity).getId());
            if (anyNull(guild)) {
                restart();
                return;
            }
            channel = guild.getTextChannels().get(0);
        } else {
            channel = (IInviteContainer) bot.getInstance().getGuildChannelById(((GuildChannel) entity).getId());
        }

        if (anyNull(channel)) {
            restart();
            return;
        }

        channel
                .createInvite()
                .setMaxUses(maxUses == null ? null : maxUses.intValue())
                .setMaxAge(maxAge == null ? null : maxAge.intValue())
                .queue(this::restart);

    }
}
