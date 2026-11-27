# 5. Implementación

> Esta sección describe el **código real** de `src/` en la rama `main`. La estructura de
> las clases y del Singleton está completa, con su lógica de negocio, validaciones y
> auditoría centralizada implementadas. La evolución frente al diseño original está
> resumida en [§5.5](#55-brecha-entre-diseño-e-implementación).

## 5.1. Estructura del código

```
src/
├── ControlAcceso.java    ← Singleton: listas, constructor, getInstance(), lógica de negocio e historial
├── Empleado.java         ← 5 atributos, constructor, getters, setters y toString()
├── Main.java             ← clase de prueba y menú interactivo por consola
├── Permiso.java          ← 4 atributos, constructor con datos, getters, setters y revocar()
├── RegistroAcceso.java   ← 5 atributos, constructor con LocalDateTime.now(), getters, setters y toString()
├── Sector.java           ← 3 atributos, constructor con datos, getters y setters
├── NivelAcceso.java      ← enum NIVEL_1 … NIVEL_5
└── (TipoPermiso.java no existe)
```

Cada clase vive en su propio archivo y el nombre del archivo coincide con el de la clase,
como exige [`Reglas-Proyecto.md`](../../Reglas-Proyecto.md).

## 5.2. El Singleton: `ControlAcceso`

Estructura central (`src/ControlAcceso.java`):

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

    // Metodo de acceso unico
    public static ControlAcceso getInstance() {
        if (instancia == null) {
            instancia = new ControlAcceso();
        }
        return instancia;
    }

    public void registrarEmpleado(Empleado empleado) { /* ... */ }
    public void registrarSector(Sector sector) { /* ... */ }
    public void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso) { /* ... */ }
    public void concederPermiso(Permiso permiso) { /* ... */ }
    public void revocarPermiso(Permiso permiso) { /* ... */ }
    public boolean verificarAcceso(int numEmpleado, int numSector) { /* ... */ }

    // Devuelve el historial de intentos
    public List<RegistroAcceso> consultarHistorial() {
        if (this.registros.isEmpty()) {
            System.out.println("El historial esta vacio: no hay intentos de acceso registrados");
            return new ArrayList<>();
        }
        // Se devuelve una copia para que el cliente no modifique la lista interna
        return new ArrayList<>(this.registros);
    }

    // Metodos auxiliares
    public Empleado buscarEmpleado(int identificacion) { /* ... */ }
    public Sector buscarSector(int identificacion) { /* ... */ }
    private Permiso buscarPermiso(Empleado empleado, Sector sector) { /* ... */ }
    private boolean tienePermisoEspecialActivo(Empleado empleado, Sector sector) { /* ... */ }
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

### Capacidades implementadas en esta clase

- **Altas con validación:** `registrarEmpleado` y `registrarSector` verifican duplicados por identificación antes de agregar.
- **Gestión de permisos:** `concederPermiso` valida existencia de empleado y sector (CP-12) y reactiva permisos previos si ya existían; `revocarPermiso` realiza baja lógica delegando en `Permiso.revocar()`.
- **Verificación de accesos:** `verificarAcceso` evalúa jerarquía con `alcanzaA()` de `NivelAcceso` (RN-01), excepciones por permiso especial activo (RN-02) y genera automáticamente un `RegistroAcceso` (RN-04 y RN-05).
- **Consulta segura de auditoría:** `consultarHistorial()` retorna `List<RegistroAcceso>` mediante una **copia defensiva** (`new ArrayList<>(this.registros)`), protegiendo la lista interna de modificaciones externas.

## 5.3. `Empleado`

Código actual (`src/Empleado.java`):

```java
public class Empleado {
    private int identificacion;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcceso;

    public Empleado(int identificacion, int dni, String nombre, String apellido,
                    NivelAcceso nivelAcceso) {
        this.identificacion = identificacion;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nivelAcceso = nivelAcceso;
    }

    public int getIdentificacion() { return identificacion; }
    public int getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public NivelAcceso getNivelAcesso() { return nivelAcceso; }
    public void setNivelAcesso(NivelAcceso nivelAcceso) { this.nivelAcceso = nivelAcceso; }
    public String toString() { /* ... */ }
}
```

## 5.4. Las demás clases

### `Sector`

```java
public class Sector {
    private int identificacion;
    private String nombre;
    private NivelAcceso nivelRequerido;

    public Sector(int identificacion, String nombre, NivelAcceso nivelRequerido) {
        // Parte de valores seguros y delega en los setters:
        // constructor y setters aplican la misma regla de validación.
        this.identificacion = 0;
        this.nombre = "";
        this.nivelRequerido = NivelAcceso.NIVEL_1;
        setIdentificacion(identificacion);
        setNombre(nombre);
        setNivelRequerido(nivelRequerido);
    }

    public int getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public NivelAcceso getNivelRequerido() { return nivelRequerido; }

    public void setIdentificacion(int identificacion) {
        if (identificacion <= 0) { /* avisa y conserva el valor anterior */ return; }
        this.identificacion = identificacion;
    }
    public void setNombre(String nombre) { /* valida no vacío y recorta */ }
    public void setNivelRequerido(NivelAcceso nivelRequerido) { /* valida no nulo */ }
    public String toString() { /* ... */ }
}
```

Dispone de constructor con parámetros, getters y setters para todos sus campos; el
constructor valida a través de los setters. El identificador se llama `identificacion`
(ADR-04 aplicado).

### `Permiso`

```java
public class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;

    public Permiso(Empleado empleado, Sector sector, boolean especial) {
        this.empleado = empleado;
        this.sector = sector;
        this.especial = especial;
        this.activo = true;
    }

    public Empleado getEmpleado() { return empleado; }
    public Sector getSector() { return sector; }
    public boolean estaActivo() { return activo; }
    public boolean esEspecial() { return especial; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public void setSector(Sector sector) { this.sector = sector; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setEspecial(boolean especial) { this.especial = especial; }
    public void revocar() { this.activo = false; }
}
```

Implementa la baja lógica mediante `revocar()` (`activo = false`) y ofrece setters para reactivación o edición.

### `RegistroAcceso`

```java
import java.time.LocalDateTime;

public class RegistroAcceso {
    private Empleado empleado;
    private Sector sector;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivo;

    public RegistroAcceso(Empleado empleado, Sector sector, boolean autorizado, String motivo) {
        this.empleado = empleado;
        this.sector = sector;
        this.autorizado = autorizado;
        this.motivo = motivo;
        this.fechaHora = LocalDateTime.now();
    }

    public Empleado getEmpleado() { return empleado; }
    public Sector getSector() { return sector; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public boolean isAutorizado() { return autorizado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public void setSector(Sector sector) { this.sector = sector; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setAutorizado(boolean autorizado) { this.autorizado = autorizado; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    @Override
    public String toString() {
        String nombreEmpleado = (getEmpleado() != null) ? getEmpleado().getNombre() : "sin empleado";
        String nombreSector = (getSector() != null) ? getSector().getNombre() : "sin sector";
        String resultado = isAutorizado() ? "PERMITIDO" : "DENEGADO";
        return "[" + getFechaHora() + "] " + nombreEmpleado + " -> " + nombreSector
                + " | " + resultado + " | " + getMotivo();
    }
}
```

Asigna la marca temporal mediante `LocalDateTime.now()` en su constructor, provee getters y setters, y dispone de `toString()` legible para inspección y consola.

### `NivelAcceso`

```java
public enum NivelAcceso {
    NIVEL_1(1, "Acceso General"),
    NIVEL_2(2, "Acceso Interno"),
    NIVEL_3(3, "Acceso Restringido"),
    NIVEL_4(4, "Acceso Tecnico"),
    NIVEL_5(5, "Acceso Directivo");

    private final int nivel;
    private final String descripcion;

    public boolean alcanzaA(NivelAcceso nivelRequerido) {
        return nivelRequerido != null && this.nivel >= nivelRequerido.getNivel();
    }
}
```

La comparación acumulativa se realiza mediante `alcanzaA()`, que compara el valor numérico
(`getNivel()`) de cada nivel.

### `Main`

`Main.java` implementa un menú interactivo por consola que permite dar de alta empleados con validación de nivel y registrarlos en la instancia única de `ControlAcceso`.

## 5.5. Brecha entre diseño e implementación

| Elemento del diseño propuesto | Código actual | Estado |
|------------------------------|---------------|--------|
| `ControlAcceso.instancia` (`private static`) | `private static ControlAcceso instancia` | ✅ |
| Constructor privado | `private ControlAcceso()` | ✅ |
| Método de acceso | `getInstance()` (el diseño decía `getInstancia()`) | ⚠️ Nombre distinto, estructura correcta |
| Colecciones `empleados`, `sectores`, `permisos`, `registros` | `private List<...>` inicializadas en el constructor | ✅ |
| Altas, niveles y permisos | Métodos implementados con validación | ✅ |
| `verificarAcceso` | Evalúa nivel, permiso especial y genera registro | ✅ |
| Registro del intento | Se genera automáticamente en `verificarAcceso` | ✅ |
| Historial | `consultarHistorial()` devuelve `List<RegistroAcceso>` (copia defensiva) | ✅ |
| `Empleado` | 6 atributos (incluye `registroValido`), constructor con validación, getters, setters y `toString()` | ✅ |
| `Sector` | `identificacion`, constructor que valida vía setters, getters, setters y `toString()` | ✅ |
| `Permiso` | `boolean especial`, constructor, setters y `revocar()` funcional | ✅ |
| `RegistroAcceso` | `LocalDateTime.now()`, getters, setters y `toString()` | ✅ |
| `NivelAcceso` | Enum `NIVEL_1`…`NIVEL_5` con valor numérico y `alcanzaA()` | ✅ |
| `TipoPermiso` | No existe (se usa `boolean especial`) | ❌ |
| `Main` interactivo | Menú por consola conectado al Singleton | ✅ |

## 5.6. Plan de métodos de `ControlAcceso`

| Método | Firma implementada | Regla / propósito | Estado |
|--------|--------------------|-------------------|--------|
| Obtener instancia | `static ControlAcceso getInstance()` | Única instancia | ✅ Implementado |
| Alta de empleado | `void registrarEmpleado(Empleado empleado)` | RN-07 / Alta con validación | ✅ Implementado |
| Alta de sector | `void registrarSector(Sector sector)` | RN-07 / Alta con validación | ✅ Implementado |
| Asignar nivel | `void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso)` | Nivel del empleado | ✅ Implementado |
| Conceder permiso | `void concederPermiso(Permiso permiso)` | RN-02 y RN-07 | ✅ Implementado |
| Revocar permiso | `void revocarPermiso(Permiso permiso)` | RN-03: baja lógica | ✅ Implementado |
| Verificar acceso | `boolean verificarAcceso(int numEmpleado, int numSector)` | RN-01, RN-02, RN-04, RN-05 | ✅ Implementado |
| Consultar historial | `List<RegistroAcceso> consultarHistorial()` | Copia defensiva de intentos | ✅ Implementado |
| Buscar empleado | `Empleado buscarEmpleado(int identificacion)` | Auxiliar de verificación | ✅ Implementado |
| Buscar sector | `Sector buscarSector(int identificacion)` | Auxiliar de verificación | ✅ Implementado |
| Buscar permiso | `Permiso buscarPermiso(Empleado, Sector)` | Reutilizado por conceder/revocar/verificar | ✅ Implementado |
| Permiso especial activo | `boolean tienePermisoEspecialActivo(Empleado, Sector)` | RN-02 | ✅ Implementado |

## 5.7. Cumplimiento de las reglas del proyecto

| Regla | Cumplimiento |
|-------|--------------|
| Lenguaje Java | ✅ |
| Una clase por archivo | ✅ |
| Clases con mayúscula inicial | ✅ |
| Atributos en minúscula | ✅ |
| Nombres de atributos claros | ✅ `nivelAcceso` y `identificacion` (ADR-04 aplicado) |
| Atributo estático privado (Singleton) | ✅ |
| Constructor privado (Singleton) | ✅ |
| Método público de instancia (Singleton) | ✅ (`getInstance()`) |
| Atributos privados | ✅ |
| Getters y setters en todos los atributos | ✅ Implementados en todas las clases del modelo |

---

[Volver al índice](../README.md) · [← Anterior: Diseño](04-diseno.md) · [Siguiente: Pruebas →](06-pruebas.md)
