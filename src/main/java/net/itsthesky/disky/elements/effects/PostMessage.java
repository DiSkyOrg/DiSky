package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.componentsv2.base.ContainerBuilder;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import okhttp3.MediaType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import static net.itsthesky.disky.api.skript.EasyElement.parseList;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Post Message")
@Description({"Posts a message to a message-channel.",
		"You can send messages in a text, private, news, post or thread channel.",
})
@Examples({"post \"Hello world!\" to text channel with id \"000\"",
		"post last embed to thread channel with id \"000\" and store it in {_message}"})
@Since("4.4.0")
public class PostMessage extends AsyncEffect {

	private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB en bytes
	private static final int REQUIRED_SAMPLE_RATE = 48000;
	private static final double MAX_DURATION = 60.0; // 60 secondes
	private static final double MIN_DURATION = 1.0; // 1 seconde
	private static final int MAX_WAVEFORM_POINTS = 256;

	static {
		Skript.registerEffect(
				PostMessage.class,
				"(post|dispatch) %fileuploads/string/messagecreatebuilder/sticker/embedbuilder/messagepollbuilder/container% (in|to) [the] %channel% [(using|with) [the] [bot] %-bot%] [with [the] reference[d] [message] %-message%] [and store (it|the message) in %-~objects%]",
				"(post|dispatch) voice message %string% (in|to) [the] %channel% [(using|with) [the] [bot] %-bot%] [and store (it|the message) in %-~objects%]"
		);
	}

	private Expression<Object> exprMessage;
	private Expression<Channel> exprChannel;
	private Expression<Bot> exprBot;
	private Expression<Message> exprReference;
	private Expression<Object> exprResult;
	private Node node;
	private boolean isVoiceMessage;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);
		node = getParser().getNode();
		isVoiceMessage = matchedPattern == 1;

		this.exprMessage = (Expression<Object>) expressions[0];
		this.exprChannel = (Expression<Channel>) expressions[1];
		this.exprBot = (Expression<Bot>) expressions[2];

		if (!isVoiceMessage) {
			this.exprReference = (Expression<Message>) expressions[3];
			this.exprResult = (Expression<Object>) expressions[4];
		} else {
			this.exprReference = null;
			this.exprResult = (Expression<Object>) expressions[3];
		}

		return exprResult == null || Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
	}

	@Override
	public void execute(@NotNull Event e) {
		final Object message = exprMessage.isSingle() ? parseSingle(exprMessage, e) : null;
		final Object[] rawUploads = !exprMessage.isSingle() ? exprMessage.getArray(e) : null;

		Channel channel = parseSingle(exprChannel, e);
		final @Nullable Message reference = parseSingle(exprReference, e);
		final Bot bot = Bot.fromContext(exprBot, e);

		if (message == null && (rawUploads == null || rawUploads.length == 0)) {
			DiSkyRuntimeHandler.error(new IllegalArgumentException("Given message or file uploads is empty or invalid."), node, false);
			return;
		}

		if (!DiSkyRuntimeHandler.checkSet(node, exprChannel, channel))
			return;

		if (!MessageChannel.class.isAssignableFrom(channel.getClass())) {
			//Skript.error("The specified channel must be a message channel.");
			DiSkyRuntimeHandler.error(new IllegalArgumentException("The specified channel must be a message channel. (got a " + channel.getClass().getSimpleName() + ")"), node, false);
			return;
		}

		if (isVoiceMessage) {
			handleVoiceMessage((String) message, (MessageChannel) channel, e);
			return;
		}

		MessageCreateAction action;
		if (message instanceof Sticker) {
			final MessageChannel messageChannel = (MessageChannel) channel;
			if (!(messageChannel instanceof GuildMessageChannel)) {
				DiSkyRuntimeHandler.error(new IllegalArgumentException("Stickers can only be sent in guild message channels."), node, false);
				return;
			}

			action = ((GuildMessageChannel) messageChannel).sendStickers((Sticker) message);
		} else {
			final MessageCreateBuilder builder;
			if (rawUploads != null) {
				final var uploads = new ArrayList<FileUpload>();
				for (Object upload : rawUploads)
					uploads.add((FileUpload) upload);
				builder = new MessageCreateBuilder().addFiles(uploads);
			} else if (message instanceof MessageCreateBuilder)
				builder = (MessageCreateBuilder) message;
			else if (message instanceof EmbedBuilder)
				builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
			else if (message instanceof MessagePollBuilder)
				builder = new MessageCreateBuilder().setPoll(((MessagePollBuilder) message).build());
			else if (message instanceof ContainerBuilder)
				builder = new MessageCreateBuilder().useComponentsV2().setComponents(((ContainerBuilder) message).build());
			else
				builder = new MessageCreateBuilder().setContent((String) message);

			action = ((MessageChannel) channel).sendMessage(builder.build());
			if (reference != null) // see https://github.com/discord-jda/JDA/pull/2749
				action = action.setMessageReference(reference);
			if (builder.getPoll() != null)
				action = action.setPoll(builder.getPoll());
		}

		final Message finalMessage;
		try {
			finalMessage = action.complete();
		} catch (Exception ex) {
			DiSkyRuntimeHandler.error(ex, node);
			return;
		}

		if (exprResult == null)
			return;
		exprResult.change(e, new Object[] {finalMessage}, Changer.ChangeMode.SET);
	}

	private void handleVoiceMessage(String filePath, MessageChannel channel, Event e) {
		try {
			File file = new File(filePath);

			// Vérification de l'existence et de la taille du fichier
			if (!file.exists() || !file.isFile()) {
				DiSkyRuntimeHandler.error(
						new IllegalArgumentException("The file at path '" + filePath + "' doesn't exist or isn't a file!"),
						node,
						false
				);
				return;
			}

			if (file.length() > MAX_FILE_SIZE) {
				DiSkyRuntimeHandler.error(
						new IllegalArgumentException("File size exceeds Discord's limit of 25MB!"),
						node,
						false
				);
				return;
			}

			if (!filePath.toLowerCase().endsWith(".wav")) {
				DiSkyRuntimeHandler.error(
						new IllegalArgumentException("Only WAV files are supported for voice messages!"),
						node,
						false
				);
				return;
			}

			// Vérification du format audio
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = audioInputStream.getFormat();
			long frames = audioInputStream.getFrameLength();
			double duration = frames / format.getFrameRate();

			// Vérification de la durée
			if (duration < MIN_DURATION || duration > MAX_DURATION) {
				DiSkyRuntimeHandler.error(
						new IllegalArgumentException("Voice message duration must be between 1 and 60 seconds! Current duration: " +
								String.format("%.2f", duration) + " seconds"),
						node,
						false
				);
				return;
			}

			// Vérification du sample rate
			if (format.getSampleRate() != REQUIRED_SAMPLE_RATE) {
				DiSkyRuntimeHandler.error(
						new IllegalArgumentException("Voice message must have a sample rate of 48000Hz! Current sample rate: " +
								format.getSampleRate() + "Hz"),
						node,
						false
				);
				return;
			}

			// Vérification des canaux audio
			if (format.getChannels() != 1) {
				DiSkyRuntimeHandler.error(
						new IllegalArgumentException("Voice message must be mono (1 channel)! Current channels: " +
								format.getChannels()),
						node,
						false
				);
				return;
			}

			// Générer la waveform
			byte[] audioData = Files.readAllBytes(file.toPath());
			byte[] waveform = generateWaveform(audioData, MAX_WAVEFORM_POINTS);

			FileUpload voiceUpload = FileUpload.fromData(file)
					.asVoiceMessage(
							MediaType.parse("audio/wav"),
							waveform,
							duration
					);

			Message finalMessage = channel
					.sendFiles(voiceUpload)
					.complete();

			if (exprResult != null) {
				exprResult.change(e, new Object[]{finalMessage}, Changer.ChangeMode.SET);
			}

		} catch (Exception ex) {
			DiSkyRuntimeHandler.error(ex, node);
		}
	}

	private static byte[] generateWaveform(byte[] audioData, int numPoints) {
		// Limiter le nombre de points à 256 comme spécifié par Discord
		numPoints = Math.min(numPoints, MAX_WAVEFORM_POINTS);

		byte[] waveform = new byte[numPoints];
		int samplesPerPoint = audioData.length / numPoints;

		// Échantillonner au maximum une fois toutes les 100ms comme spécifié
		int minSamplesPerPoint = (int) (REQUIRED_SAMPLE_RATE * 0.1); // 100ms
		samplesPerPoint = Math.max(samplesPerPoint, minSamplesPerPoint);

		for (int i = 0; i < numPoints; i++) {
			int startIndex = i * samplesPerPoint;
			int endIndex = Math.min(startIndex + samplesPerPoint, audioData.length);

			int sum = 0;
			for (int j = startIndex; j < endIndex; j++) {
				sum += Math.abs(audioData[j]);
			}

			waveform[i] = (byte) ((sum / samplesPerPoint) & 0xFF);
		}

		return waveform;
	}


	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "post " + (isVoiceMessage ? "voice message " : "") +
				exprMessage.toString(e, debug) + " to " + exprChannel.toString(e, debug) +
				(exprResult == null ? "" : " and store it in " + exprResult.toString(e, debug));
	}
}
