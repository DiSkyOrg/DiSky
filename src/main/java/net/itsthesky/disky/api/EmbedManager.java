package net.itsthesky.disky.api;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Embed template manager & database
 * @author Itsthesky (12/04/2021)
 */
public class EmbedManager {

    /**
     * The hashmap which store every templates loaded on the server.
     */
    private static final HashMap<String, EmbedBuilder> templates = new HashMap<>();

    /**
     * Register a new template using the embed template database.
     * @param id    The embed template ID
     * @param embed The embed itself
     */
    public static void registerTemplate(String id, EmbedBuilder embed) {
        templates.put(id, embed);
    }

    /**
     * Get an embed instance via its ID.
     * @param id the embed id template, may be null
     * @return The template linked with the ID if existed, else an empty embed
     */
    public static @NotNull EmbedBuilder getTemplate(@Nullable String id) {
        if (id == null) return new EmbedBuilder();
        if (!templates.containsKey(id)) return new EmbedBuilder();
        return new EmbedBuilder(templates.get(id));
    }

}