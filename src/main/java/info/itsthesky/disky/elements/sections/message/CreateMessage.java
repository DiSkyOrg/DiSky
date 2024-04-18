package info.itsthesky.disky.elements.sections.message;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.ReturningSection;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Create (rich) Message")
@Description({
		"Creates a rich message.",
		"A rich message can receive the following data:",
		" - Content",
		" - Embed[s] (default max is 1, webhooks can send up to 5)",
		" - Attachment(s) (supports images if SkImage is installed)",
		" - Components",
		"This will be used to both post & edit a message. ",
		"Simply change what you want and pass the result of the section to the edit effect."
})
@Examples("create a new message and store it in {_message}:\n" +
		"    set the content of the message to \"hello world\"\n" +
		"\n" +
		"\n" +
		"    # we create a new component row that'll hold multiple buttons\n" +
		"    create a new row and store it in {_row}:\n" +
		"\n" +
		"        add new danger button with id \"test\" named \"Hello World\" with reaction \"smile\" to the components of the row\n" +
		"        add new success button with id \"test2\" named \"yuss\" to the components of the row\n" +
		"    # we add the row containing two buttons\n" +
		"    add {_row} to the rows of message\n" +
		"        \n" +
		"    # row with one button only\n" +
		"    add new secondary button with id \"test3\" named \"Another row!\" to the rows of message\n" +
		"\n" +
		"    set {_dp} to new dropdown with id \"selector\"\n" +
		"    set min range of {_dp} to 1\n" +
		"    set max range of {_dp} to 2\n" +
		"    set placeholder of {_dp} to \"Dropdown\"\n" +
		"    loop \"one\", \"two\" and \"three\":\n" +
		"        add new option with value (loop-value) named \"Value: %loop-value%\" with description \"Click to select\" with reaction \"sparkles\" to options of {_dp}\n" +
		"    add {_dp} to the rows of message\n" +
		"\n" +
		"    make embed:\n" +
		"        set title of embed to \"hello there!\"\n" +
		"        set embed color of embed to red\n" +
		"        set image of embed to \"attachment://image1.png\"\n" +
		"    add last embed to the embeds of message\n" +
		"\n" +
		"    # SkImage's image. Images are named as: 'imageX.png' where X is the attachment's index.\n" +
		"    set {_image} to new image with size 500, 500\n" +
		"    set {_font} to new font style with name \"Arial Black\" and with size 60\n" +
		"    set {_text} to new text \"Hello World\" with color from rgb 255, 255, 255 with font {_font} centered vertically centered horizontally\n" +
		"    draw {_text} at 0, 0 on {_image}\n" +
		"\n" +
		"    add {_image} to attachments of message\n" +
		"\n" +
		"reply with {_message}")
public class CreateMessage extends ReturningSection<MessageCreateBuilder> {

	@Name("Last Message Builder")
	@Description("Represents the last message builder created within a section.")
	public static class message extends LastBuilderExpression<MessageCreateBuilder, CreateMessage> { }

	static {
		register(
				CreateMessage.class,
				MessageCreateBuilder.class,
				message.class,
				"(make|create) [a] [new] [:silent] message"
		);
	}

	private boolean silent;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
		silent = parseResult.hasTag("silent");
		return super.init(exprs, matchedPattern, isDelayed, parseResult, sectionNode, triggerItems);
	}

	@Override
	public MessageCreateBuilder createNewValue(Event event) {
		return new MessageCreateBuilder().setSuppressedNotifications(silent);
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new message";
	}

}
