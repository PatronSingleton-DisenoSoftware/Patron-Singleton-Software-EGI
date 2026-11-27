# Guía de uso

## 1. Qué hace el programa hoy

El sistema está **completo y funcional**. El programa:

- compila sin errores ni advertencias con `javac -Xlint:all`;
- permite obtener la instancia única de `ControlAcceso` mediante `getInstance()`;
- permite crear objetos `Empleado` con sus seis datos (incluidos el nivel y la validez calculada);
- tiene las clases `Sector`, `Permiso`, `RegistroAcceso` y el enum `NivelAcceso`, con su lógica y atributos encapsulados;
- implementa la verificación de accesos, la gestión de permisos y la auditoría de intentos centralizada;
- incluye una demostración automática del patrón que puede ejecutarse varias veces sin duplicar datos.

El punto de entrada `src/Main.java` contiene un menú interactivo por consola para operar
el gestor Singleton.

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
System.out.println(empleado.getIdEmpleado()); // 1
System.out.println(empleado.getNivelAcceso());    // NIVEL_2 (Acceso Interno)
System.out.println(empleado.isRegistroValido());  // true

// La validez no se puede forzar si los datos no acompañan:
Empleado invalido = new Empleado(0, 123, "", null, null);
System.out.println(invalido.isRegistroValido());  // false
```

## 4. Menú del programa

| Opción | Acción |
|--------|--------|
| 1 | Agregar empleado (valida ID, DNI, nombre, apellido y nivel) |
| 2 | Agregar sector (valida ID, nombre y nivel requerido) |
| 3 | Listar empleados |
| 4 | Listar sectores |
| 5 | Conceder permiso especial |
| 6 | Revocar permiso especial |
| 7 | Simular punto de acceso (`verificarAcceso`) |
| 8 | Consultar historial de accesos |
| 9 | Ejecutar demostración automática completa |
| 10 | Asignar nivel de acceso a un empleado ya registrado |
| 0 | Salir |

## 5. Flujo interno (implementación)

| Paso | Acción | Estado |
|------|--------|--------|
| 1 | Obtener `ControlAcceso.getInstance()` | ✅ disponible |
| 2 | Crear el `Empleado` | ✅ constructor con validación |
| 3 | Darlo de alta en el gestor | ✅ `registrarEmpleado()` valida datos y duplicados por ID y DNI |
| 4 | Definir `Sector` y `Permiso` | ✅ constructores, getters y setters con validación |
| 5 | Registrar un acceso | ✅ `verificarAcceso()` valida y genera `RegistroAcceso` con fecha/hora actual |
| 6 | Consultar los registros | ✅ `consultarHistorial()` devuelve copia de `List<RegistroAcceso>` |

## 6. Ejecutar desde la terminal

```powershell
javac -Xlint:all -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

La opción 9 ejecuta la demostración automática: compara dos llamadas a `getInstance()`,
registra datos de prueba solo si no existen y recorre los casos de nivel, permiso especial,
revocación e historial. Puede ejecutarse más de una vez sin duplicar datos.

La opción 10 permite cambiar el nivel de un empleado ya registrado: pide su ID, muestra el
nivel actual y solicita el nuevo (1 a 5). El cambio se aplica sobre la misma instancia y se
ve en la próxima verificación de acceso.

## 7. Errores frecuentes

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `cannot find symbol: ControlAcceso` | `src` no está marcada como Sources Root | Clic derecho en `src` → Mark Directory as → Sources Root |
| `Project SDK is not defined` | No hay JDK asignado | File → Project Structure → SDK |
| `class file has wrong version` | Compilado con un JDK distinto al de ejecución | Recompilar con el JDK del proyecto |
| `new ControlAcceso()` no compila | El constructor es privado (correcto) | Usar `ControlAcceso.getInstance()` |
| Se abre el proyecto como "archivo suelto" | Se abrió un `.java` en vez de la carpeta | File → Open → carpeta raíz |

---

[Volver al índice](../README.md) · [← Anterior: Instalación del entorno](instalacion-entorno.md)
