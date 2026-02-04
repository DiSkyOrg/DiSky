package net.itsthesky.disky.elements.properties.role.colors;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Color;
import net.dv8tion.jda.api.entities.RoleColors;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.core.SkriptUtils;
import org.jetbrains.annotations.Nullable;

@Name("Secondary Role Color")
@Description("Returns the primary color of a role's colors.")
@Since("4.28.0")
@SeeAlso({PrimaryRoleColor.class, TertiaryRoleColor.class})
public class SecondaryRoleColor extends SimplePropertyExpression<RoleColors, Color> {

    static {
        register(
                SecondaryRoleColor.class,
                Color.class,
                "secondary [role] color",
                "rolecolors"
        );
    }

    @Override
    public @Nullable Color convert(RoleColors from) {
        return SkriptUtils.convert(from.getSecondary());
    }

    @Override
    protected String getPropertyName() {
        return "secondary role color";
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }
}
