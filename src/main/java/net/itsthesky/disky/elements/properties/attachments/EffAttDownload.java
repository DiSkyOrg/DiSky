package net.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;

@Name("Download Attachment")
@Description("Download the specific attachment to a file path.")
@Examples("download {_attachment} in folder \"plugins/data/attachments/\"")
public class EffAttDownload extends AsyncEffect {

    static {
        Skript.registerEffect(EffAttDownload.class,
                "(download|dl) [the] [attachment] %attachment% (in|to) [the] [(folder|path)] %string%");
    }

    private Expression<Message.Attachment> exprAtt;
    private Expression<String> exprPath;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        this.exprAtt = (Expression<Message.Attachment>) exprs[0];
        this.exprPath = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        Message.Attachment attachment = exprAtt.getSingle(e);
        String path = exprPath.getSingle(e);
        if (attachment == null || path == null) return;
        File file = new File(path);
        if (!file.isDirectory() && file.exists())
            file.delete();
        if (file.isDirectory())
            file.mkdirs();

        if (!file.isDirectory()) {
            File parent = file.getParentFile();
            while (parent != null && !parent.exists()) {
                parent.mkdirs();
                parent = parent.getParentFile();
            }
        }

        try {
            attachment.getProxy().downloadToPath(
                    file.isDirectory() ? Paths.get(file.getPath() + "/" + attachment.getFileName()) : Paths.get(file.getPath())
            ).join();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, getNode());
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "download attachment " + exprAtt.toString(e, debug) + " to folder " + exprPath.toString(e, debug);
    }

}
