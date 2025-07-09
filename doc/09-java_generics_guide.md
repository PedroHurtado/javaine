# Guía Completa de Genéricos en Java

## Tabla de Contenidos
1. [Introducción a los Genéricos](#introducción-a-los-genéricos)
2. [Sintaxis Básica](#sintaxis-básica)
3. [Clases Genéricas](#clases-genéricas)
4. [Métodos Genéricos](#métodos-genéricos)
5. [Interfaces Genéricas](#interfaces-genéricas)
6. [Wildcards (Comodines)](#wildcards-comodines)
7. [Bounded Type Parameters](#bounded-type-parameters)
8. [Erasure de Tipos](#erasure-de-tipos)
9. [Ejemplos Prácticos](#ejemplos-prácticos)
10. [Buenas Prácticas](#buenas-prácticas)

## Introducción a los Genéricos

Los genéricos en Java permiten escribir código reutilizable y type-safe (seguro en tipos). Fueron introducidos en Java 5 para resolver problemas de casting y proporcionar mayor seguridad en tiempo de compilación.

### Ventajas de los Genéricos:
- **Type Safety**: Detección de errores en tiempo de compilación
- **Eliminación de casting**: No necesitas hacer cast explícito
- **Código más legible**: El código es más claro y expresivo
- **Reutilización**: Un mismo código puede trabajar con diferentes tipos

## Sintaxis Básica

### Declaración de Tipos Genéricos
```java
// Sintaxis básica
List<String> lista = new ArrayList<String>();

// Desde Java 7 - Diamond Operator
List<String> lista = new ArrayList<>();

// Múltiples parámetros de tipo
Map<String, Integer> mapa = new HashMap<>();
```

### Convenciones de Nomenclatura
- **T** - Type (Tipo)
- **E** - Element (Elemento)
- **K** - Key (Clave)
- **V** - Value (Valor)
- **N** - Number (Número)
- **S, U, V** - 2º, 3º, 4º tipos

## Clases Genéricas

### Clase Genérica Simple
```java
public class Caja<T> {
    private T contenido;
    
    public void guardar(T item) {
        this.contenido = item;
    }
    
    public T obtener() {
        return contenido;
    }
    
    public boolean estaVacia() {
        return contenido == null;
    }
}

// Uso
Caja<String> cajaTexto = new Caja<>();
cajaTexto.guardar("Hola Mundo");
String texto = cajaTexto.obtener(); // No necesita casting

Caja<Integer> cajaNumero = new Caja<>();
cajaNumero.guardar(42);
Integer numero = cajaNumero.obtener();
```

### Clase con Múltiples Parámetros de Tipo
```java
public class Pair<T, U> {
    private T primero;
    private U segundo;
    
    public Pair(T primero, U segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }
    
    public T getPrimero() {
        return primero;
    }
    
    public U getSegundo() {
        return segundo;
    }
    
    public void setPrimero(T primero) {
        this.primero = primero;
    }
    
    public void setSegundo(U segundo) {
        this.segundo = segundo;
    }
    
    @Override
    public String toString() {
        return "(" + primero + ", " + segundo + ")";
    }
}

// Uso
Pair<String, Integer> nombreEdad = new Pair<>("Juan", 25);
Pair<Double, String> coordenada = new Pair<>(3.14, "PI");
```

## Métodos Genéricos

### Método Genérico en Clase No Genérica
```java
public class Utilidades {
    
    // Método genérico estático
    public static <T> void intercambiar(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    // Método genérico que retorna el tipo
    public static <T> T obtenerPrimero(List<T> lista) {
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }
    
    // Método con múltiples tipos genéricos
    public static <T, U> Pair<T, U> crearPair(T primero, U segundo) {
        return new Pair<>(primero, segundo);
    }
}

// Uso
String[] nombres = {"Ana", "Luis", "Carlos"};
Utilidades.intercambiar(nombres, 0, 2); // Carlos, Luis, Ana

List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);
Integer primero = Utilidades.obtenerPrimero(numeros); // 1

Pair<String, Integer> par = Utilidades.crearPair("Edad", 30);
```

### Método Genérico en Clase Genérica
```java
public class Contenedor<T> {
    private List<T> elementos;
    
    public Contenedor() {
        this.elementos = new ArrayList<>();
    }
    
    public void agregar(T elemento) {
        elementos.add(elemento);
    }
    
    // Método genérico adicional
    public <U> U procesar(Function<T, U> procesador, int indice) {
        if (indice >= 0 && indice < elementos.size()) {
            return procesador.apply(elementos.get(indice));
        }
        return null;
    }
    
    public List<T> obtenerTodos() {
        return new ArrayList<>(elementos);
    }
}
```

## Interfaces Genéricas

### Interface Genérica Simple
```java
public interface Comparable<T> {
    int compareTo(T other);
}

public interface Repository<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    void delete(ID id);
}
```

### Implementación de Interface Genérica
```java
public class Usuario implements Comparable<Usuario> {
    private String nombre;
    private int edad;
    
    public Usuario(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }
    
    @Override
    public int compareTo(Usuario otro) {
        return this.nombre.compareTo(otro.nombre);
    }
    
    // getters y setters...
}

public class UsuarioRepository implements Repository<Usuario, Long> {
    private Map<Long, Usuario> usuarios = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public void save(Usuario usuario) {
        usuarios.put(nextId++, usuario);
    }
    
    @Override
    public Usuario findById(Long id) {
        return usuarios.get(id);
    }
    
    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios.values());
    }
    
    @Override
    public void delete(Long id) {
        usuarios.remove(id);
    }
}
```

## Wildcards (Comodines)

### Unbounded Wildcard (?)
```java
public static void imprimirLista(List<?> lista) {
    for (Object elemento : lista) {
        System.out.println(elemento);
    }
}

// Puede recibir cualquier tipo de List
List<String> textos = Arrays.asList("a", "b", "c");
List<Integer> numeros = Arrays.asList(1, 2, 3);
imprimirLista(textos);
imprimirLista(numeros);
```

### Upper Bounded Wildcard (? extends)
```java
// Solo acepta Number y sus subclases
public static double sumar(List<? extends Number> numeros) {
    double suma = 0.0;
    for (Number num : numeros) {
        suma += num.doubleValue();
    }
    return suma;
}

List<Integer> enteros = Arrays.asList(1, 2, 3);
List<Double> decimales = Arrays.asList(1.1, 2.2, 3.3);
List<Float> flotantes = Arrays.asList(1.0f, 2.0f, 3.0f);

double suma1 = sumar(enteros);    // Válido
double suma2 = sumar(decimales);  // Válido
double suma3 = sumar(flotantes);  // Válido
```

### Lower Bounded Wildcard (? super)
```java
// Solo acepta Integer y sus superclases
public static void agregarNumeros(List<? super Integer> lista) {
    lista.add(1);
    lista.add(2);
    lista.add(3);
}

List<Integer> enteros = new ArrayList<>();
List<Number> numeros = new ArrayList<>();
List<Object> objetos = new ArrayList<>();

agregarNumeros(enteros);  // Válido
agregarNumeros(numeros);  // Válido
agregarNumeros(objetos);  // Válido
```

## Bounded Type Parameters

### Upper Bound (extends)
```java
// T debe ser Number o una subclase de Number
public class CalculadoraNumerica<T extends Number> {
    private List<T> numeros;
    
    public CalculadoraNumerica() {
        this.numeros = new ArrayList<>();
    }
    
    public void agregar(T numero) {
        numeros.add(numero);
    }
    
    public double promedio() {
        if (numeros.isEmpty()) {
            return 0.0;
        }
        
        double suma = 0.0;
        for (T numero : numeros) {
            suma += numero.doubleValue(); // Método disponible por ser Number
        }
        return suma / numeros.size();
    }
    
    public T maximo() {
        if (numeros.isEmpty()) {
            return null;
        }
        
        T max = numeros.get(0);
        for (T numero : numeros) {
            if (numero.doubleValue() > max.doubleValue()) {
                max = numero;
            }
        }
        return max;
    }
}

// Uso
CalculadoraNumerica<Integer> calcInt = new CalculadoraNumerica<>();
calcInt.agregar(10);
calcInt.agregar(20);
calcInt.agregar(30);
System.out.println("Promedio: " + calcInt.promedio()); // 20.0

CalculadoraNumerica<Double> calcDouble = new CalculadoraNumerica<>();
calcDouble.agregar(1.5);
calcDouble.agregar(2.5);
calcDouble.agregar(3.5);
System.out.println("Máximo: " + calcDouble.maximo()); // 3.5
```

### Multiple Bounds
```java
public interface Dibujable {
    void dibujar();
}

public class Figura implements Dibujable {
    protected String color;
    
    public Figura(String color) {
        this.color = color;
    }
    
    @Override
    public void dibujar() {
        System.out.println("Dibujando figura de color " + color);
    }
}

// T debe extender Figura E implementar Dibujable
public class Canvas<T extends Figura & Dibujable> {
    private List<T> figuras;
    
    public Canvas() {
        this.figuras = new ArrayList<>();
    }
    
    public void agregar(T figura) {
        figuras.add(figura);
    }
    
    public void dibujarTodo() {
        for (T figura : figuras) {
            figura.dibujar(); // Método disponible por implementar Dibujable
        }
    }
}
```

## Erasure de Tipos

### Concepto de Type Erasure
```java
// En tiempo de compilación
List<String> listaString = new ArrayList<String>();
List<Integer> listaInteger = new ArrayList<Integer>();

// En tiempo de ejecución (después del erasure)
List listaString = new ArrayList();
List listaInteger = new ArrayList();

// Por eso esto es válido en runtime pero no en compile time
public class EjemploErasure {
    // Esto NO compila - métodos con la misma signatura después del erasure
    /*
    public void procesar(List<String> lista) { }
    public void procesar(List<Integer> lista) { } // Error de compilación
    */
    
    // Solución: usar diferentes nombres de método
    public void procesarStrings(List<String> lista) { }
    public void procesarIntegers(List<Integer> lista) { }
}
```

### Limitaciones del Type Erasure
```java
public class LimitacionesGenericos<T> {
    
    // NO PERMITIDO: No se puede crear instancia de T
    /*
    public T crear() {
        return new T(); // Error de compilación
    }
    */
    
    // NO PERMITIDO: No se puede crear array de tipo genérico
    /*
    public T[] crearArray(int tamaño) {
        return new T[tamaño]; // Error de compilación
    }
    */
    
    // NO PERMITIDO: No se puede usar instanceof con tipos genéricos
    /*
    public boolean esInstancia(Object obj) {
        return obj instanceof T; // Error de compilación
    }
    */
    
    // SOLUCIÓN: Usar Class<T> como parámetro
    private Class<T> tipo;
    
    public LimitacionesGenericos(Class<T> tipo) {
        this.tipo = tipo;
    }
    
    public T crear() throws InstantiationException, IllegalAccessException {
        return tipo.newInstance();
    }
    
    public boolean esInstancia(Object obj) {
        return tipo.isInstance(obj);
    }
}
```

## Ejemplos Prácticos

### 1. Sistema de Cache Genérico
```java
public class Cache<K, V> {
    private final Map<K, V> cache;
    private final int maxSize;
    
    public Cache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<K, V>(maxSize + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > Cache.this.maxSize;
            }
        };
    }
    
    public synchronized V get(K key) {
        return cache.get(key);
    }
    
    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }
    
    public synchronized void remove(K key) {
        cache.remove(key);
    }
    
    public synchronized void clear() {
        cache.clear();
    }
    
    public synchronized int size() {
        return cache.size();
    }
    
    public synchronized boolean containsKey(K key) {
        return cache.containsKey(key);
    }
}

// Uso
Cache<String, Usuario> cacheUsuarios = new Cache<>(100);
cacheUsuarios.put("user1", new Usuario("Juan", 25));
Usuario usuario = cacheUsuarios.get("user1");
```

### 2. Builder Pattern Genérico
```java
public abstract class Builder<T> {
    public abstract T build();
}

public class Producto {
    private final String nombre;
    private final double precio;
    private final String categoria;
    private final String descripcion;
    
    private Producto(ProductoBuilder builder) {
        this.nombre = builder.nombre;
        this.precio = builder.precio;
        this.categoria = builder.categoria;
        this.descripcion = builder.descripcion;
    }
    
    public static class ProductoBuilder extends Builder<Producto> {
        private String nombre;
        private double precio;
        private String categoria;
        private String descripcion;
        
        public ProductoBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }
        
        public ProductoBuilder precio(double precio) {
            this.precio = precio;
            return this;
        }
        
        public ProductoBuilder categoria(String categoria) {
            this.categoria = categoria;
            return this;
        }
        
        public ProductoBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }
        
        @Override
        public Producto build() {
            return new Producto(this);
        }
    }
    
    // getters...
}

// Uso
Producto producto = new Producto.ProductoBuilder()
    .nombre("Laptop")
    .precio(999.99)
    .categoria("Electrónicos")
    .descripcion("Laptop gaming de alta gama")
    .build();
```

### 3. Validador Genérico
```java
@FunctionalInterface
public interface Validador<T> {
    boolean validar(T objeto);
}

public class ValidadorCompuesto<T> {
    private final List<Validador<T>> validadores;
    
    public ValidadorCompuesto() {
        this.validadores = new ArrayList<>();
    }
    
    public ValidadorCompuesto<T> agregar(Validador<T> validador) {
        validadores.add(validador);
        return this;
    }
    
    public boolean validarTodo(T objeto) {
        return validadores.stream().allMatch(v -> v.validar(objeto));
    }
    
    public boolean validarCualquiera(T objeto) {
        return validadores.stream().anyMatch(v -> v.validar(objeto));
    }
    
    public List<String> obtenerErrores(T objeto, 
                                      Function<Validador<T>, String> mensajeError) {
        return validadores.stream()
            .filter(v -> !v.validar(objeto))
            .map(mensajeError)
            .collect(Collectors.toList());
    }
}

// Uso
ValidadorCompuesto<String> validadorEmail = new ValidadorCompuesto<String>()
    .agregar(email -> email != null)
    .agregar(email -> email.contains("@"))
    .agregar(email -> email.length() > 5);

boolean esValido = validadorEmail.validarTodo("usuario@ejemplo.com");
```

## Buenas Prácticas

### 1. Usar Nombres Descriptivos
```java
// Bueno
public class Repository<Entity, ID> { }
public interface Mapper<Source, Target> { }

// Menos claro
public class Repository<T, U> { }
public interface Mapper<S, T> { }
```

### 2. Principio PECS (Producer Extends, Consumer Super)
```java
// Producer - cuando PRODUCES elementos (los lee)
public void copiar(List<? extends T> source, List<? super T> destination) {
    for (T item : source) {    // source PRODUCE elementos
        destination.add(item); // destination CONSUME elementos
    }
}
```

### 3. Evitar Raw Types
```java
// Malo
List lista = new ArrayList();
lista.add("String");
lista.add(123); // Problema en runtime

// Bueno
List<Object> lista = new ArrayList<>();
lista.add("String");
lista.add(123); // Ambos son Objects
```

### 4. Usar Diamond Operator
```java
// Desde Java 7
Map<String, List<Integer>> mapa = new HashMap<>(); // Preferido
Map<String, List<Integer>> mapa = new HashMap<String, List<Integer>>(); // Redundante
```

### 5. Considerar Wildcards para APIs Flexibles
```java
// Más flexible
public void procesar(Collection<? extends Number> numeros) { }

// Menos flexible
public void procesar(Collection<Number> numeros) { }
```

### 6. Documentar Restricciones de Tipo
```java
/**
 * Ordena una lista de elementos comparables.
 * @param <T> tipo de elemento que debe implementar Comparable
 * @param lista lista a ordenar
 */
public static <T extends Comparable<T>> void ordenar(List<T> lista) {
    Collections.sort(lista);
}
```

## Conclusión

Los genéricos en Java son una herramienta poderosa que proporciona:

- **Seguridad de tipos** en tiempo de compilación
- **Código más limpio** sin necesidad de casting
- **Mejor rendimiento** al evitar boxing/unboxing innecesario
- **APIs más expresivas** y fáciles de usar

Dominar los genéricos es esencial para escribir código Java moderno y eficiente. La práctica constante con diferentes escenarios te ayudará a aplicar estos conceptos de manera natural en tus proyectos.

---

