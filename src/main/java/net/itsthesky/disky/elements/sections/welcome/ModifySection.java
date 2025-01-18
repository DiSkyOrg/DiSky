package net.itsthesky.disky.elements.sections.welcome;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Modify Welcome Screen")
@Description({"Modify the welcome screen of a guild.",
"At the end, the request will be sent to discord to update the welcome screen."})
@Examples("discord command setup <guild>:\n" +
		"    trigger:\n" +
		"        modify welcome screen of arg-1:\n" +
		"            change the screen description to \"Welcome to the server! Please read the rules and get roles before chatting.\"\n" +
		"            add channel with id \"937001799896956991\" named \"Read our rules\" with reaction \"\uD83D\uDCDC\" to the screen\n" +
		"            add channel with id \"952199041335316520\" named \"Get roles\" with reaction \"\uD83C\uDF9F️\" to the screen")
@Since("4.10.0")
public class ModifySection extends Section {

	static {
		Skript.registerSection(ModifySection.class, "modify [the] welcome screen (of|for) [the] [guild] %guild%");
	}

	private Expression<Guild> exprGuild;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
		exprGuild = (Expression<Guild>) exprs[0];
		loadCode(sectionNode);
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(@NotNull Event e) {
		final Guild guild = exprGuild.getSingle(e);
		if (guild == null)
			return getNext();
		final List<ScreenElement> elements = new ArrayList<>();

		last.setNext(null);
		TriggerItem triggerItem = first;
		while (triggerItem != null) {
			if (triggerItem instanceof ScreenElement)
				elements.add((ScreenElement) triggerItem);
			triggerItem = triggerItem.getNext();
		}

		GuildWelcomeScreenManager manager = guild.modifyWelcomeScreen();
		for (ScreenElement item : elements)
			manager = item.apply(manager, e);
		manager.queue();

		return getNext();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "modify welcome screen of guild " + exprGuild.toString(e, debug);
	}
}
