package info.itsthesky.disky.elements.structures.slash;

import info.itsthesky.disky.elements.structures.slash.models.ParsedGroup;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SlashGroupManager {

    private static final Map<String, ParsedGroup> GROUPS = new java.util.HashMap<>();

    public static void register(ParsedGroup group) {
        GROUPS.put(group.getName(), group);
    }

    public static List<ParsedGroup> getGroups() {
        return new ArrayList<>(GROUPS.values());
    }

    public static @Nullable ParsedGroup getGroup(String name) {
        return GROUPS.get(name);
    }

}
