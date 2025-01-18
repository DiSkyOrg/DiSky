package net.itsthesky.disky.elements.sections.welcome;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Add Welcome Screen Channel")
@Description({"Add a channel to the welcome screen of a guild.",
		"Can only be used in a 'modify welcome screen' section."})
@Examples("discord command setup <guild>:\n" +
		"    trigger:\n" +
		"        modify welcome screen of arg-1:\n" +
		"            change the screen description to \"Welcome to the server! Please read the rules and get roles before chatting.\"\n" +
		"            add channel with id \"937001799896956991\" named \"Read our rules\" with reaction \"\uD83D\uDCDC\" to the screen\n" +
		"            add channel with id \"952199041335316520\" named \"Get roles\" with reaction \"\uD83C\uDF9F️\" to the screen")
@Since("4.10.0")
public class AddScreenChannel extends Effect implements ScreenElement {

	static {
		Skript.registerEffect(AddScreenChannel.class,
				"add [the] [channel] %channel% (named|with name) %string% [with [emoji] %-emote%] [to [the] [welcome] screen]");
	}

	private Expression<Channel> exprChannel;
	private Expression<String> exprName;
	private Expression<Emote> exprEmoji;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprChannel = (Expression<Channel>) exprs[0];
		exprName = (Expression<String>) exprs[1];
		exprEmoji = (Expression<Emote>) exprs[2];
		return true;
	}

	@Override
	public @NotNull GuildWelcomeScreenManager apply(@NotNull GuildWelcomeScreenManager manager, @NotNull Event event) {
		final Channel channel = EasyElement.parseSingle(exprChannel, event);
		final String name = EasyElement.parseSingle(exprName, event);
		final Emote emoji = EasyElement.parseSingle(exprEmoji, event);

		if (name == null || !(channel instanceof StandardGuildChannel))
			return manager;

		final List<GuildWelcomeScreen.Channel> current = new ArrayList<>(manager.getWelcomeChannels());
		if (current.size() >= 5)
			return manager;

		if (emoji == null)
			current.add(GuildWelcomeScreen.Channel.of((StandardGuildChannel) channel, name));
		else
			current.add(GuildWelcomeScreen.Channel.of((StandardGuildChannel) channel, name, emoji.getEmoji()));

		return manager.setWelcomeChannels(current);
	}

	@Override
	protected void execute(@NotNull Event e) {
		throw new UnsupportedOperationException("This effect is not supposed to be 'walked'!");
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "add channel " + exprChannel.toString(e, debug) + " named " + exprName.toString(e, debug) + " with emoji " + exprEmoji.toString(e, debug);
	}

}
