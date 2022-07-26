package info.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.sections.SecLoop;
import ch.njol.skript.sections.SecWhile;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SimpleGetterExpression;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlashCompletionEvent extends DiSkyEvent<CommandAutoCompleteInteractionEvent> {

	static {
		register("Slash Completion", SlashCompletionEvent.class, BukkitSlashCompletionEvent.class,
				"slash completion [receive[d]]")
				.description("Fired when Discord ask an argument completion.",
						"Use 'event-string' to get the command name. Use normal return effect to return the actual completions.",
						"Modal can NOT be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitSlashCompletionEvent.class);

		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, String.class,
				event -> event.getJDAEvent().getCommandPath());
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getMessageChannel());

		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asTextChannel() : null);
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitSlashCompletionEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asPrivateChannel() : null);
	}

	public static class Return extends Effect {

		static {
			Skript.registerEffect(
					Return.class,
					"return %slashchoices%"
			);
		}

		private Expression<Command.Choice> exprChoices;

		@Override
		protected void execute(@NotNull Event e) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected @Nullable TriggerItem walk(@NotNull Event e) {
			debug(e, false);
			final Command.Choice[] choices = EasyElement.parseList(exprChoices, e, new Command.Choice[0]);
			if (choices.length == 0)
				return null;

			((BukkitSlashCompletionEvent) e).getJDAEvent().replyChoices(choices).queue();

			TriggerSection parent = getParent();
			while (parent != null) {
				if (parent instanceof SecLoop) {
					((SecLoop) parent).exit(e);
				} else if (parent instanceof SecWhile) {
					((SecWhile) parent).reset();
				}
				parent = parent.getParent();
			}

			return null;
		}

		@Override
		public @NotNull String toString(@Nullable Event e, boolean debug) {
			return "return choices " + exprChoices.toString(e, debug);
		}

		@Override
		public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
			if (!EasyElement.containsEvent(BukkitSlashCompletionEvent.class))
				return false;
			exprChoices = (Expression<Command.Choice>) exprs[0];
			return true;
		}
	}

	@Name("Current Argument")
	@Description("The current argument being completed.")
	@Examples("current argument")
	public static class CurrentArgument extends SimpleGetterExpression<String, BukkitSlashCompletionEvent> {

		static {
			Skript.registerExpression(
					CurrentArgument.class,
					String.class,
					ExpressionType.COMBINED,
					"current( |-)arg[ument] [name]"
			);
		}

		@Override
		protected String getValue() {
			return "current argument";
		}

		@Override
		protected Class<BukkitSlashCompletionEvent> getEvent() {
			return BukkitSlashCompletionEvent.class;
		}

		@Override
		protected String convert(BukkitSlashCompletionEvent event) {
			return event.getJDAEvent().getInteraction().getFocusedOption().getName();
		}

		@Override
		public @NotNull Class<? extends String> getReturnType() {
			return String.class;
		}
	}

	@Name("Slash Command Argument")
	@Description({"Represents a slash command argument.",
			"The name is the ID used when defining the slash command.",
			"Specify the type, so that Skript can parse it correctly. (if it's a number, operation wil be allowed for example)",
			"The type should be the same used when defining the argument in the command."})
	@Examples({"# I'm doing /ban time:30 user:*user id*, so:",
			"set {_time} to argument \"time\" as integer",
			"set {_user} to argument \"user\" as user"})
	public static class ArgValue extends SimpleGetterExpression<Object, BukkitSlashCompletionEvent> {

		static {
			Skript.registerExpression(
					ArgValue.class,
					Object.class,
					ExpressionType.COMBINED,
					"[the] arg[ument] [(named|with name)] %string% as %optiontype%"
			);
		}

		private Expression<String> exprName;
		private OptionType type;

		@Override
		@SuppressWarnings("ALL")
		public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
			if (!super.init(exprs, matchedPattern, isDelayed, parseResult))
				return false;

			exprName = (Expression<String>) exprs[0];
			type = ((Expression<OptionType>) exprs[1]).getSingle(null);
			if (type == null) {
				Skript.error("You must provide a literal (= constant) value for the option type.");
				return false;
			}
			return true;
		}

		@Override
		protected String getValue() {
			return "argument " + exprName.toString(null, false);
		}

		@Override
		protected Class<BukkitSlashCompletionEvent> getEvent() {
			return BukkitSlashCompletionEvent.class;
		}

		@Override
		protected Object convert(BukkitSlashCompletionEvent e) {
			final String name = EasyElement.parseSingle(exprName, e, null);
			if (name == null)
				return null;

			final OptionMapping option = e.getJDAEvent().getOption(name);
			if (option == null)
				return null;

			return JDAUtils.parseOptionValue(option);
		}

		@Override
		public @NotNull Class<?> getReturnType() {
			return JDAUtils.getOptionClass(type);
		}
	}

	public static class BukkitSlashCompletionEvent extends SimpleDiSkyEvent<CommandAutoCompleteInteractionEvent> implements InteractionEvent {
		public BukkitSlashCompletionEvent(SlashCompletionEvent event) {}

		@Override
		public GenericInteractionCreateEvent getInteractionEvent() {
			return getJDAEvent();
		}

	}
}