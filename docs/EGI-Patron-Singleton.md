# Patrón Singleton — Sistema de Control de Acceso

### Evaluación Global Integradora (EGI) · Diseño de Software

| Dato | Valor |
|------|-------|
| **Modalidad** | Grupal |
| **Integrantes** | Candito, Yampa <br> Rodriguez, Federico <br> Gilio, Giuliano <br> Marasco, Lucas |
| **Carrera** | Tecnicatura Universitaria en Desarrollo de Software |
| **Espacio curricular** | Diseño de Software — 2.º año, 3.er semestre |
| **Institución** | ITU — Instituto Tecnológico Universitario, Universidad Nacional de Cuyo, Sede Este |
| **Docente** | Lic. Ignacio G. Fontaine Digier |
| **Fecha de entrega** | 30/11/2026 |
| **Versión del documento** | 1.1 |
| **Patrón elegido** | **Singleton** |
| **Lenguaje** | **Java** (versión 23; código compatible con Java 17+) |
| **Repositorio GitHub** | <https://github.com/PatronSingleton-DisenoSoftware/Patron-Singleton-Software-EGI> |

> **Estado de este documento:** informe completo. Las cuatro secciones requeridas
> presentan el análisis del problema, el diseño arquitectónico alineado con el diagrama UML,
> la implementación del código fuente y el análisis crítico de principios SOLID y patrón Singleton.

---

## Índice

- [Sección I. Planteamiento y justificación del problema](#sección-i-planteamiento-y-justificación-del-problema)
- [Sección II. Diseño arquitectónico](#sección-ii-diseño-arquitectónico)
- [Sección III. Implementación práctica](#sección-iii-implementación-práctica)
- [Sección IV. Análisis crítico y principios SOLID](#sección-iv-análisis-crítico-y-principios-solid)
- [Anexo A. Decisiones de diseño](#anexo-a-decisiones-de-diseño)
- [Anexo B. Código fuente completo](#anexo-b-código-fuente-completo)
- [Lista de control de la consigna](#lista-de-control-de-la-consigna)

---

# Sección I. Planteamiento y justificación del problema

## 1.1. Escenario

La empresa **ENVIDIA** administra un edificio con sectores de acceso restringido: oficinas
administrativas, Recursos Humanos, depósitos y una sala de servidores. Cada sector tiene un
punto de acceso físico (lector de credencial) desde el cual un empleado intenta ingresar.

El sistema debe, para cada intento:

1. verificar la identificación del empleado,
2. consultar sus autorizaciones vigentes,
3. decidir si el ingreso se autoriza o se rechaza,
4. dejar un registro del intento, con fecha, hora y motivo.

El escenario es propio de este trabajo: no corresponde a los ejemplos analizados en clase
(envíos marítimos y terrestres para Factory Method).

## 1.2. La solución convencional y sus problemas de diseño

La forma "natural" de resolverlo sin patrones es que **cada punto de acceso construya su
propio gestor** de empleados y permisos: `new GestorAcceso()` en cada puerta. Esa decisión,
que parece inofensiva, produce exactamente los problemas que la consigna pide analizar:

### Acoplamiento fuerte

Cada puerta queda acoplada a una implementación concreta del gestor y a su propio ciclo de
vida. Si mañana el gestor necesita una dependencia (una base de datos, un servicio de
auditoría), hay que modificar **todos** los puntos de construcción. Además, los puntos de
acceso no pueden coordinarse entre sí porque no comparten estado ni conocen al mismo objeto.

### Rigidez ante el cambio

Agregar una regla nueva —por ejemplo, "los permisos vencen a los 30 días"— obliga a
replicar la lógica en cada copia del gestor. Cada cambio se multiplica por la cantidad de
puertas y cualquier olvido genera comportamiento distinto según el sector.

### Duplicación de estado (el problema crítico)

Si Recursos Humanos revoca el permiso de un empleado, la baja se aplica únicamente en la
instancia donde se ejecutó la acción. Las demás puertas siguen trabajando con su copia:

> Un empleado al que se le revocó el acceso puede ingresar por cualquier otra puerta que
> todavía no actualizó sus datos.

No es duplicación de código solamente: es **duplicación de la verdad**. Cada instancia
tiene su propia versión de quién puede entrar y adónde. El control de acceso deja de ser
centralizado y la empresa queda con una vulnerabilidad de seguridad, no con un problema
de estilo.

### Desperdicio de recursos

Cada instancia mantiene su propia lista de empleados, sectores y registros. Con N puertas
hay N copias de la misma información en memoria, y N lugares donde crece el historial sin
control.

### Resumen del contraste

| Aspecto | Sin patrón (N instancias) | Con Singleton (1 instancia) |
|---------|---------------------------|-----------------------------|
| Fuente de verdad | N copias que divergen | Una sola |
| Revocación de permiso | Se aplica en una puerta | Se aplica en todo el edificio |
| Cambio de regla | Se replica en cada copia | Se modifica una vez |
| Uso de memoria | N copias del mismo estado | Una copia compartida |
| Auditoría | Historial fragmentado | Historial único y consultable |

## 1.3. Justificación del patrón elegido

El requisito del dominio no es "crear objetos de una familia" (Factory Method) ni
"notificar cambios a varios interesados" (Observer): es que **exista un único gestor de
seguridad compartido por todo el sistema**. Ese es exactamente el problema que resuelve
Singleton.

Singleton garantiza:

1. **Una única instancia** de `ControlAcceso`, controlada por la propia clase.
2. **Un punto de acceso global** (`getInstance()`) para que cualquier puerta consulte el
   mismo estado sin recibirlo por parámetro.
3. **Coherencia inmediata**: una revocación se ve en todos los sectores en el mismo
   instante, porque no hay copias.

Frente a las alternativas del menú de la consigna:

| Patrón | Por qué no es la mejor opción aquí |
|--------|-------------------------------------|
| Factory Method | Resuelve la creación de objetos de una familia; no impide que existan varios gestores ni garantiza estado compartido. |
| Observer | Resolvería "avisar" a las puertas de un cambio, pero sigue permitiendo múltiples gestores como origen del estado: ataca el síntoma, no la causa. |

El patrón elegido **no se usa como adorno**: sin él, el sistema no puede cumplir su
requisito de seguridad. Esa es la justificación técnica que pide la Sección I.

## 1.4. Alcance

| Dentro del alcance | Fuera del alcance |
|--------------------|-------------------|
| Modelo de dominio: `Empleado`, `Sector`, `Permiso`, `RegistroAcceso` | Persistencia en base de datos |
| `ControlAcceso` como Singleton | Interfaz gráfica (la consigna no la exige) |
| Niveles de acceso acumulativos (1 a 5) | Integración con hardware de lectores |
| Permisos especiales y su revocación | Autenticación biométrica |
| Registro y consulta del historial de intentos | Despliegue en servidores de aplicación |

---

# Sección II. Diseño arquitectónico

## 2.1. Diagrama de clases UML

![Figura 1. Diagrama de clases UML del sistema de control de acceso](assets/uml-clases.png)

*Fuente editable del diagrama: [`assets/uml-clases.puml`](assets/uml-clases.puml). Los
miembros `static` aparecen subrayados; la visibilidad (`-` privado, `+` público) se
muestra en cada miembro.*

> Las figuras 1 y 2 están embebidas como imágenes, así que se incluyen al exportar a PDF
> sin depender de que el conversor renderice Mermaid.

## 2.2. Roles del patrón Singleton

La consigna exige respetar estrictamente los roles del patrón y mostrar la visibilidad
(`+` público, `-` privado, `#` protegido).

| Rol del patrón | Elemento en el diseño | Visibilidad |
|----------------|-----------------------|-------------|
| Atributo con la instancia única | `ControlAcceso.instancia` | `- private static` |
| Constructor privado | `ControlAcceso()` | `- private` |
| Método estático de acceso | `ControlAcceso.getInstance()` | `+ public static` |
| Cliente | `Main` (clase de prueba / consola) | `+ public static void main` |

| Elemento | Visibilidad | Motivo |
|----------|-------------|--------|
| `instancia` | `-` | Nadie fuera de la clase puede reemplazar la referencia. |
| Constructor | `-` | Impide `new ControlAcceso()` desde el cliente. |
| `getInstance()` | `+` | Único camino permitido para obtener el objeto. |
| Atributos del dominio (`empleados`, `sectores`, `permisos`, `registros`) | `-` | Encapsulamiento; se accede por métodos públicos. |
| Métodos de gestión | `+` | API que consumen los puntos de acceso. |
| Métodos auxiliares de búsqueda y de permisos | `+` / `-` | `buscarEmpleado`, `buscarEmpleadoPorDni` y `buscarSector` (públicos); `buscarPermiso` y `tienePermisoEspecialActivo` (privados). |

## 2.3. Clases, atributos y responsabilidades

| Clase | Atributos clave | Responsabilidad |
|-------|-----------------|-----------------|
| `ControlAcceso` | `instancia`, `empleados`, `sectores`, `permisos`, `registros`, `mostrarMensajesRegistro` | Fuente única de verdad: altas, niveles, permisos, verificación y consulta. |
| `Empleado` | `idEmpleado`, `dni`, `nombre`, `apellido`, `nivelAcceso`, `registroValido` | Datos del empleado, su nivel y la validez calculada de sus datos. |
| `Sector` | `idSector`, `nombre`, `nivelRequerido` | Área restringida y nivel mínimo para ingresar. |
| `Permiso` | `empleado`, `sector`, `activo`, `especial` | Relación empleado–sector, con baja lógica. |
| `RegistroAcceso` | `empleado`, `sector`, `fechaHora`, `autorizado`, `motivo` | Evidencia del intento, para auditoría. |
| `NivelAcceso` | `NIVEL_1` … `NIVEL_5` | Niveles acumulativos según su valor numérico (`getNivel()`). |

> **Nombres tal como están en el código.** El diagrama y estas tablas usan los
> identificadores reales de `src/` en `main`, incluidos `getInstance()`, `nivelAcceso`,
> `idEmpleado`, `idSector`, `registroValido` y `especial`. Las brechas que aún quedan
> frente a los ADR (`boolean especial` y `verificarAcceso` que registra) están documentadas
> en el [Anexo](#anexo-a-decisiones-de-diseño) y en
> [`informe/05-implementacion.md`](informe/05-implementacion.md#55-brecha-entre-diseño-e-implementación).
>
> Las clases `ControlAcceso`, `Empleado`, `Sector`, `Permiso` y `RegistroAcceso` se
> declaran `final`: no se heredan y el compilador puede verificar esa restricción
> ([ADR-09](#anexo-a-decisiones-de-diseño)).

## 2.4. Reglas de negocio del dominio

Las reglas describen el comportamiento que el sistema **debe** cumplir. La columna de
estado indica si el código de `main` ya las implementa.

| ID | Regla | Estado en el código |
|----|-------|---------------------|
| RN-01 | Un empleado con nivel *n* puede ingresar a todo sector cuyo `nivelRequerido` sea `NIVEL_1` … `NIVEL_n`, según el valor numérico del enum. | ✅ Implementado vía `getNivel()` |
| RN-02 | Un permiso con `especial == true` y `activo == true` permite el ingreso aunque el nivel sea insuficiente. | ✅ Implementado en `tienePermisoEspecialActivo()` |
| RN-03 | Un permiso revocado (`activo == false`) no habilita el ingreso y no se elimina. | ✅ Implementado en `revocarPermiso()` y `Permiso.revocar()` |
| RN-04 | Todo intento se registra, sea autorizado o rechazado. | ✅ Implementado en `verificarAcceso()` |
| RN-05 | Todo rechazo registra un `motivo`. | ✅ Implementado con motivos descriptivos |
| RN-06 | La revocación es visible de inmediato en todos los sectores (consecuencia de la instancia única). | ✅ Garantizado por Singleton |
| RN-07 | Solo se concede un permiso a un empleado y un sector existentes. | ✅ Validado en `concederPermiso()` |

## 2.5. Flujo previsto de un intento de ingreso

![Figura 2. Secuencia de un intento de ingreso](assets/flujo-acceso.png)

*Fuente editable del diagrama: [`assets/flujo-acceso.puml`](assets/flujo-acceso.puml).*

---

# Sección III. Implementación práctica

## 3.1. Entorno, versión y ejecución

| Requisito | Valor |
|-----------|-------|
| Lenguaje | Java |
| Versión usada | JDK 23.0.1 (el código no usa APIs posteriores a Java 17) |
| IDE | IntelliJ IDEA Community 2025.2 |
| Estructura de carpetas | `src/` para fuentes, `docs/` para documentación, `out/` para binarios |

### Cómo compilar y ejecutar desde la terminal

```powershell
# Compilar todo src/ y dejar los .class en out/
javac -d out (Get-ChildItem src\*.java).FullName

# Ejecutar la clase principal
java -cp out Main
```

## 3.2. Estructura de archivos

```
src/
├── ControlAcceso.java    ← Singleton: listas, constructor, getInstance(), lógica de negocio e historial
├── Empleado.java         ← 6 atributos (incluye registroValido), validación de DNI y datos, getters, setters y toString()
├── Main.java             ← clase de prueba, menú interactivo y demostración automática
├── Permiso.java          ← 4 atributos, constructor con datos, getters, setters y revocar()
├── RegistroAcceso.java   ← 5 atributos, constructor con LocalDateTime.now(), getters, setters y toString()
├── Sector.java           ← 3 atributos, validación vía setters, getters, setters y toString()
└── NivelAcceso.java      ← enum NIVEL_1 … NIVEL_5
```

## 3.3. Código del Singleton (`ControlAcceso.java`)

Los bloques de esta sección son extractos; el código íntegro de las siete clases está en el [Anexo B](#anexo-b-código-fuente-completo).

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

    public void setMostrarMensajesRegistro(boolean mostrar) { /* ... */ }

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

    // Auxiliares de busqueda y permisos
    public Empleado buscarEmpleado(int idEmpleado) { /* ... */ }
    public Empleado buscarEmpleadoPorDni(int dni) { /* ... */ }
    public Sector buscarSector(int idSector) { /* ... */ }
    private Permiso buscarPermiso(Empleado empleado, Sector sector) { /* ... */ }
    private boolean tienePermisoEspecialActivo(Empleado empleado, Sector sector) { /* ... */ }
}
```

| Elemento | Por qué |
|----------|---------|
| `private static ControlAcceso instancia;` | `static`: un solo casillero para toda la clase. `private`: nadie de afuera lo reemplaza. |
| `private ControlAcceso()` | `new ControlAcceso()` no compila fuera de la clase. Además inicializa las cuatro listas. |
| `public static ... getInstance()` | Único camino de acceso; crea la instancia la primera vez. |
| `if (instancia == null)` | Inicialización perezosa (*lazy*). |
| `private final List<...>` | La referencia a cada colección no se reemplaza después del constructor. |
| `mostrarMensajesRegistro` | Permite silenciar los avisos de la carga inicial y reactivarlos para el menú. |
| Métodos de gestión | Implementan validaciones completas, jerarquías de nivel y revocación. |
| `verificarAcceso(int, int)` y `consultarHistorial()` | Validan permisos, registran intentos de acceso y retornan copia defensiva de la colección (`List<RegistroAcceso>`). |

## 3.4. Clase de prueba (`Main`)

`Main.java` contiene el punto de entrada interactivo que utiliza el Singleton `ControlAcceso` para dar de alta empleados, validar entradas por consola y alimentar el estado centralizado del sistema.

Para verificar que dos llamadas devuelven exactamente la misma referencia en memoria:

```java
ControlAcceso puertaPrincipal = ControlAcceso.getInstance();
ControlAcceso salaServidores = ControlAcceso.getInstance();

System.out.println("Puerta principal: " + puertaPrincipal);
System.out.println("Sala de servidores: " + salaServidores);
System.out.println("¿Misma instancia? " + (puertaPrincipal == salaServidores));
```

Salida esperada:

```
Puerta principal: ControlAcceso@1b6d3586
Sala de servidores: ControlAcceso@1b6d3586
¿Misma instancia? true
```

### Flujo completo demostrado por el sistema

1. Obtener el gestor desde dos "puertas" distintas y comprobar que es el mismo objeto.
2. Registrar un sector (`Sala de servidores`, nivel `NIVEL_4`) y dos empleados.
3. Un empleado `NIVEL_2` intenta entrar → **rechazado** con motivo.
4. Se le concede un permiso con `especial = true` → vuelve a intentar → **autorizado**.
5. Se revoca el permiso → intenta de nuevo → **rechazado**.
6. Se consulta el historial: 3 registros con fecha/hora y resultado.

Ese recorrido demuestra las dos caras del patrón: una sola instancia y un estado
compartido que cambia para todos los puntos de acceso. El gestor implementa la lógica
de `verificarAcceso(int, int)`, el registro de cada intento y `consultarHistorial()`
devolviendo la colección de registros (`List<RegistroAcceso>`) como copia defensiva.

La demostración automática (opción 9 del menú) es **idempotente**: antes de dar de alta
sus sectores y empleados de prueba consulta si ya existen, de modo que puede ejecutarse
varias veces sobre la misma instancia sin duplicar datos ni imprimir avisos de "ya existe".
La carga inicial de `main()` silencia los mensajes de registro con
`setMostrarMensajesRegistro(false)` y los reactiva antes de mostrar el menú.

El menú también incluye la opción 10, que permite reasignar el nivel de un empleado ya
registrado mediante `asignarNivel(empleado, nivel)`. El cambio se refleja de inmediato en
la siguiente verificación de acceso, porque el estado es único.

## 3.5. Estado del código frente a la consigna

| Requisito de la Sección III | Estado |
|-----------------------------|--------|
| Código completo, sin partes por completar | ✅ Las entidades y la lógica de ControlAcceso están implementadas |
| Restricciones del patrón (constructor privado, atributo estático privado, acceso estático público) | ✅ Implementadas |
| Clase de prueba con flujo simulado | ✅ Main interactivo con demostración automática conectada al Singleton |
| Versión del lenguaje indicada | ✅ Java 23 (compatible 17+) |
| Cómo se ejecuta | ✅ Comandos en §3.1 |
| Compilación sin advertencias | ✅ `javac -Xlint:all` sin advertencias |
| Enlace al GitHub personal | ✅ Repositorio público en §3.6 |

## 3.6. Enlace al repositorio

**Repositorio:** <https://github.com/PatronSingleton-DisenoSoftware/Patron-Singleton-Software-EGI>

El repositorio contiene `src/` con el código, `docs/` con este documento y las
instrucciones de ejecución en `docs/manual/`.

---

# Sección IV. Análisis crítico y principios SOLID

## 4.1. Relación con SOLID

La consigna pide conectar la teoría con el propio código, indicando beneficios y
tensiones. El análisis se hace sobre `ControlAcceso`, que es donde el patrón concentra
decisiones.

### S — Responsabilidad Única (tensión principal)

`ControlAcceso` acumula, como mínimo, estas responsabilidades:

1. garantizar la instancia única (`instancia`, `getInstance()`);
2. administrar empleados (`registrarEmpleado`);
3. administrar sectores (`registrarSector`);
4. administrar permisos (`concederPermiso`, `revocarPermiso`);
5. asignar niveles (`asignarNivel`);
6. decidir autorizaciones y exponer el historial (`verificarAcceso(int, int)`,
   `consultarHistorial()`).

Son **seis motivos para cambiar** la misma clase: si cambia la política de permisos, si se
agrega persistencia, si se modifica el formato del historial. El patrón, por su propia
naturaleza de "punto único", empuja en dirección contraria a SRP. La consigna lo señala
explícitamente como el punto de partida para SOLID.

**Mitigación aplicada:** `consultarHistorial()` devuelve una copia defensiva (`new ArrayList<>(this.registros)`)
para proteger el encapsulamiento del historial. `verificarAcceso` gestiona internamente la creación del registro.

**Mitigación posible a futuro:** extraer `RepositorioEmpleados`, `RepositorioSectores`,
`ServicioPermisos` y `ServicioRegistros`, y dejar a `ControlAcceso` como fachada. Se evita
hoy porque el alcance de la EGI no lo exige y agregaría clases sin necesidad demostrada.

### O — Abierto/Cerrado (tensión)

- **Beneficio parcial:** agregar un sector nuevo (Depósito, nivel `NIVEL_2`) o un empleado
  nuevo **no requiere tocar código**: es un dato. Los niveles viven en el enum
  `NivelAcceso` y los sectores en la colección `sectores`.
- **Tensión:** agregar una **regla** nueva (por ejemplo, "no se permite ingresar entre las
  23 y las 6") obliga a modificar `verificarAcceso()`. La clase no está cerrada a
  modificación en su dimensión de comportamiento.

**Alternativa:** modelar las reglas como estrategias (`ReglaAcceso` con implementaciones
`ReglaNivel`, `ReglaHorario`) y que `verificarAcceso()` recorra una lista. Sería la
solución "abierta/cerrada"; se documenta como mejora, no se implementa en esta entrega.

### L — Sustitución de Liskov (sin conflicto)

El diseño no usa jerarquías de herencia: `Empleado`, `Sector`, `Permiso` y
`RegistroAcceso` son clases independientes; `NivelAcceso` es un enum y `TipoPermiso` no
existe en el código. Además, las cinco clases del dominio se declaran `final`, de modo
que ni siquiera por herencia se puede alterar su contrato. No hay subtipos que puedan
romper el contrato de un supertipo, por lo que LSP no se ve comprometido. Es una
consecuencia de haber preferido **composición** (un `Permiso` referencia a un `Empleado`
y a un `Sector`) sobre herencia.

### I — Segregación de Interfaces

Hoy no hay interfaces, así que el principio no se viola ni se aprovecha. Si se introdujera
una interfaz `GestorAcceso` con los ocho métodos públicos, cualquier cliente que solo
necesite consultar el historial se vería obligado a depender de métodos que no usa
(acoplamiento innecesario). La versión correcta sería segregarla:

- `ConsultorAcceso` → `verificarAcceso`, `consultarHistorial`
- `AdministradorAcceso` → altas, niveles, permisos

### D — Inversión de Dependencias (tensión para testing)

`Main` y los puntos de acceso llaman a `ControlAcceso.getInstance()`: dependen de una
**clase concreta** y de un método estático. No hay forma de sustituir el gestor por un
doble de prueba sin tocar el código. Esto es la raíz de la "dificultad de testing" que la
consigna pide analizar.

**Mejora:** definir una interfaz `GestorAcceso`, hacer que `ControlAcceso` la implemente y
que los clientes reciban la dependencia por constructor (inyección). El Singleton quedaría
como **una** implementación posible, no como la única forma de obtener el colaborador.

## 4.2. Ventajas técnicas del diseño

| Ventaja | Dónde se ve en el código |
|---------|--------------------------|
| **Fuente única de verdad** | `instancia` + las cuatro colecciones en `ControlAcceso`. |
| **Desacoplamiento entre puntos de acceso** | Las puertas no se conocen entre sí; solo llaman a `getInstance()`. |
| **Coherencia inmediata** | `revocarPermiso()` modifica el único estado y el próximo `verificarAcceso()` de cualquier puerta lo refleja de inmediato. |
| **Eficiencia de recursos** | Una sola copia de empleados, sectores, permisos y registros en memoria. |
| **Auditoría centralizada** | `consultarHistorial()` apunta a una única colección de registros, no a historiales fragmentados. |
| **Extensibilidad de datos** | Agregar sectores o empleados no requiere recompilar lógica nueva. |

## 4.3. Desventajas y riesgos

| Riesgo | Explicación | Mitigación |
|--------|-------------|------------|
| **Estado global** | Cualquier clase puede obtener el gestor y modificar el sistema; las dependencias quedan ocultas en el código. | Inyectar el gestor en lugar de llamar a `getInstance()` dentro de la lógica. |
| **Problemas en entornos multihilo** | Dos hilos que evalúen `instancia == null` a la vez pueden crear dos instancias. Además, las colecciones (`ArrayList`) no son seguras para escritura concurrente. | `synchronized`, inicialización anticipada, holder idiom o `enum`; colecciones concurrentes. |
| **Dificultades de testing** | El estado persiste entre pruebas y no se puede sustituir por un mock. | Interfaz + inyección; método de reinicio visible solo en el paquete de pruebas. |
| **Tensión con SRP** | La clase central acumula creación, altas, niveles, permisos, verificación y consulta del historial. | Extraer servicios y dejar una fachada. |
| **Fugas de memoria** | La referencia `static` mantiene viva la instancia durante toda la vida del `ClassLoader`; en servidores de aplicación, un redeploy puede dejar el objeto anterior retenido. En escritorio el impacto es bajo. | Evitar estado estático en aplicaciones con recarga de clases; cerrar y liberar recursos. |
| **Crecimiento sin límite del historial** | `registros` crece con cada intento de acceso y no tiene purga. | Política de retención o persistencia con rotación. |
| **Serialización y reflexión** | Pueden crear una segunda instancia si no se protege (`readResolve`) o si el constructor privado se fuerza por reflexión. | Usar `enum` como implementación más robusta. |

## 4.4. Autoevaluación

La estructura del patrón resuelve el problema que el escenario planteaba: con una sola
instancia, la revocación de un permiso no puede quedar "en una puerta". Esa es la ganancia
concreta demostrada por el código del Singleton.

El costo también es concreto: `ControlAcceso` es la clase con más motivos para cambiar del
sistema, el testing es más difícil de lo que sería con dependencias inyectadas y la
implementación actual no es segura frente a hilos. Ninguno de esos costos invalida la
elección del patrón, pero **un informe que los omitiera sería incompleto**: la consigna
evalúa justamente el criterio para reconocerlos.

La deuda técnica queda registrada en el [Anexo](#anexo-a-decisiones-de-diseño) (ADR-02, ADR-06 y ADR-07).

---

# Anexo A. Decisiones de diseño

Decisiones tomadas antes de programar, con el estado real de aplicación en el código de
`main`. El detalle extendido está en `docs/informe/09-decisiones.md`.

| ADR | Decisión | Motivo | Estado en el código |
|-----|----------|--------|---------------------|
| ADR-01 | `NivelAcceso` como enum con valor numérico | Evita niveles inválidos y centraliza la comparación acumulativa. | ✅ Aplicado: enum con `NIVEL_1`…`NIVEL_5` y `alcanzaA()` por `getNivel()`. |
| ADR-02 | `TipoPermiso` como enum | El atributo `tipo` no tenía tipo definido. | ❌ No aplicado: se usa `boolean especial`; `TipoPermiso.java` no existe. |
| ADR-03 | `fechaHora` como `LocalDateTime` | Permite ordenar y filtrar el historial sin parsear texto. | ✅ Aplicado: asignado automáticamente en constructor con `LocalDateTime.now()`. |
| ADR-04 | `idEmpleado` / `idSector` en lugar de `idS` | Nombres explícitos por entidad y consistentes con la convención. | ✅ Aplicado: atributos, getters y setters renombrados. |
| ADR-05 | La colección `permisos` vive en `ControlAcceso` | Sin ella, conceder y revocar no tendrían un único lugar donde persistir. | ✅ Aplicado: `private List<Permiso> permisos`. |
| ADR-06 | `verificarAcceso()` no registra; `registrarIntento()` sí | Separa consulta de efecto secundario. | ❌ No aplicado: `verificarAcceso(int, int)` evalúa y registra; `consultarHistorial()` devuelve copia. |
| ADR-07 | Singleton lazy sin `synchronized` | Claridad didáctica; el riesgo multihilo se documenta en §4.3. | ✅ Aplicado: `getInstance()` crea la instancia en la primera llamada. |
| ADR-08 | Setters en todas las clases | Cumple la regla de la cátedra, aun cuando `RegistroAcceso` podría ser inmutable. | ✅ Aplicado: constructores, getters y setters en todas las clases; copia defensiva en historial. |
| ADR-09 | Clases de dominio `final` y validez calculada | Evita herencia no prevista y advertencias `this-escape`; la validez se recalcula desde los datos. | ✅ Aplicado: `final` en las cinco clases; `Empleado.datosValidos()` y `setRegistroValido()` no permiten forzar un estado inválido. |

---

# Anexo B. Código fuente completo

Se reproduce literalmente el contenido de `src/` (una clase por archivo). Es el mismo
código que compila con `javac -Xlint:all` y que se ejecuta con `java -cp out Main`.

### ControlAcceso.java

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
    private ControlAcceso(){
        this.empleados = new ArrayList<>();
        this.sectores = new ArrayList<>();
        this.permisos = new ArrayList<>();
        this.registros = new ArrayList<>();
    }

    // Metodo de acceso unico
    public static ControlAcceso getInstance() {
        if (instancia == null){
            instancia = new ControlAcceso();
        }
        return instancia;
    }

    // Metodos publicos o privados
    public void registrarEmpleado(Empleado empleado){
        // Guarda: no se aceptan datos vacios
        if (empleado == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        if (!empleado.isRegistroValido()) {
            System.out.println("Error, no se puede registrar un empleado con datos invalidos");
            return;
        }
        if (empleado.getIdEmpleado() <= 0) {
            System.out.println("Error, el ID del empleado debe ser mayor a 0");
            return;
        }
        // Un empleado no se puede dar de alta dos veces por ID
        if (buscarEmpleado(empleado.getIdEmpleado()) != null){
            System.out.println("Ya existe un empleado con ID " + empleado.getIdEmpleado());
            return;   // no se agrega
        }
        // Tampoco por DNI
        if (buscarEmpleadoPorDni(empleado.getDni()) != null){
            System.out.println("Ya existe un empleado con DNI " + empleado.getDni());
            return;   // no se agrega
        }
        empleados.add(empleado);
        if (mostrarMensajesRegistro) {
            System.out.println("Empleado registrado: " + empleado.getNombre() + " " + empleado.getApellido());
        }
    }

    public void registrarSector(Sector sector){
        // Guarda: no se aceptan datos vacios
        if (sector == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        if (sector.getIdSector() <= 0){
            System.out.println("Error, el ID del sector debe ser mayor a 0");
            return;
        }
        if (!sector.datosValidos()){
            System.out.println("Error, no se puede registrar un sector con datos invalidos");
            return;
        }
        // Un sector no se puede dar de alta dos veces
        if (buscarSector(sector.getIdSector()) != null){
            System.out.println("Ya existe un sector con ID " + sector.getIdSector());
            return;   // no se agrega
        }
        sectores.add(sector);
        if (mostrarMensajesRegistro) {
            System.out.println("Sector registrado: " + sector.getNombre());
        }
    }

    public void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso){
        // Guarda: no se aceptan datos vacios
        if (empleado == null || nivelAcceso == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        // Solo se le asigna nivel a un empleado ya registrado
        Empleado registrado = buscarEmpleado(empleado.getIdEmpleado());
        if (registrado == null){
            System.out.println("No existe un empleado con ID " + empleado.getIdEmpleado());
            return;
        }
        registrado.setNivelAcceso(nivelAcceso);
        System.out.println("Nivel de " + registrado.getNombre() + " actualizado a " + nivelAcceso);
    }

    public void concederPermiso(Permiso permiso){
        // Guarda: no se aceptan datos vacios
        if (permiso == null || permiso.getEmpleado() == null || permiso.getSector() == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        Empleado empleadoRegistrado = buscarEmpleado(permiso.getEmpleado().getIdEmpleado());
        Sector sectorRegistrado = buscarSector(permiso.getSector().getIdSector());

        // CP-12: el permiso necesita un empleado y un sector que ya existan
        if (empleadoRegistrado == null){
            System.out.println("No existe el empleado con ID " + permiso.getEmpleado().getIdEmpleado()
                    + ". Permiso rechazado (CP-12)");
            return;
        }
        if (sectorRegistrado == null){
            System.out.println("No existe el sector con ID " + permiso.getSector().getIdSector()
                    + ". Permiso rechazado (CP-12)");
            return;
        }

        // Se guardan las referencias registradas, no las copias que traiga el cliente
        permiso.setEmpleado(empleadoRegistrado);
        permiso.setSector(sectorRegistrado);

        // Un mismo empleado y sector no se cargan dos veces
        Permiso existente = buscarPermiso(empleadoRegistrado, sectorRegistrado);
        if (existente != null && existente.estaActivo()){
            System.out.println("Ya existe un permiso activo para ese empleado y sector");
            return;
        }
        // Si estaba revocado, se vuelve a habilitar en vez de duplicar
        if (existente != null){
            existente.setEmpleado(empleadoRegistrado);
            existente.setSector(sectorRegistrado);
            existente.setActivo(true);
            existente.setEspecial(permiso.esEspecial());
            System.out.println("Permiso reactivado para " + empleadoRegistrado.getNombre()
                    + " en " + sectorRegistrado.getNombre());
            return;
        }
        permisos.add(permiso);
        System.out.println("Permiso concedido a " + empleadoRegistrado.getNombre()
                + " en " + sectorRegistrado.getNombre());
    }

    public void revocarPermiso(Permiso permiso){
        // Guarda: no se aceptan datos vacios
        if (permiso == null || permiso.getEmpleado() == null || permiso.getSector() == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        // Se revoca el permiso guardado, no el objeto que llega por parametro
        Permiso existente = buscarPermiso(permiso.getEmpleado(), permiso.getSector());
        if (existente == null){
            System.out.println("No existe un permiso para ese empleado y sector");
            return;
        }
        if (!existente.estaActivo()){
            System.out.println("El permiso ya estaba revocado");
            return;
        }
        existente.revocar();
        System.out.println("Permiso revocado a " + existente.getEmpleado().getNombre()
                + " en " + existente.getSector().getNombre());
    }

    public boolean verificarAcceso(int numEmpleado, int numSector){
        Empleado empleado = buscarEmpleado(numEmpleado);
        Sector sector = buscarSector(numSector);

        // 1. Validaciones previas con registro en el historial (RN-04 y RN-05)
        if (empleado == null){
            String motivo = "Empleado no registrado";
            System.out.println("Acceso denegado: " + motivo);
            this.registros.add(new RegistroAcceso(null, sector, false, motivo));
            return false;
        }
        if (sector == null) {
            String motivo = "Sector inexistente";
            System.out.println("Acceso denegado: " + motivo);
            this.registros.add(new RegistroAcceso(empleado, null, false, motivo));
            return false;
        }
        boolean autorizado = false;
        String motivo = null;

        // 2. Verificar jerarquia de niveles (RN-01)
        if (empleado.getNivelAcceso() != null && empleado.getNivelAcceso().alcanzaA(sector.getNivelRequerido())) {
            autorizado = true;
            motivo = "Acceso autorizado por nivel";
        }
        // 3. Si el nivel no alcanza, verificar permiso especial activo (RN-02)
        else if (tienePermisoEspecialActivo(empleado, sector)) {
            autorizado = true;
            motivo = "Acceso autorizado por permiso especial";
        }
        // 4. Rechazo por nivel insuficiente (RN-05)
        else {
            autorizado = false;
            motivo = "Nivel insuficiente";
            System.out.println("Acceso denegado a " + empleado.getNombre() + " al sector "
                    + sector.getNombre() + ". Motivo: " + motivo);
        }
        // 5. Registrar intento en el historial (RN-04)
        RegistroAcceso registro = new RegistroAcceso(empleado, sector, autorizado, motivo);
        this.registros.add(registro);

        if (autorizado) {
            System.out.println("Acceso permitido a " + empleado.getNombre() + " al sector " + sector.getNombre());
        }

        return autorizado;
    }


    // Devuelve el historial de intentos (copia defensiva)
    public List<RegistroAcceso> consultarHistorial(){
        if (this.registros.isEmpty()){
            System.out.println("El historial esta vacio: no hay intentos de acceso registrados");
            return new ArrayList<>();
        }
        // Se devuelve una copia para que el cliente no modifique la lista interna
        return new ArrayList<>(this.registros);
    }

    // Devuelve copia defensiva de empleados registrados
    public List<Empleado> getEmpleados() {
        return new ArrayList<>(this.empleados);
    }

    // Devuelve copia defensiva de sectores registrados
    public List<Sector> getSectores() {
        return new ArrayList<>(this.sectores);
    }

    // Devuelve copia defensiva de permisos
    public List<Permiso> getPermisos() {
        return new ArrayList<>(this.permisos);
    }

    // --- Ayudantes -------
    public Empleado buscarEmpleado(int idEmpleado){
        for (Empleado emp : this.empleados){
            if (emp.getIdEmpleado() == idEmpleado){
                return emp;
            }
        }
        return null;
    }

    public Empleado buscarEmpleadoPorDni(int dni){
        for (Empleado emp : this.empleados){
            if (emp.getDni() == dni){
                return emp;
            }
        }
        return null;
    }

    public Sector buscarSector(int idSector){
        for (Sector sec : this.sectores){
            if (sec.getIdSector() == idSector){
                return sec;
            }
        }
        return null;
    }

    private Permiso buscarPermiso(Empleado empleado, Sector sector){
        for (Permiso permiso : permisos){
            if (permiso.getEmpleado() != null && permiso.getSector() != null
                    && permiso.getEmpleado().getIdEmpleado() == empleado.getIdEmpleado()
                    && permiso.getSector().getIdSector() == sector.getIdSector()){
                return permiso;
            }
        }
        return null;
    }

    private boolean tienePermisoEspecialActivo(Empleado empleado, Sector sector) {
        for (Permiso p : this.permisos) {
            // Coincide empleado y sector, es especial y sigue activo
            if (p.getEmpleado() != null && p.getSector() != null
                    && p.getEmpleado().getIdEmpleado() == empleado.getIdEmpleado()
                    && p.getSector().getIdSector() == sector.getIdSector()
                    && p.esEspecial()
                    && p.estaActivo()) {
                return true;
            }
        }
        return false;
    }
}
```

### Empleado.java

```java
public final class Empleado {
    private int idEmpleado;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcceso;
    private boolean registroValido;

    public Empleado(int idEmpleado, int dni, String nombre, String apellido, NivelAcceso nivelAcceso) {
        // Se guardan los datos primero: si algo es invalido, el objeto conserva
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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        if (!esDniValido(dni)) {
            System.out.println("Error: el DNI debe tener 7 u 8 digitos validos.");
            return;
        }
        this.dni = dni;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        if (idEmpleado <= 0) {
            System.out.println("Error: el ID del empleado debe ser mayor a 0.");
            return;
        }
        this.idEmpleado = idEmpleado;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().equals("")) {
            System.out.println("Error: el apellido no puede estar vacio.");
            return;
        }
        this.apellido = apellido.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().equals("")) {
            System.out.println("Error: el nombre no puede estar vacio.");
            return;
        }
        this.nombre = nombre.trim();
    }

    public NivelAcceso getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(NivelAcceso nivelAcceso) {
        if (nivelAcceso == null) {
            System.out.println("Error: el nivel de acceso no puede ser nulo.");
            return;
        }
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String toString() {
        return "Empleado [ID: " + idEmpleado + " | DNI: " + dni + " | Nombre: " + nombre + " " + apellido + " | Nivel: " + nivelAcceso + "]";
    }
}
```

### Sector.java

```java
public final class Sector {
    private int idSector;
    private String nombre;
    private NivelAcceso nivelRequerido;

    public Sector(int idSector, String nombre, NivelAcceso nivelRequerido){
        // Se parte de valores seguros y se delega en los setters,
        // asi el constructor y los setters validan siempre igual.
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

    public String getNombre() {
        return nombre;
    }

    public int getIdSector() {
        return idSector;
    }

    public NivelAcceso getNivelRequerido() {
        return nivelRequerido;
    }

    public void setIdSector(int idSector) {
        if (idSector <= 0) {
            System.out.println("Error: el ID del sector debe ser mayor a 0.");
            return;
        }
        this.idSector = idSector;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().equals("")) {
            System.out.println("Error: el nombre del sector no puede estar vacio.");
            return;
        }
        this.nombre = nombre.trim();
    }

    public void setNivelRequerido(NivelAcceso nivelRequerido) {
        if (nivelRequerido == null) {
            System.out.println("Error: el nivel requerido no puede ser nulo.");
            return;
        }
        this.nivelRequerido = nivelRequerido;
    }

    @Override
    public String toString() {
        return "Sector [ID: " + idSector + " | Nombre: " + nombre + " | Nivel Requerido: " + nivelRequerido + "]";
    }
}
```

### Permiso.java

```java
public final class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;


    public Permiso(Empleado empleado, Sector sector, boolean especial){
        this.empleado = empleado;
        this.sector = sector;
        this.especial = especial;
        this.activo = true;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Sector getSector() {
        return sector;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean esEspecial(){
        return especial;
    }

    public void setEmpleado(Empleado empleado) {
        if (empleado == null) {
            System.out.println("Error: el empleado del permiso no puede ser nulo.");
            return;
        }
        this.empleado = empleado;
    }

    public void setSector(Sector sector) {
        if (sector == null) {
            System.out.println("Error: el sector del permiso no puede ser nulo.");
            return;
        }
        this.sector = sector;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public void revocar(){
        this.activo = false;
    }

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

### RegistroAcceso.java

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


    public RegistroAcceso(Empleado empleado, Sector sector, boolean autorizado, String motivo){
        // empleado y sector pueden ser null si el intento se hizo con una
        // ID que no existe; el motivo explica el rechazo.
        this.empleado = empleado;
        this.sector = sector;
        this.autorizado = autorizado;
        this.fechaHora = LocalDateTime.now();
        setMotivo(motivo);   // valida que el motivo no este vacio (CP-11)
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Sector getSector() {
        return sector;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora del registro no puede ser nula");
        }
        this.fechaHora = fechaHora;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public void setMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo del registro no puede estar vacio");
        }
        this.motivo = motivo.trim();
    }

    @Override
    public String toString() {
        // Si el intento vino con datos inexistentes, se muestra el texto en vez de un null
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

### NivelAcceso.java

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

    public int getNivel() {
        return nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Devuelve true si este nivel alcanza al nivel requerido (RN-01)
    public boolean alcanzaA(NivelAcceso nivelRequerido) {
        if (nivelRequerido == null) {
            return false;
        }
        return this.nivel >= nivelRequerido.getNivel();
    }

    @Override
    public String toString() {
        return name() + " (" + descripcion + ")";
    }
}
```

### Main.java

```java
import java.util.List;
import java.util.Scanner;

public class Main {

    // Lee un entero como linea completa para poder rechazar entradas mezcladas o invalidas.
    private static int leerEntero(Scanner entradaDatos, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = entradaDatos.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException error) {
                System.out.println("El tipo de dato no coincide. Ingrese un numero entero.");
            }
        }
    }

    // Los nombres aceptan letras Unicode, espacios, guiones y apostrofes.
    private static String leerNombre(Scanner entradaDatos, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = entradaDatos.nextLine().trim();
            if (entrada.matches("(?U)[\\p{L}]+(?:[ '\\-][\\p{L}]+)*")) {
                return entrada;
            }
            System.out.println("El tipo de dato no coincide. Ingrese un nombre con letras.");
        }
    }

    // El nombre del sector puede incluir numeros, por ejemplo "Piso 2", pero debe tener letras.
    private static String leerNombreSector(Scanner entradaDatos, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = entradaDatos.nextLine().trim();
            if (!entrada.isEmpty() && entrada.matches("(?s).*\\p{L}.*")) {
                return entrada;
            }
            System.out.println("El tipo de dato no coincide. Ingrese un texto con al menos una letra.");
        }
    }

    // Metodo auxiliar para leer y validar un NivelAcceso por consola
    private static NivelAcceso leerNivelAcceso(Scanner entradaDatos) {
        System.out.println("Nivel 1 - Acceso General");
        System.out.println("Nivel 2 - Acceso Interno");
        System.out.println("Nivel 3 - Acceso Restringido");
        System.out.println("Nivel 4 - Acceso Tecnico");
        System.out.println("Nivel 5 - Acceso Directivo");

        int nivel;
        do {
            nivel = leerEntero(entradaDatos, "Seleccione nivel (1 a 5): ");
            if (nivel < 1 || nivel > 5) {
                System.out.println("Nivel invalido. Debe ser un numero entre 1 y 5.");
            }
        } while (nivel < 1 || nivel > 5);

        return NivelAcceso.values()[nivel - 1];
    }

    // 1 - Registrar empleado
    public static void funcionRegistrarEmpleado(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Empleado ---");
        int idEmpleado = leerEntero(entradaDatos, "ID del empleado: ");

        if (idEmpleado <= 0) {
            System.out.println("Error: El ID del empleado debe ser mayor a 0.");
            return;
        }
        // Evitar que coincida con el ID de un empleado
        if (control.buscarEmpleado(idEmpleado) != null) {
            System.out.println("Error: Ya existe un empleado con ese ID.");
            return;
        }

        System.out.print("DNI: ");
        String dniTexto = entradaDatos.nextLine().trim();

        boolean soloNumeros = !dniTexto.equals("");
        for (int i = 0; i < dniTexto.length(); i++) {
            if (!Character.isDigit(dniTexto.charAt(i))) {
                soloNumeros = false;
                break;
            }
        }

        if (!soloNumeros || (dniTexto.length() != 7 && dniTexto.length() != 8)) {
            System.out.println("DNI invalido. Debe tener 7 u 8 digitos numericos.");
            return;
        }

        int dni = Integer.parseInt(dniTexto);
        // Evitar que se repita el DNI de otro empleado
        if (control.buscarEmpleadoPorDni(dni) != null) {
            System.out.println("Error: Ya existe un empleado con ese DNI.");
            return;
        }

        String nombre = leerNombre(entradaDatos, "Nombre: ");
        String apellido = leerNombre(entradaDatos, "Apellido: ");

        boolean nombreValido = (nombre != null && !nombre.trim().equals(""));
        boolean apellidoValido = (apellido != null && !apellido.trim().equals(""));

        if (!nombreValido || !apellidoValido) {
            System.out.println("No se puede registrar este empleado porque no tiene nombre o apellido.");
            return;
        }

        NivelAcceso nivelAcceso = leerNivelAcceso(entradaDatos);

        Empleado empleado = new Empleado(idEmpleado, dni, nombre.trim(), apellido.trim(), nivelAcceso);
        if (empleado.isRegistroValido()) {
            control.registrarEmpleado(empleado);
        }
    }

    // 2 - Registrar sector
    public static void funcionRegistrarSector(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Sector ---");
        int idSector = leerEntero(entradaDatos, "ID del Sector: ");

        if (idSector <= 0) {
            System.out.println("Error: El ID del sector debe ser mayor a 0.");
            return;
        }

        // Evitar que se repita el ID de otro sector
        if (control.buscarSector(idSector) != null) {
            System.out.println("Error: Ya existe un sector con ese ID.");
            return;
        }

        String nombre = leerNombreSector(entradaDatos, "Nombre del Sector: ");

        System.out.println("Nivel minimo requerido para ingresar al sector:");
        NivelAcceso nivelRequerido = leerNivelAcceso(entradaDatos);

        Sector sector = new Sector(idSector, nombre, nivelRequerido);
        control.registrarSector(sector);
    }

    // 3 - Listar empleados
    public static void funcionListarEmpleados(ControlAcceso control) {
        System.out.println("\n--- Lista de Empleados Registrados ---");
        List<Empleado> empleados = control.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados en el sistema.");
            return;
        }
        for (Empleado emp : empleados) {
            System.out.println("- " + emp);
        }
    }

    // 4 - Listar sectores
    public static void funcionListarSectores(ControlAcceso control) {
        System.out.println("\n--- Lista de Sectores Registrados ---");
        List<Sector> sectores = control.getSectores();
        if (sectores.isEmpty()) {
            System.out.println("No hay sectores registrados en el sistema.");
            return;
        }
        for (Sector sec : sectores) {
            System.out.println("- " + sec);
        }
    }

    // 5 - Conceder permiso especial
    public static void funcionConcederPermiso(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Conceder Permiso Especial ---");
        int idEmpleado = leerEntero(entradaDatos, "ID del Empleado: ");
        int idSector = leerEntero(entradaDatos, "ID del Sector: ");

        Empleado emp = control.buscarEmpleado(idEmpleado);
        Sector sec = control.buscarSector(idSector);

        if (emp == null) {
            System.out.println("Error: No se encontro ningun empleado con ID " + idEmpleado);
            return;
        }
        if (sec == null) {
            System.out.println("Error: No se encontro ningun sector con ID " + idSector);
            return;
        }

        Permiso permiso = new Permiso(emp, sec, true);
        control.concederPermiso(permiso);
    }

    // 6 - Revocar permiso
    public static void funcionRevocarPermiso(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Revocar Permiso ---");
        int idEmpleado = leerEntero(entradaDatos, "ID del Empleado: ");
        int idSector = leerEntero(entradaDatos, "ID del Sector: ");

        Empleado emp = control.buscarEmpleado(idEmpleado);
        Sector sec = control.buscarSector(idSector);

        if (emp == null || sec == null) {
            System.out.println("Error: Empleado o Sector inexistente.");
            return;
        }


        Permiso permiso = new Permiso(emp, sec, true);
        control.revocarPermiso(permiso);
    }

    // 7 - Verificar acceso
    public static void funcionVerificarAcceso(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Punto de Acceso (Lector de Credencial) ---");
        int idEmpleado = leerEntero(entradaDatos, "Ingrese ID del Empleado: ");
        int idSector = leerEntero(entradaDatos, "Ingrese ID del Sector: ");

        boolean autorizado = control.verificarAcceso(idEmpleado, idSector);
        if (autorizado) {
            System.out.println(">>> RESULTADO: ACCESO PERMITIDO <<<");
        } else {
            System.out.println(">>> RESULTADO: ACCESO DENEGADO <<<");
        }
    }

    // 8 - Consultar historial
    public static void funcionConsultarHistorial(ControlAcceso control) {
        System.out.println("\n--- Auditoria / Historial de Intentos de Acceso ---");
        List<RegistroAcceso> historial = control.consultarHistorial();
        if (historial.isEmpty()) {
            return;
        }
        for (RegistroAcceso reg : historial) {
            System.out.println("- " + reg);
        }
    }

    // 10 - Asignar nivel de acceso a un empleado ya registrado
    public static void funcionAsignarNivel(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Asignar Nivel de Acceso ---");
        int idEmpleado = leerEntero(entradaDatos, "ID del Empleado: ");

        Empleado empleado = control.buscarEmpleado(idEmpleado);
        if (empleado == null) {
            System.out.println("Error: No se encontro ningun empleado con ID " + idEmpleado);
            return;
        }

        System.out.println("Nivel actual de " + empleado.getNombre() + ": " + empleado.getNivelAcceso());
        NivelAcceso nivelAcceso = leerNivelAcceso(entradaDatos);
        control.asignarNivel(empleado, nivelAcceso);
    }

    // 9 - Demostracion automatica del patron Singleton y reglas de negocio
    public static void funcionDemostracionAutomatica(ControlAcceso control) {
        System.out.println("\n========================================================");
        System.out.println("         DEMOSTRACION AUTOMATICA DEL SISTEMA            ");
        System.out.println("========================================================");

        // CP-01 y CP-02: Demostracion de unicidad de instancia
        System.out.println("\n[1] Verificando unicidad del Singleton:");
        ControlAcceso puerta1 = ControlAcceso.getInstance();
        ControlAcceso puerta2 = ControlAcceso.getInstance();
        System.out.println("    Puerta 1 referencia: " + puerta1);
        System.out.println("    Puerta 2 referencia: " + puerta2);
        System.out.println("    Son la misma instancia (puerta1 == puerta2)? " + (puerta1 == puerta2));

        // Registro de datos de prueba
        System.out.println("\n[2] Registrando sectores y empleados de prueba:");
        // IDs propios de la demo: no se repiten con los datos precargados en main()
        Sector recepcion = new Sector(110, "Recepcion", NivelAcceso.NIVEL_1);
        Sector servidores = new Sector(120, "Sala de Servidores", NivelAcceso.NIVEL_4);
        // Solo se dan de alta si no existen, para poder repetir la demo.
        if (control.buscarSector(110) == null) {
            control.registrarSector(recepcion);
        }
        if (control.buscarSector(120) == null) {
            control.registrarSector(servidores);
        }

        Empleado ana = new Empleado(101, 40111222, "Ana", "Gomez", NivelAcceso.NIVEL_2);
        Empleado carlos = new Empleado(102, 35333444, "Carlos", "Lopez", NivelAcceso.NIVEL_4);
        if (control.buscarEmpleado(101) == null) {
            control.registrarEmpleado(ana);
        }
        if (control.buscarEmpleado(102) == null) {
            control.registrarEmpleado(carlos);
        }

        // Verificaciones por nivel
        System.out.println("\n[3] Prueba de acceso por nivel acumulativo:");
        System.out.println("    - Ana (ID 101, NIVEL_2) intenta entrar a Recepcion (ID 110, NIVEL_1):");
        control.verificarAcceso(101, 110);

        System.out.println("    - Ana (ID 101, NIVEL_2) intenta entrar a Sala de Servidores (ID 120, NIVEL_4):");
        control.verificarAcceso(101, 120);

        System.out.println("    - Carlos (ID 102, NIVEL_4) intenta entrar a Sala de Servidores (ID 120, NIVEL_4):");
        control.verificarAcceso(102, 120);

        // Permiso especial y revocacion
        System.out.println("\n[4] Prueba de permiso especial y revocacion:");
        System.out.println("    - Concediendo permiso especial a Ana (ID 101) para Sala de Servidores...");
        control.concederPermiso(new Permiso(ana, servidores, true));
        System.out.println("    - Ana (ID 101) intenta entrar nuevamente a Sala de Servidores:");
        control.verificarAcceso(101, 120);

        System.out.println("    - Revocando permiso especial de Ana (ID 101)...");
        control.revocarPermiso(new Permiso(ana, servidores, true));
        System.out.println("    - Ana (ID 101) intenta entrar tras la revocacion:");
        control.verificarAcceso(101, 120);

        // Historial centralizado
        System.out.println("\n[5] Auditoria del historial centralizado:");
        for (RegistroAcceso reg : control.consultarHistorial()) {
            System.out.println("    " + reg);
        }
        System.out.println("========================================================\n");
    }

    public static void main(String[] args) {
        Scanner entradaDatos = new Scanner(System.in);
        ControlAcceso control = ControlAcceso.getInstance();

        control.setMostrarMensajesRegistro(false); // Silencia los avisos de la carga inicial

        // 5 Empleados por defecto
        Empleado e1 = new Empleado(1, 45123456, "Juan", "Perez", NivelAcceso.NIVEL_1);
        Empleado e2 = new Empleado(2, 42345678, "Maria", "Gonzalez", NivelAcceso.NIVEL_2);
        Empleado e3 = new Empleado(3, 39876543, "Lucas", "Fernandez", NivelAcceso.NIVEL_3);
        Empleado e4 = new Empleado(4, 35678901, "Sofia", "Martinez", NivelAcceso.NIVEL_2);
        Empleado e5 = new Empleado(5, 47890123, "Pedro", "Rodriguez", NivelAcceso.NIVEL_1);

        control.registrarEmpleado(e1);
        control.registrarEmpleado(e2);
        control.registrarEmpleado(e3);
        control.registrarEmpleado(e4);
        control.registrarEmpleado(e5);

        // 5 Sectores por defecto
        Sector s1 = new Sector(1, "Recepcion", NivelAcceso.NIVEL_1);
        Sector s2 = new Sector(2, "Administracion", NivelAcceso.NIVEL_2);
        Sector s3 = new Sector(3, "Oficinas", NivelAcceso.NIVEL_3);
        Sector s4 = new Sector(4, "Sala de Servidores", NivelAcceso.NIVEL_4);
        Sector s5 = new Sector(5, "Direccion", NivelAcceso.NIVEL_5);

        control.registrarSector(s1);
        control.registrarSector(s2);
        control.registrarSector(s3);
        control.registrarSector(s4);
        control.registrarSector(s5);

        // Reactiva los avisos para las altas que haga el usuario desde el menu
        control.setMostrarMensajesRegistro(true);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n========== SISTEMA DE CONTROL DE ACCESO ==========");
            System.out.println("1 - Agregar empleado");
            System.out.println("2 - Agregar sector");
            System.out.println("3 - Listar empleados");
            System.out.println("4 - Listar sectores");
            System.out.println("5 - Conceder permiso especial");
            System.out.println("6 - Revocar permiso especial");
            System.out.println("7 - Simular punto de acceso (verificar acceso)");
            System.out.println("8 - Consultar historial de accesos");
            System.out.println("9 - Ejecutar demostracion automatica completa");
            System.out.println("10 - Asignar nivel de acceso a un empleado");
            System.out.println("0 - Salir");
            int opcion = leerEntero(entradaDatos, "Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    funcionRegistrarEmpleado(entradaDatos, control);
                    break;
                case 2:
                    funcionRegistrarSector(entradaDatos, control);
                    break;
                case 3:
                    funcionListarEmpleados(control);
                    break;
                case 4:
                    funcionListarSectores(control);
                    break;
                case 5:
                    funcionConcederPermiso(entradaDatos, control);
                    break;
                case 6:
                    funcionRevocarPermiso(entradaDatos, control);
                    break;
                case 7:
                    funcionVerificarAcceso(entradaDatos, control);
                    break;
                case 8:
                    funcionConsultarHistorial(control);
                    break;
                case 9:
                    funcionDemostracionAutomatica(control);
                    break;
                case 10:
                    funcionAsignarNivel(entradaDatos, control);
                    break;
                case 0:
                    salir = true;
                    System.out.println("Cerrando sistema de control de acceso. Hasta luego!");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
            }


        }
        entradaDatos.close();
    }
}
```

---

# Lista de control de la consigna

| # | Ítem de la lista de control | Estado |
|---|-----------------------------|--------|
| 1 | El documento indica el patrón y el lenguaje elegidos | ✅ Singleton · Java |
| 2 | El escenario es un problema de negocio real, distinto de los vistos en clase | ✅ ENVIDIA / control de acceso |
| 3 | La Sección I justifica el patrón frente a una solución sin patrones | ✅ §1.2 y §1.3 |
| 4 | El diagrama UML respeta los roles del patrón y muestra visibilidad | ✅ §2.1 (imagen embebida) y §2.2 |
| 5 | El código está completo, se ejecuta tal como se entrega y respeta el patrón | ✅ Código completo y ejecutable |
| 6 | El programa tiene su propia clase de prueba con flujo simulado | ✅ Main interactivo conectado al Singleton |
| 7 | El código está en GitHub con acceso para el docente y el enlace figura en la Sección III | ✅ Repositorio público en §3.6 |
| 8 | La Sección IV analiza SOLID y ventajas/desventajas con referencias al código | ✅ §4.1 a §4.4 |
| 9 | Todo está en un único documento con las cuatro secciones en orden | ✅ este documento |

## Qué falta para poder entregar

1. Exportar este documento a PDF y subirlo al campus virtual.
