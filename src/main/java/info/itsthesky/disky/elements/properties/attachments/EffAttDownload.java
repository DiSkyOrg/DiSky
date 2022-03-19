package info.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Name("Download Attachment")
@Description("Download the specific attachment to a file path.")
@Examples("download {_attachment} in folder \"plugins/data/attachments/\"")
public class EffAttDownload extends WaiterEffect {

    static {
        Skript.registerEffect(EffAttDownload.class,
                "(download|dl) [the] [attachment] %attachment% (in|to) [the] [(folder|path)] %string%");
    }

    private Expression<Message.Attachment> exprAtt;
    private Expression<String> exprPath;

    @SuppressWarnings("unchecked")
    @Override
    public boolean initEffect(Expression @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        this.exprAtt = (Expression<Message.Attachment>) exprs[0];
        this.exprPath = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e) {
        Message.Attachment attachment = exprAtt.getSingle(e);
        String path = exprPath.getSingle(e);
        if (attachment == null || path == null) return;
        File file = new File(path);
        if (file.isDirectory()) {
            DiSky.getErrorHandler().exception(e, "DiSky tried to download an attachment, but cannot since the specified path is a folder and not a file.");
            return;
        }
        if (file.exists())
            file.delete();

        try (InputStream in = new URL(attachment.getUrl()).openStream()) {
            Files.copy(in, file.toPath());
        } catch (IOException ex) {
            DiSky.getErrorHandler().exception(e, ex);
        }
        restart();
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "download attachment " + exprAtt.toString(e, debug) + " to folder " + exprPath.toString(e, debug);
    }

}
