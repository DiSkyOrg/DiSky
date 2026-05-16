package net.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.itsthesky.disky.elements.componentsv2.base.INewComponentBuilder;
import net.itsthesky.disky.elements.properties.polls.PollAnswerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Discord ID")
@Description({"Get the unique long value (ID) that represent a discord entity.",
		"For poll answers, this is the 1-indexed position of the answer within its poll (first answer is 1).",
		"Returns null for poll answers built locally (e.g. via 'new poll answer with content ...') as the ID is only assigned by Discord once the poll has been sent."})
@Examples({"discord id of event-channel",
		"discord id of event-guild",
		"discord id of {_pollAnswer}"})
public class DiscordId extends SimplePropertyExpression<Object, String> {

	static {
		register(DiscordId.class,
				String.class,
				"discord id",
				"channel/role/user/threadchannel/scheduledevent/member/sticker/message/dropdown/button/guild/applicationinfo/webhook/newcomponent/pollanswer");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "discord id";
	}

	@Override
	public @Nullable String convert(Object entity) {
		if (entity instanceof final ISnowflake snowflake)
			return snowflake.getId();
		if (entity instanceof final ActionComponent component)
			return component.getCustomId();
		if (entity instanceof final INewComponentBuilder<?> newComponentBuilder)
			return newComponentBuilder.getCustomId();
		if (entity instanceof final PollAnswerData pollAnswer)
			return pollAnswer.getId() == 0L ? null : String.valueOf(pollAnswer.getId());
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
}