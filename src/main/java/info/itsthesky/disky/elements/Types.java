package info.itsthesky.disky.elements;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.elements.commands.CommandEvent;
import info.itsthesky.disky.elements.commands.CommandObject;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import info.itsthesky.disky.elements.properties.polls.PollAnswerData;
import info.itsthesky.disky.elements.sections.automod.FilterType;
import info.itsthesky.disky.managers.wrappers.AutoModRuleBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Types {

    public static class DiSkyConverters {

        static {
            Converters.registerConverter(Member.class, Role[].class, member -> member.getRoles().toArray(new Role[0]));
            Converters.registerConverter(Member.class, User.class, Member::getUser);
            Converters.registerConverter(Bot.class, User.class, bot -> bot.getInstance().getSelfUser());

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
                null).eventExpression().register();
        new DiSkyType<>(ComponentInteraction.class, "interaction",
                null, null).eventExpression().register();
        new DiSkyType<>(GuildChannel.class, "guildchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getGuildChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(TextChannel.class, "textchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getTextChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(VoiceChannel.class, "voicechannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getVoiceChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(AudioChannel.class, "audiochannel", Channel::getName, null).eventExpression().register();
        new DiSkyType<>(ThreadChannel.class, "threadchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getThreadChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(Category.class, "category",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getCategoryById(input))
        ).eventExpression().register();
        new DiSkyType<>(NewsChannel.class, "newschannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getNewsChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(StageChannel.class, "stagechannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getStageChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(PrivateChannel.class, "privatechannel",
                PrivateChannel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getPrivateChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(ForumChannel.class, "forumchannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getForumChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(MediaChannel.class, "mediachannel",
                Channel::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getMediaChannelById(input))
        ).eventExpression().register();
        new DiSkyType<>(ChannelAction.class, "channelaction",
                action -> action.getType().name(),
                null
        ).eventExpression().register();
        new DiSkyType<>(RoleAction.class, "roleaction",
                action -> "role action",
                null
        ).eventExpression().register();
        new DiSkyType<>(ChannelType.class, "channeltype",
                type -> type.name().toLowerCase().replace("_", " "),
                input -> {
                    if (input.equalsIgnoreCase("text"))
                        return null;
                    return input.equalsIgnoreCase("chat") ? ChannelType.TEXT : ChannelType.valueOf(input.toUpperCase());
                }
        ).eventExpression().register();

        /*
        Components
         */

        new DiSkyType<>(ComponentRow.class, "row",
                row -> row.asComponents().stream().map(c -> c.toData().toString()).collect(Collectors.toList()).toString(),
                null).eventExpression().register();
        new DiSkyType<>(Modal.Builder.class, "modal",
                Modal.Builder::getId,
                null).eventExpression().register();
        new DiSkyType<>(Button.class, "button",
                ActionComponent::getId,
                null).eventExpression().register();
        new DiSkyType<>(SelectMenu.Builder.class, "dropdown",
                SelectMenu.Builder::getId,
                null).eventExpression().register();
        new DiSkyType<>(SelectOption.class, "selectoption",
                option -> option.toData().toString(),
                null).eventExpression().register();
        new DiSkyType<>(TextInput.Builder.class, "textinput",
                TextInput.Builder::getId,
                null).eventExpression().register();
        DiSkyType.fromEnum(ButtonStyle.class, "buttonstyle", "buttonstyle").register();

        /*
        Slash commands
         */
        new DiSkyType<>(SlashCommandData.class, "slashcommand",
                slash -> slash.toData().toString(),
                null).eventExpression().register();
        new DiSkyType<>(SubcommandGroupData.class, "slashcommandgroup",
                slash -> slash.toData().toString(),
                null).eventExpression().register();
        new DiSkyType<>(SubcommandData.class, "subslashcommand",
                slash -> slash.toData().toString(),
                null).eventExpression().register();
        new DiSkyType<>(OptionData.class, "slashoption",
                slash -> slash.toData().toString(),
                null).eventExpression().register();
        new DiSkyType<>(Command.Choice.class, "slashchoice",
                Command.Choice::getName,
                null).eventExpression().register();
        DiSkyType.fromEnum(OptionType.class, "optiontype", "optiontype").register();

        /*
        Guild Entities
         */

        new DiSkyType<>(Role.class, "role",
                Role::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getRoleById(input))
        ).eventExpression().register();
        new DiSkyType<>(ForumTag.class, "forumtag",
                ForumTag::getName,
                null
        ).eventExpression().register();
        new DiSkyType<>(ScheduledEvent.class, "scheduledevent",
                ScheduledEvent::getName,
                null
        ).eventExpression().register();
        new DiSkyType<>(Webhook.class, "webhook",
                Webhook::getName,
                null
        ).eventExpression().register();

        /*
        Message Entities
         */
        new DiSkyType<>(Message.class, "message",
                Message::getContentRaw,
                id -> CommandEvent.lastEvent == null ? null : CommandEvent.lastEvent.getMessageChannel().getHistory().getMessageById(id)
        ).eventExpression().register();
        new DiSkyType<>(Message.Attachment.class, "attachment",
                Message.Attachment::getUrl,
                null
        ).eventExpression().register();
        new DiSkyType<>(MessageCreateBuilder.class, "messagecreatebuilder",
                MessageCreateBuilder::getContent, null
        ).eventExpression().register();
        new DiSkyType<>(Emote.class, "emote",
                Emote::getAsMention,
                null
        ).eventExpression().register();
        new DiSkyType<>(MessageReaction.class, "reaction",
                messageReaction -> Emote.fromUnion(messageReaction.getEmoji()).getAsMention(),
                null
        ).eventExpression().register();
        new DiSkyType<>(Sticker.class, "sticker",
                Sticker::getName,
                null
        ).eventExpression().register();
        new DiSkyType<>(EmbedBuilder.class, "embedbuilder",
                embedBuilder -> embedBuilder.getDescriptionBuilder().toString(),
                null
        ).eventExpression().register();
        new DiSkyType<>(MessageEmbed.Field.class, "embedfield",
                MessageEmbed.Field::getValue,
                null
        ).eventExpression().register();
        new DiSkyType<>(MessagePollBuilder.class, "messagepollbuilder",
                v -> "a discord poll",
                null
        ).eventExpression().register();
        new DiSkyType<>(PollAnswerData.class, "pollanswer",
                PollAnswerData::asString,
                null
        ).eventExpression().register();
        new DiSkyType<>(MessagePoll.class, "messagepoll",
                v -> v.getQuestion().getText(),
                null
        ).eventExpression().register();

        /*
        Global Entities
         */

        if (!DiSky.getConfiguration().getOrSetDefault("fix-skript-online-status", false))
            DiSkyType.fromEnum(OnlineStatus.class, "onlinestatus", "onlinestatus").register();

        DiSkyType.fromEnum(Permission.class, "permission", "permission").register();
        new DiSkyType<>(User.class, "user",
                user -> user.getEffectiveName(),
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getUserById(input))
        ).eventExpression().register();
        new DiSkyType<>(User.Profile.class, "userprofile",
                User.Profile::toString,
                null
        ).eventExpression().register();
        new DiSkyType<>(Activity.class, "activity",
                Activity::getName,
                null
        ).eventExpression().register();
        new DiSkyType<>(Guild.Ban.class, "ban",
                Guild.Ban::getReason,
                null
        ).eventExpression().register();
        new DiSkyType<>(Invite.class, "invite",
                Invite::getUrl,
                null
        ).eventExpression().register();
        new DiSkyType<>(AuditLogEntry.class, "logentry",
                ISnowflake::getId,
                null
        ).eventExpression().register();
        new DiSkyType<>(CommandObject.class, "discordcommand",
                CommandObject::getName,
                null).eventExpression().register();
        new DiSkyType<>(Guild.class, "guild",
                Guild::getName,
                input -> DiSky.getManager().searchIfAnyPresent(bot -> bot.getInstance().getGuildById(input))
        ).eventExpression().register();
        new DiSkyType<>(Member.class, "member",
                member -> member.getUser().getEffectiveName(),
                id -> {
                    final CommandEvent event = CommandEvent.lastEvent;
                    if (event == null)
                        return null;
                    if (event.getJDAEvent().isFromGuild())
                        return CommandEvent.lastEvent.getGuild().getMemberById(id);
                    return null;
                }).eventExpression().register();
        new DiSkyType<>(Bot.class, "bot",
                member -> member.getInstance().getSelfUser().getEffectiveName(),
                input -> DiSky.getManager().fromName(input))
                .eventExpression()
                .register();
        new DiSkyType<>(ApplicationInfo.class, "applicationinfo",
                ApplicationInfo::getName, null).eventExpression()
                .register();
        DiSkyType.fromEnum(Member.MemberFlag.class, "memberflag", "memberflag")
                .eventExpression()
                .register();
        new DiSkyType<>(AutoModRuleBuilder.class, "automodrule",
                AutoModRuleBuilder::toString,
                null).eventExpression().register();
        DiSkyType.fromEnum(FilterType.class, "filtertype", "filtertype")
                .eventExpression().register();
    }
}
