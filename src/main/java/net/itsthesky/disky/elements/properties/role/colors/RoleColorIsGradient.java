package net.itsthesky.disky.elements.properties.role.colors;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.RoleColors;

@Name("Role Color Is Gradient")
@Description("Checks whether the role color is gradient.")
@Since("4.28.0")
public class RoleColorIsGradient extends PropertyCondition<RoleColors> {

    static {
        PropertyCondition.infoBuilder(
                RoleColorIsGradient.class,
                PropertyType.BE,
                "gradient",
                "rolecolors"
        );
    }

    @Override
    public boolean check(RoleColors value) {
        return value.isGradient();
    }

    @Override
    protected String getPropertyName() {
        return "gradient role color";
    }
}
