package info.itsthesky.disky.elements;

import ch.njol.skript.registrations.Converters;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.elements.commands.CommandData;
import info.itsthesky.disky.elements.commands.CommandEvent;
import info.itsthesky.disky.elements.commands.CommandFactory;
import info.itsthesky.disky.elements.commands.CommandObject;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Types {

    public static class DiSkyConverters {

        static {
            Converters.registerConverter(Member.class, Role[].class, member -> member.getRoles().toArray(new Role[0]));
            Converters.registerConverter(Member.class, User.class, Member::getUser);

            Converters.registerConverter(Message.class, String.class, Message::getContentRaw);
            Converters.registerConverter(Emote.class, String.class, Emote::toString);

            Converters.registerConverter(IMentionable.class, String.class, IMentionable::getAsMention);
            Converters.registerConverter(ISnowflake.class, String.class, ISnowflake::getId);

            Converters.registerConverter(Button.class, ComponentRow.class, btn -> new ComponentRow(null, null, Collections.singletonList(btn)));
            Converters.registerConverter(SelectMenu.class, ComponentRow.class, menu -> new ComponentRow(menu, null, new ArrayList<>()));
        }

    }

    static {

        /*
        Channel Entities
         */
        new DiSkyType<>(Channel.class, "channel",
                Channel::getName,
                null).register();
        new DiSkyType<>(GuildChannel.class, "guildchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getGuildChannelById(input))
        ).register();
        new DiSkyType<>(TextChannel.class, "textchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getTextChannelById(input))
        ).register();
        new DiSkyType<>(VoiceChannel.class, "voicechannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getVoiceChannelById(input))
        ).register();
        new DiSkyType<>(ThreadChannel.class, "threadchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getThreadChannelById(input))
        ).register();
        new DiSkyType<>(Category.class, "category",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getCategoryById(input))
        ).register();
        new DiSkyType<>(NewsChannel.class, "newschannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getNewsChannelById(input))
        ).register();
        new DiSkyType<>(StageChannel.class, "stagechannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getStageChannelById(input))
        ).register();
        new DiSkyType<>(PrivateChannel.class, "privatechannel",
                PrivateChannel::getName,
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
                row -> row.asComponents().stream().map(c -> c.toData().toString()).collect(Collectors.toList()).toString(),
                null).register();
        new DiSkyType<>(Modal.Builder.class, "modal",
                Modal.Builder::getId,
                null).register();
        new DiSkyType<>(Button.class, "button",
                ActionComponent::getId,
                null).register();
        new DiSkyType<>(SelectMenu.Builder.class, "dropdown",
                SelectMenu.Builder::getId,
                null).register();
        new DiSkyType<>(SelectOption.class, "selectoption",
                option -> option.toData().toString(),
                null).register();
        new DiSkyType<>(TextInput.Builder.class, "textinput",
                TextInput.Builder::getId,
                null).register();
        DiSkyType.fromEnum(ButtonStyle.class, "buttonstyle", "buttonstyle").register();

        /*
        Slash commands
         */
        new DiSkyType<>(SlashCommandData.class, "slashcommand",
                slash -> slash.toData().toString(),
                null).register();
        new DiSkyType<>(SubcommandGroupData.class, "slashcommandgroup",
                slash -> slash.toData().toString(),
                null).register();
        new DiSkyType<>(SubcommandData.class, "subslashcommand",
                slash -> slash.toData().toString(),
                null).register();
        new DiSkyType<>(OptionData.class, "slashoption",
                slash -> slash.toData().toString(),
                null).register();
        new DiSkyType<>(Command.Choice.class, "slashchoice",
                Command.Choice::getName,
                null).register();
        DiSkyType.fromEnum(OptionType.class, "optiontype", "optiontype").register();

        /*
        Guild Entities
         */

        new DiSkyType<>(Role.class, "role",
                Role::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getRoleById(input))
        ).register();

        /*
        Message Entities
         */
        new DiSkyType<>(Message.class, "message",
                Message::getContentRaw,
                id -> CommandEvent.lastEvent.getMessageChannel().getHistory().getMessageById(id)
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

        DiSkyType.fromEnum(AttachmentOption.class, "attachmentoption", "attachmentoption").register();
        DiSkyType.fromEnum(OnlineStatus.class, "onlinestatus", "onlinestatus").register();
        new DiSkyType<>(User.class, "user",
                user -> user.getName() + "#" + user.getDiscriminator(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getUserById(input))
        ).register();
        new DiSkyType<>(AuditLogEntry.class, "auditlogentry",
                ISnowflake::getId,
                null
        ).register();
        new DiSkyType<>(CommandObject.class, "discordcommand",
                CommandObject::getName,
                null).register();
        new DiSkyType<>(Guild.class, "guild",
                Guild::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getGuildById(input))
        ).register();
        new DiSkyType<>(Member.class, "member",
                member -> member.getUser().getName() + "#" + member.getUser().getDiscriminator(),
                id -> {
                    final CommandEvent event = CommandEvent.lastEvent;
                    if (event.getJDAEvent().isFromGuild())
                        return CommandEvent.lastEvent.getGuild().getMemberById(id);
                    return null;
                }).register();
        new DiSkyType<>(Bot.class, "bot",
                member -> member.getInstance().getSelfUser().getName() + "#" + member.getInstance().getSelfUser().getDiscriminator(),
                input -> DiSky.getManager().fromName(input)).register();
    }
}
