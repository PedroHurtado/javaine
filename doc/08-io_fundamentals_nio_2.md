# I/O en Java (Fundamentos y NIO.2)

## 1. Introducción a la Entrada/Salida (I/O) en Java

La Entrada/Salida (Input/Output o I/O) en Java se refiere al mecanismo mediante el cual un programa puede leer datos desde fuentes externas (como archivos, teclado, red) o escribir datos a destinos (como archivos, consola, red).

Java proporciona dos principales APIs para la gestión de I/O:

- La API tradicional de I/O (`java.io`)
- La API de NIO (New Input/Output), mejorada en Java 7 con NIO.2 (`java.nio.file`)

## 2. Fundamentos de `java.io`

### 2.1 Flujos (Streams)

Los flujos son la base de `java.io` y se dividen en dos grandes categorías:

- **Flujos de bytes:** para manejar datos binarios.
  - `InputStream` y `OutputStream` son las clases base.
- **Flujos de caracteres:** para manejar datos de texto.
  - `Reader` y `Writer` son las clases base.

### 2.2 Ejemplo de lectura/escritura con `java.io`

```java
import java.io.*;

public class EjemploIO {
    public static void main(String[] args) throws IOException {
        // Escritura
        FileWriter writer = new FileWriter("ejemplo.txt");
        writer.write("Hola Mundo\n");
        writer.close();

        // Lectura
        FileReader reader = new FileReader("ejemplo.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);

        String linea;
        while ((linea = bufferedReader.readLine()) != null) {
            System.out.println(linea);
        }

        bufferedReader.close();
    }
}
```

## 3. NIO y NIO.2 (`java.nio`, `java.nio.file`)

NIO.2 introducido en Java 7 mejora considerablemente el manejo de archivos y directorios:

### 3.1 `Path` y `Files`

- `Path`: representa una ruta en el sistema de archivos.
- `Files`: utilidades para manipular archivos y directorios.

```java
import java.nio.file.*;
import java.io.IOException;

public class EjemploNIO2 {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("ejemplo.txt");

        // Escritura
        Files.write(path, "Contenido con NIO2".getBytes());

        // Lectura
        String contenido = Files.readString(path);
        System.out.println(contenido);
    }
}
```

### 3.2 Ventajas de NIO.2

- Soporte para enlaces simbólicos.
- Mejor manejo de errores.
- Operaciones atómicas.
- Soporte para operaciones asincrónicas (con `AsynchronousFileChannel`).

## 4. NIO vs I/O Tradicional

| Característica               | java.io  | java.nio.file (NIO.2)   |
| ---------------------------- | -------- | ----------------------- |
| Basado en flujos             | Sí       | No (basado en buffers)  |
| Bloqueante                   | Sí       | Puede ser no bloqueante |
| Lectura/escritura más simple | Sí       | Sí (con `Files`)        |
| Soporte asincrónico          | No       | Sí                      |
| Acceso directo a archivos    | Limitado | Avanzado                |

## 5. Conclusión

La API `java.io` es suficiente para operaciones básicas de I/O, pero si se necesita mayor control, mejor rendimiento o características avanzadas, `java.nio.file` (NIO.2) es la opción recomendada.

---

## Recursos adicionales

- [Documentación oficial de ](https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html)[`java.io`](https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html)
- [Documentación oficial de ](https://docs.oracle.com/javase/8/docs/api/java/nio/file/package-summary.html)[`java.nio.file`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/package-summary.html)

