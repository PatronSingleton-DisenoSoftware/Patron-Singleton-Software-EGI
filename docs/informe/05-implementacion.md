# 5. Implementación

> Esta sección describe el **código real** de `src/` en la rama `main`. La estructura de
> las clases y del Singleton está completa, con su lógica de negocio, validaciones y
> auditoría centralizada implementadas. La evolución frente al diseño original está
> resumida en [§5.5](#55-brecha-entre-diseño-e-implementación).

## 5.1. Estructura del código

```
src/
├── ControlAcceso.java    ← Singleton: listas, constructor, getInstance(), lógica de negocio e historial
├── Empleado.java         ← 6 atributos (incluye registroValido), validación de DNI y datos, getters, setters y toString()
├── Main.java             ← clase de prueba, menú interactivo y demostración automática
├── Permiso.java          ← 4 atributos, constructor con datos, getters, setters y revocar()
├── RegistroAcceso.java   ← 5 atributos, constructor con LocalDateTime.now(), getters, setters y toString()
├── Sector.java           ← 3 atributos, validación vía setters, getters, setters y toString()
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

public final class ControlAcceso {
    // Atributos
    private final List<Empleado> empleados;
    private final List<Sector> sectores;
    private final List<Permiso> permisos;
    private final List<RegistroAcceso> registros;
    private static ControlAcceso instancia;
    private boolean mostrarMensajesRegistro = true;

    public void setMostrarMensajesRegistro(boolean mostrar) {
        this.mostrarMensajesRegistro = mostrar;
    }

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
    public Empleado buscarEmpleado(int idEmpleado) { /* ... */ }
    public Empleado buscarEmpleadoPorDni(int dni) { /* ... */ }
    public Sector buscarSector(int idSector) { /* ... */ }
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

- **Altas con validación:** `registrarEmpleado` y `registrarSector` verifican datos mínimos, identificadores positivos y duplicados por ID; el alta de empleado también rechaza un DNI repetido.
- **Gestión de permisos:** `concederPermiso` valida existencia de empleado y sector (CP-12), guarda las referencias registradas, evita duplicados activos y reactiva un permiso revocado en lugar de crear otro; `revocarPermiso` realiza baja lógica delegando en `Permiso.revocar()`.
- **Verificación de accesos:** `verificarAcceso` evalúa jerarquía con `alcanzaA()` de `NivelAcceso` (RN-01), excepciones por permiso especial activo (RN-02) y genera automáticamente un `RegistroAcceso` (RN-04 y RN-05), incluso cuando el empleado o el sector no existen.
- **Consulta segura de auditoría:** `consultarHistorial()`, `getEmpleados()`, `getSectores()` y `getPermisos()` retornan **copias defensivas** (`new ArrayList<>(...)`), protegiendo las colecciones internas de modificaciones externas.
- **Salida controlada:** `mostrarMensajesRegistro` permite silenciar los avisos durante la carga inicial y reactivarlos para las altas del menú.

## 5.3. `Empleado`

Código actual (`src/Empleado.java`):

```java
public final class Empleado {
    private int idEmpleado;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcceso;
    private boolean registroValido;

    public Empleado(int idEmpleado, int dni, String nombre, String apellido,
                    NivelAcceso nivelAcceso) {
        // Guarda los datos primero: si algo es invalido, el objeto conserva
        // lo que se intento cargar y resulta mas facil de inspeccionar.
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.nombre = (nombre == null) ? null : nombre.trim();
        this.apellido = (apellido == null) ? null : apellido.trim();
        this.nivelAcceso = (nivelAcceso == null) ? NivelAcceso.NIVEL_1 : nivelAcceso;
        this.registroValido = false;

        if (this.nombre == null || this.nombre.isEmpty()
                || this.apellido == null || this.apellido.isEmpty()) {
            System.out.println("No se puede registrar este empleado porque no tiene nombre o apellido.");
            return;
        }
        if (!esDniValido(this.dni)) {
            System.out.println("No se puede registrar este empleado porque el DNI no tiene la cantidad de digitos valida (7 u 8 digitos).");
            return;
        }
        if (this.idEmpleado <= 0) {
            System.out.println("No se puede registrar este empleado porque el ID del empleado debe ser mayor a 0.");
            return;
        }
        this.registroValido = true;
    }

    public static boolean esDniValido(int dni) {
        if (dni <= 0) {
            return false;
        }
        String dniTexto = String.valueOf(dni).trim();
        return dniTexto.length() == 7 || dniTexto.length() == 8;
    }

    public boolean isRegistroValido() {
        return registroValido && datosValidos();
    }

    public void setRegistroValido(boolean registroValido) {
        // Un objeto solo queda marcado como valido si sus datos realmente lo son.
        this.registroValido = registroValido && datosValidos();
    }

    // Recalcula la validez a partir de los datos actuales.
    public boolean datosValidos() {
        return idEmpleado > 0
                && esDniValido(dni)
                && nombre != null && !nombre.trim().isEmpty()
                && apellido != null && !apellido.trim().isEmpty()
                && nivelAcceso != null;
    }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { /* valida > 0 */ }
    public int getDni() { return dni; }
    public void setDni(int dni) { /* valida 7 u 8 digitos */ }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { /* valida no vacio y recorta */ }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { /* valida no vacio y recorta */ }
    public NivelAcceso getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(NivelAcceso nivelAcceso) { /* valida no nulo */ }

    @Override
    public String toString() { /* ... */ }
}
```

La validez no es un dato que el cliente pueda imponer: `isRegistroValido()` combina la
marca `registroValido` con `datosValidos()`, y `setRegistroValido(true)` vuelve a
recalcularla. Así, si un setter deja el nombre vacío o el DNI inválido, el empleado no
puede registrarse aunque alguien intente forzar la bandera.

## 5.4. Las demás clases

### `Sector`

```java
public final class Sector {
    private int idSector;
    private String nombre;
    private NivelAcceso nivelRequerido;

    public Sector(int idSector, String nombre, NivelAcceso nivelRequerido) {
        // Parte de valores seguros y delega en los setters:
        // constructor y setters aplican la misma regla de validación.
        this.idSector = 0;
        this.nombre = "";
        this.nivelRequerido = NivelAcceso.NIVEL_1;
        setIdSector(idSector);
        setNombre(nombre);
        setNivelRequerido(nivelRequerido);
    }

    // Comprueba que el sector tenga los datos minimos para registrarse.
    public boolean datosValidos() {
        return idSector > 0
                && nombre != null && !nombre.trim().isEmpty()
                && nivelRequerido != null;
    }

    public int getIdSector() { return idSector; }
    public String getNombre() { return nombre; }
    public NivelAcceso getNivelRequerido() { return nivelRequerido; }

    public void setIdSector(int idSector) {
        if (idSector <= 0) { /* avisa y conserva el valor anterior */ return; }
        this.idSector = idSector;
    }
    public void setNombre(String nombre) { /* valida no vacío y recorta */ }
    public void setNivelRequerido(NivelAcceso nivelRequerido) { /* valida no nulo */ }
    public String toString() { /* ... */ }
}
```

Dispone de constructor con parámetros, getters y setters para todos sus campos; el
constructor valida a través de los setters. El identificador se llama `idSector`
(ADR-04 aplicado). `datosValidos()` permite que `registrarSector()` rechace un sector
incompleto antes de agregarlo a la colección.

### `Permiso`

```java
public final class Permiso {
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

    public void setEmpleado(Empleado empleado) {
        if (empleado == null) { /* avisa y conserva el valor anterior */ return; }
        this.empleado = empleado;
    }
    public void setSector(Sector sector) {
        if (sector == null) { /* avisa y conserva el valor anterior */ return; }
        this.sector = sector;
    }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setEspecial(boolean especial) { this.especial = especial; }
    public void revocar() { this.activo = false; }

    @Override
    public String toString() {
        String estado = activo ? "ACTIVO" : "REVOCADO";
        String tipo = especial ? "Especial" : "Regular";
        String emp = (empleado != null) ? empleado.getNombre() + " " + empleado.getApellido() : "Sin empleado";
        String sec = (sector != null) ? sector.getNombre() : "Sin sector";
        return "Permiso [" + estado + " | " + tipo + "] Empleado: " + emp + " -> Sector: " + sec;
    }
}
```

Implementa la baja lógica mediante `revocar()` (`activo = false`) y ofrece setters para
reactivación o edición. Los setters de `empleado` y `sector` rechazan `null`, de modo que
un permiso no queda a medio armar. `ControlAcceso.concederPermiso()` reemplaza las
referencias recibidas por las que están registradas y reactiva el permiso revocado si ya
existía.

### `RegistroAcceso`

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class RegistroAcceso {
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Empleado empleado;
    private Sector sector;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivo;

    public RegistroAcceso(Empleado empleado, Sector sector, boolean autorizado, String motivo) {
        // empleado y sector pueden ser null si el intento se hizo con una
        // ID que no existe; el motivo explica el rechazo.
        this.empleado = empleado;
        this.sector = sector;
        this.autorizado = autorizado;
        this.fechaHora = LocalDateTime.now();
        setMotivo(motivo);   // valida que el motivo no este vacio (CP-11)
    }

    public Empleado getEmpleado() { return empleado; }
    public Sector getSector() { return sector; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public boolean isAutorizado() { return autorizado; }

    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public void setSector(Sector sector) { this.sector = sector; }
    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora del registro no puede ser nula");
        }
        this.fechaHora = fechaHora;
    }
    public void setAutorizado(boolean autorizado) { this.autorizado = autorizado; }
    public void setMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo del registro no puede estar vacio");
        }
        this.motivo = motivo.trim();
    }

    @Override
    public String toString() {
        String nombreEmpleado = (getEmpleado() == null)
                ? "Empleado no registrado"
                : getEmpleado().getNombre() + " " + getEmpleado().getApellido()
                        + " (ID " + getEmpleado().getIdEmpleado() + ")";
        String nombreSector = (getSector() == null)
                ? "Sector inexistente"
                : getSector().getNombre() + " (ID " + getSector().getIdSector() + ")";
        String resultado = isAutorizado() ? "AUTORIZADO" : "DENEGADO";

        return getFechaHora().format(FORMATO_FECHA_HORA) + " | "
                + nombreEmpleado + " | "
                + nombreSector + " | "
                + resultado + " | "
                + getMotivo();
    }
}
```

Asigna la marca temporal mediante `LocalDateTime.now()` en su constructor, provee getters
y setters, y dispone de `toString()` legible para inspección y consola. A diferencia del
resto de las entidades, `empleado` y `sector` **sí admiten `null`**: un intento con una ID
inexistente también debe quedar auditado, y `toString()` lo muestra como
"Empleado no registrado" o "Sector inexistente". El motivo, en cambio, es obligatorio:
`setMotivo()` lanza `IllegalArgumentException` si llega vacío.

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

    private NivelAcceso(int nivel, String descripcion) {
        this.nivel = nivel;
        this.descripcion = descripcion;
    }

    public int getNivel() { return nivel; }
    public String getDescripcion() { return descripcion; }

    public boolean alcanzaA(NivelAcceso nivelRequerido) {
        if (nivelRequerido == null) {
            return false;
        }
        return this.nivel >= nivelRequerido.getNivel();
    }
}
```

La comparación acumulativa se realiza mediante `alcanzaA()`, que compara el valor numérico
(`getNivel()`) de cada nivel. Un nivel `null` no alcanza a ningún sector.

### `Main`

`Main.java` implementa un menú interactivo por consola que permite dar de alta empleados
con validación de nivel, registrar sectores, conceder y revocar permisos, verificar
accesos y consultar el historial, todo contra la instancia única de `ControlAcceso`.

La opción 9 ejecuta `funcionDemostracionAutomatica(ControlAcceso)`: compara dos llamadas a
`getInstance()`, registra datos de prueba solo si no existen y recorre el flujo de nivel,
permiso especial, revocación e historial. Es idempotente, así que puede repetirse sin
duplicar datos.

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
| `Empleado` | 6 atributos (incluye `registroValido`), constructor con validación, `esDniValido()`, `datosValidos()`, getters, setters y `toString()` | ✅ |
| `Sector` | `idSector`, constructor que valida vía setters, `datosValidos()`, getters, setters y `toString()` | ✅ |
| `Permiso` | `boolean especial`, constructor, setters con validación de nulos y `revocar()` funcional | ✅ |
| `RegistroAcceso` | `LocalDateTime.now()`, getters, setters con validación de fecha/motivo y `toString()` | ✅ |
| `NivelAcceso` | Enum `NIVEL_1`…`NIVEL_5` con valor numérico y `alcanzaA()` | ✅ |
| `TipoPermiso` | No existe (se usa `boolean especial`) | ❌ |
| `Main` interactivo | Menú por consola más demostración automática idempotente | ✅ |
| Clases del dominio | `final` en `ControlAcceso`, `Empleado`, `Sector`, `Permiso` y `RegistroAcceso` | ✅ |

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
| Buscar empleado | `Empleado buscarEmpleado(int idEmpleado)` | Auxiliar de verificación | ✅ Implementado |
| Buscar empleado por DNI | `Empleado buscarEmpleadoPorDni(int dni)` | Evita DNI repetidos en el alta | ✅ Implementado |
| Buscar sector | `Sector buscarSector(int idSector)` | Auxiliar de verificación | ✅ Implementado |
| Buscar permiso | `Permiso buscarPermiso(Empleado, Sector)` | Reutilizado por conceder/revocar/verificar | ✅ Implementado |
| Permiso especial activo | `boolean tienePermisoEspecialActivo(Empleado, Sector)` | RN-02 | ✅ Implementado |
| Mensajes de registro | `void setMostrarMensajesRegistro(boolean mostrar)` | Silencia o reactiva avisos de alta | ✅ Implementado |

## 5.7. Cumplimiento de las reglas del proyecto

| Regla | Cumplimiento |
|-------|--------------|
| Lenguaje Java | ✅ |
| Una clase por archivo | ✅ |
| Clases con mayúscula inicial | ✅ |
| Atributos en minúscula | ✅ |
| Nombres de atributos claros | ✅ `nivelAcceso`, `idEmpleado` e `idSector` (ADR-04 aplicado) |
| Atributo estático privado (Singleton) | ✅ |
| Constructor privado (Singleton) | ✅ |
| Método público de instancia (Singleton) | ✅ (`getInstance()`) |
| Atributos privados | ✅ |
| Getters y setters en todos los atributos | ✅ Implementados en todas las clases del modelo |
| Clases sin herencia no prevista | ✅ Las cinco clases del dominio se declaran `final` (ADR-09) |
| Compilación limpia | ✅ `javac -Xlint:all` sin advertencias |

---

[Volver al índice](../README.md) · [← Anterior: Diseño](04-diseno.md) · [Siguiente: Pruebas →](06-pruebas.md)
