package info.itsthesky.disky.elements.structures.slash.models;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.HashMap;
import java.util.Map;

public class ParsedGroup {

    private String description;
    private Map<DiscordLocale, String> descriptionLocalizations = new HashMap<>();

    private String name;
    private Map<DiscordLocale, String> nameLocalizations = new HashMap<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<DiscordLocale, String> getDescriptionLocalizations() {
        return descriptionLocalizations;
    }

    public void setDescriptionLocalizations(Map<DiscordLocale, String> descriptionLocalizations) {
        this.descriptionLocalizations = descriptionLocalizations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<DiscordLocale, String> getNameLocalizations() {
        return nameLocalizations;
    }

    public void setNameLocalizations(Map<DiscordLocale, String> nameLocalizations) {
        this.nameLocalizations = nameLocalizations;
    }
}
