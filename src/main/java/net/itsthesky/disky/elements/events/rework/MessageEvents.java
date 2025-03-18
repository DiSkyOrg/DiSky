package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.api.events.specific.MessageEvent;
import net.itsthesky.disky.managers.MessageManager;

public class MessageEvents {

    static {
        EventRegistryFactory.builder(MessageReceivedEvent.class)
                .name("Message Receive")
                .patterns("message receive[d]")
                .description("Fired when any bot receive an actual message.",
                        "This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
                .example("on message received:")
                .example("\tif message is from guild:")
                .example("\t\treply with \"I just received '%event-message%' from %mention tag of event-channel%!\"")
                .example("\telse:")
                .example("\t\treply with \"I just received '%event-message%' from %mention tag of event-user%!\"")
                .implementMessage(GenericMessageEvent::getChannel)
                .value(Message.class, MessageReceivedEvent::getMessage)
                .value(Guild.class, MessageReceivedEvent::getGuild)
                .value(Member.class, MessageReceivedEvent::getMember)
                .value(User.class, MessageReceivedEvent::getAuthor)
                .value(MessageChannel.class, MessageReceivedEvent::getChannel)
                .value(GuildChannel.class, event -> event.isFromGuild() ? event.getGuildChannel() : null)
                .value(TextChannel.class, event -> event.isFromType(ChannelType.TEXT) ? event.getChannel().asTextChannel() : null)
                .value(NewsChannel.class, event -> event.isFromType(ChannelType.NEWS) ? event.getChannel().asNewsChannel() : null)
                .value(ThreadChannel.class, event -> event.isFromType(ChannelType.GUILD_PUBLIC_THREAD) || event.isFromType(ChannelType.GUILD_PRIVATE_THREAD) ? event.getChannel().asThreadChannel() : null)
                .value(PrivateChannel.class, event -> !event.isFromGuild() ? event.getChannel().asPrivateChannel() : null)
                .register();

        EventRegistryFactory.builder(MessageDeleteEvent.class)
                .name("Message Delete")
                .patterns("message delete[d]")
                .description("Fired when any message is deleted.",
                        "Use 'event-string' to get the old message content, only works if this message was cached by DiSky before hand.",
                        "This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
                .implementMessage(GenericMessageEvent::getChannel)
                .value(Guild.class, event -> event.isFromGuild() ? event.getGuild() : null)
                .value(MessageChannel.class, MessageDeleteEvent::getChannel)
                .value(String.class, event -> MessageManager.getManager(event.getJDA()).getDeletedMessageContent(event.getMessageIdLong()))
                .value(Message.class, event -> {
                    DiSky.debug("Getting message from cache ["+ event.getJDA().getSelfUser().getId() +"]: " + event.getMessageIdLong());
                    final MessageManager manager = MessageManager.getManager(event.getJDA());
                    DiSky.debug(manager.getDeletedMessage(event.getMessageIdLong()) == null ? "Message is null" : ("Message is not null: " + manager.getDeletedMessage(event.getMessageIdLong()).getContentRaw()));
                    return manager.getDeletedMessage(event.getMessageIdLong());
                })
                .value(GuildChannel.class, event -> event.isFromGuild() ? event.getGuildChannel() : null)
                .value(TextChannel.class, event -> event.isFromGuild() ? event.getChannel().asTextChannel() : null)
                .value(NewsChannel.class, event -> event.isFromGuild() ? event.getChannel().asNewsChannel() : null)
                .value(ThreadChannel.class, event -> event.isFromGuild() ? event.getChannel().asThreadChannel() : null)
                .value(PrivateChannel.class, event -> !event.isFromGuild() ? event.getChannel().asPrivateChannel() : null)
                .value(Number.class, MessageDeleteEvent::getMessageIdLong)
                .author(event -> event.isFromGuild() ? event.getGuild() : null)
                .register();

        EventRegistryFactory.builder(MessageUpdateEvent.class)
                .name("Message Edit")
                .patterns("message edit[ed]")
                .description("Fired when any message is edited / updated.",
                        "Use 'event-string' to get the old message content, only works if this message was cached by DiSky before hand.",
                        "This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
                .implementMessage(GenericMessageEvent::getChannel)
                .value(Guild.class, event -> event.isFromGuild() ? event.getGuild() : null)
                .value(MessageChannel.class, MessageUpdateEvent::getChannel)
                .value(Message.class, MessageUpdateEvent::getMessage)
                .value(String.class, event -> MessageManager.getManager(event.getJDA()).getEditedMessageOldContent(event.getMessageIdLong()))
                .value(GuildChannel.class, event -> event.isFromGuild() ? event.getGuildChannel() : null)
                .value(TextChannel.class, event -> event.isFromGuild() ? event.getChannel().asTextChannel() : null)
                .value(NewsChannel.class, event -> event.isFromGuild() ? event.getChannel().asNewsChannel() : null)
                .value(ThreadChannel.class, event -> event.isFromGuild() ? event.getChannel().asThreadChannel() : null)
                .value(PrivateChannel.class, event -> !event.isFromGuild() ? event.getChannel().asPrivateChannel() : null)
                .register();
    }
}