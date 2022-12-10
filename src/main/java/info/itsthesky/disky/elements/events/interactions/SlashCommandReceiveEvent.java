package info.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SimpleGetterExpression;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;

public class SlashCommandReceiveEvent extends DiSkyEvent<SlashCommandInteractionEvent> {

	static {
		register("Slash Command", SlashCommandReceiveEvent.class, BukkitSlashCommandReceiveEvent.class,
				"slash command [receive[d]]")
				.description("Fired when a user execute a specific slash command.",
						"Use 'event-string' to get the command name. Don't forget to either reply or defer the interaction, You can only defer using the wait pattern  e.g: 'defer the interaction and wait [silently].",
						"Modal can be shown in this interaction.",
						"You can get value of arguments using 'argument \"name\" as string' for example.");

		SkriptUtils.registerBotValue(BukkitSlashCommandReceiveEvent.class);

		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, String.class,
				event -> event.getJDAEvent().getFullCommandName());
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getMessageChannel());

		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitSlashCommandReceiveEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	@Name("Slash Command Argument")
	@Description({"Represents a slash command argument.",
			"The name is the ID used when defining the slash command.",
			"Specify the type, so that Skript can parse it correctly. (if it's a number, operation wil be allowed for example)",
			"The type should be the same used when defining the argument in the command."})
	@Examples({"# I'm doing /ban time:30 user:*user id*, so:",
			"set {_time} to argument \"time\" as integer",
			"set {_user} to argument \"user\" as user"})
	public static class ArgValue extends SimpleGetterExpression<Object, BukkitSlashCommandReceiveEvent> {

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
		protected Class<BukkitSlashCommandReceiveEvent> getEvent() {
			return BukkitSlashCommandReceiveEvent.class;
		}

		@Override
		protected Object convert(BukkitSlashCommandReceiveEvent e) {
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

	public static class BukkitSlashCommandReceiveEvent extends SimpleDiSkyEvent<SlashCommandInteractionEvent> implements ModalEvent, InteractionEvent {
		public BukkitSlashCommandReceiveEvent(SlashCommandReceiveEvent event) {
			super(true);
		}

		@Override
		public GenericInteractionCreateEvent getInteractionEvent() {
			return getJDAEvent();
		}

		@Override
		public ModalCallbackAction replyModal(@NotNull Modal modal) {
			return getJDAEvent().replyModal(modal);
		}
	}
}
