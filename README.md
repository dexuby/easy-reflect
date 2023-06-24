## EasyReflect

[![](https://jitpack.io/v/dexuby/EasyReflect.svg)](https://jitpack.io/#dexuby/EasyReflect)

Example usage:
```java
final EasyReflect easyReflect = EasyReflect.builder()
    .classLoader(ClassLoader.getSystemClassLoader())
    .ignoredPackage("com.example.project.external")
    .resolvePackage("com.example.project")
    .build();

easyReflect.resolvePackage(ClassLoader.getSystemClassLoader(), "com.example.external");
    
final Map<Class<?>, ExampleAnnotation> results = easyReflect.findAnnotatedClasses(ExampleAnnotation.class);
final ExampleAnnotation exampleAnnotation = results.get(Example.class);
assertEquals("exampleStringValue", exampleAnnotation.value())
```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
	<url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.dexuby</groupId>
    <artifactId>EasyReflect</artifactId>
    <version>...</version>
</dependency>
```
