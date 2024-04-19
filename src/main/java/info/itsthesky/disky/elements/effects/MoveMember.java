package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.*;

@Name("Move Member")
@Description({"Move a member to another voice chat.",
        "You can only move a member if they were previously in a voice channel."})
@Examples({"move event-member to {_voice}"})

public class MoveMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                MoveMember.class,
                "[voice] move [the] discord [member] %member% to [a] [voice[( |-)channel]] %voicechannel%"
        );
    }

    private Expression<Member> exprMember;
    private Expression<VoiceChannel> exprVoiceChannel;

    @Override
    public boolean init(Expression[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMember = (Expression<Member>) exprs[0];
        exprVoiceChannel = (Expression<VoiceChannel>) exprs[1];

        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Member member = parseSingle(exprMember, e, null);
        final VoiceChannel voice = parseSingle(exprVoiceChannel, e, null);

        if (member == null || voice == null) {
            return;
        }

        try {
            member.getGuild().moveVoiceMember(member, voice).complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "move " + exprMember.toString(e, debug) + " to channel " + exprVoiceChannel.toString(e, debug);
    }
}