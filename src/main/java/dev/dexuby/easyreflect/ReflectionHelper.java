package dev.dexuby.easyreflect;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ReflectionHelper {

    public static List<Class<?>> getPackageClasses(final String packageName, final ClassLoader providedClassLoader) {

        try {
            final ClassLoader classLoader = providedClassLoader != null
                    ? providedClassLoader
                    : Thread.currentThread().getContextClassLoader();
            assert classLoader != null;

            final String path = packageName.replace('.', '/');
            final Enumeration<URL> resources = classLoader.getResources(path);

            final List<File> directories = new ArrayList<>();
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                directories.add(new File(resource.getFile()));
            }

            final List<Class<?>> classes = new ArrayList<>();
            for (final File directory : directories)
                classes.addAll(getDirectoryClasses(directory, packageName));

            return classes;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static List<Class<?>> getPackageClasses(final String packageName) {

        return getPackageClasses(packageName, null);

    }

    public static List<Class<?>> getDirectoryClasses(final File directory, final String packageName) {

        final List<Class<?>> classes = new ArrayList<>();

        try {
            if (!directory.exists()) return classes;

            final File[] files = directory.listFiles();
            assert files != null;

            for (final File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(getDirectoryClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return classes;

    }

}
