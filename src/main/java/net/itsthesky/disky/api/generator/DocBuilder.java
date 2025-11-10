package net.itsthesky.disky.api.generator;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.Classes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.modules.DiSkyModule;
import net.itsthesky.disky.elements.effects.RetrieveEventValue;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;

/**
 * Mostly copied from Skylyxx's DocBuilder, changed two/three things for module support
 */
public class DocBuilder {

    private final DiSky instance;
    private final Gson gson;

    public DocBuilder(DiSky instance) {
        this.instance = instance;
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

    public void generate(boolean includeTypesInEventValues, @Nullable String specificModule) {
        getInstance().getLogger().info("Generating documentation...");
        final List<SimpleDocElement> effects = new ArrayList<>();
        for (final SyntaxElementInfo<? extends Effect> doc : Skript.getEffects())
            if (isFromDiSky(doc)) effects.add(new SimpleDocElement(doc));

        final List<SimpleDocElement> conditions = new ArrayList<>();
        for (final SyntaxElementInfo<? extends Condition> doc : Skript.getConditions())
            if (isFromDiSky(doc)) conditions.add(new SimpleDocElement(doc));

        final List<SimpleDocElement> sections = new ArrayList<>();
        for (final SyntaxElementInfo<? extends Section> doc : Skript.getSections())
            if (isFromDiSky(doc)) sections.add(new SimpleDocElement(doc));

        final List<EventDocElement> events = new ArrayList<>();
        for (final SkriptEventInfo<?> doc : Skript.getEvents())
            if (isFromDiSky(doc)) events.add(new EventDocElement(doc, includeTypesInEventValues));

        final List<TypeDocElement> types = new ArrayList<>();
        for (final ClassInfo<?> classInfo : Classes.getClassInfos())
            if (isFromDiSky(classInfo)) types.add(new TypeDocElement(classInfo));

        final List<ExpressionDocElement> expressions = new ArrayList<>();
        for (Iterator<ExpressionInfo<?, ?>> it = Skript.getExpressions(); it.hasNext(); ) {
            ExpressionInfo<?, ?> doc = it.next();
            if (isFromDiSky(doc)) expressions.add(new ExpressionDocElement(doc));
        }

        // Filter by module
        if (specificModule != null) {
            effects.removeIf(element -> !specificModule.equals(element.getModule()));
            conditions.removeIf(element -> !specificModule.equals(element.getModule()));
            sections.removeIf(element -> !specificModule.equals(element.getModule()));
            events.removeIf(element -> !specificModule.equals(element.getRequiredPlugins()[0]));
            expressions.removeIf(element -> !specificModule.equals(element.getModule()));
        }

        final DocDocument doc = new DocDocument(effects, conditions, sections, types, events, expressions);
        final String json = gson.toJson(doc);
        try {
            final String fileName = specificModule == null ? "doc.json" : specificModule + ".json";
            Files.write(new File(getInstance().getDataFolder(), fileName).toPath(), json.getBytes());
        } catch (IOException e) {
            getInstance().getLogger().severe("Could not write documentation to file!");
            e.printStackTrace();
        }
        getInstance().getLogger().info("Successfully generated documentation!");
    }

    public DiSky getInstance() {
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    private boolean isFromDiSky(Object element) {
        final SkriptAddon addon;
        if (element instanceof SkriptEventInfo<?>)
            addon = getAddon(((SkriptEventInfo<?>) element));
        else if (element instanceof SyntaxElementInfo<?>)
            addon = getAddon(((SyntaxElementInfo<?>) element));
        else if (element instanceof ClassInfo<?>)
            addon = getAddon(((ClassInfo<?>) element));
        else
            return false;
        if (addon == null) {
            final Class<?> clazz = getElementClass(element);
            if (clazz == null)
                return false;
            for (DiSkyModule module : DiSky.getModuleManager().getModules()) {
                final String modulePackage = module.getClass().getPackage().getName();
                final String elementPackage = clazz.getPackage().getName();
                if (elementPackage.contains(modulePackage))
                    return true;
            }
        }
        return addon != null && addon == DiSky.getAddonInstance();
    }

    public static boolean isFromModule(SyntaxElementInfo<?> info, DiSkyModule module) {
        final String modulePackage = module.getClass().getPackage().getName();
        final String elementPackage = info.getElementClass().getPackage().getName();
        return elementPackage.contains(modulePackage);
    }

    public static boolean isFromModule(ClassInfo<?> info, DiSkyModule module) {
        final String modulePackage = module.getClass().getPackage().getName();
        final String elementPackage = info.getC().getPackage().getName();
        return elementPackage.contains(modulePackage);
    }

    private Class<?> getElementClass(Object element) {
        if (element instanceof SkriptEventInfo<?>)
            return ((SkriptEventInfo<?>) element).getElementClass();
        else if (element instanceof SyntaxElementInfo<?>)
            return ((SyntaxElementInfo<?>) element).getElementClass();
        else if (element instanceof ClassInfo<?>)
            return ((ClassInfo<?>) element).getC();
        else
            return null;
    }

    @Nullable
    public static SkriptAddon getAddon(SkriptEventInfo<?> eventInfo) {
        return getAddon(eventInfo.originClassPath);
    }

    @Nullable
    public static SkriptAddon getAddon(ClassInfo<?> classInfo) {
        if (classInfo.getParser() != null)
            return getAddon(classInfo.getParser().getClass().getName());
        if (classInfo.getSerializer() != null)
            return getAddon(classInfo.getSerializer().getClass().getName());
        if (classInfo.getChanger() != null)
            return getAddon(classInfo.getChanger().getClass().getName());
        return null;
    }

    @Nullable
    public static SkriptAddon getAddon(SyntaxElementInfo<?> elementInfo) {
        return getAddon(elementInfo.getElementClass().getName());
    }

    public static class DocDocument {

        private final EventDocElement[] events;
        private final TypeDocElement[] types;
        private final SimpleDocElement[] conditions;
        private final SimpleDocElement[] effects;
        private final SimpleDocElement[] sections;
        private final SimpleDocElement[] expressions;

        public DocDocument(List<SimpleDocElement> effects,
                           List<SimpleDocElement> conditions,
                           List<SimpleDocElement> sections,
                           List<TypeDocElement> types,
                           List<EventDocElement> events,
                           List<ExpressionDocElement> expressions) {
            this.effects = effects.toArray(new SimpleDocElement[0]);
            this.conditions = conditions.toArray(new SimpleDocElement[0]);
            this.sections = sections.toArray(new SimpleDocElement[0]);
            this.events = events.toArray(new EventDocElement[0]);
            this.expressions = expressions.toArray(new SimpleDocElement[0]);
            this.types = types.toArray(new TypeDocElement[0]);
        }

        public EventDocElement[] getEvents() {
            return events;
        }

        public SimpleDocElement[] getConditions() {
            return conditions;
        }

        public SimpleDocElement[] getEffects() {
            return effects;
        }

        public SimpleDocElement[] getSections() {
            return sections;
        }

        public SimpleDocElement[] getExpressions() {
            return expressions;
        }

        public TypeDocElement[] getTypes() {
            return types;
        }
    }

    @Nullable
    public static SkriptAddon getAddon(String clazzName) {
        if (clazzName.startsWith("ch.njol.skript"))
            return Skript.getAddonInstance();
        for (SkriptAddon addon : Skript.getAddons()) {
            if (clazzName.startsWith(addon.plugin.getClass().getPackage().getName()))
                return addon;
        }
        return null;
    }

    private static String getAnnotationOr(SyntaxElementInfo<?> elementInfo, Class<? extends Annotation> annotationClass, String defaultValue) {
        final Class<?> clazz = elementInfo.getElementClass();
        if (!clazz.isAnnotationPresent(annotationClass))
            return defaultValue;
        final Annotation annotation = clazz.getAnnotation(annotationClass);
        try {
            return (String) annotationClass.getDeclaredMethod("value").invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return defaultValue;
        }
    }

    private static String parseClassInfo(Class<?> clazz) {
        final ClassInfo<?> classInfo = Classes.getExactClassInfo(clazz);
        return classInfo == null ? null : classInfo.getCodeName();
    }

    private static String[] getAnnotationOrs(SyntaxElementInfo<?> elementInfo, Class<? extends Annotation> annotationClass, String[] defaultValue) {
        final Class<?> clazz = elementInfo.getElementClass();
        if (!clazz.isAnnotationPresent(annotationClass))
            return defaultValue;
        final Annotation annotation = clazz.getAnnotation(annotationClass);
        try {
            return (String[]) annotationClass.getDeclaredMethod("value").invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return defaultValue;
        }
    }

    private static boolean parseCancellable(SkriptEventInfo<?> info) {
        boolean cancellable = true;
        for (Class<? extends Event> clazz : info.events) {
            if (!Cancellable.class.isAssignableFrom(clazz)) {
                cancellable = false;
                break;
            }
        }
        return cancellable;
    }

    private static String[] parseValues(SkriptEventInfo<?> info, boolean includeTimes) {
        final Set<String> eventValues = new HashSet<>();
        final Class[][] values = EventValuesGetter.getEventValues(info.events);

        int time = -1;
        for (Class<?>[] c : values) {
            for (Class<?> clazz : c) {
                final String codeName = Classes.getExactClassName(clazz);
                if (codeName != null)
                    eventValues.add("event-" + codeName + (includeTimes ? (time == 1 ? " (new)" : (time == -1 ? " (past)" : "")) : ""));
            }
            time++;
        }
        return eventValues.toArray(new String[0]);
    }

    public static class TypeDocElement {

        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String since;
        private final @Nullable String codeName;
        private final @Nullable String[] description;
        private final @Nullable String[] examples;

        public TypeDocElement(ClassInfo<?> classInfo) {
            this.id = classInfo.getDocumentationID();
            this.name = classInfo.getDocName();
            this.since = classInfo.getSince();
            this.codeName = classInfo.getCodeName();
            this.description = classInfo.getDescription();
            this.examples = classInfo.getExamples();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSince() {
            return since;
        }

        public String getCodeName() {
            return codeName;
        }

        public String[] getDescription() {
            return description;
        }

        public String[] getExamples() {
            return examples;
        }
    }

    public static class EventDocElement {

        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String since;
        private final @Nullable String[] description;
        private final @Nullable String[] patterns;
        private final @Nullable String[] examples;
        private final @Nullable String[] requiredPlugins;
        private final @Nullable String[] eventValues;
        private final @Nullable String[] retrieveValues;
        private final boolean cancellable;

        public EventDocElement(SkriptEventInfo<?> info, boolean inculdeTimes) {
            id = info.getId();
            name = info.getName();
            description = info.getDescription();
            patterns = info.getPatterns();
            examples = info.getExamples();
            since = info.getSince();
            requiredPlugins = info.getRequiredPlugins();
            eventValues = null; //parseValues(info, inculdeTimes);
            cancellable = parseCancellable(info);
            retrieveValues = RetrieveEventValue.VALUES.getOrDefault(info.events[0], new ArrayList<>())
                    .stream()
                    .map(RetrieveEventValue.RetrieveValueInfo::getCodeName)
                    .toArray(String[]::new);
        }

        public @Nullable String getId() {
            return id;
        }

        public @Nullable String getName() {
            return name;
        }

        public String[] getDescription() {
            return description;
        }

        public String[] getPatterns() {
            return patterns;
        }

        public String[] getExamples() {
            return examples;
        }

        public @Nullable String getSince() {
            return since;
        }

        public String[] getRequiredPlugins() {
            return requiredPlugins;
        }

        public String[] getEventValues() {
            return eventValues;
        }

        public String[] getRetrieveValues() {
            return retrieveValues;
        }

        public boolean isCancellable() {
            return cancellable;
        }
    }

    public static class SimpleDocElement {

        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String since;
        private final @Nullable String[] description;
        private final @Nullable String[] patterns;
        private final @Nullable String[] examples;
        private final @Nullable String[] requiredPlugins;
        private final @Nullable String module;

        public SimpleDocElement(SyntaxElementInfo<?> info) {
            this.id = getAnnotationOr(info, DocumentationId.class, info.getElementClass().getSimpleName());
            this.name = getAnnotationOr(info, Name.class, null);
            this.description = getAnnotationOrs(info, Description.class, null);
            this.patterns = info.getPatterns();
            this.examples = getAnnotationOrs(info, Examples.class, null);
            var sinces = getAnnotationOrs(info, Since.class, null);
            this.since = sinces == null ? null : String.join(", ", sinces);
            this.requiredPlugins = getAnnotationOrs(info, RequiredPlugins.class, null);
            this.module = getAnnotationOr(info, Module.class, null);
        }

        public @Nullable String getId() {
            return id;
        }

        public @Nullable String getName() {
            return name;
        }

        public String[] getDescription() {
            return description;
        }

        public String[] getPatterns() {
            return patterns;
        }

        public String[] getExamples() {
            return examples;
        }

        public @Nullable String getSince() {
            return since;
        }

        public String[] getRequiredPlugins() {
            return requiredPlugins;
        }

        public @Nullable String getModule() {
            return module;
        }
    }

    public static class ExpressionDocElement extends SimpleDocElement {

        private final String returnType;

        public ExpressionDocElement(ExpressionInfo<?, ?> info) {
            super(info);
            this.returnType = parseClassInfo(info.getReturnType());
        }

        public String getReturnType() {
            return returnType;
        }
    }
}
