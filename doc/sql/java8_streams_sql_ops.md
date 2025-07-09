# Operaciones SQL con Java 8 Streams

## Índice
1. [Configuración Inicial](#configuración-inicial)
2. [Inner Join](#inner-join)
3. [Left Join](#left-join)
4. [Order By (ASC y DESC)](#order-by-asc-y-desc)
5. [Group By](#group-by)
6. [Ejemplos Combinados](#ejemplos-combinados)

---

## Configuración Inicial

### Clases de Ejemplo

```java
// Clase Usuario
public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private Integer edad;
    
    // Constructores, getters y setters
    public Usuario(Long id, String nombre, String email, Integer edad) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
    }
    
    // getters y toString()
}

// Clase Pedido
public class Pedido {
    private Long id;
    private Long usuarioId;
    private String producto;
    private Double precio;
    private LocalDate fecha;
    
    public Pedido(Long id, Long usuarioId, String producto, Double precio, LocalDate fecha) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.producto = producto;
        this.precio = precio;
        this.fecha = fecha;
    }
    
    // getters y toString()
}

// Clase para resultados de join
public class UsuarioPedido {
    private Usuario usuario;
    private Pedido pedido;
    
    public UsuarioPedido(Usuario usuario, Pedido pedido) {
        this.usuario = usuario;
        this.pedido = pedido;
    }
    
    // getters y toString()
}
```

### Datos de Prueba

```java
List<Usuario> usuarios = Arrays.asList(
    new Usuario(1L, "Ana García", "ana@email.com", 25),
    new Usuario(2L, "Carlos López", "carlos@email.com", 30),
    new Usuario(3L, "María Rodríguez", "maria@email.com", 28),
    new Usuario(4L, "Juan Pérez", "juan@email.com", 35),
    new Usuario(5L, "Lucía Martín", "lucia@email.com", 22)
);

List<Pedido> pedidos = Arrays.asList(
    new Pedido(1L, 1L, "Laptop", 999.99, LocalDate.of(2024, 1, 15)),
    new Pedido(2L, 2L, "Mouse", 25.50, LocalDate.of(2024, 1, 16)),
    new Pedido(3L, 1L, "Teclado", 75.00, LocalDate.of(2024, 1, 17)),
    new Pedido(4L, 3L, "Monitor", 299.99, LocalDate.of(2024, 1, 18)),
    new Pedido(5L, 2L, "Audífonos", 89.99, LocalDate.of(2024, 1, 19)),
    new Pedido(6L, 6L, "Webcam", 59.99, LocalDate.of(2024, 1, 20)) // Usuario inexistente
);
```

---

## Inner Join

### Implementación con Streams

```java
// INNER JOIN: Solo registros que tienen coincidencia en ambas tablas
public List<UsuarioPedido> innerJoin(List<Usuario> usuarios, List<Pedido> pedidos) {
    return usuarios.stream()
        .flatMap(usuario -> pedidos.stream()
            .filter(pedido -> pedido.getUsuarioId().equals(usuario.getId()))
            .map(pedido -> new UsuarioPedido(usuario, pedido))
        )
        .collect(Collectors.toList());
}

// Uso
List<UsuarioPedido> resultado = innerJoin(usuarios, pedidos);
resultado.forEach(System.out::println);
```

### Equivalente SQL
```sql
SELECT u.*, p.* 
FROM usuarios u 
INNER JOIN pedidos p ON u.id = p.usuario_id
```

### Resultado Esperado
```
UsuarioPedido{usuario=Ana García, pedido=Laptop $999.99}
UsuarioPedido{usuario=Ana García, pedido=Teclado $75.00}
UsuarioPedido{usuario=Carlos López, pedido=Mouse $25.50}
UsuarioPedido{usuario=Carlos López, pedido=Audífonos $89.99}
UsuarioPedido{usuario=María Rodríguez, pedido=Monitor $299.99}
```

---

## Left Join

### Implementación con Streams

```java
// LEFT JOIN: Todos los registros de la tabla izquierda + coincidencias de la derecha
public List<UsuarioPedido> leftJoin(List<Usuario> usuarios, List<Pedido> pedidos) {
    return usuarios.stream()
        .flatMap(usuario -> {
            List<Pedido> pedidosUsuario = pedidos.stream()
                .filter(pedido -> pedido.getUsuarioId().equals(usuario.getId()))
                .collect(Collectors.toList());
            
            // Si no hay pedidos, crear entrada con pedido null
            if (pedidosUsuario.isEmpty()) {
                return Stream.of(new UsuarioPedido(usuario, null));
            }
            
            // Si hay pedidos, crear entrada para cada uno
            return pedidosUsuario.stream()
                .map(pedido -> new UsuarioPedido(usuario, pedido));
        })
        .collect(Collectors.toList());
}

// Uso
List<UsuarioPedido> resultado = leftJoin(usuarios, pedidos);
resultado.forEach(System.out::println);
```

### Equivalente SQL
```sql
SELECT u.*, p.* 
FROM usuarios u 
LEFT JOIN pedidos p ON u.id = p.usuario_id
```

### Resultado Esperado
```
UsuarioPedido{usuario=Ana García, pedido=Laptop $999.99}
UsuarioPedido{usuario=Ana García, pedido=Teclado $75.00}
UsuarioPedido{usuario=Carlos López, pedido=Mouse $25.50}
UsuarioPedido{usuario=Carlos López, pedido=Audífonos $89.99}
UsuarioPedido{usuario=María Rodríguez, pedido=Monitor $299.99}
UsuarioPedido{usuario=Juan Pérez, pedido=null}
UsuarioPedido{usuario=Lucía Martín, pedido=null}
```

---

## Order By (ASC y DESC)

### Ordenamiento Simple

```java
// ORDER BY edad ASC
List<Usuario> porEdadAsc = usuarios.stream()
    .sorted(Comparator.comparing(Usuario::getEdad))
    .collect(Collectors.toList());

// ORDER BY edad DESC
List<Usuario> porEdadDesc = usuarios.stream()
    .sorted(Comparator.comparing(Usuario::getEdad).reversed())
    .collect(Collectors.toList());

// ORDER BY nombre ASC
List<Usuario> porNombreAsc = usuarios.stream()
    .sorted(Comparator.comparing(Usuario::getNombre))
    .collect(Collectors.toList());
```

### Ordenamiento Múltiple

```java
// ORDER BY edad ASC, nombre DESC
List<Usuario> ordenMultiple = usuarios.stream()
    .sorted(Comparator.comparing(Usuario::getEdad)
        .thenComparing(Usuario::getNombre, Comparator.reverseOrder()))
    .collect(Collectors.toList());

// ORDER BY edad DESC, nombre ASC
List<Usuario> ordenMultiple2 = usuarios.stream()
    .sorted(Comparator.comparing(Usuario::getEdad).reversed()
        .thenComparing(Usuario::getNombre))
    .collect(Collectors.toList());
```

### Ordenamiento con Campos Nulos

```java
// ORDER BY email ASC NULLS LAST
List<Usuario> conNulos = usuarios.stream()
    .sorted(Comparator.comparing(Usuario::getEmail, 
        Comparator.nullsLast(Comparator.naturalOrder())))
    .collect(Collectors.toList());
```

### Equivalente SQL
```sql
-- Simple
SELECT * FROM usuarios ORDER BY edad ASC;
SELECT * FROM usuarios ORDER BY edad DESC;

-- Múltiple
SELECT * FROM usuarios ORDER BY edad ASC, nombre DESC;
SELECT * FROM usuarios ORDER BY edad DESC, nombre ASC;
```

---

## Group By

### Agrupación Simple

```java
// GROUP BY edad
Map<Integer, List<Usuario>> porEdad = usuarios.stream()
    .collect(Collectors.groupingBy(Usuario::getEdad));

// GROUP BY con conteo: COUNT(*)
Map<Integer, Long> conteoporEdad = usuarios.stream()
    .collect(Collectors.groupingBy(Usuario::getEdad, Collectors.counting()));

// GROUP BY con suma: SUM(edad)
Map<Integer, Integer> sumaPorEdad = usuarios.stream()
    .collect(Collectors.groupingBy(Usuario::getEdad, 
        Collectors.summingInt(Usuario::getEdad)));
```

### Agrupación con Múltiples Campos

```java
// GROUP BY edad, substring(nombre, 1, 1) - Agrupar por edad y primera letra del nombre
Map<String, List<Usuario>> porEdadYLetra = usuarios.stream()
    .collect(Collectors.groupingBy(usuario -> 
        usuario.getEdad() + "-" + usuario.getNombre().substring(0, 1)));
```

### Agrupación con Operaciones Complejas

```java
// GROUP BY edad HAVING COUNT(*) > 1
Map<Integer, List<Usuario>> gruposGrandes = usuarios.stream()
    .collect(Collectors.groupingBy(Usuario::getEdad))
    .entrySet().stream()
    .filter(entry -> entry.getValue().size() > 1)
    .collect(Collectors.toMap(
        Map.Entry::getKey, 
        Map.Entry::getValue
    ));

// GROUP BY con estadísticas
Map<Integer, IntSummaryStatistics> estadisticasPorEdad = usuarios.stream()
    .collect(Collectors.groupingBy(Usuario::getEdad,
        Collectors.summarizingInt(Usuario::getEdad)));
```

### Agrupación de Pedidos

```java
// GROUP BY usuario_id con suma de precios
Map<Long, Double> totalPorUsuario = pedidos.stream()
    .collect(Collectors.groupingBy(Pedido::getUsuarioId,
        Collectors.summingDouble(Pedido::getPrecio)));

// GROUP BY usuario_id con promedio de precios
Map<Long, Double> promedioPorUsuario = pedidos.stream()
    .collect(Collectors.groupingBy(Pedido::getUsuarioId,
        Collectors.averagingDouble(Pedido::getPrecio)));

// GROUP BY mes con conteo de pedidos
Map<Month, Long> pedidosPorMes = pedidos.stream()
    .collect(Collectors.groupingBy(pedido -> pedido.getFecha().getMonth(),
        Collectors.counting()));
```

### Equivalente SQL
```sql
-- Simple
SELECT edad, COUNT(*) FROM usuarios GROUP BY edad;
SELECT edad, SUM(edad) FROM usuarios GROUP BY edad;

-- Con HAVING
SELECT edad, COUNT(*) FROM usuarios GROUP BY edad HAVING COUNT(*) > 1;

-- Pedidos
SELECT usuario_id, SUM(precio) FROM pedidos GROUP BY usuario_id;
SELECT usuario_id, AVG(precio) FROM pedidos GROUP BY usuario_id;
```

---

## Ejemplos Combinados

### Inner Join + Group By + Order By

```java
// Obtener total de compras por usuario, ordenado por total DESC
public Map<String, Double> totalComprasPorUsuarioOrdenado(List<Usuario> usuarios, List<Pedido> pedidos) {
    return usuarios.stream()
        .flatMap(usuario -> pedidos.stream()
            .filter(pedido -> pedido.getUsuarioId().equals(usuario.getId()))
            .map(pedido -> new UsuarioPedido(usuario, pedido))
        )
        .collect(Collectors.groupingBy(
            up -> up.getUsuario().getNombre(),
            Collectors.summingDouble(up -> up.getPedido().getPrecio())
        ))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
}
```

### Left Join + Filtro + Ordenamiento

```java
// Usuarios con o sin pedidos, filtrar por edad > 25, ordenar por nombre
public List<UsuarioPedido> usuariosConPedidosFiltrados(List<Usuario> usuarios, List<Pedido> pedidos) {
    return usuarios.stream()
        .filter(usuario -> usuario.getEdad() > 25)
        .flatMap(usuario -> {
            List<Pedido> pedidosUsuario = pedidos.stream()
                .filter(pedido -> pedido.getUsuarioId().equals(usuario.getId()))
                .collect(Collectors.toList());
            
            if (pedidosUsuario.isEmpty()) {
                return Stream.of(new UsuarioPedido(usuario, null));
            }
            
            return pedidosUsuario.stream()
                .map(pedido -> new UsuarioPedido(usuario, pedido));
        })
        .sorted(Comparator.comparing(up -> up.getUsuario().getNombre()))
        .collect(Collectors.toList());
}
```

### Reporte Completo con Múltiples Operaciones

```java
// Reporte: Usuarios con sus totales de compra, ordenados por total DESC
public void generarReporte(List<Usuario> usuarios, List<Pedido> pedidos) {
    Map<Usuario, Double> reporteUsuarios = usuarios.stream()
        .collect(Collectors.toMap(
            usuario -> usuario,
            usuario -> pedidos.stream()
                .filter(pedido -> pedido.getUsuarioId().equals(usuario.getId()))
                .mapToDouble(Pedido::getPrecio)
                .sum()
        ))
        .entrySet().stream()
        .sorted(Map.Entry.<Usuario, Double>comparingByValue().reversed())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
    
    // Mostrar reporte
    System.out.println("=== REPORTE DE COMPRAS POR USUARIO ===");
    reporteUsuarios.forEach((usuario, total) -> {
        System.out.printf("%-20s | Edad: %2d | Total: $%.2f%n", 
            usuario.getNombre(), usuario.getEdad(), total);
    });
}
```

### Equivalente SQL del Reporte
```sql
SELECT u.nombre, u.edad, COALESCE(SUM(p.precio), 0) as total
FROM usuarios u
LEFT JOIN pedidos p ON u.id = p.usuario_id
GROUP BY u.id, u.nombre, u.edad
ORDER BY total DESC;
```

---

## Consejos y Buenas Prácticas

### Rendimiento
- Para datasets grandes, considera usar `parallelStream()` en lugar de `stream()`
- Los joins con streams pueden ser menos eficientes que usar `Map` para lookups
- Para operaciones complejas, considera crear índices temporales con `Map`

### Legibilidad
- Divide operaciones complejas en métodos más pequeños
- Usa nombres descriptivos para las variables intermedias
- Combina múltiples operaciones en una sola pipeline cuando sea posible

### Alternativa Optimizada para Joins
```java
// Crear índice para joins más eficientes
Map<Long, Usuario> usuarioIndex = usuarios.stream()
    .collect(Collectors.toMap(Usuario::getId, Function.identity()));

// Inner join optimizado
List<UsuarioPedido> innerJoinOptimizado = pedidos.stream()
    .filter(pedido -> usuarioIndex.containsKey(pedido.getUsuarioId()))
    .map(pedido -> new UsuarioPedido(usuarioIndex.get(pedido.getUsuarioId()), pedido))
    .collect(Collectors.toList());
```

Esta guía te proporciona las herramientas necesarias para realizar operaciones similares a SQL usando Java 8 Streams de manera eficiente y legible.