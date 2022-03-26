package info.itsthesky.disky.elements;

import ch.njol.skript.registrations.Converters;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.elements.commands.CommandData;
import info.itsthesky.disky.elements.commands.CommandObject;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class Types {

    public static class DiSkyConverters {

        static {
            Converters.registerConverter(Member.class, Role[].class, member -> member.getRoles().toArray(new Role[0]));
            Converters.registerConverter(Member.class, User.class, Member::getUser);

            Converters.registerConverter(Message.class, String.class, Message::getContentRaw);
            Converters.registerConverter(Emote.class, String.class, Emote::toString);

            Converters.registerConverter(IMentionable.class, String.class, IMentionable::getAsMention);
            Converters.registerConverter(ISnowflake.class, String.class, ISnowflake::getId);
        }

    }

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
        new DiSkyType<>(Category.class, "category",
                channel -> "category named " + channel.getName() + " with id " + channel.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getCategoryById(input))
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
        new DiSkyType<>(ChannelAction.class, "channelaction",
                action -> action.getType().name(),
                null
        ).register();
        DiSkyType.fromEnum(ChannelType.class, "channeltype", "channeltype").register();

        /*
        Components
         */

        new DiSkyType<>(ComponentRow.class, "row",
                row -> "component row with " + row.asComponents(),
                null).register();
        new DiSkyType<>(Modal.Builder.class, "modal",
                modal -> "modal with id " + modal.getId(),
                null).register();
        new DiSkyType<>(Button.class, "button",
                button -> "button with id " + button.getId(),
                null).register();
        new DiSkyType<>(SelectMenu.Builder.class, "dropdown",
                button -> "dropdown with id " + button.getId(),
                null).register();
        new DiSkyType<>(SelectOption.class, "selectoption",
                option -> option.toData().toString(),
                null).register();
        new DiSkyType<>(TextInput.Builder.class, "textinput",
                button -> "textinput with id " + button.getId(),
                null).register();
        DiSkyType.fromEnum(ButtonStyle.class, "buttonstyle", "buttonstyle").register();

        /*
        Slash commands
         */
        new DiSkyType<>(SlashCommandData.class, "slashcommand",
                slash -> "slash command data: " + slash.toData(),
                null).register();
        new DiSkyType<>(SubcommandGroupData.class, "slashcommandgroup",
                slash -> "sub group data: " + slash.toData(),
                null).register();
        new DiSkyType<>(SubcommandData.class, "subslashcommand",
                slash -> "sub command data: " + slash.toData(),
                null).register();
        new DiSkyType<>(OptionData.class, "slashoption",
                slash -> "slash option data: " + slash.toData(),
                null).register();
        new DiSkyType<>(Command.Choice.class, "slashchoice",
                Command.Choice::getName,
                null).register();
        DiSkyType.fromEnum(OptionType.class, "optiontype", "optiontype").register();

        /*
        Guild Entities
         */

        new DiSkyType<>(Role.class, "role",
                role -> "role named " + role.getName() + " with id " + role.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getRoleById(input))
        ).register();

        /*
        Message Entities
         */
        new DiSkyType<>(Message.class, "message",
                Message::getContentRaw,
                // TODO: 11/02/2022 Make message parsing working
                null
        ).register();
        new DiSkyType<>(Message.Attachment.class, "attachment",
                Message.Attachment::getUrl,
                null
        ).register();
        new DiSkyType<>(MessageBuilder.class, "messagebuilder",
                messageBuilder -> messageBuilder.getStringBuilder().toString(),
                null
        ).register();
        new DiSkyType<>(Emote.class, "emote",
                Emote::getAsMention,
                null
        ).register();
        new DiSkyType<>(EmbedBuilder.class, "embedbuilder",
                embedBuilder -> embedBuilder.getDescriptionBuilder().toString(),
                null
        ).register();

        /*
        Global Entities
         */

        new DiSkyType<>(User.class, "user",
                user -> "user " + user.getName() + "#" + user.getDiscriminator(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getUserById(input))
        ).register();
        new DiSkyType<>(CommandObject.class, "discordcommand",
                CommandObject::getName,
                null).register();
        new DiSkyType<>(Guild.class, "guild",
                guild -> "guild named " + guild.getName() + " with id " + guild.getId(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getGuildById(input))
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
