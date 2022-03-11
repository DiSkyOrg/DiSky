package info.itsthesky.disky.elements.components;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

public class Test extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.getMessage().getContentRaw().equals("!testmodal"))
			return;

		final Button button = Button.success("test", "Click for the modal");

		event.getChannel()
				.sendMessage("Hello world")
				.setActionRows(ActionRow.of(button))
				.queue();
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		if (!event.getButton().getId().equals("test"))
			return;
		TextInput email = TextInput.create("email", "Email", TextInputStyle.SHORT)
				.setPlaceholder("Enter your E-mail")
				.setRequired(true)
				.setMinLength(10)
				.setMaxLength(100) // or setRequiredRange(10, 100)
				.build();

		Button button = Button.danger("danger1", "hello world!");
		Button button2 = Button.secondary("second1", "Click me :p");

		SelectMenu selectMenu = SelectMenu.create("menu")
				.addOption("ex1", "Example 1")
				.addOption("ex2", "Example 2", "Welcome!")
				.setRequiredRange(1, 1)
				.build();

		TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
				.setPlaceholder("Your concerns go here")
				.setRequired(true)
				.setMinLength(30)
				.setMaxLength(1000)
				.build();

		Modal modal = Modal.create("support", "Support")
				.addActionRows(ActionRow.of(email), ActionRow.of(body),
						ActionRow.of(button, button2))
				.build();

		event.replyModal(modal).queue();
	}
}
