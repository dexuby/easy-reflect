## EasyReflect

[![](https://jitpack.io/v/dexuby/EasyReflect.svg)](https://jitpack.io/#dexuby/EasyReflect)

Example usage:
```java
final EasyReflect easyReflect = new EasyReflect.Builder()
    .ignoredPackage("com.example.project.external")
    .resolvePackage(ClassLoader.getSystemClassLoader(), "com.example.project")
    .build();
    
final Map<Class<?>, ExampleAnnotation> results = easyReflect.findAnnotatedClasses(ExampleAnnotation.class);
final ExampleAnnotation exampleAnnotation = results.get(Example.class);
assertEquals("exampleStringValue", exampleAnnotation.value())
```
