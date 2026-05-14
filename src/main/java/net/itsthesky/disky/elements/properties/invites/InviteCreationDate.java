package net.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Invite;
import net.itsthesky.disky.api.generator.SeeAlso;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TimeZone;

@Name("Invite Creation Date")
@Description({"Returns the creation date of the given invite.",
        "This only works on expanded invites and will throw an IllegalStateException otherwise."})
@Examples({"set {_created} to invite creation date of event-invite",
        "reply with invite creation date of event-invite"})
@Since("4.28.0")
@SeeAlso(Invite.class)
public class InviteCreationDate extends InviteProperty<Date> {

    static {
        register(
                InviteCreationDate.class,
                Date.class,
                "creation date"
        );
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }

    @Override
    public @Nullable Date convert(Invite invite) {
        return new Date(invite.getTimeCreated().toInstant().toEpochMilli(), TimeZone.getTimeZone("GMT"));
    }
}