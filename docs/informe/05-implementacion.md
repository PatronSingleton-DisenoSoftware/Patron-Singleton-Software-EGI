# 5. Implementación

> Esta sección describe el **código real** del repositorio. El diseño ya está cerrado
> (ver [Diseño](04-diseno.md) y [Decisiones](09-decisiones.md)), pero el código todavía no
> lo implementa en su totalidad. Todo lo que falta se marca `Pendiente`.

## 5.1. Estructura del código

```
src/
├── ControlAcceso.java    ← Singleton (esqueleto)
├── Empleado.java         ← 4 de 5 atributos
├── Main.java             ← vacío
├── Permiso.java          ← vacía
├── RegistroAcceso.java   ← vacía
├── Sector.java           ← vacía
├── NivelAcceso.java      ← no existe
└── TipoPermiso.java      ← no existe
```

Cada clase vive en su propio archivo y el nombre del archivo coincide con el de la clase,
como exige [`Reglas-Proyecto.md`](../../Reglas-Proyecto.md).

## 5.2. El Singleton: `ControlAcceso`

Código actual (`src/ControlAcceso.java`):

```java
public class ControlAcceso {
    // Atributos
    private static ControlAcceso instancia;
    // Constructor de acceso unico
    private ControlAcceso(){
        // Logica del constructor
    }
    // Getters y Setters

// Metodo de acceso unico
    public static ControlAcceso getInstancia() {
        if (instancia == null){
            instancia = new ControlAcceso();
        }
        return instancia;
    }
}
```

### Anatomía, línea por línea

| Elemento | Por qué |
|----------|---------|
| `private static ControlAcceso instancia;` | `static`: el dato pertenece a la clase, hay un solo casillero para todos. `private`: nadie fuera de la clase puede reemplazarlo. |
| `private ControlAcceso()` | Bloquea `new ControlAcceso()` desde cualquier otra clase. |
| `public static ControlAcceso getInstancia()` | Único camino permitido para obtener el objeto. `static` porque se invoca sobre la clase, no sobre una instancia existente. |
| `if (instancia == null)` | Creación perezosa (*lazy*): solo se construye la primera vez. |

### Traza de ejecución

```
1ª llamada: instancia == null  ->  se crea       ->  devuelve referencia A
2ª llamada: instancia != null  ->  no se crea    ->  devuelve la misma referencia A
```

Consecuencia: `ControlAcceso.getInstancia() == ControlAcceso.getInstancia()` es `true`.

### Qué falta en esta clase

- Atributos `empleados`, `sectores`, `permisos` y `registros` (`Pendiente`).
- Todo el bloque de métodos de gestión (`Pendiente`, ver §5.6).

## 5.3. `Empleado`

Código actual (`src/Empleado.java`):

```java
public class Empleado {
    private int identificacion;
    private int dni;
    private String nombre;
    private String apellido;

    public Empleado(int identificacion, int dni, String nombre, String apellido){
        this.identificacion = identificacion;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public int getDni() {
        return dni;
    }

    public int getIdentificacion() {
        return identificacion;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

}
```

Observaciones:

- Todos los atributos son `private`, con nombres descriptivos en minúscula.
- El constructor recibe los datos y los asigna con `this.` para desambiguar
  parámetro/variable de instancia.
- **Falta `nivelAcceso`**; según [ADR-01](09-decisiones.md#adr-01-nivelacceso-como-enum)
  será de tipo `NivelAcceso`, no `int` (`Pendiente`).
- **Faltan los setters**, exigidos por la regla "Acceso a lectura y escritura de
  atributos mediante modificadores de acceso" (`Pendiente`).
- No hay `toString()`, lo que dificulta mostrar los datos por consola en las pruebas
  (`Pendiente`). Se propone un formato legible: `Gómez, Ana (DNI 30111222) - RESTRINGIDO`.

## 5.4. Clases pendientes

`Sector`, `Permiso` y `RegistroAcceso` existen solo como declaraciones vacías:

```java
public class Sector {

}
```

`Main` tampoco tiene lógica:

```java
public class Main {
    public static void main(String[] args) {

    }
}
```

Además faltan por crear los enums `NivelAcceso` y `TipoPermiso`.

## 5.5. Brecha entre diseño e implementación

| Elemento del diseño | Código | Estado |
|---------------------|--------|--------|
| `ControlAcceso.instancia` | ✅ | Implementado |
| Constructor privado | ✅ | Implementado |
| `getInstancia()` | ✅ | Implementado |
| `empleados` / `sectores` / `permisos` / `registros` | ❌ | Faltan |
| `Empleado` (4 atributos, constructor, getters) | ✅ | Implementado |
| `Empleado.nivelAcceso: NivelAcceso` | ❌ | Falta |
| `Empleado` setters y `toString()` | ❌ | Faltan |
| `Sector` (`identificacion`, `nombre`, `nivelRequerido`) | ❌ | Vacía |
| `Permiso` (`empleado`, `sector`, `activo`, `tipo`) | ❌ | Vacía |
| `RegistroAcceso` (`fechaHora: LocalDateTime`, `autorizado`, `motivo`) | ❌ | Vacía |
| `NivelAcceso` (enum) | ❌ | No existe |
| `TipoPermiso` (enum) | ❌ | No existe |
| `Main` de demostración | ❌ | Vacío |

## 5.6. Plan de métodos de `ControlAcceso`

> Estado: **propuesto**, no implementado. Es el orden de trabajo sugerido: cada bloque
> debe compilar y poder explicarse antes de pasar al siguiente.

| Método | Firma propuesta | Regla / propósito |
|--------|-----------------|-------------------|
| Obtener instancia | `static ControlAcceso getInstancia()` | ✅ Implementado |
| Alta de empleado | `void registrarEmpleado(Empleado empleado)` | RN-07: solo se opera con existentes |
| Alta de sector | `void registrarSector(Sector sector)` | RN-07 |
| Búsqueda de empleado | `Empleado buscarEmpleado(int identificacion)` | Auxiliar de permisos y niveles |
| Búsqueda de sector | `Sector buscarSector(int identificacion)` | Auxiliar de permisos |
| Asignar nivel | `void asignarNivelAcceso(int identificacion, NivelAcceso nivel)` | Nivel del empleado |
| Conceder permiso | `void concederPermiso(Empleado empleado, Sector sector, TipoPermiso tipo)` | RN-07 |
| Revocar permiso | `void revocarPermiso(Empleado empleado, Sector sector)` | RN-03: baja lógica (`activo = false`) |
| Verificar acceso | `boolean verificarAcceso(Empleado empleado, Sector sector)` | RN-01 y RN-02, sin registrar |
| Registrar intento | `RegistroAcceso registrarIntento(Empleado empleado, Sector sector)` | RN-04, RN-05 y RN-06 |
| Consultar historial | `List<RegistroAcceso> obtenerHistorial()` | Devuelve copia (ADR-08) |
| *(privado)* Buscar permiso | `Permiso buscarPermiso(Empleado empleado, Sector sector)` | Reutilizado por conceder/revocar/verificar |
| *(privado)* Permiso especial activo | `boolean tienePermisoEspecialActivo(Empleado empleado, Sector sector)` | RN-02 |
| *(privado)* Motivo de rechazo | `String motivoRechazo(Empleado empleado, Sector sector)` | RN-05 |

### Orden de implementación sugerido

1. `NivelAcceso` y `TipoPermiso` (no dependen de nada).
2. `Empleado` completo (con `nivelAcceso`, setters y `toString()`).
3. `Sector`.
4. `Permiso`.
5. `RegistroAcceso`.
6. `ControlAcceso`: colecciones → altas → búsquedas → permisos → verificación → registro.
7. `Main` con el caso de demostración.

## 5.7. Cumplimiento de las reglas del proyecto

| Regla | Cumplimiento |
|-------|--------------|
| Lenguaje Java | ✅ |
| Una clase por archivo | ✅ |
| Clases con mayúscula inicial | ✅ |
| Atributos en minúscula | ✅ |
| Nombres de atributos claros | ✅ |
| Atributo estático privado (Singleton) | ✅ |
| Constructor privado (Singleton) | ✅ |
| Método público de instancia (Singleton) | ✅ (`getInstancia()`) |
| Atributos privados | ✅ en lo implementado |
| Getters y setters | ⚠️ Getter sí; setter pendiente (ADR-08) |

---

[Volver al índice](../README.md) · [← Anterior: Diseño](04-diseno.md) · [Siguiente: Pruebas →](06-pruebas.md)
