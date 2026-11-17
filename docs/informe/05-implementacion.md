# 5. Implementación

> Esta sección describe el **código real** de `src/` en la rama `main`. La estructura de
> las siete clases y del Singleton está creada; la **lógica de negocio todavía no está
> implementada** y se marca como `Pendiente`. La brecha con el diseño propuesto está
> resumida en [§5.5](#55-brecha-entre-diseño-e-implementación).

## 5.1. Estructura del código

```
src/
├── ControlAcceso.java    ← Singleton: atributos, constructor, getInstance() y firmas
├── Empleado.java         ← 5 atributos, constructor, getters, setters y toString()
├── Main.java             ← clase de prueba, vacía
├── Permiso.java          ← 4 atributos, getters y revocar() vacío
├── RegistroAcceso.java   ← 5 atributos y 3 getters
├── Sector.java           ← 3 atributos y getters (sin setters)
├── NivelAcceso.java      ← enum NIVEL_1 … NIVEL_5
└── (TipoPermiso.java no existe)
```

Cada clase vive en su propio archivo y el nombre del archivo coincide con el de la clase,
como exige [`Reglas-Proyecto.md`](../../Reglas-Proyecto.md).

## 5.2. El Singleton: `ControlAcceso`

Código actual (`src/ControlAcceso.java`):

```java
import java.util.ArrayList;
import java.util.List;

public class ControlAcceso {
    // Atributos
    private List<Empleado> empleados;
    private List<Sector> sectores;
    private List<Permiso> permisos;
    private List<RegistroAcceso> registros;
    private static ControlAcceso instancia;

    // Constructor de acceso unico
    private ControlAcceso() {
        this.empleados = new ArrayList<>();
        this.sectores = new ArrayList<>();
        this.permisos = new ArrayList<>();
        this.registros = new ArrayList<>();
    }

    // Getters y Setters

    // Metodo de acceso unico
    public static ControlAcceso getInstance() {
        if (instancia == null) {
            instancia = new ControlAcceso();
        }
        return instancia;
    }

    // Metodos publicos o privados
    public void registrarEmpleado(Empleado empleado) {
        // Logica
    }

    public void registrarSector(Sector sector) {
        // Logica
    }

    public void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso) {
        // Logica
    }

    public void concederPermiso(Permiso permiso) {
        // Logica
    }

    public void revocarPermiso(Permiso permiso) {
        // Logica
    }

    public boolean verificarAcceso(int numEmpleado, int numSector) {
        // Retorno de ejemplo para que no marque el error
        boolean ejemplo = false;
        return ejemplo;
    }

    public RegistroAcceso consultarHistorial() {
        // Esta en null a modo de ejemplo para que no de error
        return null;
    }
}
```

### Anatomía del patrón, línea por línea

| Elemento | Por qué |
|----------|---------|
| `private static ControlAcceso instancia;` | `static`: el dato pertenece a la clase, hay un solo casillero para todos. `private`: nadie fuera de la clase puede reemplazarlo. |
| `private ControlAcceso()` | Bloquea `new ControlAcceso()` desde cualquier otra clase e inicializa las cuatro listas. |
| `public static ControlAcceso getInstance()` | Único camino permitido para obtener el objeto. `static` porque se invoca sobre la clase, no sobre una instancia existente. |
| `if (instancia == null)` | Creación perezosa (*lazy*): solo se construye la primera vez. |

### Traza de ejecución

```
1ª llamada: instancia == null  ->  se crea       ->  devuelve referencia A
2ª llamada: instancia != null  ->  no se crea    ->  devuelve la misma referencia A
```

Consecuencia: `ControlAcceso.getInstance() == ControlAcceso.getInstance()` es `true`.

### Qué falta en esta clase

- Cuerpo de `registrarEmpleado`, `registrarSector`, `asignarNivel`, `concederPermiso` y
  `revocarPermiso` (`Pendiente`).
- Búsquedas auxiliares por identificación (`Pendiente`).
- `verificarAcceso(int, int)` con la comparación de niveles y permisos (`Pendiente`).
- Un método que registre el intento (`Pendiente`).
- `consultarHistorial()` que devuelva la colección de registros (`Pendiente`).

## 5.3. `Empleado`

Código actual (`src/Empleado.java`):

```java
public class Empleado {
    private int identificacion;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcesso;

    public Empleado(int identificacion, int dni, String nombre, String apellido,
                    NivelAcceso nivelAcceso) {
        this.identificacion = identificacion;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nivelAcesso = nivelAcceso;
    }

    public int getDni() { return dni; }
    public int getIdentificacion() { return identificacion; }
    public String getApellido() { return apellido; }
    public String getNombre() { return nombre; }
    public NivelAcceso getNivelAcesso() { return nivelAcesso; }

    public void setNivelAcesso(NivelAcceso nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    @Override
    public String toString() {
        return "ID-Empleado " + getIdentificacion()
                + "Nombre " + getNombre()
                + "Apellido " + getApellido()
                + "Nivel de Acceso " + getNivelAcesso();
    }
}
```

Observaciones:

- Los cinco atributos son `private`, con nombres descriptivos en minúscula.
- El constructor recibe los datos y los asigna con `this.` para desambiguar
  parámetro/variable de instancia.
- El atributo se llama **`nivelAcesso`** (con una «c» de más) y sus accesores son
  `getNivelAcesso()` / `setNivelAcesso()`. Es un typo respecto de `nivelAcceso`; está
  pendiente corregirlo o aceptarlo formalmente.
- `toString()` no separa los campos con comas ni espacios finales; conviene ajustarlo
  cuando se use en la salida de la demostración.
- Tiene **getter y setter** del nivel, pero no setters para los demás atributos.

## 5.4. Las demás clases

### `Sector`

```java
public class Sector {
    private int idS;
    private String nombre;
    private NivelAcceso nivelRequerido;

    public Sector() {
    }

    public String getNombre() { return nombre; }
    public int getIdS() { return idS; }
    public NivelAcceso getNivelRequerido() { return nivelRequerido; }
}
```

Tiene getters pero **no setters** ni constructor con parámetros: hoy no hay forma de
asignarle valores a un sector desde fuera de la clase.

### `Permiso`

```java
public class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;

    public Permiso() {
    }

    public Empleado getEmpleado() { return empleado; }
    public Sector getSector() { return sector; }
    public boolean estaActivo() { return activo; }
    public boolean esEspecial() { return especial; }

    public void revocar() {
    }
}
```

`revocar()` está declarado pero **vacío**: no cambia `activo`. Tampoco hay setters ni una
forma de construir un permiso con datos.

### `RegistroAcceso`

```java
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistroAcceso {
    private Empleado empleado;
    private Sector sector;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivo;

    public RegistroAcceso() {
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public boolean isAutorizado() { return autorizado; }
}
```

El constructor no asigna `fechaHora`, no hay setters y no se exponen `getEmpleado()` ni
`getSector()`. El `import java.time.LocalDate;` no se usa.

### `NivelAcceso`

```java
public enum NivelAcceso {
    NIVEL_1, // Acceso General
    NIVEL_2, // Acceso Interno
    NIVEL_3, // Acceso Restringido
    NIVEL_4, // Acceso Tecnico
    NIVEL_5  // Acceso Directivo
}
```

No guarda valores numéricos ni expone `getNivel()` o `alcanzaA()`. La comparación
acumulativa se puede hacer con `ordinal()`, pero todavía no está implementada.

### `Main`

```java
public class Main {
    public static void main(String[] args) {

    }
}
```

La clase de prueba existe pero **no demuestra nada todavía**. La versión mínima propuesta
está en [Pruebas §6.3](06-pruebas.md#63-código-propuesto-para-main-cp-01-a-cp-03).

## 5.5. Brecha entre diseño e implementación

| Elemento del diseño propuesto | Código actual | Estado |
|------------------------------|---------------|--------|
| `ControlAcceso.instancia` (`private static`) | `private static ControlAcceso instancia` | ✅ |
| Constructor privado | `private ControlAcceso()` | ✅ |
| Método de acceso | `getInstance()` (el diseño decía `getInstancia()`) | ⚠️ Nombre distinto, estructura correcta |
| Colecciones `empleados`, `sectores`, `permisos`, `registros` | `private List<...>` inicializadas en el constructor | ✅ |
| Altas, niveles y permisos | Métodos declarados con `// Logica` | ❌ Sin cuerpo |
| `verificarAcceso` | `verificarAcceso(int, int)` devuelve `false` | ❌ Sin lógica |
| Registro del intento | No existe `registrarIntento` | ❌ |
| Historial | `consultarHistorial()` devuelve `null` | ❌ |
| `Empleado` | 5 atributos, constructor, getters, setters y `toString()` | ✅ |
| Nombre `Empleado.nivelAcceso` | `nivelAcesso` (typo) | ⚠️ |
| `Sector` | `idS`, getters, sin setters | ⚠️ Incompleto |
| `Permiso` | `boolean especial`, `revocar()` vacío | ⚠️ Incompleto |
| `RegistroAcceso` | `LocalDateTime` sin asignar, sin setters | ⚠️ Incompleto |
| `NivelAcceso` | Enum `NIVEL_1`…`NIVEL_5`, sin valor ni comparación | ⚠️ Parcial |
| `TipoPermiso` | No existe | ❌ |
| `Main` de demostración | Vacío | ❌ |

## 5.6. Plan de métodos de `ControlAcceso`

> Estado: **propuesto**. Las firmas marcadas «existente» ya están en el código; las demás
> hay que agregarlas. Cada bloque debe compilar y poder explicarse antes de pasar al
> siguiente.

| Método | Firma propuesta | Regla / propósito | Estado |
|--------|-----------------|-------------------|--------|
| Obtener instancia | `static ControlAcceso getInstance()` | Única instancia | ✅ Existente |
| Alta de empleado | `void registrarEmpleado(Empleado empleado)` | RN-07 | ⚠️ Firma existente, sin cuerpo |
| Alta de sector | `void registrarSector(Sector sector)` | RN-07 | ⚠️ Firma existente, sin cuerpo |
| Asignar nivel | `void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso)` | Nivel del empleado | ⚠️ Firma existente, sin cuerpo |
| Conceder permiso | `void concederPermiso(Permiso permiso)` | RN-02 y RN-07 | ⚠️ Firma existente, sin cuerpo |
| Revocar permiso | `void revocarPermiso(Permiso permiso)` | RN-03: baja lógica | ⚠️ Firma existente, sin cuerpo |
| Verificar acceso | `boolean verificarAcceso(int numEmpleado, int numSector)` | RN-01 y RN-02 | ⚠️ Firma existente, devuelve `false` |
| Consultar historial | `RegistroAcceso consultarHistorial()` | Historial de intentos | ⚠️ Firma existente, devuelve `null` |
| *(nuevo)* Buscar empleado | `Empleado buscarEmpleado(int identificacion)` | Auxiliar de verificación | ❌ Propuesto |
| *(nuevo)* Buscar sector | `Sector buscarSector(int identificacion)` | Auxiliar de verificación | ❌ Propuesto |
| *(nuevo)* Registrar intento | `RegistroAcceso registrarIntento(int numEmpleado, int numSector)` | RN-04 y RN-05 | ❌ Propuesto |
| *(nuevo)* Buscar permiso | `Permiso buscarPermiso(Empleado, Sector)` | Reutilizado por conceder/revocar/verificar | ❌ Propuesto |
| *(nuevo)* Permiso especial activo | `boolean tienePermisoEspecialActivo(Empleado, Sector)` | RN-02 | ❌ Propuesto |

### Orden de implementación sugerido

1. `NivelAcceso`: comparación por `ordinal()` (o valor numérico, si se retoma ADR-01).
2. `Sector`: constructor con parámetros y setters.
3. `Permiso`: constructor, setters y `revocar()`.
4. `RegistroAcceso`: constructor que asigna `fechaHora` y setters.
5. `ControlAcceso`: colecciones → altas → búsquedas → permisos → verificación → registro.
6. `Main` con el caso de demostración.

## 5.7. Cumplimiento de las reglas del proyecto

| Regla | Cumplimiento |
|-------|--------------|
| Lenguaje Java | ✅ |
| Una clase por archivo | ✅ |
| Clases con mayúscula inicial | ✅ |
| Atributos en minúscula | ✅ |
| Nombres de atributos claros | ⚠️ `nivelAcesso` tiene un typo y `idS` es una abreviatura |
| Atributo estático privado (Singleton) | ✅ |
| Constructor privado (Singleton) | ✅ |
| Método público de instancia (Singleton) | ✅ (`getInstance()`) |
| Atributos privados | ✅ |
| Getters y setters en todos los atributos | ⚠️ Solo `Empleado` tiene setters; `Sector` y `RegistroAcceso` no |

---

[Volver al índice](../README.md) · [← Anterior: Diseño](04-diseno.md) · [Siguiente: Pruebas →](06-pruebas.md)
