package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Name("Create Post")
@Description({"Create a new post in a forum channel. The output value will be the newly created thread channel."})
@Since("4.4.4")
@Examples({
		"create a new post in forum channel with id \"000\" named \"I need help!\" with message \"please help me!\"",
		"create a new post in forum channel with id \"000\" named \"I need help!\" with message \"please help me!\" with tags \"help\" and \"support\""
})
public class CreatePost extends SpecificBotEffect<ThreadChannel> {

	static {
		Skript.registerEffect(
				CreatePost.class,
				"create [a] [new] post in [channel] %forumchannel% (with name|named) %string% [with message] %string/messagecreatebuilder/embedbuilder% [with [the] tags %-strings%] [and store (it|the thread) in %-object%]"
		);
	}

	private Expression<ForumChannel> exprChannel;
	private Expression<String> exprName;
	private Expression<Object> exprMessage;
	private Expression<String> exprTags;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprChannel = (Expression<ForumChannel>) expressions[0];
		exprName = (Expression<String>) expressions[1];
		exprMessage = (Expression<Object>) expressions[2];
		exprTags = (Expression<String>) expressions[3];
		return validateVariable(expressions[4], false, true);
	}

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final ForumChannel channel = parseSingle(exprChannel, e);
		final String name = parseSingle(exprName, e);
		final Object message = parseSingle(exprMessage, e);
		final String[] tags = parseList(exprTags, e, new String[0]);
		if (anyNull(this, channel, message, name)) {
			restart();
			return;
		}

		final MessageCreateBuilder builder;
		if (message instanceof MessageCreateBuilder)
			builder = (MessageCreateBuilder) message;
		else if (message instanceof EmbedBuilder)
			builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
		else
			builder = new MessageCreateBuilder().setContent((String) message);

		final List<ForumTag> parsedTags = new ArrayList<>();
		// Parsing tags
		for (String input : tags) {
			final Matcher numeral = Pattern.compile("^([0-9]+)$").matcher(input);
			if (numeral.matches()) {
				final ForumTag tag = channel.getAvailableTagById(input);
				if (tag == null) {
					Skript.warning("The tag with ID " + input + " doesn't exist in the channel " + channel.getName() + "!");
					continue;
				}
				parsedTags.add(tag);
			} else {
				final List<ForumTag> tag = channel.getAvailableTagsByName(input, true);
				if (tag.isEmpty()) {
					Skript.warning("The tag with name " + input + " doesn't exist in the channel " + channel.getName() + "!");
					continue;
				}
				parsedTags.add(tag.get(0));
			}
		}

		if (channel.isTagRequired() && parsedTags.isEmpty()) {
			Skript.warning("The forum " + channel.getName() + " requires at least one tag to create a post (you provided none)!");
			restart();
			return;
		}

		channel.createForumPost(name, builder.build())
				.setTags(parsedTags.stream().map(tag -> ForumTagSnowflake.fromId(tag.getId())).collect(Collectors.toList()))
				.queue(post -> restart(post.getThreadChannel()), ex -> {
					DiSky.getErrorHandler().exception(e, ex);
					restart();
				});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new post in channel " + exprChannel.toString(e, debug) + " with name " + exprName.toString(e, debug) + " with message " + exprMessage.toString(e, debug) + " with tags " + exprTags.toString(e, debug);
	}
}
