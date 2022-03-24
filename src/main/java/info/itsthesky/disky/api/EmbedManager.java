package info.itsthesky.disky.api;

import net.dv8tion.jda.api.EmbedBuilder;

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
     * @param id the embed id template
     * @return The template linked with the ID if exist, else an empty embed
     */
    public static EmbedBuilder getTemplate(String id) {
        if (!templates.containsKey(id)) return new EmbedBuilder();
        return new EmbedBuilder(templates.get(id));
    }

}