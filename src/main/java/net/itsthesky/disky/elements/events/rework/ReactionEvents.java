package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.api.events.specific.MessageEvent;

public class ReactionEvents {

    static {
        EventRegistryFactory.builder(MessageReactionAddEvent.class)
                .name("Reaction Add")
                .patterns("(reaction|emote)[s] add[ed]")
                .description("Fired when a message, that can be seen by the bot, receive a reaction.",
                        "This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
                .implement(MessageEvent.class)
                .restValue("message", MessageReactionAddEvent::retrieveMessage)
                .value(Guild.class, MessageReactionAddEvent::getGuild)
                .value(Member.class, MessageReactionAddEvent::getMember)
                .value(User.class, MessageReactionAddEvent::getUser)
                .value(MessageChannel.class, MessageReactionAddEvent::getChannel)
                .value(Emote.class, event -> Emote.fromUnion(event.getEmoji()))
                .value(MessageReaction.class, MessageReactionAddEvent::getReaction)
                .value(long.class, MessageReactionAddEvent::getMessageAuthorIdLong)
                .value(GuildChannel.class, event -> event.isFromGuild() ? event.getGuildChannel() : null)
                .value(TextChannel.class, event -> event.isFromGuild() ? event.getChannel().asTextChannel() : null)
                .value(NewsChannel.class, event -> event.isFromGuild() ? event.getChannel().asNewsChannel() : null)
                .value(ThreadChannel.class, event -> event.isFromGuild() ? event.getChannel().asThreadChannel() : null)
                .value(PrivateChannel.class, event -> !event.isFromGuild() ? event.getChannel().asPrivateChannel() : null)
                .register();

        EventRegistryFactory.builder(MessageReactionRemoveEvent.class)
                .name("Reaction Remove")
                .patterns("(reaction|emote)[s] remove[d]")
                .description("Fired when an user remove a reaction from a specific message.",
                        "This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
                .implement(MessageEvent.class)
                .restValue("message", MessageReactionRemoveEvent::retrieveMessage)
                .value(Guild.class, MessageReactionRemoveEvent::getGuild)
                .value(Member.class, MessageReactionRemoveEvent::getMember)
                .value(User.class, MessageReactionRemoveEvent::getUser)
                .value(MessageChannel.class, MessageReactionRemoveEvent::getChannel)
                .value(Emote.class, event -> Emote.fromUnion(event.getEmoji()))
                .value(GuildChannel.class, event -> event.isFromGuild() ? event.getGuildChannel() : null)
                .value(TextChannel.class, event -> event.isFromGuild() ? event.getChannel().asTextChannel() : null)
                .value(NewsChannel.class, event -> event.isFromGuild() ? event.getChannel().asNewsChannel() : null)
                .value(ThreadChannel.class, event -> event.isFromGuild() ? event.getChannel().asThreadChannel() : null)
                .value(PrivateChannel.class, event -> !event.isFromGuild() ? event.getChannel().asPrivateChannel() : null)
                .register();

        EventRegistryFactory.builder(MessageReactionRemoveAllEvent.class)
                .name("Reaction Remove All")
                .patterns("(reaction|emote)[s] (remove[d] all|clear|reset)")
                .description("Fired when an user remove every reactions from a message.",
                        "This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
                .implement(MessageEvent.class)
                .restValue("message", event -> event.getChannel().retrieveMessageById(event.getMessageId()))
                .value(Guild.class, MessageReactionRemoveAllEvent::getGuild)
                .value(MessageChannel.class, MessageReactionRemoveAllEvent::getChannel)
                .value(GuildChannel.class, event -> event.isFromGuild() ? event.getGuildChannel() : null)
                .value(TextChannel.class, event -> event.isFromGuild() ? event.getChannel().asTextChannel() : null)
                .value(NewsChannel.class, event -> event.isFromGuild() ? event.getChannel().asNewsChannel() : null)
                .value(ThreadChannel.class, event -> event.isFromGuild() ? event.getChannel().asThreadChannel() : null)
                .value(PrivateChannel.class, event -> !event.isFromGuild() ? event.getChannel().asPrivateChannel() : null)
                .author(event -> event.isFromGuild() ? event.getGuild() : null)
                .register();
    }
}