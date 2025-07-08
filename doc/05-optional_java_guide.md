# Optional<T> en Java: Guía completa y comparación con Objects.requireNonNull()

## ¿Qué es Optional<T>?

`Optional<T>` es una clase contenedora introducida en Java 8 que puede contener o no un valor de tipo `T`. Su propósito principal es proporcionar una alternativa más segura y expresiva a las referencias nulas, ayudando a evitar el temido `NullPointerException`.

## Características principales de Optional<T>

### 1. Inmutabilidad
Una vez creado, un `Optional` no puede ser modificado. Es un objeto inmutable que representa un estado específico.

### 2. Expresividad
Hace explícito en el código cuando un valor puede estar presente o ausente, mejorando la legibilidad y comprensión del código.

### 3. Encadenamiento seguro
Permite encadenar operaciones de forma segura sin verificaciones explícitas de null.

## Creación de Optional

### Optional.empty()
```java
Optional<String> emptyOptional = Optional.empty();
```

### Optional.of()
```java
String valor = "Hello World";
Optional<String> optional = Optional.of(valor); // Lanza excepción si valor es null
```

### Optional.ofNullable()
```java
String valorPosiblementeNulo = getNullableValue();
Optional<String> optional = Optional.ofNullable(valorPosiblementeNulo);
```

## Métodos principales de Optional

### Verificación de presencia
```java
Optional<String> optional = Optional.of("Hello");

// Verificar si tiene valor
boolean isPresent = optional.isPresent();

// Verificar si está vacío (Java 11+)
boolean isEmpty = optional.isEmpty();
```

### Obtención de valores
```java
// Obtener valor (lanza excepción si está vacío)
String value = optional.get();

// Obtener valor con alternativa
String value = optional.orElse("Valor por defecto");

// Obtener valor con función que proporciona alternativa
String value = optional.orElseGet(() -> "Valor calculado");

// Lanzar excepción personalizada si está vacío
String value = optional.orElseThrow(() -> new IllegalStateException("Valor requerido"));
```

### Transformaciones
```java
Optional<String> optional = Optional.of("hello");

// Transformar valor si está presente
Optional<String> upperCase = optional.map(String::toUpperCase);

// Transformación que retorna Optional
Optional<Integer> length = optional.flatMap(s -> Optional.of(s.length()));

// Filtrar basado en condición
Optional<String> filtered = optional.filter(s -> s.length() > 3);
```

### Ejecución condicional
```java
Optional<String> optional = Optional.of("Hello");

// Ejecutar acción si está presente
optional.ifPresent(System.out::println);

// Ejecutar acción si está presente, otra si está vacío (Java 9+)
optional.ifPresentOrElse(
    System.out::println,
    () -> System.out.println("Valor ausente")
);
```

## Objects.requireNonNull()

`Objects.requireNonNull()` es un método utilitario introducido en Java 7 que verifica que un objeto no sea null y lanza `NullPointerException` si lo es.

### Sintaxis básica
```java
import java.util.Objects;

public void metodo(String parametro) {
    // Verificación con mensaje por defecto
    this.campo = Objects.requireNonNull(parametro);
    
    // Verificación con mensaje personalizado
    this.campo = Objects.requireNonNull(parametro, "El parámetro no puede ser null");
    
    // Verificación con mensaje generado por función (Java 8+)
    this.campo = Objects.requireNonNull(parametro, () -> "Error: " + getContextInfo());
}
```

## Comparación detallada

### Propósito y filosofía

**Optional<T>:**
- Representa explícitamente la ausencia opcional de un valor
- Promueve programación funcional y encadenamiento seguro
- Hace que la posibilidad de ausencia sea parte del tipo

**Objects.requireNonNull():**
- Validación inmediata de precondiciones
- Falla rápido cuando se detecta un estado inválido
- Enfoque imperativo tradicional

### Momento de uso

**Optional<T>:**
```java
// Búsqueda que puede no encontrar resultado
public Optional<User> findUserById(Long id) {
    User user = repository.findById(id);
    return Optional.ofNullable(user);
}

// Procesamiento encadenado seguro
public String processUser(Long id) {
    return findUserById(id)
        .map(User::getName)
        .map(String::toUpperCase)
        .orElse("USUARIO NO ENCONTRADO");
}
```

**Objects.requireNonNull():**
```java
// Validación de parámetros obligatorios
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository no puede ser null");
    }
    
    public void saveUser(User user) {
        Objects.requireNonNull(user, "User no puede ser null");
        Objects.requireNonNull(user.getName(), "El nombre del usuario es obligatorio");
        repository.save(user);
    }
}
```

### Rendimiento

**Optional<T>:**
- Crea objetos adicionales en heap
- Overhead de encapsulación
- Más costoso en operaciones simples

**Objects.requireNonNull():**
- Verificación directa sin objetos adicionales
- Rendimiento superior
- Overhead mínimo

### Casos de uso recomendados

## Cuándo usar Optional<T>

### 1. Valores de retorno que pueden estar ausentes
```java
public Optional<String> getConfigValue(String key) {
    String value = properties.getProperty(key);
    return Optional.ofNullable(value);
}
```

### 2. Campos opcionales en clases
```java
public class Person {
    private String name;
    private Optional<String> middleName;
    private Optional<LocalDate> birthDate;
    
    // Constructor y métodos
}
```

### 3. Operaciones encadenadas complejas
```java
public Optional<String> getFormattedAddress(Long userId) {
    return findUserById(userId)
        .flatMap(User::getAddress)
        .map(Address::format)
        .filter(addr -> !addr.isEmpty());
}
```

### 4. APIs donde la ausencia es parte del diseño
```java
public class CacheService<K, V> {
    public Optional<V> get(K key) {
        V value = cache.get(key);
        return Optional.ofNullable(value);
    }
}
```

## Cuándo usar Objects.requireNonNull()

### 1. Validación de parámetros obligatorios
```java
public void processOrder(Customer customer, List<Item> items) {
    Objects.requireNonNull(customer, "Customer es obligatorio");
    Objects.requireNonNull(items, "Items no puede ser null");
    
    // Procesamiento
}
```

### 2. Inicialización de campos obligatorios
```java
public class OrderService {
    private final PaymentProcessor processor;
    private final NotificationService notificationService;
    
    public OrderService(PaymentProcessor processor, NotificationService notificationService) {
        this.processor = Objects.requireNonNull(processor);
        this.notificationService = Objects.requireNonNull(notificationService);
    }
}
```

### 3. Validación en setters
```java
public void setEmail(String email) {
    this.email = Objects.requireNonNull(email, "Email no puede ser null");
}
```

### 4. Verificación de estados internos
```java
public void processPayment() {
    Objects.requireNonNull(this.currentUser, "Usuario debe estar autenticado");
    Objects.requireNonNull(this.cart, "Carrito no puede estar vacío");
    
    // Procesamiento
}
```

## Antipatrones y errores comunes

### Con Optional<T>

**❌ No hagas esto:**
```java
// Usar Optional como parámetro
public void processUser(Optional<User> user) { ... }

// Usar Optional.get() sin verificar
Optional<String> opt = getValue();
String value = opt.get(); // Puede lanzar excepción

// Usar Optional para colecciones
Optional<List<String>> items = Optional.of(new ArrayList<>());
```

**✅ Haz esto:**
```java
// Pasar valor directo o null
public void processUser(User user) {
    if (user != null) {
        // procesamiento
    }
}

// Siempre verificar o usar métodos seguros
Optional<String> opt = getValue();
String value = opt.orElse("default");

// Usar colección vacía en lugar de Optional
List<String> items = getItems(); // Retorna lista vacía si no hay items
```

### Con Objects.requireNonNull()

**❌ No hagas esto:**
```java
// Usar para validación de lógica de negocio
public void withdraw(double amount) {
    Objects.requireNonNull(amount); // amount es primitivo, no puede ser null
    // Debería validar que amount > 0
}

// Sobreuso en validaciones complejas
public void complexValidation(User user) {
    Objects.requireNonNull(user);
    Objects.requireNonNull(user.getName());
    Objects.requireNonNull(user.getEmail());
    // Mejor usar un validation framework
}
```

**✅ Haz esto:**
```java
// Validar referencias, no primitivos
public void withdraw(Double amount) {
    Objects.requireNonNull(amount, "Amount no puede ser null");
    if (amount <= 0) {
        throw new IllegalArgumentException("Amount debe ser positivo");
    }
}
```

## Conclusión

**Optional<T>** es ideal para expresar la ausencia opcional de valores en APIs, especialmente en valores de retorno y operaciones encadenadas. Mejora la legibilidad del código y reduce los NullPointerExceptions cuando se usa correctamente.

**Objects.requireNonNull()** es perfecto para validaciones de precondiciones y fail-fast scenarios. Es más eficiente y directo para validar que los parámetros obligatorios no sean null.

La elección entre ambos depende del contexto: usa Optional para valores que naturalmente pueden estar ausentes, y requireNonNull para validar que los valores obligatorios estén presentes.