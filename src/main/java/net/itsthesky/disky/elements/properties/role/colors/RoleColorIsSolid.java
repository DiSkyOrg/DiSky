package net.itsthesky.disky.elements.properties.role.colors;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Color;
import net.dv8tion.jda.api.entities.RoleColors;
import net.itsthesky.disky.core.SkriptUtils;
import org.jetbrains.annotations.Nullable;

@Name("Role Color Is Solid")
@Description("Checks whether the role color is solid (not gradient or holographic).")
@Since("4.28.0")
public class RoleColorIsSolid extends PropertyCondition<RoleColors> {

    static {
        PropertyCondition.infoBuilder(
                RoleColorIsSolid.class,
                PropertyType.BE,
                "solid",
                "rolecolors"
        );
    }

    @Override
    public boolean check(RoleColors value) {
        return value.isSolid();
    }

    @Override
    protected String getPropertyName() {
        return "solid role color";
    }
}
