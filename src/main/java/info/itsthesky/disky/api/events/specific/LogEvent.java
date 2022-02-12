package info.itsthesky.disky.api.events.specific;

import net.dv8tion.jda.api.entities.User;

/**
 * Mean a {@link User} changed something on the logs
 */
public interface LogEvent {

    User getActionAuthor();

}
