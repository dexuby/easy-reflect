package dev.dexuby.easyreflect;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ClassLoaderResolver {

    public final static String MANIFEST_FILE = "META-INF/MANIFEST.MF";
    public final static char CLASS_CHAR = '$';

    private final Set<File> processed = new HashSet<>();
    private final List<String> resolvedClassPaths = new ArrayList<>();

    private final ClassLoader classLoader;
    private final Set<String> ignoredPackages;

    public ClassLoaderResolver(final ClassLoader classLoader) {

        this.classLoader = classLoader;
        this.ignoredPackages = new HashSet<>();

    }

    public ClassLoaderResolver(final ClassLoader classLoader, final Set<String> ignoredPackages) {

        this.classLoader = classLoader;
        this.ignoredPackages = ignoredPackages;

    }

    public void addIgnoredPackageName(final String packageName) {

        this.ignoredPackages.add(packageName);

    }

    public void addIgnoredPackageNames(final String... packageNames) {

        this.ignoredPackages.addAll(Arrays.asList(packageNames));

    }

    public boolean resolve() {

        try {
            for (final Map.Entry<File, ClassLoader> entry : getClassPathEntries(classLoader).entrySet())
                process(entry.getKey(), entry.getValue());
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public List<String> getClassPaths() {

        final List<String> filteredPaths = new ArrayList<>();
        for (final String classPath : resolvedClassPaths) {
            if (classPath.indexOf(CLASS_CHAR) == -1)
                filteredPaths.add(classPath);
        }

        return filteredPaths;

    }

    public List<String> getClassPaths(final String packageName) {

        final List<String> filteredPaths = new ArrayList<>();
        for (final String classPath : getClassPaths()) {
            final String className = ReflectionHelper.getClassName(classPath);
            final String classPackageName = ReflectionHelper.getPackageName(className);
            if (classPackageName.equals(packageName))
                filteredPaths.add(classPath);
        }

        return filteredPaths;

    }

    public List<String> getClassPathsRecursive(final String packageName) {

        final List<String> filteredPaths = new ArrayList<>();
        for (final String classPath : getClassPaths()) {
            final String className = ReflectionHelper.getClassName(classPath);
            if (className.startsWith(packageName) &&
                    !this.ignoredPackages.contains(ReflectionHelper.getPackageName(className)))
                filteredPaths.add(classPath);
        }

        return filteredPaths;

    }

    private Map<File, ClassLoader> getClassPathEntries(final ClassLoader classLoader) {

        final Map<File, ClassLoader> entries = new HashMap<>();
        final ClassLoader parentClassLoader = classLoader.getParent();
        if (parentClassLoader != null)
            entries.putAll(getClassPathEntries(parentClassLoader));

        for (final URL url : getClassLoaderUrls(classLoader)) {
            if (!url.getProtocol().equals("file")) continue;
            final File file = new File(url.getFile());
            if (!entries.containsKey(file))
                entries.put(file, classLoader);
        }

        return entries;

    }

    private List<URL> getClassLoaderUrls(final ClassLoader classLoader) {

        if (classLoader instanceof URLClassLoader) {
            return Arrays.asList(((URLClassLoader) classLoader).getURLs());
        } else if (classLoader.equals(ClassLoader.getSystemClassLoader())) {
            return parseJavaClassPath();
        } else {
            return Collections.emptyList();
        }

    }

    private List<URL> parseJavaClassPath() {

        final List<URL> urls = new ArrayList<>();
        for (final String entry : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {
            try {
                try {
                    urls.add(new File(entry).toURI().toURL());
                } catch (final SecurityException ex) {
                    urls.add(new URL("file", null, new File(entry).getAbsolutePath()));
                }
            } catch (final MalformedURLException ex) {
                //
            }
        }

        return urls;

    }

    private void process(final File file, final ClassLoader classLoader) throws IOException {

        if (!processed.add(file.getCanonicalFile()) || !file.exists()) return;

        if (file.isDirectory()) {
            processDirectory(file, classLoader);
        } else {
            processJarFile(file, classLoader);
        }

    }

    private void processDirectory(final File directory, final ClassLoader classLoader) {

        processDirectory(directory, classLoader, "");

    }

    private void processDirectory(final File directory, final ClassLoader classLoader, final String packagePrefix) {

        final File[] files = directory.listFiles();
        if (files == null) return;

        for (final File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, classLoader, packagePrefix + file.getName() + "/");
            } else {
                final String resourceName = packagePrefix + file.getName();
                if (resourceName.equals(MANIFEST_FILE)) continue;
                resolvedClassPaths.add(resourceName);
            }
        }

    }

    private void processJarFile(final File file, final ClassLoader classLoader) throws IOException {

        try (JarFile jarFile = new JarFile(file)) {
            for (final File classPath : getClassPathsFromManifest(file, jarFile.getManifest()))
                process(classPath, classLoader);
            scanJarFile(jarFile, classLoader);
        }

    }

    private void scanJarFile(final JarFile jarFile, final ClassLoader classLoader) {

        final Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            final JarEntry entry = enumeration.nextElement();
            if (!entry.isDirectory() && !entry.getName().equals(MANIFEST_FILE))
                resolvedClassPaths.add(entry.getName());
        }

    }

    private Set<File> getClassPathsFromManifest(final File jarFile, final Manifest manifest) throws MalformedURLException {

        final Set<File> classPaths = new HashSet<>();
        if (manifest == null) return classPaths;

        final String classPathAttribute = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
        if (classPathAttribute == null) return classPaths;

        for (final String path : classPathAttribute.split(" ")) {
            final URL url = getClassPathEntry(jarFile, path);
            if (url.getProtocol().equals("file"))
                classPaths.add(new File(url.getFile()));
        }

        return classPaths;

    }

    private URL getClassPathEntry(final File jarFile, final String path) throws MalformedURLException {

        return new URL(jarFile.toURI().toURL(), path);

    }

}
