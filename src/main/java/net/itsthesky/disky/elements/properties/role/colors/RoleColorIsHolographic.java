package net.itsthesky.disky.elements.properties.role.colors;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.RoleColors;

@Name("Role Color Is Holographic")
@Description("Checks whether the role color is holographic.")
@Since("4.28.0")
public class RoleColorIsHolographic extends PropertyCondition<RoleColors> {

    static {
        PropertyCondition.infoBuilder(
                RoleColorIsHolographic.class,
                PropertyType.BE,
                "holographic",
                "rolecolors"
        );
    }

    @Override
    public boolean check(RoleColors value) {
        return value.isHolographic();
    }

    @Override
    protected String getPropertyName() {
        return "holographic role color";
    }
}
