# Guía Completa de Streams en Java

## Índice
1. [Introducción a los Streams](#introducción-a-los-streams)
2. [Métodos Terminadores](#métodos-terminadores)
3. [Finalización de Streams](#finalización-de-streams)
4. [Reutilización de Streams](#reutilización-de-streams)
5. [Streams Paralelos](#streams-paralelos)
6. [Streams Personalizados](#streams-personalizados)
7. [Mejores Prácticas](#mejores-prácticas)
8. [Ejemplos Prácticos](#ejemplos-prácticos)

## Introducción a los Streams

Los Streams en Java son una abstracción que permite procesar secuencias de elementos de forma declarativa. Introducidos en Java 8, proporcionan una API funcional para realizar operaciones complejas sobre colecciones de datos.

### Características Fundamentales

- **Inmutabilidad**: Los streams no modifican la fuente de datos original
- **Lazy Evaluation**: Las operaciones intermedias se ejecutan solo cuando se invoca una operación terminal
- **Funcional**: Basado en programación funcional con lambdas y method references
- **Composición**: Permite encadenar operaciones de forma fluida

### Tipos de Operaciones

```java
// Operaciones Intermedias (lazy)
Stream<T> filter(Predicate<T> predicate)
Stream<T> map(Function<T, R> mapper)
Stream<T> sorted()
Stream<T> distinct()
Stream<T> limit(long maxSize)
Stream<T> skip(long n)

// Operaciones Terminales (eager)
void forEach(Consumer<T> action)
Optional<T> reduce(BinaryOperator<T> accumulator)
<R> R collect(Collector<T, A, R> collector)
long count()
boolean anyMatch(Predicate<T> predicate)
```

## Métodos Terminadores

Los métodos terminadores son operaciones que finalizan el pipeline del stream y producen un resultado final. Una vez ejecutados, el stream se considera "consumido" y no puede ser reutilizado.

### forEach y forEachOrdered

```java
// forEach - no garantiza orden en streams paralelos
List<String> nombres = Arrays.asList("Ana", "Carlos", "Beatriz");
nombres.stream().forEach(System.out::println);

// forEachOrdered - mantiene el orden incluso en streams paralelos
nombres.parallelStream().forEachOrdered(System.out::println);
```

### collect

El método `collect` es uno de los más potentes y versátiles:

```java
List<String> nombres = Arrays.asList("Ana", "Carlos", "Beatriz", "David");

// Recolectar en diferentes estructuras
List<String> lista = nombres.stream()
    .filter(n -> n.length() > 3)
    .collect(Collectors.toList());

Set<String> conjunto = nombres.stream()
    .collect(Collectors.toSet());

Map<Integer, List<String>> agrupados = nombres.stream()
    .collect(Collectors.groupingBy(String::length));

// Collectors personalizados
String concatenado = nombres.stream()
    .collect(Collectors.joining(", ", "[", "]"));
```

### reduce

Reduce combina elementos del stream en un solo valor:

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);

// Suma con reduce
Optional<Integer> suma = numeros.stream()
    .reduce((a, b) -> a + b);

// Reduce con valor inicial
Integer sumaTotal = numeros.stream()
    .reduce(0, (a, b) -> a + b);

// Reduce con combiner (útil para streams paralelos)
Integer sumaParalela = numeros.parallelStream()
    .reduce(0, 
           (a, b) -> a + b,          // accumulator
           (a, b) -> a + b);         // combiner
```

### Operaciones de Búsqueda

```java
List<String> palabras = Arrays.asList("casa", "carro", "computadora", "café");

// findFirst - encuentra el primer elemento
Optional<String> primera = palabras.stream()
    .filter(p -> p.startsWith("c"))
    .findFirst();

// findAny - encuentra cualquier elemento (útil en paralelo)
Optional<String> cualquiera = palabras.parallelStream()
    .filter(p -> p.startsWith("c"))
    .findAny();

// anyMatch, allMatch, noneMatch
boolean tieneCarros = palabras.stream()
    .anyMatch(p -> p.equals("carro"));

boolean todasEmpiezanConC = palabras.stream()
    .allMatch(p -> p.startsWith("c"));

boolean ningunaEmpiezaConZ = palabras.stream()
    .noneMatch(p -> p.startsWith("z"));
```

### Operaciones de Agregación

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// count
long cantidad = numeros.stream()
    .filter(n -> n % 2 == 0)
    .count();

// min y max
Optional<Integer> minimo = numeros.stream()
    .min(Integer::compareTo);

Optional<Integer> maximo = numeros.stream()
    .max(Integer::compareTo);

// Estadísticas con números
IntSummaryStatistics stats = numeros.stream()
    .mapToInt(Integer::intValue)
    .summaryStatistics();

System.out.println("Promedio: " + stats.getAverage());
System.out.println("Suma: " + stats.getSum());
System.out.println("Min: " + stats.getMin());
System.out.println("Max: " + stats.getMax());
```

## Finalización de Streams

### Ciclo de Vida de un Stream

Un stream pasa por tres fases:
1. **Creación**: Se crea el stream desde una fuente de datos
2. **Procesamiento**: Se aplican operaciones intermedias (lazy)
3. **Terminación**: Se ejecuta una operación terminal que consume el stream

```java
List<String> nombres = Arrays.asList("Ana", "Carlos", "Beatriz");

// Fase 1: Creación
Stream<String> stream = nombres.stream();

// Fase 2: Procesamiento (lazy - no se ejecuta aún)
Stream<String> procesado = stream
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase);

// Fase 3: Terminación (eager - se ejecuta todo el pipeline)
List<String> resultado = procesado.collect(Collectors.toList());
```

### Estados del Stream

```java
// Stream activo
Stream<Integer> numeros = Stream.of(1, 2, 3, 4, 5);

// Stream consumido después de operación terminal
long cuenta = numeros.count();

// Intentar usar el stream nuevamente lanza IllegalStateException
try {
    numeros.forEach(System.out::println); // ¡Error!
} catch (IllegalStateException e) {
    System.out.println("Stream ya fue consumido");
}
```

### Manejo de Resources con Streams

```java
// Streams que implementan AutoCloseable
try (Stream<String> lineas = Files.lines(Paths.get("archivo.txt"))) {
    lineas.filter(linea -> !linea.isEmpty())
          .forEach(System.out::println);
} catch (IOException e) {
    e.printStackTrace();
}
```

## Reutilización de Streams

### Problema de Reutilización

Los streams no pueden ser reutilizados una vez que se ha ejecutado una operación terminal:

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numeros.stream();

// Primera operación terminal
long cuenta = stream.count();

// Segunda operación terminal - ¡Error!
// stream.forEach(System.out::println); // IllegalStateException
```

### Soluciones para Reutilización

#### 1. Recrear el Stream

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);

// Crear función para generar streams
Supplier<Stream<Integer>> streamSupplier = () -> numeros.stream();

// Usar múltiples veces
long cuenta = streamSupplier.get().count();
int suma = streamSupplier.get().mapToInt(Integer::intValue).sum();
```

#### 2. Usar Supplier de Streams

```java
public class StreamReutilizable<T> {
    private final Supplier<Stream<T>> streamSupplier;
    
    public StreamReutilizable(Supplier<Stream<T>> streamSupplier) {
        this.streamSupplier = streamSupplier;
    }
    
    public Stream<T> stream() {
        return streamSupplier.get();
    }
}

// Uso
List<String> datos = Arrays.asList("a", "b", "c");
StreamReutilizable<String> reutilizable = 
    new StreamReutilizable<>(() -> datos.stream());

// Múltiples operaciones
long cuenta = reutilizable.stream().count();
List<String> mayusculas = reutilizable.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

#### 3. Patrón Builder para Streams

```java
public class StreamBuilder<T> {
    private final List<T> data;
    private final List<Function<Stream<T>, Stream<T>>> operations;
    
    public StreamBuilder(List<T> data) {
        this.data = data;
        this.operations = new ArrayList<>();
    }
    
    public StreamBuilder<T> filter(Predicate<T> predicate) {
        operations.add(stream -> stream.filter(predicate));
        return this;
    }
    
    public <R> StreamBuilder<R> map(Function<T, R> mapper) {
        // Implementación para transformar tipos
        return new StreamBuilder<>(Collections.emptyList());
    }
    
    public Stream<T> build() {
        Stream<T> stream = data.stream();
        for (Function<Stream<T>, Stream<T>> op : operations) {
            stream = op.apply(stream);
        }
        return stream;
    }
}
```

## Streams Paralelos

### Creación de Streams Paralelos

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Método 1: parallelStream()
Stream<Integer> paralelo1 = numeros.parallelStream();

// Método 2: parallel() en stream secuencial
Stream<Integer> paralelo2 = numeros.stream().parallel();

// Verificar si es paralelo
boolean esParalelo = numeros.parallelStream().isParallel();
```

### Cuándo Usar Streams Paralelos

```java
// Bueno para streams paralelos: operaciones CPU-intensivas
List<Integer> numeros = IntStream.range(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

// Operación costosa que se beneficia del paralelismo
List<Double> raices = numeros.parallelStream()
    .map(n -> Math.sqrt(n * n * n))
    .collect(Collectors.toList());
```

### Configuración del Pool de Threads

```java
// Configurar el tamaño del ForkJoinPool
System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");

// Usar ForkJoinPool personalizado
ForkJoinPool customThreadPool = new ForkJoinPool(4);
try {
    List<Integer> resultado = customThreadPool.submit(() ->
        numeros.parallelStream()
               .map(this::operacionCostosa)
               .collect(Collectors.toList())
    ).get();
} finally {
    customThreadPool.shutdown();
}
```

### Consideraciones de Rendimiento

```java
// Medición de rendimiento
public class StreamPerformanceTest {
    private static final int SIZE = 1_000_000;
    
    public static void main(String[] args) {
        List<Integer> data = IntStream.range(0, SIZE)
            .boxed()
            .collect(Collectors.toList());
        
        // Secuencial
        long startTime = System.nanoTime();
        long sumSeq = data.stream()
            .mapToLong(StreamPerformanceTest::operacionCostosa)
            .sum();
        long seqTime = System.nanoTime() - startTime;
        
        // Paralelo
        startTime = System.nanoTime();
        long sumPar = data.parallelStream()
            .mapToLong(StreamPerformanceTest::operacionCostosa)
            .sum();
        long parTime = System.nanoTime() - startTime;
        
        System.out.println("Secuencial: " + seqTime / 1_000_000 + " ms");
        System.out.println("Paralelo: " + parTime / 1_000_000 + " ms");
    }
    
    private static long operacionCostosa(int n) {
        return IntStream.range(0, n % 1000)
            .map(i -> i * i)
            .sum();
    }
}
```

### Pitfalls de Streams Paralelos

```java
// MAL: Operaciones con estado compartido
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> resultado = new ArrayList<>(); // ¡No thread-safe!

// Esto puede causar problemas de concurrencia
numeros.parallelStream()
       .forEach(resultado::add); // ¡Peligroso!

// BIEN: Usar collectors thread-safe
List<Integer> resultadoSeguro = numeros.parallelStream()
    .collect(Collectors.toList());
```

## Streams Personalizados

### Implementación Básica de Stream

```java
public class CustomStream<T> {
    private final List<T> data;
    private final List<Function<Stream<T>, Stream<T>>> operations;
    
    public CustomStream(List<T> data) {
        this.data = new ArrayList<>(data);
        this.operations = new ArrayList<>();
    }
    
    public CustomStream<T> filter(Predicate<T> predicate) {
        operations.add(stream -> stream.filter(predicate));
        return this;
    }
    
    public <R> CustomStream<R> map(Function<T, R> mapper) {
        return new CustomStream<>(
            this.toList().stream()
                .map(mapper)
                .collect(Collectors.toList())
        );
    }
    
    public CustomStream<T> peek(Consumer<T> action) {
        operations.add(stream -> stream.peek(action));
        return this;
    }
    
    public List<T> toList() {
        Stream<T> stream = data.stream();
        for (Function<Stream<T>, Stream<T>> operation : operations) {
            stream = operation.apply(stream);
        }
        return stream.collect(Collectors.toList());
    }
    
    public Optional<T> findFirst() {
        return toStream().findFirst();
    }
    
    private Stream<T> toStream() {
        Stream<T> stream = data.stream();
        for (Function<Stream<T>, Stream<T>> operation : operations) {
            stream = operation.apply(stream);
        }
        return stream;
    }
}
```

### Stream con Caché

```java
public class CachedStream<T> {
    private final Supplier<Stream<T>> streamSupplier;
    private List<T> cache;
    private boolean cached = false;
    
    public CachedStream(Supplier<Stream<T>> streamSupplier) {
        this.streamSupplier = streamSupplier;
    }
    
    public synchronized Stream<T> stream() {
        if (!cached) {
            cache = streamSupplier.get().collect(Collectors.toList());
            cached = true;
        }
        return cache.stream();
    }
    
    public void invalidateCache() {
        cached = false;
        cache = null;
    }
}
```

### Stream con Logging

```java
public class LoggingStream<T> {
    private final Stream<T> stream;
    private final String name;
    
    public LoggingStream(Stream<T> stream, String name) {
        this.stream = stream;
        this.name = name;
    }
    
    public LoggingStream<T> filter(Predicate<T> predicate) {
        return new LoggingStream<>(
            stream.filter(element -> {
                boolean result = predicate.test(element);
                System.out.println(name + " - Filter: " + element + " -> " + result);
                return result;
            }), name
        );
    }
    
    public <R> LoggingStream<R> map(Function<T, R> mapper) {
        return new LoggingStream<>(
            stream.map(element -> {
                R result = mapper.apply(element);
                System.out.println(name + " - Map: " + element + " -> " + result);
                return result;
            }), name
        );
    }
    
    public List<T> collect() {
        System.out.println(name + " - Collecting results");
        return stream.collect(Collectors.toList());
    }
}
```

### Stream Builder Avanzado

```java
public class AdvancedStreamBuilder<T> {
    private final List<T> data;
    private final List<StreamOperation<T>> operations;
    
    public AdvancedStreamBuilder(Collection<T> data) {
        this.data = new ArrayList<>(data);
        this.operations = new ArrayList<>();
    }
    
    public AdvancedStreamBuilder<T> filter(Predicate<T> predicate) {
        operations.add(new FilterOperation<>(predicate));
        return this;
    }
    
    public <R> AdvancedStreamBuilder<R> map(Function<T, R> mapper) {
        // Crear nuevo builder con tipo transformado
        List<R> mappedData = data.stream()
            .map(mapper)
            .collect(Collectors.toList());
        return new AdvancedStreamBuilder<>(mappedData);
    }
    
    public AdvancedStreamBuilder<T> sorted() {
        operations.add(new SortOperation<>());
        return this;
    }
    
    public AdvancedStreamBuilder<T> distinct() {
        operations.add(new DistinctOperation<>());
        return this;
    }
    
    public Stream<T> build() {
        Stream<T> stream = data.stream();
        for (StreamOperation<T> operation : operations) {
            stream = operation.apply(stream);
        }
        return stream;
    }
    
    // Interfaces para operaciones
    private interface StreamOperation<T> {
        Stream<T> apply(Stream<T> stream);
    }
    
    private static class FilterOperation<T> implements StreamOperation<T> {
        private final Predicate<T> predicate;
        
        FilterOperation(Predicate<T> predicate) {
            this.predicate = predicate;
        }
        
        @Override
        public Stream<T> apply(Stream<T> stream) {
            return stream.filter(predicate);
        }
    }
    
    private static class SortOperation<T> implements StreamOperation<T> {
        @Override
        public Stream<T> apply(Stream<T> stream) {
            return stream.sorted();
        }
    }
    
    private static class DistinctOperation<T> implements StreamOperation<T> {
        @Override
        public Stream<T> apply(Stream<T> stream) {
            return stream.distinct();
        }
    }
}
```

## Mejores Prácticas

### 1. Evitar Efectos Secundarios

```java
// MAL: Modificar estado externo
List<String> resultado = new ArrayList<>();
nombres.stream()
    .filter(n -> n.length() > 3)
    .forEach(resultado::add); // Efecto secundario

// BIEN: Usar collectors
List<String> resultado = nombres.stream()
    .filter(n -> n.length() > 3)
    .collect(Collectors.toList());
```

### 2. Preferir Method References

```java
// MAL: Lambda innecesario
nombres.stream()
    .map(n -> n.toUpperCase())
    .collect(Collectors.toList());

// BIEN: Method reference
nombres.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### 3. Usar Streams Especializados

```java
// MAL: Boxing innecesario
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);
int suma = numeros.stream()
    .reduce(0, (a, b) -> a + b);

// BIEN: Stream especializado
int suma = numeros.stream()
    .mapToInt(Integer::intValue)
    .sum();
```

### 4. Manejo de Optionals

```java
// MAL: Usar get() directamente
String resultado = nombres.stream()
    .filter(n -> n.startsWith("A"))
    .findFirst()
    .get(); // Puede lanzar NoSuchElementException

// BIEN: Manejo seguro de Optional
String resultado = nombres.stream()
    .filter(n -> n.startsWith("A"))
    .findFirst()
    .orElse("No encontrado");
```

## Ejemplos Prácticos

### Ejemplo 1: Procesamiento de Datos de Empleados

```java
public class EmpleadoStreamExample {
    static class Empleado {
        private String nombre;
        private String departamento;
        private double salario;
        private int edad;
        
        // Constructor, getters y setters
        public Empleado(String nombre, String departamento, double salario, int edad) {
            this.nombre = nombre;
            this.departamento = departamento;
            this.salario = salario;
            this.edad = edad;
        }
        
        // Getters
        public String getNombre() { return nombre; }
        public String getDepartamento() { return departamento; }
        public double getSalario() { return salario; }
        public int getEdad() { return edad; }
    }
    
    public static void main(String[] args) {
        List<Empleado> empleados = Arrays.asList(
            new Empleado("Ana", "IT", 75000, 30),
            new Empleado("Carlos", "HR", 65000, 35),
            new Empleado("Beatriz", "IT", 85000, 28),
            new Empleado("David", "Finance", 70000, 32)
        );
        
        // Salario promedio por departamento
        Map<String, Double> salarioPromedio = empleados.stream()
            .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.averagingDouble(Empleado::getSalario)
            ));
        
        // Empleados de IT con salario > 80000
        List<String> empleadosIT = empleados.stream()
            .filter(e -> e.getDepartamento().equals("IT"))
            .filter(e -> e.getSalario() > 80000)
            .map(Empleado::getNombre)
            .collect(Collectors.toList());
        
        // Empleado con mayor salario
        Optional<Empleado> empleadoMayorSalario = empleados.stream()
            .max(Comparator.comparing(Empleado::getSalario));
        
        System.out.println("Salario promedio por departamento: " + salarioPromedio);
        System.out.println("Empleados IT con salario > 80000: " + empleadosIT);
        empleadoMayorSalario.ifPresent(e -> 
            System.out.println("Empleado con mayor salario: " + e.getNombre()));
    }
}
```

### Ejemplo 2: Análisis de Texto

```java
public class TextAnalysisExample {
    public static void main(String[] args) {
        String texto = "Java streams son una herramienta poderosa para " +
                      "procesar datos de forma declarativa y funcional";
        
        // Dividir en palabras y analizar
        List<String> palabras = Arrays.asList(texto.toLowerCase().split("\\s+"));
        
        // Palabras más largas que 4 caracteres
        List<String> palabrasLargas = palabras.stream()
            .filter(p -> p.length() > 4)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        // Contar frecuencia de palabras
        Map<String, Long> frecuencia = palabras.stream()
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));
        
        // Palabra más común
        Optional<Map.Entry<String, Long>> palabraMasComun = frecuencia.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());
        
        System.out.println("Palabras largas: " + palabrasLargas);
        System.out.println("Frecuencia: " + frecuencia);
        palabraMasComun.ifPresent(e -> 
            System.out.println("Palabra más común: " + e.getKey() + " (" + e.getValue() + ")"));
    }
}
```

### Ejemplo 3: Stream Personalizado con Validación

```java
public class ValidatingStream<T> {
    private final Stream<T> stream;
    private final List<Predicate<T>> validators;
    
    public ValidatingStream(Stream<T> stream) {
        this.stream = stream;
        this.validators = new ArrayList<>();
    }
    
    public ValidatingStream<T> addValidator(Predicate<T> validator) {
        validators.add(validator);
        return this;
    }
    
    public ValidatingStream<T> filter(Predicate<T> predicate) {
        return new ValidatingStream<>(stream.filter(predicate));
    }
    
    public <R> ValidatingStream<R> map(Function<T, R> mapper) {
        return new ValidatingStream<>(stream.map(mapper));
    }
    
    public List<T> collectValid() {
        Predicate<T> combinedValidator = validators.stream()
            .reduce(Predicate::and)
            .orElse(t -> true);
        
        return stream.filter(combinedValidator)
                    .collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
        List<Integer> numeros = Arrays.asList(-5, 0, 3, 8, 12, 15, 20);
        
        List<Integer> validos = new ValidatingStream<>(numeros.stream())
            .addValidator(n -> n > 0)          // Positivos
            .addValidator(n -> n < 100)        // Menores que 100
            .addValidator(n -> n % 2 == 0)     // Pares
            .collectValid();
        
        System.out.println("Números válidos: " + validos);
    }
}
```

---

## Conclusión

Los Streams de Java proporcionan una API poderosa y expresiva para el procesamiento de datos. Comprender sus métodos terminadores, ciclo de vida, limitaciones de reutilización y capacidades de paralelización es esencial para escribir código eficiente y mantenible.

### Puntos Clave

- Los streams se consumen con operaciones terminales y no pueden reutilizarse
- Los streams paralelos pueden mejorar el rendimiento en operaciones CPU-intensivas
- Los streams personalizados permiten crear abstracciones específicas del dominio
- Siempre considerar las mejores prácticas para escribir código limpio y eficiente

### Recursos Adicionales

- [Documentación oficial de Java Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
- [Java 8 Stream API Tutorial](https://www.oracle.com/technical-resources/articles/java/ma14-java-se-8-streams.html)
- [Effective Java by Joshua Bloch - Stream Guidelines](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)

---
