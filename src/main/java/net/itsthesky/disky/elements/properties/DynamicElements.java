package net.itsthesky.disky.elements.properties;

import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.pagination.PinnedMessagePaginationAction;
import net.itsthesky.disky.api.skript.reflects.ReflectClassFactory;
import net.itsthesky.disky.api.skript.reflects.state.SkriptStateRegistry;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModExecution;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.itsthesky.disky.core.SkriptUtils;

import static net.itsthesky.disky.api.skript.reflects.ReflectClassFactory.register;

public final class DynamicElements {

    static {
        registerLogs();
        registerThreadProperties();
        registerAutoMod();
        registerOtherProperties();
    }

    public static void registerLogs() {
        register( "logentry", "logged user", User.class, "log[ged] (user|author)", AuditLogEntry::getUser,
                new ReflectClassFactory.Documentation("Logged User", "The user who triggered the log entry.",
                        "logged user of event-logentry", "4.11.0"));
        register( "logentry", "logged guild", Guild.class, "log[ged] guild", AuditLogEntry::getGuild,
                new ReflectClassFactory.Documentation("Logged Guild", "The guild where the log entry has been triggered.",
                        "logged guild of event-logentry", "4.11.0"));
        register( "logentry", "logged id", String.class, "log[ged] id", AuditLogEntry::getId,
                new ReflectClassFactory.Documentation("Logged ID", "The ID of the log entry.",
                        "logged id of event-logentry", "4.11.0"));
        register( "logentry", "logged action", ActionType.class, "log[ged] action [type]", AuditLogEntry::getType,
                new ReflectClassFactory.Documentation("Logged Action", "The action type of the log entry.",
                        "logged action of event-logentry", "4.11.0"));
        register( "logentry", "logged reason", String.class, "log[ged] reason", AuditLogEntry::getReason,
                new ReflectClassFactory.Documentation("Logged Reason", "The action reason of the log entry.",
                        "logged reason of event-logentry", "4.17.2"));
    }

    public static void registerThreadProperties() {

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "locked", ThreadChannel::isLocked,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setLocked(value)
                            : entity.getManager().setLocked(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "archived", ThreadChannel::isArchived,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setArchived(value)
                            : entity.getManager().setArchived(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "pinned", ThreadChannel::isPinned,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setPinned(value)
                            : entity.getManager().setPinned(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "invitable", ThreadChannel::isInvitable,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setInvitable(value)
                            : entity.getManager().setInvitable(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

    }

    public static void registerOtherProperties() {
        register("pinnedmessage", "pin[ned] (date|time)", Date.class, "pinned message date", msg ->
                        SkriptUtils.convertDateTime(((PinnedMessagePaginationAction.PinnedMessage) msg).getTimePinned()),
                new ReflectClassFactory.Documentation("Date of Pinned Message", "The date when the message was pinned.",
                        "pinned date of {_msg}", "4.24.0"));
    }

    public static void registerAutoMod() {
        register("automod", "message content", String.class, "message content", AutoModExecution::getContent,
                new ReflectClassFactory.Documentation("Message Content", "The message content that triggered the automod.",
                        "message content of event-automod", "4.21.0"));

        register("automod", "the matched content", String.class, "matched content", AutoModExecution::getMatchedContent,
                new ReflectClassFactory.Documentation("Matched Content", "The substring match of the message content which triggered this rule.",
                        "matched content of event-automod", "4.21.0"));

        register("automod", "matched keyword", String.class, "matched keyword", AutoModExecution::getMatchedKeyword,
                new ReflectClassFactory.Documentation("Matched Keyword", "the keyword that was found in the message content.",
                        "matched keyword of event-automod", "4.21.0"));

        register("automod", "automod response", AutoModResponse.class, "automod response", AutoModExecution::getResponse,
                new ReflectClassFactory.Documentation("AutoMod Response", "The automod response that has been triggered by this event.",
                        "automod response of event-automod", "4.21.0"));

        register("automod", "rule id", String.class, "rule id", AutoModExecution::getRuleId,
                new ReflectClassFactory.Documentation("Rule ID", "The id of the AutoMod Rule which has been triggered.",
                        "rule id of event-automod", "4.21.0"));

        register("automod", "automod action type", AutoModTriggerType.class, "automod action [type]", AutoModExecution::getTriggerType,
                new ReflectClassFactory.Documentation("AutoMod action", "The action type of the automod.",
                        "automod action of event-automod", "4.21.0"));

        register("automod", "automod alert message id", String.class, "[automod] alert message id", AutoModExecution::getAlertMessageId,
                new ReflectClassFactory.Documentation("Alert Message ID", "The alert message id sent to the alert channel.",
                        "alert message id of event-automod", "4.21.0"));

    }

}
