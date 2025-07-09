# Excepciones y Aserciones en Java

## Tabla de Contenidos
1. [Excepciones](#excepciones)
2. [Manejo de Errores](#manejo-de-errores)
3. [Aserciones](#aserciones)
4. [Mejores Prácticas](#mejores-prácticas)
5. [Ejemplos Prácticos](#ejemplos-prácticos)

---

## Excepciones

### ¿Qué son las Excepciones en Java?

Las excepciones son objetos que representan condiciones anormales que ocurren durante la ejecución de un programa. En Java, todas las excepciones son subclases de la clase `Throwable`.

### Jerarquía de Excepciones

```
Throwable
├── Error (No se debe capturar)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception
    ├── RuntimeException (Unchecked)
    │   ├── NullPointerException
    │   ├── IndexOutOfBoundsException
    │   ├── IllegalArgumentException
    │   └── ClassCastException
    └── Checked Exceptions
        ├── IOException
        ├── SQLException
        ├── ClassNotFoundException
        └── InterruptedException
```

### Tipos de Excepciones

#### 1. Checked Exceptions (Excepciones Verificadas)
Deben ser manejadas obligatoriamente en tiempo de compilación.

```java
public void readFile(String filename) throws IOException {
    FileReader file = new FileReader(filename); // Puede lanzar IOException
    BufferedReader reader = new BufferedReader(file);
    // ...
}
```

#### 2. Unchecked Exceptions (Excepciones No Verificadas)
Extienden de `RuntimeException` y no requieren manejo obligatorio.

```java
public void divide(int a, int b) {
    if (b == 0) {
        throw new IllegalArgumentException("División por cero no permitida");
    }
    int result = a / b;
}
```

#### 3. Errors
Representan problemas graves del sistema que normalmente no se deben capturar.

```java
// No se debe hacer esto:
try {
    // código
} catch (OutOfMemoryError e) {
    // Generalmente no se puede recuperar de esto
}
```

### Creación de Excepciones Personalizadas

```java
// Checked Exception personalizada
public class ValidationException extends Exception {
    private String field;
    private Object value;
    
    public ValidationException(String message, String field, Object value) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    public String getField() { return field; }
    public Object getValue() { return value; }
}

// Unchecked Exception personalizada
public class BusinessLogicException extends RuntimeException {
    private int errorCode;
    
    public BusinessLogicException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessLogicException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() { return errorCode; }
}
```

---

## Manejo de Errores

### Try-Catch-Finally

#### Sintaxis Básica

```java
try {
    // Código que puede lanzar excepciones
    String result = riskyOperation();
} catch (SpecificException e) {
    // Manejo específico
    System.err.println("Error específico: " + e.getMessage());
} catch (Exception e) {
    // Manejo genérico
    System.err.println("Error general: " + e.getMessage());
} finally {
    // Código que siempre se ejecuta
    cleanup();
}
```

#### Multi-Catch (Java 7+)

```java
try {
    performOperation();
} catch (IOException | SQLException e) {
    // Manejo común para ambas excepciones
    logger.error("Error de I/O o base de datos", e);
    throw new ServiceException("Operación fallida", e);
}
```

#### Try-with-Resources (Java 7+)

```java
// Manejo automático de recursos
try (FileReader file = new FileReader("data.txt");
     BufferedReader reader = new BufferedReader(file)) {
    
    return reader.readLine();
} catch (IOException e) {
    throw new DataProcessingException("Error leyendo archivo", e);
}
// Los recursos se cierran automáticamente
```

### Propagación de Excepciones

#### Throws en Métodos

```java
public class FileProcessor {
    
    public String processFile(String filename) 
            throws IOException, ValidationException {
        
        validateFilename(filename);
        return readAndProcess(filename);
    }
    
    private void validateFilename(String filename) throws ValidationException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new ValidationException("Nombre de archivo inválido", "filename", filename);
        }
    }
    
    private String readAndProcess(String filename) throws IOException {
        try (Scanner scanner = new Scanner(new File(filename))) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            return content.toString();
        }
    }
}
```

#### Re-lanzamiento con Contexto

```java
public class ServiceLayer {
    
    public User getUserById(int id) throws UserNotFoundException {
        try {
            return userRepository.findById(id);
        } catch (SQLException e) {
            // Re-lanzar con más contexto
            throw new UserNotFoundException(
                "No se pudo obtener usuario con ID: " + id, e);
        }
    }
}
```

### Manejo de Excepciones en Cadena

```java
public class OrderService {
    
    public void processOrder(Order order) throws OrderProcessingException {
        try {
            validateOrder(order);
            calculateTotal(order);
            saveOrder(order);
            sendConfirmation(order);
        } catch (ValidationException e) {
            throw new OrderProcessingException("Validación fallida", e);
        } catch (CalculationException e) {
            throw new OrderProcessingException("Error en cálculos", e);
        } catch (PersistenceException e) {
            throw new OrderProcessingException("Error guardando orden", e);
        } catch (NotificationException e) {
            // Log pero no fallar toda la operación
            logger.warn("No se pudo enviar confirmación", e);
        }
    }
}
```

---

## Aserciones

### ¿Qué son las Aserciones?

Las aserciones son declaraciones que verifican suposiciones sobre el estado del programa durante el desarrollo y testing. En Java se introdujeron en la versión 1.4.

### Sintaxis de Aserciones

```java
// Forma básica
assert condition;

// Con mensaje personalizado
assert condition : "Mensaje de error";
```

### Habilitación de Aserciones

```bash
# Habilitar aserciones para todas las clases
java -ea MyProgram

# Habilitar para un paquete específico
java -ea:com.mycompany.mypackage... MyProgram

# Deshabilitar aserciones (por defecto)
java -da MyProgram
```

### Casos de Uso de Aserciones

#### 1. Validación de Precondiciones

```java
public void withdraw(double amount) {
    assert amount > 0 : "El monto debe ser positivo: " + amount;
    assert amount <= balance : "Fondos insuficientes: " + amount + " > " + balance;
    
    balance -= amount;
}
```

#### 2. Validación de Postcondiciones

```java
public List<String> sortNames(List<String> names) {
    List<String> sorted = new ArrayList<>(names);
    Collections.sort(sorted);
    
    // Verificar que el resultado esté ordenado
    assert isSorted(sorted) : "La lista no está ordenada correctamente";
    
    return sorted;
}

private boolean isSorted(List<String> list) {
    for (int i = 1; i < list.size(); i++) {
        if (list.get(i-1).compareTo(list.get(i)) > 0) {
            return false;
        }
    }
    return true;
}
```

#### 3. Validación de Invariantes

```java
public class BankAccount {
    private double balance;
    private final double minBalance;
    
    public BankAccount(double initialBalance, double minBalance) {
        this.balance = initialBalance;
        this.minBalance = minBalance;
        assertInvariant();
    }
    
    public void deposit(double amount) {
        assert amount > 0 : "Monto debe ser positivo";
        balance += amount;
        assertInvariant();
    }
    
    private void assertInvariant() {
        assert balance >= minBalance : 
            "Invariante violada: balance(" + balance + ") < minBalance(" + minBalance + ")";
    }
}
```

#### 4. Validación de Estados Internos

```java
public class Stack<T> {
    private T[] elements;
    private int size;
    
    @SuppressWarnings("unchecked")
    public Stack(int capacity) {
        elements = (T[]) new Object[capacity];
        size = 0;
        assertInvariant();
    }
    
    public void push(T element) {
        assert element != null : "No se puede agregar null";
        assert size < elements.length : "Stack lleno";
        
        elements[size++] = element;
        assertInvariant();
    }
    
    public T pop() {
        assert size > 0 : "Stack vacío";
        
        T element = elements[--size];
        elements[size] = null; // Evitar memory leaks
        assertInvariant();
        return element;
    }
    
    private void assertInvariant() {
        assert size >= 0 && size <= elements.length : 
            "Tamaño inválido: " + size;
        assert (size == 0) || (elements[size-1] != null) : 
            "Elemento superior es null";
    }
}
```

---

## Mejores Prácticas

### Para Excepciones

#### ✅ Buenas Prácticas

```java
// 1. Capturar excepciones específicas
try {
    processFile(filename);
} catch (FileNotFoundException e) {
    logger.error("Archivo no encontrado: " + filename, e);
    return getDefaultContent();
} catch (IOException e) {
    logger.error("Error de I/O procesando archivo", e);
    throw new ProcessingException("No se pudo procesar el archivo", e);
}

// 2. Proporcionar información útil
public void validateAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException(
            "Edad inválida: " + age + ". Debe estar entre 0 y 150");
    }
}

// 3. Usar try-with-resources
public String readFile(String filename) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
        return reader.lines().collect(Collectors.joining("\n"));
    }
}

// 4. Documentar excepciones
/**
 * Procesa un pedido de usuario
 * 
 * @param order el pedido a procesar
 * @throws ValidationException si el pedido es inválido
 * @throws PaymentException si el pago falla
 * @throws InventoryException si no hay stock suficiente
 */
public void processOrder(Order order) 
        throws ValidationException, PaymentException, InventoryException {
    // implementación
}
```

#### ❌ Malas Prácticas

```java
// 1. Capturar Exception genérica
try {
    riskyOperation();
} catch (Exception e) {
    // Muy genérico, dificulta el debugging
}

// 2. Ignorar excepciones
try {
    riskyOperation();
} catch (IOException e) {
    // Silenciar excepciones es peligroso
}

// 3. Usar excepciones para control de flujo
public boolean userExists(String username) {
    try {
        findUser(username);
        return true;
    } catch (UserNotFoundException e) {
        return false; // Mal uso de excepciones
    }
}
```

### Para Aserciones

#### ✅ Buenas Prácticas

```java
// 1. Usar para validar precondiciones internas
private void processArray(int[] array, int startIndex) {
    assert array != null : "Array no puede ser null";
    assert startIndex >= 0 && startIndex < array.length : 
        "Índice fuera de rango: " + startIndex;
    // ...
}

// 2. Incluir información de diagnóstico
assert balance >= 0 : "Balance negativo: " + balance + " para cuenta: " + accountId;

// 3. Usar para documentar suposiciones
public void bubbleSort(int[] array) {
    // Suposición: el array no está vacío
    assert array.length > 0 : "Array vacío no se puede ordenar";
    
    for (int i = 0; i < array.length - 1; i++) {
        for (int j = 0; j < array.length - i - 1; j++) {
            if (array[j] > array[j + 1]) {
                swap(array, j, j + 1);
            }
        }
    }
    
    // Postcondición: el array está ordenado
    assert isSorted(array) : "Array no quedó ordenado";
}
```

#### ❌ Malas Prácticas

```java
// 1. No usar aserciones para validar entrada de usuario
public void setUserAge(int age) {
    // ❌ Malo - la entrada del usuario debe validarse siempre
    assert age >= 0 : "Edad debe ser positiva";
    this.age = age;
}

// 2. No usar aserciones con efectos secundarios
public boolean isValid() {
    // ❌ Malo - tiene efecto secundario
    assert incrementCounter() > 0 : "Counter debe ser positivo";
    return true;
}
```

---

## Ejemplos Prácticos

### Ejemplo Completo: Sistema de Gestión de Biblioteca

```java
public class Library {
    private Map<String, Book> books;
    private Map<String, User> users;
    
    public Library() {
        this.books = new HashMap<>();
        this.users = new HashMap<>();
    }
    
    /**
     * Presta un libro a un usuario
     * 
     * @param bookId ID del libro
     * @param userId ID del usuario
     * @throws BookNotFoundException si el libro no existe
     * @throws UserNotFoundException si el usuario no existe
     * @throws BookNotAvailableException si el libro ya está prestado
     * @throws UserLimitExceededException si el usuario excede el límite
     */
    public void lendBook(String bookId, String userId) 
            throws BookNotFoundException, UserNotFoundException, 
                   BookNotAvailableException, UserLimitExceededException {
        
        // Validar precondiciones con aserciones (desarrollo)
        assert bookId != null && !bookId.trim().isEmpty() : 
            "Book ID no puede ser null o vacío";
        assert userId != null && !userId.trim().isEmpty() : 
            "User ID no puede ser null o vacío";
        
        // Validar existencia (excepciones checked)
        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Libro no encontrado: " + bookId);
        }
        
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado: " + userId);
        }
        
        // Validar disponibilidad
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(
                "Libro no disponible: " + book.getTitle());
        }
        
        // Validar límite de usuario
        if (user.getBorrowedBooks().size() >= user.getMaxBooks()) {
            throw new UserLimitExceededException(
                "Usuario excede límite de libros: " + user.getMaxBooks());
        }
        
        // Realizar el préstamo
        try {
            book.setAvailable(false);
            book.setBorrowedBy(userId);
            book.setBorrowDate(LocalDate.now());
            user.addBorrowedBook(bookId);
            
            // Verificar postcondición
            assert !book.isAvailable() : "Libro debería estar no disponible";
            assert book.getBorrowedBy().equals(userId) : "Prestamista incorrecto";
            assert user.getBorrowedBooks().contains(bookId) : 
                "Libro no agregado a lista de usuario";
                
        } catch (Exception e) {
            // Rollback en caso de error
            book.setAvailable(true);
            book.setBorrowedBy(null);
            book.setBorrowDate(null);
            throw new LendingException("Error prestando libro", e);
        }
    }
    
    public void returnBook(String bookId, String userId) 
            throws BookNotFoundException, InvalidReturnException {
        
        assert bookId != null : "Book ID no puede ser null";
        assert userId != null : "User ID no puede ser null";
        
        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Libro no encontrado: " + bookId);
        }
        
        if (!userId.equals(book.getBorrowedBy())) {
            throw new InvalidReturnException(
                "Usuario no es quien prestó el libro: " + userId);
        }
        
        User user = users.get(userId);
        if (user != null) {
            user.removeBorrowedBook(bookId);
        }
        
        book.setAvailable(true);
        book.setBorrowedBy(null);
        book.setBorrowDate(null);
        
        // Verificar postcondición
        assert book.isAvailable() : "Libro debería estar disponible";
        assert book.getBorrowedBy() == null : "Libro no debería tener prestamista";
    }
}

// Excepciones personalizadas
class LibraryException extends Exception {
    public LibraryException(String message) { super(message); }
    public LibraryException(String message, Throwable cause) { super(message, cause); }
}

class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String message) { super(message); }
}

class UserNotFoundException extends LibraryException {
    public UserNotFoundException(String message) { super(message); }
}

class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String message) { super(message); }
}

class UserLimitExceededException extends LibraryException {
    public UserLimitExceededException(String message) { super(message); }
}

class InvalidReturnException extends LibraryException {
    public InvalidReturnException(String message) { super(message); }
}

class LendingException extends RuntimeException {
    public LendingException(String message, Throwable cause) { super(message, cause); }
}
```

### Ejemplo de Testing con Excepciones

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    private Library library;
    private Book book;
    private User user;
    
    @BeforeEach
    void setUp() {
        library = new Library();
        book = new Book("1", "Java Effective", "Joshua Bloch");
        user = new User("user1", "John Doe", 3);
        
        library.addBook(book);
        library.addUser(user);
    }
    
    @Test
    void testLendBookSuccess() {
        assertDoesNotThrow(() -> {
            library.lendBook("1", "user1");
        });
        
        assertFalse(book.isAvailable());
        assertEquals("user1", book.getBorrowedBy());
        assertTrue(user.getBorrowedBooks().contains("1"));
    }
    
    @Test
    void testLendBookNotFound() {
        BookNotFoundException exception = assertThrows(
            BookNotFoundException.class,
            () -> library.lendBook("999", "user1")
        );
        
        assertEquals("Libro no encontrado: 999", exception.getMessage());
    }
    
    @Test
    void testLendBookUserNotFound() {
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> library.lendBook("1", "user999")
        );
        
        assertEquals("Usuario no encontrado: user999", exception.getMessage());
    }
    
    @Test
    void testLendBookNotAvailable() throws Exception {
        // Prestar el libro primero
        library.lendBook("1", "user1");
        
        // Intentar prestarlo de nuevo
        BookNotAvailableException exception = assertThrows(
            BookNotAvailableException.class,
            () -> library.lendBook("1", "user1")
        );
        
        assertTrue(exception.getMessage().contains("no disponible"));
    }
}
```

---

## Resumen

### Excepciones
- **Checked**: Deben ser manejadas obligatoriamente
- **Unchecked**: Extienden RuntimeException, manejo opcional
- **Personalizar**: Crear excepciones específicas del dominio
- **Propagar**: Usar throws para declarar excepciones
- **Capturar**: Try-catch-finally para manejo local

### Aserciones
- **Desarrollo**: Verificar suposiciones durante desarrollo
- **Habilitar**: Usar -ea para activar en runtime
- **Precondiciones**: Validar entrada de métodos privados
- **Postcondiciones**: Verificar resultados
- **Invariantes**: Mantener consistencia de estado

### Cuándo Usar Cada Una
- **Excepciones**: Condiciones excepcionales que el programa debe manejar
- **Aserciones**: Verificar suposiciones durante desarrollo y testing
- **Validación**: Excepciones para entrada de usuario, aserciones para validación interna