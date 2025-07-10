# Meta-anotaciones de Java - Guía Completa

Las meta-anotaciones son anotaciones que se aplican a otras anotaciones para definir su comportamiento y características. Java proporciona varias meta-anotaciones estándar que controlan cómo se procesan y utilizan las anotaciones personalizadas.

## 1. @Target

Define dónde se puede aplicar una anotación (qué elementos del código pueden ser anotados).

### Sintaxis
```java
@Target(ElementType.valor)
@Target({ElementType.valor1, ElementType.valor2, ...})
```

### Valores de ElementType

| Valor | Descripción | Ejemplo de uso |
|-------|-------------|----------------|
| `TYPE` | Clases, interfaces, enums | `@Entity class User {}` |
| `FIELD` | Campos/atributos | `@Column private String name;` |
| `METHOD` | Métodos | `@Override public String toString()` |
| `PARAMETER` | Parámetros de métodos | `void save(@Valid User user)` |
| `CONSTRUCTOR` | Constructores | `@Inject public Service() {}` |
| `LOCAL_VARIABLE` | Variables locales | `@SuppressWarnings("unused") int x;` |
| `ANNOTATION_TYPE` | Anotaciones | `@Target(TYPE) @interface MyAnnotation` |
| `PACKAGE` | Paquetes | En `package-info.java` |
| `TYPE_PARAMETER` | Parámetros de tipo genérico | `class List<@NonNull T>` |
| `TYPE_USE` | Uso de tipos | `@NonNull String name;` |
| `MODULE` | Módulos | En `module-info.java` |
| `RECORD_COMPONENT` | Componentes de records | `record User(@Id String name)` |

### Ejemplos
```java
// Solo para métodos
@Target(ElementType.METHOD)
public @interface Override {}

// Para campos y métodos
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonProperty {}

// Para cualquier uso de tipo
@Target(ElementType.TYPE_USE)
public @interface NonNull {}
```

## 2. @Retention

Define cuánto tiempo se conserva la información de la anotación.

### Sintaxis
```java
@Retention(RetentionPolicy.valor)
```

### Valores de RetentionPolicy

| Valor | Descripción | Cuándo usar |
|-------|-------------|-------------|
| `SOURCE` | Solo en código fuente, descartada por el compilador | Para herramientas de análisis de código |
| `CLASS` | En bytecode pero no en runtime | Para procesamiento durante compilación |
| `RUNTIME` | Disponible en runtime via reflection | Para frameworks que usan reflection |

### Ejemplos
```java
// Solo durante compilación
@Retention(RetentionPolicy.SOURCE)
public @interface Override {}

// Para reflection en runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {}

// En bytecode (por defecto)
@Retention(RetentionPolicy.CLASS)
public @interface Generated {}
```

## 3. @Documented

Indica que la anotación debe incluirse en la documentación generada por JavaDoc.

### Sintaxis
```java
@Documented
public @interface MiAnotacion {}
```

### Ejemplo
```java
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMethod {
    String description() default "";
}

// En JavaDoc aparecerá la anotación
@ApiMethod(description = "Obtiene usuario por ID")
public User getUserById(Long id) {
    // ...
}
```

## 4. @Inherited

Permite que una anotación sea heredada por subclases.

### Sintaxis
```java
@Inherited
public @interface MiAnotacion {}
```

### Ejemplo
```java
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {}

@Service
public class BaseService {}

// UserService hereda automáticamente @Service
public class UserService extends BaseService {}
```

### Limitaciones
- Solo funciona con anotaciones aplicadas a clases
- No funciona con interfaces, métodos o campos
- La herencia es solo de clase padre a clase hija

## 5. @Repeatable

Permite que una anotación se aplique múltiples veces al mismo elemento.

### Sintaxis
```java
@Repeatable(ContenedorAnotacion.class)
public @interface AnotacionRepetible {}

public @interface ContenedorAnotacion {
    AnotacionRepetible[] value();
}
```

### Ejemplo completo
```java
// Anotación contenedora
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Roles {
    Role[] value();
}

// Anotación repetible
@Repeatable(Roles.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {
    String value();
}

// Uso múltiple
@Role("ADMIN")
@Role("USER")
@Role("MODERATOR")
public class User {}
```

## 6. @Native

Indica que un campo puede ser referenciado desde código nativo.

### Sintaxis
```java
@Native
public static final int CONSTANTE = 42;
```

### Ejemplo
```java
public class MathConstants {
    @Native
    public static final double PI = 3.14159265358979323846;
    
    @Native
    public static final int MAX_VALUE = Integer.MAX_VALUE;
}
```

## Ejemplos Prácticos Combinados

### Anotación para validación
```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {
    String message() default "El valor no puede ser nulo";
    Class<?>[] groups() default {};
}
```

### Anotación para configuración
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Configuration {
    String value() default "";
    boolean proxyBeanMethods() default true;
}
```

### Anotación repetible para permisos
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {
    Permission[] value();
}

@Repeatable(Permissions.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String value();
}

// Uso
@Permission("READ")
@Permission("WRITE")
@Permission("DELETE")
public void adminMethod() {}
```

## Buenas Prácticas

1. **Siempre especificar @Target**: Define claramente dónde se puede usar tu anotación
2. **Elegir la @Retention correcta**: 
   - `SOURCE` para herramientas de análisis
   - `CLASS` para procesamiento en compilación
   - `RUNTIME` para frameworks que usan reflection
3. **Usar @Documented**: Para anotaciones que aportan información valiosa
4. **@Inherited con cuidado**: Solo cuando realmente necesites herencia
5. **@Repeatable**: Para casos donde múltiples valores tienen sentido

## Resumen de Combinaciones Comunes

| Propósito | @Target | @Retention | Otras |
|-----------|---------|------------|-------|
| Validación | FIELD, METHOD, PARAMETER | RUNTIME | @Documented |
| Configuración | TYPE | RUNTIME | @Documented, @Inherited |
| Análisis estático | TYPE, METHOD, FIELD | SOURCE | - |
| Procesamiento en compilación | TYPE, METHOD | CLASS | - |
| Inyección de dependencias | FIELD, METHOD, CONSTRUCTOR | RUNTIME | @Documented |

Las meta-anotaciones son fundamentales para crear anotaciones personalizadas efectivas y bien diseñadas en Java.