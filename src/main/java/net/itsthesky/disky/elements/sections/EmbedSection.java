package net.itsthesky.disky.elements.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.EmbedManager;
import net.itsthesky.disky.api.skript.ReturningSection;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Embed Builder")
@Description("This builder allow you to make embed easily. You can specify the template used, you must register this template before use it!")
@Since("3.0")
@SeeAlso(EmbedBuilder.class)
@Examples("discord command embed:\n" +
        "    prefixes: !\n" +
        "    trigger:\n" +
        "        make embed:\n" +
        "            set title of embed to \"Title\"\n" +
        "            set description of embed to \"Description%nl%The title leads to the URL, if given\"\n" +
        "            set author of the embed to \"Author name (Can point to URL)\"\n" +
        "            set author icon of embed to \"https://cdn.discordapp.com/emojis/825811394963177533.png?v=1\"\n" +
        "            set author url of embed to \"https://www.youtube.com/watch?v=i33DB6R8YUY\"\n" +
        "            set embed color of the embed to orange\n" +
        "            add inline field named \"Field Name\" with value \"Colour sets %nl%< that\" to fields of embed\n" +
        "            add inline field named \"Field Name\" with value \"Color is a java Color%nl%Not a string\" to fields of embed\n" +
        "            add inline field named \"Field Name\" with value \"Field value\" to fields of embed\n" +
        "            add field named \"Non-inline field name\" with value \"The number of fields that can be shown on the same row is limited to 3, but is limited to 2 when an image is included\" to fields of embed\n" +
        "            set image of embed to \"https://media.discordapp.net/attachments/237757030708936714/390520880242884608/8xAac.png?width=508&height=522\"\n" +
        "            set thumbnail of embed to \"https://cdn.discordapp.com/emojis/825811394963177533.png?v=1\"\n" +
        "            set title url of embed to \"https://www.crunchyroll.com/fr/tonikawa-over-the-moon-for-you\"\n" +
        "            set footer of embed to \"Footer text\"\n" +
        "            set footer icon of embed to \"https://cdn.discordapp.com/emojis/825811394963177533.png?v=1\"\n" +
        "            set timestamp of embed to now\n" +
        "        reply with last embed")
public class EmbedSection extends ReturningSection<EmbedBuilder> {

    public static EmbedSection lastSection;
    public static class embed extends LastBuilderExpression<EmbedBuilder, EmbedSection> { }

    private Expression<String> exprID;

    static {
        register(
                EmbedSection.class,
                EmbedBuilder.class,
                embed.class,
                "make [a] [new] [discord] [message] embed [using [the] [template] [(named|with name|with id)] %-string%]"
        );
    }

    @Override
    public EmbedBuilder createNewValue(Event event) {
        lastSection = this;

        if (exprID != null) {
            String id = exprID.getSingle(event);
            if (id == null) return new EmbedBuilder();
            return new EmbedBuilder(EmbedManager.getTemplate(id));
        } else {
            return new EmbedBuilder();
        }
    }

    @Override
    public boolean init(Expression<?>[] exprs,
                        int matchedPattern,
                        @NotNull Kleenean isDelayed,
                        @NotNull ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {
        exprID = (Expression<String>) exprs[0];
        return super.init(exprs, matchedPattern, isDelayed, parseResult, sectionNode, triggerItems);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        if (exprID != null) {
            return "make new discord embed using template " + exprID.toString(e, debug);
        } else {
            return "make new discord embed";
        }
    }
}
