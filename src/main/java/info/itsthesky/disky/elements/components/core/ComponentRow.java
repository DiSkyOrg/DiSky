package info.itsthesky.disky.elements.components.core;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentRow {

	private SelectMenu menu;
	private TextInput input;
	private final List<Button> buttons;

	public ComponentRow(SelectMenu menu, TextInput input, List<Button> buttons) {
		this.menu = menu;
		this.input = input;
		this.buttons = buttons;
	}

	public ComponentRow() {
		menu = null;
		input = null;
		buttons = new ArrayList<>();
	}

	public static List<ComponentRow> from(List<ActionRow> rows) {
		final List<ComponentRow> news = new ArrayList<>();
		for (ActionRow row : rows)
		{
			switch (row.getType()) {
				case BUTTON:
					news.add(new ComponentRow(null, null, row.getButtons()));
					break;
				case SELECT_MENU:
					news.add(new ComponentRow(row.getComponents().stream()
							.filter(item -> item.getType().equals(Component.Type.SELECT_MENU))
							.map(item -> ((SelectMenu) item))
							.findAny()
							.orElse(null), null, null));
					break;
			}
		}
		return news;
	}

	public TextInput getInput() {
		return input;
	}

	public void setInput(TextInput input) {
		this.input = input;
	}

	public List<ItemComponent> asComponents() {
		if (getMenu() != null)
			return Collections.singletonList(getMenu());
		if (getInput() != null)
			return Collections.singletonList(getInput());
		return new ArrayList<>(getButtons());
	}

	public void setMenu(SelectMenu menu) {
		this.menu = menu;
	}

	public void add(Button button) {
		buttons.add(button);
	}

	public SelectMenu getMenu() {
		return menu;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public ActionRow asActionRow() {
		return ActionRow.of(asComponents());
	}

	public void addAll(List<Component> components) {
		for (Component component : components) {
			if (component instanceof SelectMenu)
				setMenu((SelectMenu) component);
			else if (component instanceof Button)
				add((Button) component);
			else if (component instanceof TextInput)
				setInput((TextInput) component);
		}
	}
}
