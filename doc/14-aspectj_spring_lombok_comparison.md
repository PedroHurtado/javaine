# Comparativa AspectJ vs Spring AOP y Análisis de Lombok

## 1. Comparativa: AspectJ vs Spring AOP

### AspectJ

**Definición**: AspectJ es un framework de programación orientada a aspectos (AOP) completo que extiende Java con construcciones adicionales para definir aspectos.

#### Características principales:
- **Weaving completo**: Compile-time, post-compile-time y load-time weaving
- **Sintaxis nativa**: Utiliza sintaxis específica de AspectJ (@AspectJ o sintaxis nativa)
- **Capacidades avanzadas**: Soporta todos los tipos de join points
- **Independiente del contenedor**: No requiere Spring Framework
- **Rendimiento**: Mejor rendimiento debido al weaving en tiempo de compilación

#### Tipos de Weaving:
- **Compile-time weaving**: Integración durante la compilación
- **Post-compile weaving**: Modificación de bytecode después de la compilación
- **Load-time weaving**: Instrumentación durante la carga de clases

#### Ventajas:
- Funcionalidades AOP completas
- Mejor rendimiento
- Soporte para todos los join points (field access, constructor calls, etc.)
- No limitado a métodos públicos
- Funciona con cualquier objeto Java

#### Desventajas:
- Curva de aprendizaje más pronunciada
- Requiere compilador especial o configuración de weaving
- Mayor complejidad de setup
- Debugging más complejo

### Spring AOP

**Definición**: Spring AOP es una implementación simplificada de AOP que se integra con el contenedor de Spring y utiliza proxies dinámicos.

#### Características principales:
- **Proxy-based**: Utiliza proxies JDK dinámicos o CGLIB
- **Integración con Spring**: Completamente integrado con el ecosistema Spring
- **Sintaxis @AspectJ**: Utiliza anotaciones de AspectJ para definir aspectos
- **Runtime weaving**: Los aspectos se aplican en tiempo de ejecución
- **Simplicidad**: Configuración y uso más sencillos

#### Mecanismo de funcionamiento:
```java
// Ejemplo de aspecto en Spring AOP
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Executing: " + joinPoint.getSignature().getName());
    }
}
```

#### Ventajas:
- Fácil integración con Spring
- Configuración sencilla
- No requiere compilador especial
- Debugging más sencillo
- Menor curva de aprendizaje

#### Desventajas:
- Limitado a métodos públicos
- Solo funciona con beans de Spring
- Overhead de proxies en runtime
- Funcionalidades AOP limitadas
- No soporta field interception

### Tabla Comparativa

| Característica | AspectJ | Spring AOP |
|----------------|---------|------------|
| **Weaving** | Compile-time, Post-compile, Load-time | Runtime (Proxy-based) |
| **Join Points** | Todos (methods, fields, constructors, etc.) | Solo method execution |
| **Targets** | Cualquier objeto Java | Solo Spring beans |
| **Visibilidad** | Todos los métodos | Solo métodos públicos |
| **Rendimiento** | Mejor (sin overhead de proxy) | Overhead de proxy |
| **Configuración** | Más compleja | Más sencilla |
| **Integración** | Framework agnóstico | Específico de Spring |
| **Curva de aprendizaje** | Pronunciada | Suave |

## 2. Análisis de Lombok

### ¿Qué es Lombok?

Lombok es una biblioteca Java que utiliza **procesamiento de anotaciones** para generar código automáticamente durante la compilación. **Sí, efectivamente trabaja con el AST (Abstract Syntax Tree)** para modificar y generar código.

### Funcionamiento basado en AST

```java
// Código original
@Data
public class User {
    private String name;
    private int age;
}

// Código generado por Lombok (conceptualmente)
public class User {
    private String name;
    private int age;
    
    // Getters generados
    public String getName() { return this.name; }
    public int getAge() { return this.age; }
    
    // Setters generados
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    
    // toString generado
    public String toString() { /* implementación */ }
    
    // equals y hashCode generados
    public boolean equals(Object o) { /* implementación */ }
    public int hashCode() { /* implementación */ }
}
```

### Proceso de generación:

1. **Annotation Processing**: Lombok utiliza la API de procesamiento de anotaciones de Java
2. **AST Manipulation**: Modifica el árbol de sintaxis abstracta durante la compilación
3. **Code Generation**: Genera métodos, constructores y otros elementos de código
4. **Bytecode Integration**: El código generado se integra en el bytecode final

### Principales anotaciones de Lombok:

- `@Getter/@Setter`: Genera getters y setters
- `@Data`: Combina @Getter, @Setter, @ToString, @EqualsAndHashCode
- `@Builder`: Implementa el patrón Builder
- `@NoArgsConstructor/@AllArgsConstructor`: Genera constructores
- `@Slf4j`: Genera logger

## 3. Retention Policies en Anotaciones

### Definición de @Retention

La anotación `@Retention` especifica cuándo debe estar disponible una anotación durante el ciclo de vida de la aplicación.

### Tipos de RetentionPolicy:

#### 1. RetentionPolicy.SOURCE
```java
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ProcessAtCompileTime {
    String value() default "";
}
```
- **Disponibilidad**: Solo durante la compilación
- **Uso**: Procesamiento de anotaciones, validaciones de compilación
- **Ejemplo**: Lombok, @SuppressWarnings

#### 2. RetentionPolicy.CLASS
```java
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface BytecodeProcessing {
    String value() default "";
}
```
- **Disponibilidad**: Hasta el bytecode (incluida en .class)
- **Uso**: Instrumentación de bytecode, análisis estático
- **Ejemplo**: Anotaciones para frameworks de instrumentación

#### 3. RetentionPolicy.RUNTIME
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RuntimeProcessing {
    String value() default "";
}
```
- **Disponibilidad**: Durante la ejecución
- **Uso**: Reflexión, configuración runtime, AOP
- **Ejemplo**: @Autowired, @RequestMapping, @Transactional

### Ejemplo práctico de diferentes comportamientos:

```java
// Anotación para procesamiento en tiempo de compilación
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenerateBuilder {
    // Lombok-style: procesada y removida después de la compilación
}

// Anotación para instrumentación de bytecode
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Instrument {
    // Disponible para herramientas de análisis de bytecode
}

// Anotación para configuración runtime
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {
    String value() default "";
    // Leída en runtime por frameworks como Spring
}
```

### Uso en diferentes contextos:

#### Lombok (SOURCE):
```java
@Data // RetentionPolicy.SOURCE
public class Entity {
    private String id;
    // Código generado durante compilación, anotación descartada
}
```

#### Spring AOP (RUNTIME):
```java
@Transactional // RetentionPolicy.RUNTIME
public class Service {
    public void businessMethod() {
        // Anotación leída en runtime para crear proxies
    }
}
```

#### JPA Annotations (RUNTIME):
```java
@Entity // RetentionPolicy.RUNTIME
@Table(name = "users") // RetentionPolicy.RUNTIME
public class User {
    
    @Id // RetentionPolicy.RUNTIME
    @GeneratedValue(strategy = GenerationType.IDENTITY) // RetentionPolicy.RUNTIME
    private Long id;
    
    @Column(name = "username", nullable = false, length = 50) // RetentionPolicy.RUNTIME
    private String username;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // RetentionPolicy.RUNTIME
    private List<Order> orders;
    
    // Las anotaciones JPA se leen en runtime por el proveedor JPA
    // (Hibernate, EclipseLink, etc.) para construir el metamodelo
}
```

#### AspectJ (CLASS/RUNTIME):
```java
@Aspect // Puede ser CLASS o RUNTIME dependiendo del weaving
public class SecurityAspect {
    @Around("@annotation(Secured)")
    public Object secure(ProceedingJoinPoint joinPoint) throws Throwable {
        // Procesamiento según el tipo de weaving
        return joinPoint.proceed();
    }
}
```

## 5. Ejemplo Práctico: Lectura de Anotaciones JPA en Runtime con Reflection

### Entidad JPA de ejemplo:

```java
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ProductCategory category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    // Constructores, getters y setters...
}
```

### Clase para leer anotaciones JPA en Runtime:

```java
import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class JPAAnnotationReader {
    
    public static void main(String[] args) {
        analyzeJPAEntity(Product.class);
    }
    
    public static void analyzeJPAEntity(Class<?> entityClass) {
        System.out.println("=== Análisis de Entidad JPA: " + entityClass.getSimpleName() + " ===");
        
        // 1. Leer anotaciones a nivel de clase
        readClassAnnotations(entityClass);
        
        // 2. Leer anotaciones de campos
        readFieldAnnotations(entityClass);
        
        // 3. Leer anotaciones de métodos
        readMethodAnnotations(entityClass);
    }
    
    private static void readClassAnnotations(Class<?> entityClass) {
        System.out.println("\n--- Anotaciones de Clase ---");
        
        // Verificar si es una entidad JPA
        if (entityClass.isAnnotationPresent(Entity.class)) {
            Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
            String entityName = entityAnnotation.name().isEmpty() ? 
                entityClass.getSimpleName() : entityAnnotation.name();
            System.out.println("@Entity encontrada - Nombre: " + entityName);
        }
        
        // Leer anotación @Table
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            System.out.println("@Table encontrada:");
            System.out.println("  - Nombre: " + tableAnnotation.name());
            System.out.println("  - Schema: " + tableAnnotation.schema());
            System.out.println("  - Catalog: " + tableAnnotation.catalog());
        }
        
        // Leer otras anotaciones de clase
        System.out.println("Todas las anotaciones de clase:");
        Arrays.stream(entityClass.getAnnotations())
            .forEach(annotation -> System.out.println("  - " + annotation.annotationType().getSimpleName()));
    }
    
    private static void readFieldAnnotations(Class<?> entityClass) {
        System.out.println("\n--- Anotaciones de Campos ---");
        
        Field[] fields = entityClass.getDeclaredFields();
        
        for (Field field : fields) {
            System.out.println("\nCampo: " + field.getName() + " (" + field.getType().getSimpleName() + ")");
            
            // Leer @Id
            if (field.isAnnotationPresent(Id.class)) {
                System.out.println("  - @Id: Campo clave primaria");
            }
            
            // Leer @GeneratedValue
            if (field.isAnnotationPresent(GeneratedValue.class)) {
                GeneratedValue genValue = field.getAnnotation(GeneratedValue.class);
                System.out.println("  - @GeneratedValue: " + genValue.strategy());
            }
            
            // Leer @Column
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                System.out.println("  - @Column:");
                System.out.println("    * Nombre: " + column.name());
                System.out.println("    * Nullable: " + column.nullable());
                System.out.println("    * Length: " + column.length());
                System.out.println("    * Precision: " + column.precision());
                System.out.println("    * Scale: " + column.scale());
            }
            
            // Leer @Enumerated
            if (field.isAnnotationPresent(Enumerated.class)) {
                Enumerated enumerated = field.getAnnotation(Enumerated.class);
                System.out.println("  - @Enumerated: " + enumerated.value());
            }
            
            // Leer @OneToMany
            if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                System.out.println("  - @OneToMany:");
                System.out.println("    * MappedBy: " + oneToMany.mappedBy());
                System.out.println("    * Cascade: " + Arrays.toString(oneToMany.cascade()));
                System.out.println("    * Fetch: " + oneToMany.fetch());
            }
            
            // Leer @ManyToOne
            if (field.isAnnotationPresent(ManyToOne.class)) {
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                System.out.println("  - @ManyToOne:");
                System.out.println("    * Fetch: " + manyToOne.fetch());
                System.out.println("    * Optional: " + manyToOne.optional());
            }
            
            // Leer @JoinColumn
            if (field.isAnnotationPresent(JoinColumn.class)) {
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                System.out.println("  - @JoinColumn:");
                System.out.println("    * Nombre: " + joinColumn.name());
                System.out.println("    * Referenced Column: " + joinColumn.referencedColumnName());
            }
        }
    }
    
    private static void readMethodAnnotations(Class<?> entityClass) {
        System.out.println("\n--- Anotaciones de Métodos ---");
        
        Method[] methods = entityClass.getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.getAnnotations().length > 0) {
                System.out.println("\nMétodo: " + method.getName());
                
                // Ejemplo: leer anotaciones de validación en getters/setters
                Arrays.stream(method.getAnnotations())
                    .forEach(annotation -> {
                        System.out.println("  - " + annotation.annotationType().getSimpleName());
                        
                        // Ejemplo específico para @Transient
                        if (annotation.annotationType() == Transient.class) {
                            System.out.println("    * Campo/método marcado como transient");
                        }
                    });
            }
        }
    }
}
```

### Ejemplo de utilidad práctica - Generador de SQL:

```java
public class SimpleORMGenerator {
    
    public static String generateCreateTableSQL(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        
        // Obtener nombre de tabla
        String tableName = entityClass.getSimpleName().toLowerCase();
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!table.name().isEmpty()) {
                tableName = table.name();
            }
        }
        
        sql.append("CREATE TABLE ").append(tableName).append(" (\n");
        
        Field[] fields = entityClass.getDeclaredFields();
        boolean firstField = true;
        
        for (Field field : fields) {
            // Ignorar campos transient y relaciones
            if (field.isAnnotationPresent(Transient.class) || 
                field.isAnnotationPresent(OneToMany.class) ||
                field.isAnnotationPresent(ManyToMany.class)) {
                continue;
            }
            
            if (!firstField) {
                sql.append(",\n");
            }
            
            // Nombre de columna
            String columnName = field.getName();
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (!column.name().isEmpty()) {
                    columnName = column.name();
                }
            }
            
            sql.append("  ").append(columnName);
            
            // Tipo de datos
            String sqlType = javaSqlTypeMapping(field.getType());
            sql.append(" ").append(sqlType);
            
            // Constraints
            if (field.isAnnotationPresent(Id.class)) {
                sql.append(" PRIMARY KEY");
            }
            
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (!column.nullable()) {
                    sql.append(" NOT NULL");
                }
            }
            
            firstField = false;
        }
        
        sql.append("\n);");
        return sql.toString();
    }
    
    private static String javaSqlTypeMapping(Class<?> javaType) {
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == Integer.class || javaType == int.class) return "INT";
        if (javaType == Long.class || javaType == long.class) return "BIGINT";
        if (javaType == Double.class || javaType == double.class) return "DOUBLE";
        if (javaType == Boolean.class || javaType == boolean.class) return "BOOLEAN";
        return "VARCHAR(255)"; // default
    }
    
    public static void main(String[] args) {
        System.out.println("=== SQL Generado ===");
        System.out.println(generateCreateTableSQL(Product.class));
    }
}
```

### Salida esperada del análisis:

```
=== Análisis de Entidad JPA: Product ===

--- Anotaciones de Clase ---
@Entity encontrada - Nombre: Product
@Table encontrada:
  - Nombre: products
  - Schema: 
  - Catalog: 
Todas las anotaciones de clase:
  - Entity
  - Table

--- Anotaciones de Campos ---

Campo: id (Long)
  - @Id: Campo clave primaria
  - @GeneratedValue: IDENTITY

Campo: name (String)
  - @Column:
    * Nombre: product_name
    * Nullable: false
    * Length: 100
    * Precision: 0
    * Scale: 0

Campo: price (BigDecimal)
  - @Column:
    * Nombre: price
    * Nullable: true
    * Length: 255
    * Precision: 10
    * Scale: 2

Campo: category (ProductCategory)
  - @Enumerated: STRING
  - @Column:
    * Nombre: category
    * Nullable: true
    * Length: 255
    * Precision: 0
    * Scale: 0

Campo: orderItems (List)
  - @OneToMany:
    * MappedBy: product
    * Cascade: [ALL]
    * Fetch: LAZY
```

### Cuándo usar AspectJ:
- Necesitas funcionalidades AOP completas
- Rendimiento es crítico
- Requieres interceptar fields o constructors
- Trabajas fuera del ecosistema Spring

### Cuándo usar Spring AOP:
- Estás usando Spring Framework
- Necesitas AOP básico (logging, transacciones, seguridad)
- Prefieres simplicidad sobre funcionalidades avanzadas
- El equipo tiene experiencia limitada con AOP

### Sobre Lombok:
- Efectivamente utiliza AST manipulation
- Procesa anotaciones en tiempo de compilación
- Genera código automáticamente
- Utiliza RetentionPolicy.SOURCE

### Sobre Retention Policies:
- **SOURCE**: Para generación de código y validaciones de compilación
- **CLASS**: Para instrumentación de bytecode y análisis estático
- **RUNTIME**: Para configuración y procesamiento dinámico

La elección entre AspectJ y Spring AOP depende de tus necesidades específicas de AOP, mientras que Lombok es una herramienta complementaria que funciona independientemente de la estrategia AOP elegida.