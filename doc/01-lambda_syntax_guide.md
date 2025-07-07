# Guía Completa: Sintaxis de Lambda y Referencias a Métodos en Java

## Tabla de Contenidos
1. [Sintaxis de Lambda](#sintaxis-de-lambda)
2. [Referencias a Métodos](#referencias-a-métodos)
3. [Ejemplos Prácticos](#ejemplos-prácticos)
4. [Mejores Prácticas](#mejores-prácticas)

---

## Sintaxis de Lambda

### Expresiones Lambda Básicas

Las expresiones lambda en Java proporcionan una forma concisa de representar interfaces funcionales (interfaces con un solo método abstracto).

#### Sintaxis General
```java
(parámetros) -> { cuerpo }
```

#### Formas Básicas

**1. Sin parámetros:**
```java
() -> System.out.println("Hola mundo")
```

**2. Un parámetro (paréntesis opcionales):**
```java
x -> x * 2
// o
(x) -> x * 2
```

**3. Múltiples parámetros:**
```java
(x, y) -> x + y
```

**4. Con tipo explícito:**
```java
(int x, int y) -> x + y
```

**5. Cuerpo con múltiples statements:**
```java
(x, y) -> {
    int suma = x + y;
    System.out.println("Suma: " + suma);
    return suma;
}
```

#### Ejemplos Prácticos Básicos

```java
// Comparador simple
Comparator<String> comparador = (s1, s2) -> s1.compareTo(s2);

// Filtrar lista
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> pares = numeros.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// Mapear elementos
List<String> nombres = Arrays.asList("Ana", "Luis", "María");
List<String> mayusculas = nombres.stream()
    .map(nombre -> nombre.toUpperCase())
    .collect(Collectors.toList());
```

### Expresiones Lambda Avanzadas

#### 1. Lambda con Interfaces Funcionales Personalizadas

```java
@FunctionalInterface
interface Operacion {
    int calcular(int a, int b);
}

@FunctionalInterface
interface Validador<T> {
    boolean validar(T objeto);
}

public class LambdasAvanzadas {
    public static void main(String[] args) {
        // Operación matemática
        Operacion suma = (a, b) -> a + b;
        Operacion multiplicacion = (a, b) -> a * b;
        
        // Validador genérico
        Validador<String> validarEmail = email -> 
            email != null && email.contains("@") && email.contains(".");
        
        Validador<Integer> validarPositivo = numero -> numero > 0;
        
        System.out.println("Suma: " + suma.calcular(5, 3));
        System.out.println("Email válido: " + validarEmail.validar("test@email.com"));
    }
}
```

#### 2. Lambda con Manejo de Excepciones

```java
// Interfaz funcional que permite excepciones
@FunctionalInterface
interface FuncionConExcepcion<T, R> {
    R aplicar(T t) throws Exception;
}

public class LambdaExcepciones {
    public static <T, R> Function<T, R> envolver(FuncionConExcepcion<T, R> funcion) {
        return t -> {
            try {
                return funcion.aplicar(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    public static void main(String[] args) {
        List<String> numeros = Arrays.asList("1", "2", "abc", "4");
        
        List<Integer> enteros = numeros.stream()
            .map(envolver(Integer::parseInt))
            .collect(Collectors.toList());
    }
}
```

#### 3. Lambda con Recursión

```java
// Usando interfaz funcional para recursión
@FunctionalInterface
interface Factorial {
    int calcular(int n);
}

public class LambdaRecursion {
    public static void main(String[] args) {
        // Factorial usando lambda recursiva
        Factorial factorial = n -> {
            if (n <= 1) return 1;
            return n * factorial.calcular(n - 1); // Error: variable no final
        };
        
        // Solución correcta usando array
        Factorial[] factorialArray = new Factorial[1];
        factorialArray[0] = n -> {
            if (n <= 1) return 1;
            return n * factorialArray[0].calcular(n - 1);
        };
        
        System.out.println("Factorial de 5: " + factorialArray[0].calcular(5));
    }
}
```

### Captura de Variables (Closures)

Las lambdas pueden capturar variables del ámbito que las encierra, pero con ciertas restricciones.

#### Reglas de Captura

**1. Variables efectivamente finales:**
```java
public void ejemploCaptura() {
    int multiplicador = 2; // Efectivamente final
    
    List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> resultado = numeros.stream()
        .map(n -> n * multiplicador) // Captura multiplicador
        .collect(Collectors.toList());
        
    // multiplicador = 3; // Error: no se puede modificar
}
```

**2. Variables de instancia y estáticas:**
```java
public class CapturaVariables {
    private int variableInstancia = 10;
    private static int variableEstatica = 20;
    
    public void procesarDatos() {
        List<Integer> datos = Arrays.asList(1, 2, 3);
        
        // Captura variables de instancia
        datos.forEach(dato -> {
            System.out.println(dato + variableInstancia);
            variableInstancia++; // Permitido
        });
        
        // Captura variables estáticas
        datos.forEach(dato -> {
            System.out.println(dato + variableEstatica);
            variableEstatica++; // Permitido
        });
    }
}
```

**3. Captura de this:**
```java
public class CapturaThis {
    private String nombre = "Objeto";
    
    public void procesarElementos() {
        List<String> elementos = Arrays.asList("a", "b", "c");
        
        // Captura implícita de this
        elementos.forEach(elemento -> {
            System.out.println(this.nombre + ": " + elemento);
            this.procesar(elemento); // Llamada a método de instancia
        });
    }
    
    private void procesar(String elemento) {
        System.out.println("Procesando: " + elemento);
    }
}
```

#### Ejemplos Avanzados de Captura

```java
public class CapturaAvanzada {
    public static void main(String[] args) {
        // Captura de array (referencia es final, contenido puede cambiar)
        int[] contador = {0};
        
        List<String> palabras = Arrays.asList("uno", "dos", "tres");
        palabras.forEach(palabra -> {
            contador[0]++; // Modificación permitida
            System.out.println(contador[0] + ": " + palabra);
        });
        
        // Captura de objetos mutables
        List<String> resultado = new ArrayList<>();
        palabras.stream()
            .filter(palabra -> palabra.length() > 2)
            .forEach(palabra -> resultado.add(palabra.toUpperCase()));
        
        System.out.println("Resultado: " + resultado);
    }
}
```

### Limitaciones y Scope

#### 1. Limitaciones de Variables

```java
public class LimitacionesLambda {
    public void ejemploLimitaciones() {
        int variable = 10;
        
        // Error: variable debe ser efectivamente final
        Runnable tarea = () -> {
            System.out.println(variable);
            // variable++; // Error de compilación
        };
        
        // variable = 20; // Error: modifica variable capturada
        
        // Solución: usar wrapper mutable
        AtomicInteger contador = new AtomicInteger(0);
        IntStream.range(0, 5)
            .forEach(i -> contador.incrementAndGet());
    }
}
```

#### 2. Limitaciones de Scope

```java
public class ScopeLambda {
    private int campoInstancia = 100;
    
    public void ejemploScope() {
        int variableLocal = 50;
        
        Consumer<Integer> lambda = valor -> {
            // Acceso a campo de instancia
            System.out.println("Campo instancia: " + campoInstancia);
            
            // Acceso a variable local (efectivamente final)
            System.out.println("Variable local: " + variableLocal);
            
            // Acceso a parámetro
            System.out.println("Parámetro: " + valor);
            
            // No se puede declarar variable con mismo nombre
            // int variableLocal = 25; // Error: variable ya definida
        };
        
        lambda.accept(25);
    }
}
```

#### 3. Limitaciones con Tipos Genéricos

```java
public class LimitacionesGenericos {
    public <T> void procesarLista(List<T> lista) {
        // Inferencia de tipos limitada
        lista.stream()
            .filter(elemento -> elemento != null) // T inferido
            .map(elemento -> elemento.toString()) // String inferido
            .forEach(System.out::println);
            
        // Necesidad de especificar tipo explícitamente
        lista.stream()
            .map((T elemento) -> elemento.toString())
            .collect(Collectors.toList());
    }
}
```

---

## Referencias a Métodos

Las referencias a métodos proporcionan una forma aún más concisa de expresar lambdas cuando simplemente llaman a un método existente.

### Method References (::)

#### Sintaxis General
```java
ClaseContenedora::nombreMetodo
```

#### Tipos de Referencias a Métodos

**1. Referencia a método estático:**
```java
// Lambda: x -> Math.abs(x)
Function<Integer, Integer> valorAbsoluto = Math::abs;

// Lambda: (x, y) -> Integer.compare(x, y)
Comparator<Integer> comparador = Integer::compare;

// Ejemplos prácticos
List<String> numeros = Arrays.asList("1", "2", "3", "4");
List<Integer> enteros = numeros.stream()
    .map(Integer::parseInt) // Referencia a método estático
    .collect(Collectors.toList());
```

**2. Referencia a método de instancia de objeto particular:**
```java
String texto = "Hola Mundo";

// Lambda: s -> texto.concat(s)
Function<String, String> concatenar = texto::concat;

// Lambda: () -> texto.length()
Supplier<Integer> longitud = texto::length;

// Ejemplo práctico
List<String> palabras = Arrays.asList("Java", "Python", "JavaScript");
PrintStream out = System.out;
palabras.forEach(out::println); // Referencia a método de instancia
```

**3. Referencia a método de instancia de tipo arbitrario:**
```java
// Lambda: s -> s.toUpperCase()
Function<String, String> aMayusculas = String::toUpperCase;

// Lambda: (s1, s2) -> s1.compareTo(s2)
Comparator<String> comparador = String::compareTo;

// Ejemplo práctico
List<String> nombres = Arrays.asList("ana", "luis", "maría");
List<String> mayusculas = nombres.stream()
    .map(String::toUpperCase) // Referencia a método de instancia
    .collect(Collectors.toList());
```

**4. Referencia a método con múltiples parámetros:**
```java
// Lambda: (lista, elemento) -> lista.add(elemento)
BiFunction<List<String>, String, Boolean> agregar = List::add;

// Lambda: (mapa, clave) -> mapa.get(clave)
BiFunction<Map<String, Integer>, String, Integer> obtener = Map::get;

// Ejemplo práctico
List<String> palabras = Arrays.asList("uno", "dos", "tres");
Map<String, Integer> longitudes = palabras.stream()
    .collect(Collectors.toMap(
        Function.identity(),    // palabra -> palabra
        String::length         // palabra -> palabra.length()
    ));
```

### Constructor References

#### Referencia a Constructor Sin Parámetros
```java
// Lambda: () -> new ArrayList<>()
Supplier<List<String>> crearLista = ArrayList::new;

// Lambda: () -> new HashSet<>()
Supplier<Set<Integer>> crearSet = HashSet::new;

// Ejemplo práctico
Stream<String> stream = Stream.of("a", "b", "c");
List<String> lista = stream.collect(Collectors.toCollection(ArrayList::new));
```

#### Referencia a Constructor con Parámetros
```java
// Lambda: x -> new Integer(x)
Function<String, Integer> crearEntero = Integer::new;

// Lambda: capacity -> new ArrayList<>(capacity)
Function<Integer, List<String>> crearListaConCapacidad = ArrayList::new;

// Ejemplo con clase personalizada
class Persona {
    private String nombre;
    private int edad;
    
    public Persona(String nombre) {
        this.nombre = nombre;
    }
    
    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }
    
    // getters y toString
}

// Referencias a constructores
Function<String, Persona> crearPersona = Persona::new;
BiFunction<String, Integer, Persona> crearPersonaCompleta = Persona::new;
```

#### Referencia a Constructor de Arrays
```java
// Lambda: size -> new String[size]
IntFunction<String[]> crearArrayString = String[]::new;

// Lambda: size -> new Integer[size]
IntFunction<Integer[]> crearArrayInteger = Integer[]::new;

// Ejemplo práctico
List<String> lista = Arrays.asList("uno", "dos", "tres");
String[] array = lista.stream()
    .toArray(String[]::new); // Referencia a constructor de array
```

### Cuándo Usar Cada Tipo

#### Criterios de Decisión

**1. Usar Referencias a Métodos cuando:**
```java
// ✅ Bueno: Referencia directa a método existente
list.forEach(System.out::println);

// ❌ Evitar: Lambda innecesaria
list.forEach(item -> System.out.println(item));

// ✅ Bueno: Método estático directo
numbers.stream().map(String::valueOf);

// ❌ Evitar: Lambda que solo llama a método
numbers.stream().map(n -> String.valueOf(n));
```

**2. Usar Lambdas cuando:**
```java
// ✅ Bueno: Lógica adicional necesaria
numbers.stream()
    .filter(n -> n > 0 && n < 100)
    .map(n -> n * 2 + 1);

// ✅ Bueno: Múltiples operaciones
people.stream()
    .map(person -> {
        person.setActive(true);
        return person.getName().toUpperCase();
    });
```

#### Comparación Práctica

```java
public class ComparacionUso {
    public static void main(String[] args) {
        List<String> palabras = Arrays.asList("java", "python", "javascript");
        
        // Escenario 1: Transformación simple
        // Preferir referencia a método
        List<String> mayusculas1 = palabras.stream()
            .map(String::toUpperCase)  // ✅ Mejor
            .collect(Collectors.toList());
            
        List<String> mayusculas2 = palabras.stream()
            .map(s -> s.toUpperCase()) // ❌ Innecesario
            .collect(Collectors.toList());
            
        // Escenario 2: Lógica compleja
        // Preferir lambda
        List<String> procesadas = palabras.stream()
            .map(s -> s.length() > 4 ? s.toUpperCase() : s.toLowerCase())
            .collect(Collectors.toList());
            
        // Escenario 3: Múltiples parámetros
        // Referencia a método apropiada
        palabras.sort(String::compareTo); // ✅ Mejor
        palabras.sort((s1, s2) -> s1.compareTo(s2)); // ❌ Innecesario
    }
}
```

---

## Ejemplos Prácticos

### Ejemplo 1: Procesamiento de Datos
```java
public class ProcesadorDatos {
    public static void main(String[] args) {
        List<Empleado> empleados = Arrays.asList(
            new Empleado("Ana", 30, 50000),
            new Empleado("Luis", 25, 45000),
            new Empleado("María", 35, 60000)
        );
        
        // Usando lambdas y referencias a métodos
        double salarioPromedio = empleados.stream()
            .filter(emp -> emp.getEdad() > 25)           // Lambda
            .mapToDouble(Empleado::getSalario)           // Method reference
            .average()
            .orElse(0.0);
            
        List<String> nombresOrdenados = empleados.stream()
            .map(Empleado::getNombre)                    // Method reference
            .sorted(String::compareTo)                   // Method reference
            .collect(Collectors.toList());
            
        System.out.println("Salario promedio: " + salarioPromedio);
        System.out.println("Nombres ordenados: " + nombresOrdenados);
    }
}

class Empleado {
    private String nombre;
    private int edad;
    private double salario;
    
    // Constructor, getters y setters
    public Empleado(String nombre, int edad, double salario) {
        this.nombre = nombre;
        this.edad = edad;
        this.salario = salario;
    }
    
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public double getSalario() { return salario; }
}
```

### Ejemplo 2: Validación de Datos
```java
public class ValidadorDatos {
    public static void main(String[] args) {
        List<String> emails = Arrays.asList(
            "usuario@email.com",
            "invalido.email",
            "otro@dominio.org",
            "sin_arroba.com"
        );
        
        // Validador usando lambda
        Predicate<String> validadorEmail = email -> 
            email != null && 
            email.contains("@") && 
            email.contains(".") &&
            email.length() > 5;
            
        List<String> emailsValidos = emails.stream()
            .filter(validadorEmail)
            .collect(Collectors.toList());
            
        // Usando method reference para imprimir
        emailsValidos.forEach(System.out::println);
        
        // Contador usando captura de variable
        AtomicInteger contador = new AtomicInteger(0);
        emails.stream()
            .filter(validadorEmail)
            .forEach(email -> {
                contador.incrementAndGet();
                System.out.println("Email válido #" + contador.get() + ": " + email);
            });
    }
}
```

### Ejemplo 3: Factory con Constructor References
```java
public class FactoryEjemplo {
    public static void main(String[] args) {
        // Factory usando constructor references
        Map<String, Supplier<List<String>>> factoryListas = Map.of(
            "ArrayList", ArrayList::new,
            "LinkedList", LinkedList::new,
            "Vector", Vector::new
        );
        
        // Factory con parámetros
        Map<String, Function<Integer, List<String>>> factoryConCapacidad = Map.of(
            "ArrayList", ArrayList::new,
            "Vector", Vector::new
        );
        
        // Uso de las factories
        List<String> lista1 = factoryListas.get("ArrayList").get();
        List<String> lista2 = factoryConCapacidad.get("ArrayList").apply(100);
        
        // Factory para objetos personalizados
        BiFunction<String, Integer, Persona> factoryPersona = Persona::new;
        List<Persona> personas = Stream.of(
            factoryPersona.apply("Ana", 25),
            factoryPersona.apply("Luis", 30),
            factoryPersona.apply("María", 28)
        ).collect(Collectors.toList());
    }
}
```

---

## Mejores Prácticas

### 1. Legibilidad y Mantenimiento
```java
// ✅ Bueno: Expresivo y claro
empleados.stream()
    .filter(empleado -> empleado.isActivo())
    .map(Empleado::getNombre)
    .sorted()
    .collect(Collectors.toList());

// ❌ Evitar: Demasiado complejo en una línea
empleados.stream().filter(e -> e.isActivo() && e.getSalario() > 50000 && e.getDepartamento().equals("IT")).map(e -> e.getNombre().toUpperCase().trim()).sorted((a, b) -> a.compareTo(b)).collect(Collectors.toList());
```

### 2. Rendimiento
```java
// ✅ Bueno: Operaciones paralelas cuando sea apropiado
largeList.parallelStream()
    .filter(item -> expensiveOperation(item))
    .collect(Collectors.toList());

// ✅ Bueno: Evitar boxing/unboxing innecesario
IntStream.range(0, 1000)
    .filter(i -> i % 2 == 0)
    .sum(); // Mejor que boxear a Integer
```

### 3. Manejo de Excepciones
```java
// Wrapper para manejar excepciones en lambdas
public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> func) {
    return t -> {
        try {
            return func.apply(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

@FunctionalInterface
interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;
}

// Uso
List<String> urls = Arrays.asList("url1", "url2", "url3");
List<String> contenidos = urls.stream()
    .map(unchecked(url -> fetchContent(url)))
    .collect(Collectors.toList());
```

### 4. Reutilización de Lambdas
```java
public class ReutilizacionLambdas {
    // Predicados reutilizables
    public static final Predicate<String> NO_VACIO = s -> s != null && !s.isEmpty();
    public static final Predicate<String> ES_EMAIL = s -> s.contains("@");
    
    // Funciones reutilizables
    public static final Function<String, String> A_MAYUSCULAS = String::toUpperCase;
    public static final Function<String, String> TRIM = String::trim;
    
    public void procesarDatos(List<String> datos) {
        List<String> resultado = datos.stream()
            .filter(NO_VACIO)
            .filter(ES_EMAIL)
            .map(TRIM)
            .map(A_MAYUSCULAS)
            .collect(Collectors.toList());
    }
}
```

---

## Conclusión

Las expresiones lambda y las referencias a métodos son herramientas poderosas que mejoran significativamente la legibilidad y concisión del código Java. Su uso apropiado puede hacer que el código sea más expresivo y mantenible, especialmente cuando se trabaja con streams y APIs funcionales.

**Puntos clave a recordar:**
- Las lambdas son ideales para lógica simple y directa
- Las referencias a métodos son preferibles cuando solo se llama a un método existente
- La captura de variables tiene restricciones importantes
- El rendimiento y la legibilidad deben equilibrarse
- La reutilización de lambdas mejora la mantenibilidad del código