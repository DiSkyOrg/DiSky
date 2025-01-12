package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.User;

@Name("User is Bot")
@Description("Check either the provided user is a discord bot or not.")
@Examples({"event-user is a discord bot",
"event-member is not a discord bot"})
public class IsBot extends PropertyCondition<User> {

	static {
		register(
				IsBot.class,
				PropertyType.BE,
				"[a] discord bot",
				"users"
		);
	}

	@Override
	public boolean check(User user) {
		return user.isBot();
	}

	@Override
	protected String getPropertyName() {
		return "discord bot";
	}
}
