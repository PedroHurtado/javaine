# Default Methods y Static Methods en Interfaces Java

## Introducción

Antes de Java 8, las interfaces solo podían contener métodos abstractos y constantes. Con la llegada de Java 8 en marzo de 2014, se introdujeron dos nuevos tipos de métodos en las interfaces: **default methods** y **static methods**. Estas características revolucionaron el diseño de interfaces y proporcionaron mayor flexibilidad en la programación Java.

## Default Methods

### ¿Qué son los Default Methods?

Los default methods son métodos con implementación que se pueden definir en una interfaz usando la palabra clave `default`. Permiten agregar nueva funcionalidad a interfaces existentes sin romper la compatibilidad con las implementaciones existentes.

### Sintaxis

```java
public interface MiInterfaz {
    // Método abstracto tradicional
    void metodoAbstracto();
    
    // Default method
    default void metodoDefault() {
        System.out.println("Implementación por defecto");
    }
}
```

### Características Principales

- **Compatibilidad hacia atrás**: Las clases que implementan la interfaz no están obligadas a implementar los default methods
- **Herencia**: Los default methods se heredan como métodos normales
- **Sobrescritura**: Las clases implementadoras pueden sobrescribir los default methods
- **Acceso**: Solo pueden acceder a métodos de la interfaz, no a campos de instancia

### Ejemplo Práctico

```java
public interface Vehiculo {
    void arrancar();
    void detener();
    
    // Default method introducido en una versión posterior
    default void mostrarInfo() {
        System.out.println("Este es un vehículo genérico");
    }
    
    default void encenderLuces() {
        System.out.println("Luces encendidas");
    }
}

public class Coche implements Vehiculo {
    @Override
    public void arrancar() {
        System.out.println("El coche está arrancando");
    }
    
    @Override
    public void detener() {
        System.out.println("El coche se ha detenido");
    }
    
    // Sobrescribimos el default method
    @Override
    public void mostrarInfo() {
        System.out.println("Este es un coche específico");
    }
    
    // No es necesario implementar encenderLuces(), usa la implementación por defecto
}
```

### Resolución de Conflictos

Cuando una clase implementa múltiples interfaces con default methods que tienen la misma firma, es necesario resolver el conflicto:

```java
interface A {
    default void metodo() {
        System.out.println("Método de A");
    }
}

interface B {
    default void metodo() {
        System.out.println("Método de B");
    }
}

class MiClase implements A, B {
    @Override
    public void metodo() {
        // Debe resolver el conflicto explícitamente
        A.super.metodo(); // Llamar al método de A
        // O elegir B.super.metodo();
        // O proporcionar una implementación completamente nueva
    }
}
```

## Static Methods

### ¿Qué son los Static Methods?

Los static methods en interfaces son métodos que pertenecen a la interfaz en sí, no a las instancias de las clases que la implementan. Se introdujeron también en Java 8 para proporcionar métodos utilitarios relacionados con la interfaz.

### Sintaxis

```java
public interface MiInterfaz {
    // Static method
    static void metodoEstatico() {
        System.out.println("Método estático de la interfaz");
    }
    
    static int calcular(int a, int b) {
        return a + b;
    }
}
```

### Características Principales

- **No se heredan**: Las clases que implementan la interfaz no heredan los static methods
- **Acceso directo**: Se acceden usando el nombre de la interfaz
- **No se pueden sobrescribir**: No pueden ser sobrescritos por las clases implementadoras
- **Encapsulación**: Pueden acceder a otros métodos estáticos de la misma interfaz

### Ejemplo Práctico

```java
public interface OperacionesMatematicas {
    // Método abstracto
    double calcular(double a, double b);
    
    // Static methods utilitarios
    static double sumar(double a, double b) {
        return a + b;
    }
    
    static double restar(double a, double b) {
        return a - b;
    }
    
    static double multiplicar(double a, double b) {
        return a * b;
    }
    
    static double dividir(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("No se puede dividir por cero");
        }
        return a / b;
    }
    
    // Default method que usa static methods
    default void mostrarOperacionesDisponibles() {
        System.out.println("Operaciones disponibles: suma, resta, multiplicación, división");
    }
}

public class Calculadora implements OperacionesMatematicas {
    @Override
    public double calcular(double a, double b) {
        return OperacionesMatematicas.sumar(a, b); // Usando static method
    }
}

// Uso
public class Main {
    public static void main(String[] args) {
        // Acceso directo a static methods
        double resultado = OperacionesMatematicas.sumar(5, 3);
        System.out.println("Resultado: " + resultado);
        
        Calculadora calc = new Calculadora();
        calc.mostrarOperacionesDisponibles(); // Default method
        
        // calc.sumar(1, 2); // ERROR: No se puede acceder así
    }
}
```

## Cuándo Usar Cada Uno

### Default Methods - Usar cuando:
- Necesitas agregar funcionalidad a interfaces existentes sin romper compatibilidad
- Quieres proporcionar una implementación común para múltiples clases
- Deseas crear métodos de conveniencia que la mayoría de implementadores usarán igual

### Static Methods - Usar cuando:
- Necesitas métodos utilitarios relacionados con la interfaz
- Quieres proporcionar funcionalidad que no depende de la instancia
- Deseas crear métodos helper que soporten la funcionalidad de la interfaz

## Ventajas y Consideraciones

### Ventajas

**Default Methods:**
- Evolución de APIs sin romper compatibilidad
- Reducción de código duplicado
- Mejor organización del código

**Static Methods:**
- Encapsulación de utilidades relacionadas
- Mejor organización que las clases utilitarias separadas
- Acceso directo sin necesidad de instancias

### Consideraciones

**Default Methods:**
- Pueden crear jerarquías complejas de herencia
- Posibles conflictos con herencia múltiple
- Pueden ocultar la verdadera naturaleza abstracta de las interfaces

**Static Methods:**
- No son heredados, pueden crear confusión
- Limitados en funcionalidad comparado con métodos de instancia
- Pueden hacer las interfaces más pesadas conceptualmente

## Ejemplos Reales en Java API

### Collection Interface (Default Methods)
```java
// Agregado en Java 8
default void forEach(Consumer<? super T> action) {
    Objects.requireNonNull(action);
    for (T t : this) {
        action.accept(t);
    }
}

default Spliterator<T> spliterator() {
    return Spliterators.spliteratorUnknownSize(iterator(), 0);
}
```

### Comparator Interface (Static Methods)
```java
static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
    return (Comparator<T>) Comparators.NaturalOrderComparator.INSTANCE;
}

static <T> Comparator<T> reverseOrder() {
    return Collections.reverseOrder();
}
```

## Conclusión

Los default methods y static methods transformaron las interfaces Java de simples contratos a estructuras más ricas y funcionales. Introdujeron en Java 8, estas características permiten:

- **Evolución de APIs**: Agregar funcionalidad sin romper código existente
- **Mejor organización**: Agrupar funcionalidad relacionada
- **Flexibilidad**: Combinar abstracción con implementación concreta
- **Reutilización**: Compartir código común entre implementaciones

Su uso adecuado mejora significativamente el diseño de software Java, proporcionando herramientas poderosas para crear APIs más robustas y mantenibles.