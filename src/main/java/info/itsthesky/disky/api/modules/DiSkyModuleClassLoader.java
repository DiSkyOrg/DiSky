package info.itsthesky.disky.api.modules;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class DiSkyModuleClassLoader extends URLClassLoader {
    private final String originalPackage;
    private final String relocatedPackage;
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
    private final Set<String> processedJars = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DiSkyModuleClassLoader(URL[] urls, ClassLoader parent, String originalPackage, String relocatedPackage) {
        super(urls, parent);
        this.originalPackage = originalPackage;
        this.relocatedPackage = relocatedPackage;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Vérifie si la classe a déjà été chargée
        Class<?> loadedClass = loadedClasses.get(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // Si la classe demandée est dans le package original JDA
        if (name.startsWith(originalPackage)) {
            String relocatedName = name.replace(originalPackage, relocatedPackage);
            try {
                // Essaie de charger la classe relocalisée depuis le parent
                Class<?> relocatedClass = getParent().loadClass(relocatedName);
                loadedClasses.put(name, relocatedClass);
                return relocatedClass;
            } catch (ClassNotFoundException e) {
                // Continue avec le comportement normal si la classe relocalisée n'existe pas
            }
        }

        try {
            // Essaie de charger normalement
            Class<?> c = super.loadClass(name, resolve);
            loadedClasses.put(name, c);
            return c;
        } catch (ClassNotFoundException e) {
            // Si la classe n'est pas trouvée, vérifie les dépendances
            Class<?> dependencyClass = loadFromDependencies(name);
            if (dependencyClass != null) {
                loadedClasses.put(name, dependencyClass);
                return dependencyClass;
            }
            throw e;
        }
    }

    private Class<?> loadFromDependencies(String name) throws ClassNotFoundException {
        try {
            // Parcourt les URLs du classloader pour trouver les JARs
            for (URL url : getURLs()) {
                if (url.getProtocol().equals("file") && url.getPath().endsWith(".jar")) {
                    String jarPath = url.getPath();
                    if (processedJars.add(jarPath)) {  // Évite de traiter plusieurs fois le même JAR
                        try (JarFile jarFile = new JarFile(new File(jarPath))) {
                            // Analyse les dépendances du JAR
                            analyzeJarDependencies(jarFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ClassNotFoundException("Error analyzing dependencies", e);
        }
        return null;
    }

    private void analyzeJarDependencies(JarFile jarFile) throws IOException {
        // Lit le fichier MANIFEST.MF pour trouver les dépendances
        Manifest manifest = jarFile.getManifest();
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            String classpath = attributes.getValue(Attributes.Name.CLASS_PATH);
            if (classpath != null) {
                // Ajoute les dépendances au classloader
                for (String path : classpath.split(" ")) {
                    try {
                        File dependency = new File(new File(jarFile.getName()).getParent(), path);
                        if (dependency.exists()) {
                            addURL(dependency.toURI().toURL());
                        }
                    } catch (MalformedURLException e) {
                        // Ignore les URLs malformées
                    }
                }
            }
        }
    }

    // Méthode utilitaire pour charger un module avec ses dépendances
    public static DiSkyModuleClassLoader createWithDependencies(File moduleJar, ClassLoader parent,
                                                           String originalPackage, String relocatedPackage) throws IOException {
        // Crée une liste des URLs incluant le module et ses dépendances
        List<URL> urls = new ArrayList<>();
        urls.add(moduleJar.toURI().toURL());

        // Ajoute le dossier lib/ s'il existe
        File libDir = new File(moduleJar.getParentFile(), "lib");
        if (libDir.exists() && libDir.isDirectory()) {
            File[] libs = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (libs != null) {
                for (File lib : libs) {
                    urls.add(lib.toURI().toURL());
                }
            }
        }

        return new DiSkyModuleClassLoader(
                urls.toArray(new URL[0]),
                parent,
                originalPackage,
                relocatedPackage
        );
    }
}