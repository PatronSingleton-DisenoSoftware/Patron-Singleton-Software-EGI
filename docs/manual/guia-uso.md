# Guía de uso

## 1. Qué hace el programa hoy

El estado actual es un **esqueleto funcional**. El programa:

- compila correctamente;
- permite obtener la instancia única de `ControlAcceso` mediante `getInstance()`;
- permite crear objetos `Empleado` con sus cinco datos (incluido el nivel);
- tiene las clases `Sector`, `Permiso`, `RegistroAcceso` y el enum `NivelAcceso`, con su lógica y atributos encapsulados;
- implementa la verificación de accesos y la auditoría de intentos centralizada.

El punto de entrada `src/Main.java` contiene un menú interactivo por consola para registrar empleados en el gestor Singleton.

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

## 4. Flujo previsto (implementación)

| Paso | Acción | Estado |
|------|--------|--------|
| 1 | Obtener `ControlAcceso.getInstance()` | ✅ disponible |
| 2 | Crear el `Empleado` | ✅ disponible |
| 3 | Darlo de alta en el gestor | ✅ `registrarEmpleado()` implementado |
| 4 | Definir `Sector` y `Permiso` | ✅ constructores, getters y setters disponibles |
| 5 | Registrar un acceso | ✅ `verificarAcceso()` valida y genera `RegistroAcceso` con fecha/hora actual |
| 6 | Consultar los registros | ✅ `consultarHistorial()` devuelve copia de `List<RegistroAcceso>` |

## 5. Ejecutar desde la terminal

```powershell
javac -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

Permite registrar empleados interactivamente desde la consola.

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
