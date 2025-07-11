# Java 8 CompletableFuture - Guía Completa

## Introducción

Java 8 introdujo **CompletableFuture** como una evolución significativa en el manejo de programación asíncrona y concurrencia. Esta clase implementa tanto la interfaz **Future** como **CompletionStage**, proporcionando un enfoque más funcional y flexible para operaciones asíncronas.

## Mejoras en Concurrencia de Java 8

### 1. CompletableFuture
- **Programación reactiva**: Permite encadenar operaciones asíncronas de forma fluida
- **Combinación de futuros**: Posibilidad de combinar múltiples operaciones asíncronas
- **Manejo de excepciones mejorado**: Métodos específicos para capturar y manejar errores
- **Ejecutores personalizados**: Integración con diferentes pools de hilos

### 2. Nuevos Métodos en Stream API
- **parallelStream()**: Procesamiento paralelo automático de colecciones
- **Collectors paralelos**: Operaciones de reducción optimizadas para paralelismo

### 3. Mejoras en ForkJoinPool
- **Common Pool**: Pool compartido por defecto para operaciones paralelas
- **Mejor balanceo de carga**: Distribución más eficiente de tareas

## Comparativa: CompletableFuture vs Opciones Anteriores

### Future (Java 5+)
```java
// Enfoque tradicional con Future
ExecutorService executor = Executors.newFixedThreadPool(2);
Future<String> future = executor.submit(() -> {
    Thread.sleep(1000);
    return "Resultado";
});

// Bloqueo hasta obtener resultado
try {
    String resultado = future.get(); // BLOQUEO
    System.out.println(resultado);
} catch (Exception e) {
    e.printStackTrace();
}
```

**Limitaciones de Future:**
- Solo método `get()` bloqueante
- No se pueden encadenar operaciones
- Manejo básico de excepciones
- No hay callbacks para completación

### CompletableFuture (Java 8+)
```java
// Enfoque moderno con CompletableFuture
CompletableFuture<String> completableFuture = CompletableFuture
    .supplyAsync(() -> {
        try {
            Thread.sleep(1000);
            return "Resultado";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    })
    .thenApply(resultado -> resultado.toUpperCase())
    .thenCompose(resultado -> CompletableFuture.supplyAsync(() -> 
        resultado + " - Procesado"))
    .exceptionally(throwable -> {
        System.err.println("Error: " + throwable.getMessage());
        return "Valor por defecto";
    });

// Procesamiento asíncrono sin bloqueo
completableFuture.thenAccept(resultado -> 
    System.out.println("Resultado final: " + resultado));
```

**Ventajas de CompletableFuture:**
- Operaciones no bloqueantes
- Encadenamiento fluido de operaciones
- Manejo avanzado de excepciones
- Combinación de múltiples futuros
- Callbacks para completación

## Métodos de Obtención de Resultados

### 1. get() - Método Bloqueante
```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(2000);
        return "Resultado después de 2 segundos";
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
});

try {
    // BLOQUEA el hilo actual hasta completar
    String resultado = future.get();
    System.out.println(resultado);
} catch (InterruptedException | ExecutionException e) {
    System.err.println("Error al obtener resultado: " + e.getMessage());
}
```

### 2. get(timeout, unit) - Método Bloqueante con Timeout
```java
try {
    // BLOQUEA máximo 3 segundos
    String resultado = future.get(3, TimeUnit.SECONDS);
    System.out.println(resultado);
} catch (TimeoutException e) {
    System.err.println("Timeout: Operación tardó más de 3 segundos");
} catch (InterruptedException | ExecutionException e) {
    System.err.println("Error: " + e.getMessage());
}
```

### 3. getNow() - Método No Bloqueante
```java
// NO BLOQUEA - Retorna valor inmediatamente
String resultado = future.getNow("Valor por defecto");
System.out.println(resultado); // "Valor por defecto" si no está completo
```

### 4. join() - Método Bloqueante (sin checked exceptions)
```java
try {
    // BLOQUEA pero no lanza checked exceptions
    String resultado = future.join();
    System.out.println(resultado);
} catch (CompletionException e) {
    System.err.println("Error: " + e.getCause().getMessage());
}
```

## Manejo de Excepciones en CompletableFuture

### 1. exceptionally() - Manejo de Excepciones
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        if (Math.random() > 0.5) {
            throw new RuntimeException("Error simulado");
        }
        return "Éxito";
    })
    .exceptionally(throwable -> {
        System.err.println("Capturada excepción: " + throwable.getMessage());
        return "Valor de recuperación";
    });

future.thenAccept(System.out::println);
```

### 2. handle() - Manejo de Resultado y Excepción
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        if (Math.random() > 0.5) {
            throw new RuntimeException("Error");
        }
        return "Éxito";
    })
    .handle((resultado, excepcion) -> {
        if (excepcion != null) {
            System.err.println("Error: " + excepcion.getMessage());
            return "Manejado por handle()";
        }
        return resultado + " - Procesado correctamente";
    });
```

### 3. whenComplete() - Callback Post-Procesamiento
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> "Resultado")
    .whenComplete((resultado, excepcion) -> {
        if (excepcion != null) {
            System.err.println("Error durante ejecución: " + excepcion.getMessage());
        } else {
            System.out.println("Completado con éxito: " + resultado);
        }
    });
```

## Liberación de Recursos - Patrón "Finally"

### 1. Usando whenComplete() como Finally
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        System.out.println("Adquiriendo recurso...");
        // Simulación de trabajo con recurso
        return "Trabajo completado";
    })
    .whenComplete((resultado, excepcion) -> {
        // Este bloque SIEMPRE se ejecuta (como finally)
        System.out.println("Liberando recursos...");
        // Cerrar conexiones, archivos, etc.
    });
```

### 2. Patrón Try-With-Resources Simulado
```java
public static CompletableFuture<String> operacionConRecursos() {
    return CompletableFuture.supplyAsync(() -> {
        Resource recurso = null;
        try {
            recurso = new Resource();
            // Operación que puede fallar
            return recurso.procesar();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (recurso != null) {
                recurso.cerrar();
            }
        }
    });
}
```

### 3. Composición con Limpieza Automática
```java
public static CompletableFuture<String> operacionCompleta() {
    return CompletableFuture
        .supplyAsync(() -> {
            System.out.println("Iniciando operación...");
            return "datos";
        })
        .thenApplyAsync(datos -> {
            System.out.println("Procesando: " + datos);
            return datos.toUpperCase();
        })
        .exceptionally(throwable -> {
            System.err.println("Error en procesamiento: " + throwable.getMessage());
            return "ERROR";
        })
        .whenComplete((resultado, excepcion) -> {
            // Limpieza que siempre se ejecuta
            System.out.println("Limpieza de recursos completada");
        });
}
```

## Ejemplos Prácticos Comparativos

### Ejemplo 1: Procesamiento Secuencial vs Asíncrono

#### Enfoque Tradicional (Síncrono)
```java
public String procesamientoSecuencial() {
    try {
        String datos = obtenerDatos(); // 1 segundo
        String procesados = procesarDatos(datos); // 2 segundos
        String resultado = guardarDatos(procesados); // 1 segundo
        return resultado; // Total: 4 segundos
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        liberarRecursos();
    }
}
```

#### Enfoque CompletableFuture (Asíncrono)
```java
public CompletableFuture<String> procesamientoAsincrono() {
    return CompletableFuture
        .supplyAsync(this::obtenerDatos)
        .thenApplyAsync(this::procesarDatos)
        .thenApplyAsync(this::guardarDatos)
        .exceptionally(throwable -> {
            System.err.println("Error: " + throwable.getMessage());
            return "ERROR";
        })
        .whenComplete((resultado, excepcion) -> liberarRecursos());
}
```

### Ejemplo 2: Combinación de Múltiples Operaciones

```java
public CompletableFuture<String> operacionesCombinadas() {
    CompletableFuture<String> usuario = CompletableFuture.supplyAsync(() -> 
        obtenerUsuario());
    
    CompletableFuture<String> configuracion = CompletableFuture.supplyAsync(() -> 
        obtenerConfiguracion());
    
    return usuario.thenCombine(configuracion, (u, c) -> {
        return procesarUsuarioConConfiguracion(u, c);
    }).exceptionally(throwable -> {
        System.err.println("Error en operación combinada: " + throwable.getMessage());
        return "Configuración por defecto";
    }).whenComplete((resultado, excepcion) -> {
        System.out.println("Operación combinada completada");
    });
}
```

## Mejores Prácticas

### 1. Gestión de Pools de Hilos
```java
// Crear pool personalizado
ExecutorService customExecutor = Executors.newFixedThreadPool(4);

CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> "trabajo", customExecutor)
    .thenApplyAsync(resultado -> resultado.toUpperCase(), customExecutor)
    .whenComplete((resultado, excepcion) -> {
        customExecutor.shutdown(); // Importante: cerrar el executor
    });
```

### 2. Evitar Bloqueos Innecesarios
```java
// ❌ MAL - Bloqueo innecesario
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "datos");
String resultado = future.get(); // BLOQUEO

// ✅ BIEN - Procesamiento asíncrono
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> "datos")
    .thenAccept(resultado -> System.out.println(resultado)); // SIN BLOQUEO
```

### 3. Manejo Robusto de Excepciones
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        // Operación que puede fallar
        return operacionRiesgosa();
    })
    .handle((resultado, excepcion) -> {
        if (excepcion != null) {
            // Log del error
            logger.error("Error en operación", excepcion);
            // Retornar valor por defecto
            return "Valor seguro";
        }
        return resultado;
    })
    .whenComplete((resultado, excepcion) -> {
        // Siempre ejecutar limpieza
        limpiarRecursos();
    });
```

## Resumen de Ventajas

### CompletableFuture vs Future
| Característica | Future | CompletableFuture |
|---------------|---------|-------------------|
| **Obtención de resultado** | Solo `get()` bloqueante | `get()`, `getNow()`, `join()` |
| **Encadenamiento** | ❌ No | ✅ Sí (`thenApply`, `thenCompose`) |
| **Combinación** | ❌ No | ✅ Sí (`thenCombine`, `allOf`, `anyOf`) |
| **Manejo de excepciones** | Try-catch básico | `exceptionally()`, `handle()` |
| **Callbacks** | ❌ No | ✅ Sí (`thenAccept`, `thenRun`) |
| **Liberación de recursos** | Finally manual | `whenComplete()` |
| **Composición** | ❌ No | ✅ Sí (múltiples operadores) |

### Cuándo Usar Cada Método
- **`get()`**: Cuando necesitas bloquear y esperar el resultado
- **`getNow()`**: Cuando quieres verificar si está completo sin bloquear
- **`join()`**: Similar a `get()` pero sin checked exceptions
- **`thenAccept()`**: Para procesamiento asíncrono sin bloqueo
- **`exceptionally()`**: Para manejo específico de errores
- **`whenComplete()`**: Para limpieza de recursos (equivalente a finally)

CompletableFuture representa un salto evolutivo significativo en la programación asíncrona de Java, ofreciendo un modelo más funcional, componible y expresivo para manejar operaciones concurrentes.