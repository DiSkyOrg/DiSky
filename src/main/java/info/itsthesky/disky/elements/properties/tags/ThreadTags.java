package info.itsthesky.disky.elements.properties.tags;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Name("Post / Forum Tags")
@Description({"Get all tags of a forum channel or a thread channel.",
"You can also add or remove tags from a thread channel using this expression.",
"You must add the tag to the forum itself before adding it to the post."})
@Examples({"set {_tags::*} to tags of event-forumchannel",
"add new tag named \"resolved\" with reaction \"x\" to tags of forum with id \"000\""})
public class ThreadTags extends MultiplyPropertyExpression<Object, BaseForumTag> {

	static {
		register(
				ThreadTags.class,
				BaseForumTag.class,
				"tags",
				"threadchannel/forumchannel"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.RESET) {
			return new Class[]{
					String[].class, String.class,
					BaseForumTag.class, BaseForumTag[].class
			};
		}
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final Object entity = EasyElement.parseSingle(getExpr(), e, null);
		if (!(entity instanceof ThreadChannel || entity instanceof ForumChannel))
			return;
		if (entity instanceof ThreadChannel && !((ThreadChannel) entity).getParentChannel().getType().equals(ChannelType.FORUM))
			return;
		final ForumChannel channel = entity instanceof ThreadChannel ? ((ThreadChannel) entity).getParentChannel().asForumChannel() : (ForumChannel) entity;

		final RestAction<?> action;

		if (entity instanceof ThreadChannel) {
			final ThreadChannel threadChannel = (ThreadChannel) entity;
			final List<ForumTag> current = new ArrayList<>(threadChannel.getAppliedTags());

			final List<ForumTag> parsedTags = new ArrayList<>();
			// Parsing tags
			for (Object in : delta) {
				if (in instanceof String) {
					final String input = (String) in;
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
				} else if (in instanceof BaseForumTag) {
					parsedTags.add((ForumTag) in);
				}
			}

			switch (mode) {
				case ADD:
					current.addAll(parsedTags);
					break;
				case REMOVE:
					current.removeAll(parsedTags);
					break;
				case SET:
					current.clear();
					current.addAll(parsedTags);
					break;
				case RESET:
					current.clear();
					break;
			}

			action = threadChannel.getManager().setAppliedTags(current
					.stream()
					.map(tag -> ForumTagSnowflake.fromId(tag.getIdLong()))
					.collect(Collectors.toList()));

		} else {

			final ForumChannel forumChannel = (ForumChannel) entity;
			final List<BaseForumTag> current = new ArrayList<>(forumChannel.getAvailableTags());
			final List<BaseForumTag> baseForumTags = Arrays.asList((BaseForumTag[]) delta);

			switch (mode) {
				case ADD:
					current.addAll(baseForumTags);
					break;
				case REMOVE:
					current.removeAll(baseForumTags);
					break;
				case SET:
					current.clear();
					current.addAll(baseForumTags);
					break;
				case RESET:
					current.clear();
					break;
			}

			action = forumChannel.getManager().setAvailableTags(current);
		}

		action.queue(null, ex -> DiSky.getErrorHandler().exception(e, ex));
	}

	@Override
	public @NotNull Class<? extends ForumTag> getReturnType() {
		return ForumTag.class;
	}

	@Override
	protected String getPropertyName() {
		return "tags";
	}

	@Override
	protected ForumTag[] convert(Object obj) {
		if (obj instanceof ForumChannel)
			return ((ForumChannel) obj).getAvailableTags().toArray(new ForumTag[0]);
		if (obj instanceof ThreadChannel)
			return ((ThreadChannel) obj).getAppliedTags().toArray(new ForumTag[0]);
		Skript.error("Cannot get tags from a " + obj.getClass().getName());
		return new ForumTag[0];
	}
}
