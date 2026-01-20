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
import net.itsthesky.disky.api.datastruct.DataStructure;
import net.itsthesky.disky.api.datastruct.DataStructureEntry;
import net.itsthesky.disky.api.datastruct.EasyDSRegistry;
import net.itsthesky.disky.api.events.rework.EventBuilder;
import net.itsthesky.disky.api.modules.DiSkyModule;
import net.itsthesky.disky.elements.effects.RetrieveEventValue;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
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
                var element = new ExpressionDocElement(doc);
                expressions.add(element);
                ids.put(doc.getElementClass(), element.getId());
            }
        }

        final List<DataStructureElement> dataStructures = new ArrayList<>();
        for (final EasyDSRegistry.DataStructureEntry entry : EasyDSRegistry.REGISTERED_STRUCTS) {
            final var dsElement = new DataStructureElement(entry);
            dataStructures.add(dsElement);
            ids.put(entry.clazz(), dsElement.getId());
        }

        final List<TypeDocElement> types = new ArrayList<>();
        for (final ClassInfo<?> classInfo : Classes.getClassInfos()) {
            boolean fromDiSky = isFromDiSky(classInfo);

            var typeElement = new TypeDocElement(classInfo, ids, !fromDiSky);
            types.add(typeElement);
            ids.put(classInfo.getC(), typeElement.getId());
            DiSky.debug("Adding type doc for class: " + classInfo.getC().getName() + "; id: " + typeElement.getId());
        }

        // Process see also references
        effects.forEach(element -> element.ProcessSeeAlso(ids));
        conditions.forEach(element -> element.ProcessSeeAlso(ids));
        sections.forEach(element -> element.ProcessSeeAlso(ids));
        expressions.forEach(element -> element.ProcessSeeAlso(ids));
        dataStructures.forEach(element -> element.ProcessSeeAlso(ids));
        // Types don't need processing as it's done in the constructor!

        // Filter by module
        if (specificModule != null) {
            effects.removeIf(element -> !specificModule.equals(element.getModule()));
            conditions.removeIf(element -> !specificModule.equals(element.getModule()));
            sections.removeIf(element -> !specificModule.equals(element.getModule()));
            events.removeIf(element -> !specificModule.equals(element.getRequiredPlugins()[0]));
            expressions.removeIf(element -> !specificModule.equals(element.getModule()));
        }

        final DocDocument doc = new DocDocument(effects, conditions, sections, types, events, expressions, dataStructures);
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
        private final DataStructureElement[] dataStructures;

        public DocDocument(List<SimpleDocElement> effects,
                           List<SimpleDocElement> conditions,
                           List<SimpleDocElement> sections,
                           List<TypeDocElement> types,
                           List<EventDocElement> events,
                           List<ExpressionDocElement> expressions,
                           List<DataStructureElement> dataStructures) {
            this.effects = effects.toArray(new SimpleDocElement[0]);
            this.conditions = conditions.toArray(new SimpleDocElement[0]);
            this.sections = sections.toArray(new SimpleDocElement[0]);
            this.events = events.toArray(new EventDocElement[0]);
            this.expressions = expressions.toArray(new SimpleDocElement[0]);
            this.types = types.toArray(new TypeDocElement[0]);
            this.dataStructures = dataStructures.toArray(new DataStructureElement[0]);
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

    private static String getAnnotationOr(Object target, Class<? extends Annotation> annotationClass, String defaultValue) {
        final Class<?> clazz = target instanceof final SyntaxElementInfo<?> elementInfo ? elementInfo.getElementClass() : (Class<?>) target;
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

    private static String[] getAnnotationOrs(Object target, Class<? extends Annotation> annotationClass, String[] defaultValue) {
        final Class<?> clazz = target instanceof final SyntaxElementInfo<?> elementInfo ? elementInfo.getElementClass() : (Class<?>) target;
        if (!clazz.isAnnotationPresent(annotationClass))
            return defaultValue;
        final Annotation annotation = clazz.getAnnotation(annotationClass);
        try {
            return (String[]) annotationClass.getDeclaredMethod("value").invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return defaultValue;
        }
    }

    private static @Nullable Class<?>[] getAnnotationOrs(Object target, Class<? extends Annotation> annotationClass) {
        final Class<?> clazz = target instanceof final SyntaxElementInfo<?> elementInfo ? elementInfo.getElementClass() : (Class<?>) target;
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

    @Getter
    public static class TypeDocElement {

        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String since;
        private final @Nullable String codeName;
        private final @Nullable String[] description;
        private final @Nullable String[] examples;
        private final @Nullable String[] values;
        private final @Nullable String[] seeAlso;
        private final @Nullable String module;
        private final boolean showInDocs;

        public TypeDocElement(ClassInfo<?> classInfo, Map<Class, String> ids, boolean fromSkript) {
            this.id = classInfo.getDocumentationID() == null ? classInfo.getCodeName() : classInfo.getDocumentationID();
            this.showInDocs = !fromSkript;
            this.module = fromSkript ? "Skript" : null;
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

        private final @NotNull String originClass;
        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String[] since;
        private final @Nullable String[] description;
        private final @Nullable String[] patterns;
        private final @Nullable String[] examples;
        private final @Nullable String[] requiredPlugins;
        private final @Nullable String[] eventValues;
        private final @Nullable String[] retrieveValues;
        private final @Nullable EventExpressionEntry[] eventExpressions;
        private final boolean cancellable;
        private final boolean showInDocs = true;

        public EventDocElement(@NotNull String originClass,
                               @Nullable String id,
                               @Nullable String name,
                               @Nullable String[] since,
                               @Nullable String[] description,
                               @Nullable String[] patterns,
                               @Nullable String[] examples,
                               @Nullable String[] requiredPlugins,
                               @Nullable String[] eventValues,
                               @Nullable String[] retrieveValues,
                               @Nullable EventExpressionEntry[] eventExpressions,
                               boolean cancellable) {
            this.originClass = originClass.replace('.', '/');
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

        private final @NotNull String originClass;
        private final @Nullable String id;
        private final @Nullable String name;
        private final @Nullable String since;
        private final @Nullable String[] description;
        private final @Nullable String[] patterns;
        private final @Nullable String[] examples;
        private final @Nullable String[] requiredPlugins;
        private final @Nullable String module;
        private final boolean showInDocs = true;

        @Setter
        private String[] seeAlso;
        private final transient Class<?>[] rawSeeAlso;

        public SimpleDocElement(SyntaxElementInfo<?> info) {
            this.originClass = info.getElementClass().getName().replace('.', '/');

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
                } else {
                    DiSky.debug("Could not find see also ID for class: " + clazz.getName());
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

    @Getter
    public static class DataStructureElement {

        private final String originClass;
        private final String id;
        private final String name;
        private final String[] description;
        private final String creationExpressionPattern;
        private final String[] validationRules;
        private final boolean showInDocs = true;

        private transient Class<?>[] rawSeeAlso;
        private String[] seeAlso;

        private transient Class<?> returnType;
        private String returnTypeId;

        private final DataStructureEntryDocElement[] entries;

        public DataStructureElement(EasyDSRegistry.DataStructureEntry entry) {
            this.originClass = entry.clazz().getName().replace('.', '/');
            this.creationExpressionPattern = entry.pattern();

            final var dsClass = entry.clazz();

            this.id = getAnnotationOr(dsClass, DocumentationId.class, dsClass.getSimpleName());
            this.name = getAnnotationOr(dsClass, Name.class, null);
            this.description = getAnnotationOrs(dsClass, Description.class, null);

            if (!dsClass.isAnnotationPresent(DataStructure.class))
                throw new IllegalArgumentException("Data structure class " + dsClass.getName() + " is not annotated with @DataStructure!");

            final var dsAnnotation = dsClass.getDeclaredAnnotation(DataStructure.class);
            this.validationRules = dsAnnotation.validationRules();
            this.returnType = dsAnnotation.clazz();

            this.rawSeeAlso = getAnnotationOrs(dsClass, SeeAlso.class);

            // Get entries
            final var fields = new ArrayList<DataStructureEntryDocElement>();
            final var dummyDs = EasyDSRegistry.createDataStructureInstance((Class) dsClass);
            for (final var field : dsClass.getFields()) {
                if (!field.isAnnotationPresent(DataStructureEntry.class))
                    continue;
                final var fieldAnnotation = field.getAnnotation(DataStructureEntry.class);
                Object defaultValue = null;
                try {
                    defaultValue = field.get(dummyDs);
                } catch (IllegalAccessException ignored) {}

                fields.add(new DataStructureEntryDocElement(field.getType(), defaultValue, fieldAnnotation));
            }

            this.entries = fields.toArray(new DataStructureEntryDocElement[0]);
        }

        public void ProcessSeeAlso(Map<Class, String> ids) {
            if (rawSeeAlso == null) {
                this.seeAlso = null;
                return;
            }

            List<String> seeAlsoIds = new ArrayList<>();
            for (Class<?> clazz : rawSeeAlso) {
                String seeAlsoId = ids.get(clazz);
                if (seeAlsoId != null)
                    seeAlsoIds.add(seeAlsoId);
            }

            this.seeAlso = seeAlsoIds.toArray(new String[0]);

            for (DataStructureEntryDocElement entry : entries)
                entry.ProcessAcceptedType(ids);
        }
    }

    @Getter
    public static class DataStructureEntryDocElement {

        private final String name;
        private final String additionalInfoForAcceptedValues;
        private final String[] description;
        private final @Nullable String defaultValue;

        private transient Class<?> acceptedType;
        private String acceptedTypeId;

        public DataStructureEntryDocElement(Class<?> type, @Nullable Object defaultValue, DataStructureEntry entry) {
            this.name = entry.value();
            this.additionalInfoForAcceptedValues = entry.additionalInfoForAcceptedValues();
            this.description = entry.description().isEmpty() ? null : entry.description().split("\n");
            this.acceptedType = type;
            this.acceptedTypeId = parseClassInfo(type);

            this.defaultValue = defaultValue == null ? null : defaultValue.toString();
        }

        public void ProcessAcceptedType(Map<Class, String> ids) {
            if (this.acceptedTypeId == null)
                this.acceptedTypeId = ids.get(acceptedType);
        }

    }
}
