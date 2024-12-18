package info.itsthesky.disky.elements.sections.message;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Name("Message Builder Attachments")
@Description({"Attachments of a message builder",
		"Supports SkImage's images if the addon is installed.",
		"See also: 'Create (rich) Message'"})
public class MessageAttachments extends MultiplyPropertyExpression<MessageCreateBuilder, Object> {

	static {
		register(
				MessageAttachments.class,
				Object.class,
				"(attachment|image)[s]",
				"messagecreatebuilder"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!getParser().isCurrentSection(CreateMessage.class)) {
			Skript.error("You can only use the 'message attachments' expression inside a 'create message' section");
			return false;
		}
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
			if (DiSky.isSkImageInstalled())
				return new Class[]{
						String.class, String[].class,
						BufferedImage.class, BufferedImage[].class
				};
			else
				return new Class[]{String.class, String[].class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final MessageCreateBuilder builder = EasyElement.parseSingle(getExpr(), e, null);

		if (builder == null)
			return;

		final List<FileUpload> uploads = new ArrayList<>();

		int imageCount = 1;
		for (Object file : delta) {
			if (file instanceof String) {
				final File f = new File((String) file);
				if (!f.exists())
					continue;
				uploads.add(FileUpload.fromData(f));
			} else if (file instanceof BufferedImage) {

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					ImageIO.write((BufferedImage) file, "png", baos);
				} catch (IOException ex) {
					Skript.error("Unable to convert image to byte array:");
					ex.printStackTrace();
					continue;
				}
				final byte[] bytes = baos.toByteArray();
				uploads.add(FileUpload.fromData(bytes, "image"+ imageCount +".png"));
				imageCount++;
			}
		}

		builder.addFiles(uploads);
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	protected String getPropertyName() {
		return "attachments";
	}

	@Override
	protected Object[] convert(MessageCreateBuilder messageCreateBuilder) {
		return messageCreateBuilder.getAttachments().stream()
				.map(FileUpload::getName)
				.toArray(String[]::new);
	}

}
