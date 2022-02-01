package info.itsthesky.disky.elements;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;

public class Types {

    static {

        /*
        Channel Entities
         */
        new DiSkyType<>(Channel.class, "channel",
                channel -> "channel named " + channel.getName() + " with id " + channel.getId(),
                null).register();
        new DiSkyType<>(GuildChannel.class, "guildchannel",
                channel -> "guild channel named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getGuildChannelById(input))
        ).register();
        new DiSkyType<>(TextChannel.class, "textchannel",
                channel -> "text channel named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getTextChannelById(input))
        ).register();
        new DiSkyType<>(VoiceChannel.class, "voicechannel",
                channel -> "voice channel named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getVoiceChannelById(input))
        ).register();
        new DiSkyType<>(ThreadChannel.class, "threadchannel",
                channel -> "thread channel named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getThreadChannelById(input))
        ).register();
        new DiSkyType<>(NewsChannel.class, "newschannel",
                channel -> "news channel named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getNewsChannelById(input))
        ).register();
        new DiSkyType<>(StageChannel.class, "stagechannel",
                channel -> "stage channel named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getStageChannelById(input))
        ).register();
        new DiSkyType<>(PrivateChannel.class, "privatechannel",
                channel -> "private channel of " + channel.getUser().getName() + "#" + channel.getUser().getDiscriminator(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getPrivateChannelById(input))
        ).register();

        /*
        Guild Entities
         */

        new DiSkyType<>(Role.class, "role",
                role -> "role named " + role.getName() + " with id " + role.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getRoleById(input))
        ).register();

        /*
        Global Entities
         */

        new DiSkyType<>(User.class, "user",
                user -> "user " + user.getName() + "#" + user.getDiscriminator(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getUserById(input))
        ).register();
        // TODO: 30/01/2022 Make the parser of member working
        new DiSkyType<>(Member.class, "member",
                member -> "member " + member.getUser().getName() + "#" + member.getUser().getDiscriminator() +" in guild " + member.getGuild().getName(),
                null).register();
        new DiSkyType<>(Bot.class, "bot",
                member -> "bot " + member.getInstance().getSelfUser().getName() + "#" + member.getInstance().getSelfUser().getDiscriminator(),
                input -> DiSky.getManager().fromName(input)).register();
    }

}
