package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import de.leonhard.storage.shaded.jetbrains.annotations.Nullable;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Name("Post Message / Upload Files")
@Description({"Post a specific message as text, embed or message builder to a channel.",
"You can either join files path to the message, or only upload files with no message content.",
"Files are parsed from the Minecraft server home, and not your machine's home."})
@Examples({
        "# Send a message to a guild channel",
        "post \"Hello World\" to guild channel with id \"000\"",
        "# Send a message to a member",
        "open private channel of event-member and store it in {_channel}",
        "post \"Hello World\" to {_channel}",
        "# Upload a file without message",
        "upload \"plugins/Skript/scripts/test.sk\" to event-channel and store it in {_msg}",
        "# Upload a file with a message ",
        "post \"Here's my file :D\" to event-channel with files \"plugins/Skript/scripts/test.sk\" and store it in {_msg}",
})
public class PostMessage extends SpecificBotEffect<Message> {

    static {
        Skript.registerEffect(PostMessage.class,
                "(post|dispatch) [the] [message] %string/embedbuilder/messagebuilder% (in|to) [the] [channel] %channel%" +
                        " [with [the] (component|action)[s] [row] %-rows%] [with [the] file[s] %-strings% [with [the] option[s] %-attachmentoptions%]] [and store (it|the message) (inside|in) %-objects%]",
                "upload [the] [file[s]] %strings% [with [the] option[s] %-attachmentoptions%]] (in|to) [the] [channel] %channel% [with [the] (component|action)[s] [row] %-rows%] [and store (it|the message) (inside|in) %-objects%]");
    }

    private Expression<Object> exprMessage;
    private Expression<Object> exprReceiver;
    private Expression<ComponentRow> exprComponents;

    private Expression<String> exprFiles;
    private Expression<AttachmentOption> exprOptions;

    private boolean isOnlyUpload;

    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        isOnlyUpload = i == 1;
        if (isOnlyUpload) {
            exprMessage = null;
            exprFiles = (Expression<String>) expressions[0];
            exprOptions = (Expression<AttachmentOption>) expressions[1];
            exprReceiver = (Expression<Object>) expressions[2];
            exprComponents = (Expression<ComponentRow>) expressions[3];
            return expressions[4] == null || validateVariable(expressions[4], false);
        } else {
            exprMessage = (Expression<Object>) expressions[0];
            exprReceiver = (Expression<Object>) expressions[1];
            exprComponents = (Expression<ComponentRow>) expressions[2];
            exprFiles = (Expression<String>) expressions[3];
            exprOptions = (Expression<AttachmentOption>) expressions[4];
            return expressions[5] == null || validateVariable(expressions[5], false);
        }
    }

    @Override
    public void runEffect(Event e, Bot bot) {
        final @Nullable Object rawContent = EasyElement.parseSingle(exprMessage, e, null);
        final @Nullable MessageBuilder content = JDAUtils.constructMessage(rawContent);
        final Object receiver = exprReceiver.getSingle(e);
        final List<ComponentRow> rows = Arrays.asList(parseList(exprComponents, e, new ComponentRow[0]));
        final String[] files = EasyElement.parseList(exprFiles, e, new String[0]);
        final AttachmentOption[] options = EasyElement.parseList(exprOptions, e, new AttachmentOption[0]);
        if (anyNull(receiver) || (!isOnlyUpload && content == null)) {
            restart();
            return;
        }

        final List<ActionRow> formatted = rows
                .stream()
                .map(ComponentRow::asActionRow)
                .collect(Collectors.toList());

        final MessageChannel channel = bot != null ?
                bot.findMessageChannel((MessageChannel) receiver) : (MessageChannel) receiver;

        MessageAction action;
        if (isOnlyUpload) {
            if (files.length <= 0) {
                restart();
                return;
            }
            final String first = files[0];
            final File file = new File(first);
            if (!file.exists()) {
                Skript.error("File doesn't exist: " + file.getAbsolutePath());
                restart();
                return;
            }
            action = channel.sendFile(file, options);
        } else
            action = channel.sendMessage(content.build());
        event = e;
        action = action.setActionRows(formatted);
        if (files.length > 0)
            for (String path : files) {
                final File file = new File(path);
                if (!file.exists())
                    continue;
                action = action.addFile(file, options);
            }
        action.queue(this::restart);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "post the message " + exprMessage.toString(e, debug) + " in / to " + exprReceiver.toString(e, debug) +
                (changedVariable == null ? "" : " and store the message in " + changedVariable.toString(e, debug));
    }
}
