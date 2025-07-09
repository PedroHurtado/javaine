# Guía de Collectors en Java

## Introducción

Los **Collectors** en Java son una parte fundamental de la API de Streams introducida en Java 8. Proporcionan una forma elegante y funcional de realizar operaciones de reducción sobre streams, como agrupar, sumar, promediar, y muchas otras operaciones comunes de procesamiento de datos.

## ¿Qué es un Collector?

Un `Collector` es una interfaz que define cómo reducir los elementos de un stream a un solo resultado. Los collectors se utilizan principalmente con el método `collect()` de los streams.

```java
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.Stream;

// Ejemplo básico
List<String> nombres = Stream.of("Ana", "Juan", "María")
    .collect(Collectors.toList());
```

## Collectors Básicos

### 1. Colecciones

#### toList()
Convierte un stream en una Lista.

```java
List<String> lista = stream.collect(Collectors.toList());
```

#### toSet()
Convierte un stream en un Set (elimina duplicados).

```java
Set<String> conjunto = stream.collect(Collectors.toSet());
```

#### toMap()
Convierte un stream en un Map.

```java
Map<String, Integer> mapa = personas.stream()
    .collect(Collectors.toMap(
        Persona::getNombre,
        Persona::getEdad
    ));
```

#### toCollection()
Permite especificar el tipo exacto de colección.

```java
LinkedList<String> linkedList = stream
    .collect(Collectors.toCollection(LinkedList::new));
```

### 2. Operaciones Matemáticas

#### counting()
Cuenta el número de elementos.

```java
long cantidad = stream.collect(Collectors.counting());
```

#### summingInt/Long/Double()
Suma valores numéricos.

```java
int sumaEdades = personas.stream()
    .collect(Collectors.summingInt(Persona::getEdad));
```

#### averagingInt/Long/Double()
Calcula el promedio.

```java
double promedioEdad = personas.stream()
    .collect(Collectors.averagingInt(Persona::getEdad));
```

#### maxBy() y minBy()
Encuentra el elemento máximo o mínimo.

```java
Optional<Persona> mayorEdad = personas.stream()
    .collect(Collectors.maxBy(Comparator.comparing(Persona::getEdad)));
```

### 3. Operaciones de Cadenas

#### joining()
Une elementos en una cadena.

```java
String nombres = personas.stream()
    .map(Persona::getNombre)
    .collect(Collectors.joining(", "));

// Con prefijo y sufijo
String nombresConFormato = personas.stream()
    .map(Persona::getNombre)
    .collect(Collectors.joining(", ", "[", "]"));
```

## Collectors Avanzados

### 1. Agrupación

#### groupingBy()
Agrupa elementos por una clave específica.

```java
// Agrupar por edad
Map<Integer, List<Persona>> porEdad = personas.stream()
    .collect(Collectors.groupingBy(Persona::getEdad));

// Agrupar por rango de edad
Map<String, List<Persona>> porRangoEdad = personas.stream()
    .collect(Collectors.groupingBy(persona -> {
        if (persona.getEdad() < 18) return "Menor";
        else if (persona.getEdad() < 65) return "Adulto";
        else return "Senior";
    }));
```

#### groupingBy() con Downstream Collector
Permite aplicar otro collector a cada grupo.

```java
// Contar personas por edad
Map<Integer, Long> contarPorEdad = personas.stream()
    .collect(Collectors.groupingBy(
        Persona::getEdad,
        Collectors.counting()
    ));

// Promediar salario por departamento
Map<String, Double> promedioSalario = empleados.stream()
    .collect(Collectors.groupingBy(
        Empleado::getDepartamento,
        Collectors.averagingDouble(Empleado::getSalario)
    ));
```

### 2. Particionado

#### partitioningBy()
Divide elementos en dos grupos basado en un predicado.

```java
// Separar mayores y menores de edad
Map<Boolean, List<Persona>> mayoresMenores = personas.stream()
    .collect(Collectors.partitioningBy(p -> p.getEdad() >= 18));

List<Persona> mayores = mayoresMenores.get(true);
List<Persona> menores = mayoresMenores.get(false);
```

### 3. Collectors de Resumen

#### summarizingInt/Long/Double()
Proporciona estadísticas completas.

```java
IntSummaryStatistics estadisticas = personas.stream()
    .collect(Collectors.summarizingInt(Persona::getEdad));

System.out.println("Promedio: " + estadisticas.getAverage());
System.out.println("Máximo: " + estadisticas.getMax());
System.out.println("Mínimo: " + estadisticas.getMin());
System.out.println("Total: " + estadisticas.getSum());
System.out.println("Cantidad: " + estadisticas.getCount());
```

## Collectors Personalizados

### Usando Collector.of()

```java
// Collector personalizado que calcula la mediana
Collector<Integer, ?, Double> mediana = Collector.of(
    ArrayList::new,                    // supplier
    List::add,                         // accumulator
    (list1, list2) -> {               // combiner
        list1.addAll(list2);
        return list1;
    },
    list -> {                         // finisher
        Collections.sort(list);
        int size = list.size();
        if (size % 2 == 0) {
            return (list.get(size/2 - 1) + list.get(size/2)) / 2.0;
        } else {
            return list.get(size/2).doubleValue();
        }
    }
);

// Uso del collector personalizado
double mediana = numeros.stream()
    .collect(mediana);
```

## Combinando Collectors

### mapping()
Aplica una transformación antes de recopilar.

```java
Map<String, List<String>> nombresPorDepartamento = empleados.stream()
    .collect(Collectors.groupingBy(
        Empleado::getDepartamento,
        Collectors.mapping(
            Empleado::getNombre,
            Collectors.toList()
        )
    ));
```

### filtering()
Filtra elementos durante la recopilación.

```java
Map<String, List<Empleado>> mayoresPorDepartamento = empleados.stream()
    .collect(Collectors.groupingBy(
        Empleado::getDepartamento,
        Collectors.filtering(
            emp -> emp.getEdad() > 30,
            Collectors.toList()
        )
    ));
```

### collectingAndThen()
Aplica una transformación final al resultado.

```java
List<String> nombresInmutable = personas.stream()
    .map(Persona::getNombre)
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        Collections::unmodifiableList
    ));
```

## Ejemplos Prácticos

### Ejemplo 1: Análisis de Ventas

```java
class Venta {
    private String producto;
    private double monto;
    private String region;
    // constructores, getters, setters
}

// Ventas totales por región
Map<String, Double> ventasPorRegion = ventas.stream()
    .collect(Collectors.groupingBy(
        Venta::getRegion,
        Collectors.summingDouble(Venta::getMonto)
    ));

// Top 5 productos más vendidos
List<Map.Entry<String, Double>> topProductos = ventas.stream()
    .collect(Collectors.groupingBy(
        Venta::getProducto,
        Collectors.summingDouble(Venta::getMonto)
    ))
    .entrySet()
    .stream()
    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
    .limit(5)
    .collect(Collectors.toList());
```

### Ejemplo 2: Procesamiento de Estudiantes

```java
class Estudiante {
    private String nombre;
    private int edad;
    private double calificacion;
    private String carrera;
    // constructores, getters, setters
}

// Estudiantes agrupados por carrera con estadísticas
Map<String, IntSummaryStatistics> estadisticasPorCarrera = estudiantes.stream()
    .collect(Collectors.groupingBy(
        Estudiante::getCarrera,
        Collectors.summarizingInt(Estudiante::getEdad)
    ));

// Mejores estudiantes por carrera
Map<String, Optional<Estudiante>> mejoresPorCarrera = estudiantes.stream()
    .collect(Collectors.groupingBy(
        Estudiante::getCarrera,
        Collectors.maxBy(Comparator.comparing(Estudiante::getCalificacion))
    ));
```

## Mejores Prácticas

1. **Usa collectors predefinidos** cuando sea posible en lugar de crear collectors personalizados.

2. **Combina collectors** para operaciones complejas en lugar de usar múltiples streams.

3. **Considera el rendimiento** al elegir entre `toList()` y `toCollection()`.

4. **Usa parallel streams** con collectors que soporten operaciones concurrentes.

5. **Evita efectos secundarios** en las funciones pasadas a los collectors.

## Conclusión

Los Collectors en Java proporcionan una API poderosa y expresiva para procesar streams de datos. Desde operaciones básicas como convertir a listas hasta análisis complejos con agrupación y estadísticas, los collectors permiten escribir código más limpio y funcional.

La clave está en comprender los diferentes tipos de collectors disponibles y cómo combinarlos para resolver problemas específicos de procesamiento de datos de manera elegante y eficiente.

---

