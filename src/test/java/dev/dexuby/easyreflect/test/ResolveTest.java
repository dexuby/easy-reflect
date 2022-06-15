package dev.dexuby.easyreflect.test;

import dev.dexuby.easyreflect.EasyReflect;
import dev.dexuby.easyreflect.test.test.TestAnnotation;
import dev.dexuby.easyreflect.test.test.TestClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ResolveTest {

    @Test
    public void testIgnoredResolving() {

        final EasyReflect easyReflect = new EasyReflect.Builder()
                .resolvePackageName(ClassLoader.getSystemClassLoader(), "dev.dexuby.easyreflect.test", "dev.dexuby.easyreflect.test.test.sub")
                .build();

        final Map<Class<?>, TestAnnotation> results = easyReflect.findAnnotatedClasses(TestAnnotation.class);
        assertEquals(1, results.size());

        final TestAnnotation testAnnotation = results.get(TestClass.class);
        assertEquals("testOne", testAnnotation.value());

    }

}
