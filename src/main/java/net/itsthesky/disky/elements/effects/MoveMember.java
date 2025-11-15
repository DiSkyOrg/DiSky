package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
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
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Move/Disconnect Member")
@Description({"Move a member to another voice chat or disconnect them from their current voice channel.",
        "You can only move a member if they were previously in a voice channel.",
        "Use the second pattern to disconnect/kick the member from its current voice channel."})
@Examples({"move discord event-member to {_voice}",
        "disconnect discord event-member"})
@Since("4.14.2")
@SeeAlso({Member.class, VoiceChannel.class})
public class MoveMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                MoveMember.class,
                "[voice] move [the] discord [member] %member% to [a] [voice[( |-)channel]] %voicechannel%",
                "[voice] disconnect [the] discord [member] %member%"
        );
    }

    private Expression<Member> exprMember;
    private Expression<VoiceChannel> exprVoiceChannel;
    private boolean isDisconnect = false;

    @Override
    public boolean init(Expression[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);
        isDisconnect = i == 1;

        exprMember = (Expression<Member>) exprs[0];
        if (!isDisconnect)
            exprVoiceChannel = (Expression<VoiceChannel>) exprs[1];

        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Member member = parseSingle(exprMember, e, null);
        final VoiceChannel voice = parseSingle(exprVoiceChannel, e, null);

        if (member == null)
            return;

        try {
            member.getGuild().moveVoiceMember(member, voice).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "move " + exprMember.toString(e, debug) + " to channel " + exprVoiceChannel.toString(e, debug);
    }
}