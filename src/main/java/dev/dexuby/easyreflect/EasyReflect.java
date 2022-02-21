package dev.dexuby.easyreflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyReflect {

    private final List<Class<?>> classes = new ArrayList<>();

    private EasyReflect() {

    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Class<?>, T> findAnnotatedClasses(final Class<? extends Annotation> annotationClass) {

        final Map<Class<?>, T> foundClasses = new HashMap<>();
        for (final Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass))
                foundClasses.put(clazz, (T) clazz.getAnnotation(annotationClass));
        }

        return foundClasses;

    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Method, T> findAnnotatedMethods(final Class<? extends Annotation> annotationClass) {

        final Map<Method, T> foundMethods = new HashMap<>();
        for (final Class<?> clazz : classes) {
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotationClass))
                    foundMethods.put(method, (T) method.getAnnotation(annotationClass));
            }
        }

        return foundMethods;

    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Method, T> findAnnotatedMethods(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {

        final Map<Method, T> foundMethods = new HashMap<>();
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass))
                foundMethods.put(method, (T) method.getAnnotation(annotationClass));
        }

        return foundMethods;

    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Field, T> findAnnotatedFields(final Class<? extends Annotation> annotationClass) {

        final Map<Field, T> foundFields = new HashMap<>();
        for (final Class<?> clazz : classes) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass))
                    foundFields.put(field, (T) field.getAnnotation(annotationClass));
            }
        }

        return foundFields;

    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Field, T> findAnnotatedFields(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {

        final Map<Field, T> foundFields = new HashMap<>();
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass))
                foundFields.put(field, (T) field.getAnnotation(annotationClass));
        }

        return foundFields;

    }

    public static class Builder {

        private final EasyReflect easyReflect = new EasyReflect();

        public Builder withPackageName(final String packageName) {

            final List<Class<?>> foundClasses = ReflectionHelper.getPackageClasses(packageName);
            if (foundClasses != null) easyReflect.classes.addAll(foundClasses);

            return this;

        }

        public EasyReflect build() {

            return easyReflect;

        }

    }

}
