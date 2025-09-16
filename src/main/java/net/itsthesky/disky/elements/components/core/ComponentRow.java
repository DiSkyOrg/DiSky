package net.itsthesky.disky.elements.components.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.itsthesky.disky.elements.componentsv2.base.IContainerComponentBuilder;
import net.itsthesky.disky.elements.componentsv2.base.ISectionAccessoryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ComponentRow implements IContainerComponentBuilder<ActionRow> {

    private final List<Button> buttons = new ArrayList<>();
    private SelectMenu menu;
    private TextInput input;
    private int uniqueId = -1;

    public ComponentRow(SelectMenu menu, TextInput input, List<Button> buttons) {
        this.menu = menu;
        this.input = input;
        if (buttons != null)
            this.buttons.addAll(buttons);
    }

    public ComponentRow(List<Object> components) {
        this();

        for (Object component : components) {
            if (component instanceof final SelectMenu menu)
                setMenu(menu);
            else if (component instanceof final Button button)
                add(button);
            else if (component instanceof final TextInput input)
                setInput(input);

            else if (component instanceof final SelectMenu.Builder<?, ?> menuBuilder)
                setMenu(menuBuilder.build());
            else if (component instanceof final TextInput.Builder inputBuilder)
                setInput(inputBuilder.build());
        }
    }

    public List<ActionRowChildComponent> asComponents() {
        if (getMenu() != null)
            return Collections.singletonList(getMenu());
        return new ArrayList<>(getButtons());
    }

    public void add(Button button) {
        buttons.add(button);
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

    public boolean isEmpty() {
        return menu == null && input == null && buttons.isEmpty();
    }

    @Override
    public void loadFrom(ActionRow component) {
        if (component == null) {
            menu = null;
            input = null;
            buttons.clear();
            return;
        }

        addAll(component.getActionComponents()
                .stream()
                .map(c -> (Component) c)
                .toList());
    }

    @Override
    public ActionRow build() {
        return asActionRow();
    }

    public Button getSingleButton() {
        if (buttons.size() != 1)
            throw new IllegalStateException("Cannot get single button from a row with " + buttons.size() + " buttons.");

        return buttons.get(0);
    }
}
