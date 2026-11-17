# Guía de uso

## 1. Qué hace el programa hoy

El estado actual es un **esqueleto**. El programa:

- compila correctamente;
- permite obtener la instancia única de `ControlAcceso` mediante `getInstance()`;
- permite crear objetos `Empleado` con sus cinco datos (incluido el nivel);
- tiene las clases `Sector`, `Permiso`, `RegistroAcceso` y el enum `NivelAcceso`, pero
  sin lógica de negocio.

Todavía **no** da de alta empleados en el sistema, no registra accesos y su `main` no
imprime nada. Las secciones siguientes describen el uso previsto y marcan qué falta.

## 2. Uso del Singleton

```java
ControlAcceso control = ControlAcceso.getInstance();
```

- Es la única forma válida de obtener el gestor.
- Llamarlo dos veces devuelve exactamente el mismo objeto.
- `new ControlAcceso()` **no compila**: el constructor es privado, y eso es intencional.

## 3. Uso de `Empleado`

```java
Empleado empleado = new Empleado(1, 30111222, "Ana", "Gómez", NivelAcceso.NIVEL_2);

System.out.println(empleado.getNombre());         // Ana
System.out.println(empleado.getApellido());       // Gómez
System.out.println(empleado.getDni());            // 30111222
System.out.println(empleado.getIdentificacion()); // 1
System.out.println(empleado.getNivelAcesso());    // NIVEL_2
```

## 4. Flujo previsto (pendiente de implementación)

| Paso | Acción | Estado |
|------|--------|--------|
| 1 | Obtener `ControlAcceso.getInstance()` | ✅ disponible |
| 2 | Crear el `Empleado` | ✅ disponible |
| 3 | Darlo de alta en el gestor | ⚠️ `registrarEmpleado()` existe, sin lógica |
| 4 | Definir `Sector` y `Permiso` | ⚠️ clases creadas, sin setters ni constructores con datos |
| 5 | Registrar un acceso | ❌ falta el método; `RegistroAcceso` no asigna fecha |
| 6 | Consultar los registros | ⚠️ `consultarHistorial()` existe, devuelve `null` |

## 5. Ejecutar desde la terminal

```powershell
javac -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

Hoy la ejecución termina sin imprimir nada, porque `main` está vacío.

## 6. Errores frecuentes

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `cannot find symbol: ControlAcceso` | `src` no está marcada como Sources Root | Clic derecho en `src` → Mark Directory as → Sources Root |
| `Project SDK is not defined` | No hay JDK asignado | File → Project Structure → SDK |
| `class file has wrong version` | Compilado con un JDK distinto al de ejecución | Recompilar con el JDK del proyecto |
| `new ControlAcceso()` no compila | El constructor es privado (correcto) | Usar `ControlAcceso.getInstance()` |
| Se abre el proyecto como "archivo suelto" | Se abrió un `.java` en vez de la carpeta | File → Open → carpeta raíz |

---

[Volver al índice](../README.md) · [← Anterior: Instalación del entorno](instalacion-entorno.md)
