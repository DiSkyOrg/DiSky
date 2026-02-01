package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.itsthesky.disky.api.EmbedManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Register Embed Template")
@Description({"Register an embed template that can be reused throughout your code.",
        "Once registered, you can use the template when creating a new embed using its section or data structure."})
@Examples({"register embed template with {_embed} with name \"embed_name\"",
        "# When re-creating an embed:",
        "make a new embed using template named \"embed_name\" and store it in {_new_embed}"})
@Since("4.26.0")
public class EffRegisterEmbedTemplate extends Effect {

    static {
        Skript.registerEffect(EffRegisterEmbedTemplate.class,
                "register [new] [embed] template (with|based on) [the] [embed] %embedbuilder% [and] with [the] (name|id) %string%");
    }

    private Expression<EmbedBuilder> exprEmbed;
    private Expression<String> exprID;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprEmbed = (Expression<EmbedBuilder>) exprs[0];
        exprID = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        String id = exprID.getSingle(e);
        EmbedBuilder builder = exprEmbed.getSingle(e);
        if (id == null || builder == null) return;
        EmbedManager.registerTemplate(id, builder);
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "register new embed template from " + exprEmbed.toString(e, debug) + " with id " + exprID.toString(e, debug);
    }

}