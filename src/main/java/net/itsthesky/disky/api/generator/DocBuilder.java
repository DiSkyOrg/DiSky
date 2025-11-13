package net.itsthesky.disky.api.generator;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.ExprSets;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.Classes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.DiSkyType;
import net.itsthesky.disky.api.events.rework.EventBuilder;
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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Mostly copied from Skylyxx's DocBuilder, changed two/three things for module support
 */
@Getter
public class DocBuilder {

    private final DiSky instance;
    private final Gson gson;

    public DocBuilder(DiSky instance) {
        this.instance = instance;
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

    public void generate(boolean includeTypesInEventValues, @Nullable String specificModule) {
        getInstance().getLogger().info("Generating documentation...");

        // Map of element class to generated/specified ID
        final var ids = new HashMap<Class, String>();

        final List<SimpleDocElement> effects = new ArrayList<>();
        for (final SyntaxElementInfo<? extends Effect> doc : Skript.getEffects()) {
            if (isFromDiSky(doc)) {
                final var element = new SimpleDocElement(doc);
                effects.add(element);
                ids.put(doc.getElementClass(), element.getId());
            }
        }

        final List<SimpleDocElement> conditions = new ArrayList<>();
        for (final SyntaxElementInfo<? extends Condition> doc : Skript.getConditions()) {
            if (isFromDiSky(doc)) {
                final var element = new SimpleDocElement(doc);
                conditions.add(element);
                ids.put(doc.getElementClass(), element.getId());
            }
        }

        final List<SimpleDocElement> sections = new ArrayList<>();
        for (final SyntaxElementInfo<? extends Section> doc : Skript.getSections()) {
            if (isFromDiSky(doc)) {
                final var element = new SimpleDocElement(doc);
                sections.add(element);
                ids.put(doc.getElementClass(), element.getId());
            }
        }

        final List<EventDocElement> events = new ArrayList<>();
        for (final var evt : EventBuilder.REGISTERED_EVENTS) {
            events.add(evt.toDocElement());
        }

        final List<ExpressionDocElement> expressions = new ArrayList<>();
        for (Iterator<ExpressionInfo<?, ?>> it = Skript.getExpressions(); it.hasNext(); ) {
            ExpressionInfo<?, ?> doc = it.next();
            if (isFromDiSky(doc)) {
                expressions.add(new ExpressionDocElement(doc));
                ids.put(doc.getElementClass(), new ExpressionDocElement(doc).getId());
            }
        }

        final List<TypeDocElement> types = new ArrayList<>();
        for (final ClassInfo<?> classInfo : Classes.getClassInfos()) {
            if (isFromDiSky(classInfo)) {
                var typeElement = new TypeDocElement(classInfo, ids);
                types.add(typeElement);
                ids.put(classInfo.getC(), typeElement.getId());
            }
        }

        // Process see also references
        expressions.forEach(element -> element.ProcessSeeAlso(ids));
        effects.forEach(element -> element.ProcessSeeAlso(ids));
        conditions.forEach(element -> element.ProcessSeeAlso(ids));
        sections.forEach(element -> element.ProcessSeeAlso(ids));
        // Types don't need processing as it's done in the constructor!

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

    @Getter
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

    private static @Nullable Class<?>[] getAnnotationOrs(SyntaxElementInfo<?> elementInfo, Class<? extends Annotation> annotationClass) {
        final Class<?> clazz = elementInfo.getElementClass();
        if (!clazz.isAnnotationPresent(annotationClass))
            return null;
        final Annotation annotation = clazz.getAnnotation(annotationClass);
        try {
            return (Class<?>[]) annotationClass.getDeclaredMethod("value").invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
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
        @Getter
        private final @Nullable String[] description;
        @Getter
        private final @Nullable String[] examples;
        private final @Nullable String[] values;
        private final @Nullable String[] seeAlso;

        public TypeDocElement(ClassInfo<?> classInfo, Map<Class, String> ids) {
            this.id = classInfo.getDocumentationID();
            this.name = classInfo.getDocName();
            this.since = classInfo.getSince();
            this.codeName = classInfo.getCodeName();
            this.description = classInfo.getDescription();
            this.examples = classInfo.getExamples();
            this.values = classInfo.getSupplier() == null
                    ? null
                    : StreamSupport.stream(Spliterators.spliteratorUnknownSize(classInfo.getSupplier().get(), Spliterator.ORDERED), false)
                    .map(obj -> {
                        if (obj == null)
                            return null;
                        return obj.toString().toLowerCase();
                    })
                    .filter(Objects::nonNull)
                    .toArray(String[]::new);
            if (classInfo instanceof final DiSkyType.DiSkyTypeWrapper<?> wrapper) {
                this.seeAlso = wrapper.getDiSkyType().getDocsSeeAlso().stream()
                        .map(ids::get)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new);
            } else {
                this.seeAlso = null;
            }
        }

        public @Nullable String getId() {
            return id;
        }

        public @Nullable String getName() {
            return name;
        }

        public @Nullable String getSince() {
            return since;
        }

        public @Nullable String getCodeName() {
            return codeName;
        }

    }

    @Getter
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
        private final @Nullable EventExpressionEntry[] eventExpressions;
        private final boolean cancellable;

        public EventDocElement(SkriptEventInfo<?> info, boolean inculdeTimes) {
            id = info.getId();
            name = info.getName();
            description = info.getDescription();
            patterns = info.getPatterns();
            examples = info.getExamples();
            since = info.getSince();
            requiredPlugins = info.getRequiredPlugins();
            cancellable = parseCancellable(info);
            retrieveValues = RetrieveEventValue.VALUES.getOrDefault(info.events[0], new ArrayList<>())
                    .stream()
                    .map(RetrieveEventValue.RetrieveValueInfo::getCodeName)
                    .toArray(String[]::new);

            this.eventValues = null;
            this.eventExpressions = null;
        }

        public EventDocElement(@Nullable String id,
                               @Nullable String name,
                               @Nullable String since,
                               @Nullable String[] description,
                               @Nullable String[] patterns,
                               @Nullable String[] examples,
                               @Nullable String[] requiredPlugins,
                               @Nullable String[] eventValues,
                               @Nullable String[] retrieveValues,
                               @Nullable EventExpressionEntry[] eventExpressions,
                               boolean cancellable) {
            this.id = id;
            this.name = name;
            this.since = since;
            this.description = description;
            this.patterns = patterns;
            this.examples = examples;
            this.requiredPlugins = requiredPlugins;
            this.eventValues = eventValues;
            this.retrieveValues = retrieveValues;
            this.eventExpressions = eventExpressions;
            this.cancellable = cancellable;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class EventExpressionEntry {

        private final String pattern;
        private final String returnType;
        private final boolean isList;

    }

    @Getter
    public static class SimpleDocElement {

        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String since;
        private final @Nullable String[] description;
        private final @Nullable String[] patterns;
        private final @Nullable String[] examples;
        private final @Nullable String[] requiredPlugins;
        private final @Nullable String module;

        @Setter
        private String[] seeAlso;
        private final transient Class<?>[] rawSeeAlso;

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
            this.rawSeeAlso = getAnnotationOrs(info, SeeAlso.class);
        }

        public @Nullable String getId() {
            return id;
        }

        public @Nullable String getName() {
            return name;
        }

        public @Nullable String getSince() {
            return since;
        }

        public @Nullable String getModule() {
            return module;
        }

        public void ProcessSeeAlso(Map<Class, String> ids) {
            if (rawSeeAlso == null) {
                this.seeAlso = null;
                return;
            }

            List<String> seeAlsoIds = new ArrayList<>();
            for (Class<?> clazz : rawSeeAlso) {
                String seeAlsoId = ids.get(clazz);
                if (seeAlsoId != null) {
                    seeAlsoIds.add(seeAlsoId);
                }
            }

            this.seeAlso = seeAlsoIds.toArray(new String[0]);
        }
    }

    @Getter
    public static class ExpressionDocElement extends SimpleDocElement {

        private final String returnType;

        public ExpressionDocElement(ExpressionInfo<?, ?> info) {
            super(info);
            this.returnType = parseClassInfo(info.getReturnType());
        }

    }
}
