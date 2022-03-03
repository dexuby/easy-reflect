package dev.dexuby.easyreflect;

import java.util.ArrayList;
import java.util.List;

public class ReflectionHelper {

    public static List<Class<?>> getPackageClassesRecursive(final ClassLoader classLoader, final String packageName) {

        final List<Class<?>> classes = new ArrayList<>();

        final ClassLoaderResolver classLoaderResolver = new ClassLoaderResolver(classLoader);
        final boolean status = classLoaderResolver.resolve();

        if (!status) return classes;

        for (final String classPath : classLoaderResolver.getClassPathsRecursive(packageName)) {
            try {
                classes.add(Class.forName(getClassName(classPath), false, classLoader));
            } catch (final Exception ex) {
                //
            }
        }

        return classes;

    }

    public static String getClassName(final String classPath) {

        return classPath.substring(0, classPath.length() - ".class".length()).replace('/', '.');

    }

    public static String getPackageName(final String classPath) {

        final int lastDot = classPath.lastIndexOf(46);
        return lastDot < 0 ? "" : classPath.substring(0, lastDot);

    }

}
