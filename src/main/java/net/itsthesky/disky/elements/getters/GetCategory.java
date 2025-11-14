package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.jetbrains.annotations.NotNull;

@Name("Get Category")
@Description({"Get a category from a guild using its unique ID.",
        "Categories are global on discord, means different categories cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("category with id \"000\"")
@Since("4.0.0")
@SeeAlso(Category.class)
public class GetCategory extends BaseGetterExpression<Category> {

    static {
        register(GetCategory.class,
                Category.class,
                "category");
    }

    @Override
    protected Category get(String id, Bot bot) {
        return bot.getInstance().getCategoryById(id);
    }

    @Override
    public String getCodeName() {
        return "category";
    }

    @Override
    public @NotNull Class<? extends Category> getReturnType() {
        return Category.class;
    }
}
