package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Voice Channel")
@Description({"Get a voice channel from a guild using its unique ID.",
        "Channels are global on discord, means different channels cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("voice channel with id \"000\"")
@Since("4.0.0")
@SeeAlso(VoiceChannel.class)
public class GetVoiceChannel extends BaseGetterExpression<VoiceChannel> {

    static {
        register(GetVoiceChannel.class,
                VoiceChannel.class,
                "voice channel");
    }

    @Override
    protected VoiceChannel get(String id, Bot bot) {
        return bot.getInstance().getVoiceChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "voice channel";
    }

    @Override
    public @NotNull Class<? extends VoiceChannel> getReturnType() {
        return VoiceChannel.class;
    }
}
