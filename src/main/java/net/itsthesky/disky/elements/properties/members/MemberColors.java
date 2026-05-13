package net.itsthesky.disky.elements.properties.members;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RoleColors;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.properties.role.PropRoleColors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member Colors")
@Description({"Represents the role colors of a member's name in a guild.",
        "This is determined by the colors of the highest role assigned to the member that does not have the default colors.",
        "Returns the display colors for this member."})
@Examples({"reply with tertiary role color of (member colors of event-member)"})
@Since("4.28.0")
@SeeAlso(PropRoleColors.class)
public class MemberColors extends MemberProperty<RoleColors> {

    static {
        register(
                MemberColors.class,
                RoleColors.class,
                "[member] colo[u]rs"
        );
    }

    @Override
    public @Nullable RoleColors convert(Member member) {
        return member.getColors();
    }

    @Override
    public @NotNull Class<? extends RoleColors> getReturnType() {
        return RoleColors.class;
    }
}