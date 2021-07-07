package broccolai.tickets.core.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public final class ReflectionHelper {

    private ReflectionHelper() {
        // utility class
    }

    public static boolean classExists(final @NonNull String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static <T> @NotNull Collection<T> parametersAnnotatedBy(
            final Class<? extends Annotation> annotationClass,
            final Method method,
            final @Nullable Object @NonNull [] objectParameters
    ) {
        Collection<T> results = new ArrayList<>();
        Parameter[] reflectedParameters = method.getParameters();

        for (int i = 0; i < reflectedParameters.length; i++) {
            Parameter reflectedParameter = reflectedParameters[i];
            if (reflectedParameter != null && reflectedParameter.isAnnotationPresent(annotationClass)) {
                //noinspection unchecked
                results.add((T) objectParameters[i]);
            }
        }

        return results;
    }

    public static <T> @Nullable T parameterAnnotatedBy(
            final Class<? extends Annotation> annotationClass,
            final Method method,
            final @Nullable Object @NonNull [] objectParameters
    ) {
        Parameter[] reflectedParameters = method.getParameters();

        for (int i = 0; i < reflectedParameters.length; i++) {
            Parameter reflectedParameter = reflectedParameters[i];
            if (reflectedParameter != null && reflectedParameter.isAnnotationPresent(annotationClass)) {
                //noinspection unchecked
                return (T) objectParameters[i];
            }
        }

        return null;
    }

}