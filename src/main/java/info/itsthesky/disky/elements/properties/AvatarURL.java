package info.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.changers.ChangeableSimplePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;

@Name("User / Bot / Guild Avatar")
@Description({"Return the avatar URL of any user, guild or bot.",
"This can be changed for guilds and bots only!"})
@Examples({"avatar of event-guild", "avatar of event-user"})
public class AvatarURL extends ChangeableSimplePropertyExpression<Object, String> {

	static {
		register(
				AvatarURL.class,
				String.class,
				"avatar [url]",
				"guild/member/user/bot"
		);
	}

	@Override
	public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;
		final String value = (String) delta[0];
		final Object entity = EasyElement.parseSingle(getExpr(), e, null);
		if (entity == null || entity instanceof User)
			return;

		if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
			if (entity instanceof Guild)
				Utils.catchAction(((Guild) entity).getManager().setIcon(null), e);
			if (entity instanceof Bot)
				Utils.catchAction(((Bot) entity).getInstance().getSelfUser().getManager().setAvatar(null), e);
			return;
		}

		if (entity instanceof Guild || entity instanceof Bot) {

			final InputStream iconStream;
			if (Utils.isURL(value)) {
				try {
					iconStream = new URL(value).openStream();
				} catch (IOException ioException) {
					DiSky.getErrorHandler().exception(e, ioException);
					return;
				}
			} else {
				final File iconFile = new File(value);
				if (iconFile == null || !iconFile.exists())
					return;
				try {
					iconStream = new FileInputStream(iconFile);
				} catch (FileNotFoundException ex) {
					DiSky.getErrorHandler().exception(e, ex);
					return;
				}
			}

			final Icon icon;
			try {
				icon = Icon.from(iconStream);
			} catch (IOException ioException) {
				DiSky.getErrorHandler().exception(e, ioException);
				return;
			}

			if (entity instanceof Guild)
				Utils.catchAction(((Guild) entity).getManager().setIcon(icon), e);
			else
				Utils.catchAction(((Bot) entity).getInstance().getSelfUser().getManager().setAvatar(icon), e);
		}
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "avatar";
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.RESET, Changer.ChangeMode.DELETE))
			return new Class[] {String.class};
		return new Class[0];
	}

	@Override
	public @Nullable String convert(Object entity) {
		if (entity instanceof Bot)
			return ((Bot) entity).getInstance().getSelfUser().getEffectiveAvatarUrl();
		if (entity instanceof Guild)
			return ((Guild) entity).getIconUrl();
		if (entity instanceof User)
			return ((User) entity).getEffectiveAvatarUrl();
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
}
