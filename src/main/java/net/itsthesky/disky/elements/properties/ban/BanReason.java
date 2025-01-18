package net.itsthesky.disky.elements.properties.ban;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Ban Reason")
@Description("The optional reason which say why the user of this ban was banned.")
public class BanReason extends SimplePropertyExpression<Guild.Ban, String> {

    static {
        register(
                BanReason.class,
                String.class,
                "[ban[ned]] reason",
                "ban"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "reason";
    }

    @Override
    public @Nullable String convert(Guild.Ban ban) {
        return ban.getReason();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
