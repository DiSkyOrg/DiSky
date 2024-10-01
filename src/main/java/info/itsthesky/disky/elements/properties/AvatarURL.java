package info.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.changers.ChangeableSimplePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.INodeHolder;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.core.Utils;
import info.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;

@Name("User / Bot / Guild Avatar")
@Description({"Return the avatar URL of any user, guild or bot.",
"This can be changed for guilds and bots only!"})
@Examples({"avatar of event-guild", "avatar of event-user"})
public class AvatarURL extends ChangeableSimplePropertyExpression<Object, String>
		implements IAsyncChangeableExpression, INodeHolder {

	static {
		register(
				AvatarURL.class,
				String.class,
				"avatar [url]",
				"guild/user/sticker/member/bot/applicationinfo"
		);
	}

	private Node node;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		node = getParser().getNode();
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	@NotNull
	public Node getNode() {
		return node;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "avatar";
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
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
		if (entity instanceof Member)
			return ((Member) entity).getEffectiveAvatarUrl();
		if (entity instanceof Sticker)
			return ((Sticker) entity).getIconUrl();
		if (entity instanceof ApplicationInfo)
			return ((ApplicationInfo) entity).getIconUrl();
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
		change(e, delta, mode, true);
	}

	@Override
	public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
		change(e, delta, mode, false);
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode, boolean async) {
		if (!EasyElement.isValid(delta))
			return;
		final String value = (String) delta[0];
		final Object entity = EasyElement.parseSingle(getExpr(), e, null);
		if (entity == null || entity instanceof User)
			return;

		RestAction<?> action = null;

		if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
			if (entity instanceof final Guild guild)
				action = guild.getManager().setIcon(null);
			if (entity instanceof final Bot bot)
				action = bot.getInstance().getSelfUser().getManager().setAvatar(null);
		} else {
			final var parsedIcon = SkriptUtils.parseIcon(value);
			if (parsedIcon == null) {
				DiSkyRuntimeHandler.error(new IllegalArgumentException("Cannot parse the given icon URL: " + value), node);
				return;
			}

			if (entity instanceof final Guild guild)
				action = guild.getManager().setIcon(parsedIcon);
			else if (entity instanceof final Bot bot)
				action = bot.getInstance().getSelfUser().getManager().setAvatar(parsedIcon);
		}

		if (action != null) {
			if (async) action.complete();
			else action.queue();
		}
	}
}
