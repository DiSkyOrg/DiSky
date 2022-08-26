package info.itsthesky.disky.elements.sections.message;

import info.itsthesky.disky.api.skript.ReturningSection;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateMessage extends ReturningSection<MessageCreateBuilder> {

	public static class message extends LastBuilderExpression<MessageCreateBuilder, CreateMessage> { }

	static {
		register(
				CreateMessage.class,
				MessageCreateBuilder.class,
				message.class,
				"(make|create) [a] [new] message"
		);
	}

	@Override
	public MessageCreateBuilder createNewValue() {
		return new MessageCreateBuilder();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new message";
	}

}
