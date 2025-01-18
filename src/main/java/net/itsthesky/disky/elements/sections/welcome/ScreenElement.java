package net.itsthesky.disky.elements.sections.welcome;

import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an element of Skript that relay on the {@link ModifySection modify section}.
 */
public interface ScreenElement {

	@NotNull GuildWelcomeScreenManager apply(@NotNull GuildWelcomeScreenManager manager,
											 @NotNull Event event);

}
