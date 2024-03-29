package dev.dexuby.easyreflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unchecked")
public class EasyReflect {

    private final List<Class<?>> classes = new ArrayList<>();
    private final Set<String> ignoredPackages = new HashSet<>();

    /**
     * Used to find all classes in all resolved classes of the current instance that are annotated with the provided
     * annotation.
     *
     * @param annotationClass The annotation class.
     * @param <T>             The annotation type.
     * @return A map containing the classes mapped to the annotations.
     */

    public <T extends Annotation> Map<Class<?>, T> findAnnotatedClasses(final Class<? extends Annotation> annotationClass) {

        final Map<Class<?>, T> foundClasses = new HashMap<>();
        for (final Class<?> clazz : this.classes) {
            if (clazz.isAnnotationPresent(annotationClass))
                foundClasses.put(clazz, (T) clazz.getAnnotation(annotationClass));
        }

        return foundClasses;

    }

    /**
     * Used to find all declared methods in all resolved classes of the current instance that are annotated with the
     * provided annotation.
     *
     * @param annotationClass The annotation class.
     * @param <T>             The annotation type.
     * @return A map containing the methods mapped to the annotations.
     */

    public <T extends Annotation> Map<Method, T> findAnnotatedMethods(final Class<? extends Annotation> annotationClass) {

        final Map<Method, T> foundMethods = new HashMap<>();
        for (final Class<?> clazz : this.classes) {
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotationClass))
                    foundMethods.put(method, (T) method.getAnnotation(annotationClass));
            }
        }

        return foundMethods;

    }

    /**
     * Used to find all declared methods in the provided class that are annotated with the provided annotation.
     *
     * @param clazz           The target class.
     * @param annotationClass The annotation class.
     * @param <T>             The annotation type.
     * @return A map containing the methods mapped to the annotations.
     */

    public <T extends Annotation> Map<Method, T> findAnnotatedMethods(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {

        final Map<Method, T> foundMethods = new HashMap<>();
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass))
                foundMethods.put(method, (T) method.getAnnotation(annotationClass));
        }

        return foundMethods;

    }

    /**
     * Used to find all declared fields in all resolved classes of the current instance that are annotated with the
     * provided annotation.
     *
     * @param annotationClass The annotation class.
     * @param <T>             The annotation type.
     * @return A map containing the fields mapped to the annotations.
     */

    public <T extends Annotation> Map<Field, T> findAnnotatedFields(final Class<? extends Annotation> annotationClass) {

        final Map<Field, T> foundFields = new HashMap<>();
        for (final Class<?> clazz : this.classes) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass))
                    foundFields.put(field, (T) field.getAnnotation(annotationClass));
            }
        }

        return foundFields;

    }

    /**
     * Used to find all declared fields in the provided class that are annotated with the provided annotation.
     *
     * @param clazz           The target class.
     * @param annotationClass The annotation class.
     * @param <T>             The annotation type.
     * @return A map containing the fields mapped to the annotations.
     */

    public <T extends Annotation> Map<Field, T> findAnnotatedFields(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {

        final Map<Field, T> foundFields = new HashMap<>();
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass))
                foundFields.put(field, (T) field.getAnnotation(annotationClass));
        }

        return foundFields;

    }

    /**
     * Used to ignore the provided package.
     *
     * @param ignoredPackage The name of the package.
     */

    public void ignorePackage(final String ignoredPackage) {

        this.ignoredPackages.add(ignoredPackage);

    }

    /**
     * Used to find all classes in the provided package and store them in the current instance.
     *
     * @param classLoader The class loader.
     * @param packageName The name of the package.
     */

    public void resolvePackage(final ClassLoader classLoader, final String packageName) {

        this.classes.addAll(ReflectionHelper.getPackageClassesRecursive(classLoader, packageName, this.ignoredPackages));

    }

    /**
     * Used to get a copy of the resolved classes list.
     *
     * @return The classes list copy.
     */

    public List<Class<?>> getClasses() {

        return new ArrayList<>(classes);

    }

    public static EasyReflect.Builder builder() {

        return new EasyReflect.Builder();

    }

    public static class Builder {

        private final List<String> targetPackages = new ArrayList<>();
        private final List<String> ignoredPackages = new ArrayList<>();
        private final List<Class<?>> preResolvedClasses = new ArrayList<>();

        private ClassLoader classLoader;

        public Builder classLoader(final ClassLoader classLoader) {

            this.classLoader = classLoader;
            return this;

        }

        public Builder resolvePackage(final String packageName) {

            this.targetPackages.add(packageName);
            return this;

        }

        public Builder ignoredPackages(final String... ignoredPackages) {

            this.ignoredPackages.addAll(Arrays.asList(ignoredPackages));
            return this;

        }

        public Builder ignoredPackages(final Package... ignoredPackages) {

            for (final Package ignoredPackage : ignoredPackages)
                this.ignoredPackages.add(ignoredPackage.getName());
            return this;

        }

        public Builder ignoredPackage(final String ignoredPackage) {

            this.ignoredPackages.add(ignoredPackage);
            return this;

        }

        public Builder ignoredPackage(final Package ignoredPackage) {

            this.ignoredPackages.add(ignoredPackage.getName());
            return this;

        }

        public Builder preResolvedClass(final Class<?> clazz) {

            this.preResolvedClasses.add(clazz);
            return this;

        }

        public Builder preResolvedClasses(final List<Class<?>> classes) {

            this.preResolvedClasses.addAll(classes);
            return this;

        }

        public EasyReflect build() {

            if (this.classLoader == null)
                this.classLoader = ClassLoader.getSystemClassLoader();

            final EasyReflect easyReflect = new EasyReflect();
            easyReflect.classes.addAll(this.preResolvedClasses);
            easyReflect.ignoredPackages.addAll(this.ignoredPackages);
            for (final String targetPackage : this.targetPackages)
                easyReflect.resolvePackage(this.classLoader, targetPackage);

            return easyReflect;

        }

    }

}
