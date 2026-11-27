# 9. Decisiones de diseño (ADR)

Registro de las decisiones que modifican o completan el planteo original. Cada una sigue
el formato **Contexto → Decisión → Consecuencias → Alternativas descartadas**.

Las decisiones se tomaron **antes de implementar**: cambiar el tipo de un atributo
después obliga a tocar constructores, getters, setters y pruebas. Esta sección conserva
la decisión propuesta; la tabla siguiente indica cómo se refleja en el código de `main`.

## Estado de aplicación en el código

| ADR | Decisión | Estado | Qué hay en `src/` |
|-----|----------|--------|-------------------|
| ADR-01 | `NivelAcceso` como enum | ✅ Aplicado | Enum con valor numérico y `alcanzaA()`; atributo `nivelAcceso` |
| ADR-02 | `TipoPermiso` como enum | ❌ No aplicado | `Permiso.especial` es `boolean`; no existe `TipoPermiso.java` |
| ADR-03 | `fechaHora` como `LocalDateTime` | ✅ Aplicado | `LocalDateTime.now()` asignado automáticamente en constructor |
| ADR-04 | `Sector.identificacion` | ✅ Aplicado | Atributo, getter y setter renombrados |
| ADR-05 | `permisos` en `ControlAcceso` | ✅ Aplicado | `private List<Permiso> permisos` |
| ADR-06 | Separar verificar de registrar | ❌ No aplicado | `verificarAcceso(int, int)` evalúa y registra; `consultarHistorial()` devuelve copia |
| ADR-07 | Singleton lazy sin `synchronized` | ✅ Aplicado | `getInstance()` crea la instancia en la primera llamada |
| ADR-08 | Setters en todas las clases | ✅ Aplicado | Constructores, getters y setters completos; copia defensiva en historial |

> Las decisiones no aplicadas se mantienen documentadas para justificar el diseño
> implementado durante la defensa del proyecto.

---

## ADR-01: `NivelAcceso` como enum

**Contexto.** El planteo define `Empleado.nivelAcceso` como `private int` y, por separado,
una enumeración `NivelAcceso` con los niveles 1 a 5. Son dos representaciones del mismo
concepto: un `int` permite `asignarNivel(99)` sin que nada lo impida.

**Decisión.** `Empleado.nivelAcceso` y `Sector.nivelRequerido` usan el tipo `NivelAcceso`.
El enum debía guardar su valor numérico y exponer `getNivel()` y
`alcanzaA(NivelAcceso)`.

**Estado actual.** ✅ Aplicado: el enum declara `NIVEL_1` … `NIVEL_5` con su valor numérico
y `alcanzaA(NivelAcceso)` compara por `getNivel()`. El atributo se llama `nivelAcceso`.

**Consecuencias.**

- El compilador impide niveles inventados.
- La comparación acumulativa aprovecha el valor numérico de cada nivel.
- Los niveles son un conjunto cerrado: agregar uno nuevo es un cambio deliberado en el enum.

**Alternativas descartadas.**

- `int` con validación manual en `setNivelAcceso`: repite la validación en cada punto de uso.
- Constantes `static final int`: se pierde el tipado fuerte y el recorrido de valores.

---

## ADR-02: `TipoPermiso` como enum

**Contexto.** `Permiso.tipo` aparece sin tipo declarado ("identifica si se trata de un
permiso especial"), lo que no compila y no define qué valores admite.

**Decisión.** Se crea `enum TipoPermiso { ESTANDAR, ESPECIAL }` y el atributo pasa a ser
`private TipoPermiso tipo`.

**Estado actual.** ❌ No aplicado: `Permiso` tiene `private boolean especial`,
`esEspecial()` devuelve ese booleano y `TipoPermiso.java` no existe. Se mantiene el `boolean`
para respetar la consigna original.

**Consecuencias.**

- Un solo lugar define los tipos válidos.
- `Permiso.esEspecial()` se implementa con el atributo `boolean especial`.

**Alternativas descartadas.**

- `String tipo`: acepta `"ESPECIAL"`, `"especial"` y `"Especial"` como valores distintos.

---

## ADR-03: `fechaHora` como `LocalDateTime`

**Contexto.** El planteo define `RegistroAcceso.fechaHora` como `String`.

**Decisión.** Se usa `java.time.LocalDateTime`. El valor lo asigna el propio registro en
el constructor (`LocalDateTime.now()`), no quien lo crea.

**Estado actual.** ✅ Aplicado: el constructor asigna automáticamente `this.fechaHora = LocalDateTime.now();`.

**Consecuencias.**

- El historial se puede ordenar y filtrar por fecha sin parsear texto.
- No hay formatos inconsistentes (`"17/11/2026"` vs `"2026-11-17"`).
- El formato para mostrar se decide en la vista o en `toString()`.
- Queda registrado el momento real del intento, no el que declare el llamador.

**Alternativas descartadas.**

- `String`: obliga a parsear en cada consulta y permite datos inválidos.
- `Date`: API antigua, mutable y sin zona horaria clara.

---

## ADR-04: `identificacion` en lugar de `idS`

**Contexto.** `Sector.idS` rompe la convención del proyecto (`Empleado.identificacion`,
atributos descriptivos en minúscula) y mezcla abreviaturas con nombres completos.

**Decisión.** `Sector.identificacion`.

**Estado actual.** ✅ Aplicado: `Sector` usa `identificacion` con `getIdentificacion()` y
`setIdentificacion()`, y el UML y la documentación se actualizaron en consecuencia.

---

## ADR-05: la colección `permisos` vive en `ControlAcceso`

**Contexto.** El planteo dice que `ControlAcceso` "concede y revoca permisos", pero no
le daba una lista para guardarlos. La alternativa (guardar los permisos adentro de cada
`Empleado`) obliga a buscar por todos los empleados para saber quién puede entrar a un
sector y desincroniza las revocaciones si hay copias del objeto.

**Decisión.** `ControlAcceso` agrega `private List<Permiso> permisos`, inicializada en su
constructor.

**Estado actual.** ✅ Aplicado en `src/ControlAcceso.java`.

**Consecuencias.**

- La regla RN-06 ("la revocación es visible de inmediato para todos") se cumple por
  construcción: hay una sola lista en una sola instancia.
- `concederPermiso` agrega a esa lista; `revocarPermiso` busca allí y delega en
  `Permiso.revocar()`.
- Las consultas de acceso no recorren empleados.

**Alternativas descartadas.**

- Lista adentro de `Empleado`: acopla el empleado al sector y complica la auditoría
  global.
- Lista en cada `Sector`: divide el estado en tantos lugares como sectores haya.

---

## ADR-06: separar verificar de registrar

**Contexto.** El planteo pide "autorizar o rechazar" y "registrar el intento" como
responsabilidades distintas. Un único método que haga las dos cosas mezcla consulta con
efecto secundario y ensucia las pruebas.

**Decisión.**

- `boolean verificarAcceso(Empleado, Sector)`: evalúa RN-01 y RN-02, **no registra**.
- `RegistroAcceso registrarIntento(Empleado, Sector)`: llama a `verificarAcceso()`, arma
  el motivo, crea el registro, lo agrega a la colección y lo devuelve.

**Estado actual.** ❌ No aplicado: se conservó la firma `verificarAcceso(int numEmpleado, int numSector)`,
que evalúa los permisos y genera internamente el `RegistroAcceso` en `registros`.
La auditoría se expone con `consultarHistorial()` devolviendo `List<RegistroAcceso>`.

**Consecuencias.**

- Flujo unificado: cualquier verificación produce inmediatamente su traza de auditoría.
- Simplicidad didáctica frente a métodos adicionales.

---

## ADR-07: Singleton lazy sin `synchronized`

**Contexto.** La implementación actual crea la instancia en la primera llamada, sin
protección para concurrencia.

**Decisión.** Se mantiene por claridad didáctica: es la forma clásica del patrón y el
programa es monohilo.

**Estado actual.** ✅ Aplicado en `getInstance()`.

**Consecuencias.**

- Dos hilos que evalúen `instancia == null` a la vez podrían crear dos instancias.
- En un edificio real con varios puntos de acceso concurrentes, eso reintroduce el
  problema de desincronización.
- Queda documentado como deuda técnica en
  [Conclusiones §7.4](07-conclusiones.md#74-sobre-la-concurrencia), con las alternativas
  (sincronizar, inicialización anticipada, `enum`, double-checked locking).

**Alternativas descartadas (por ahora).**

- `synchronized`: correcto, pero oculta la estructura mínima que el trabajo busca mostrar.
- Enum de una constante: la más robusta, pero se aleja del ejercicio pedido.

---

## ADR-08: setters en todas las clases

**Contexto.** `Reglas-Proyecto.md` exige "acceso a lectura y escritura de atributos
mediante getters y setters". Conceptualmente, `RegistroAcceso` es un dato histórico y
debería ser inmutable.

**Decisión.** La decisión fue cumplir los setters en todas las clases, incluida
`RegistroAcceso`, para respetar la regla de la cátedra.

**Estado actual.** ✅ Aplicado: `Empleado`, `Sector`, `Permiso` y `RegistroAcceso`
cuentan con constructores parametrizados, getters y setters para todos sus atributos, y
`RegistroAcceso` cuenta con `toString()`.

**Consecuencias.**

- Cumplimiento explícito de la regla de la cátedra.
- Se pierde la inmutabilidad teórica del historial: un setter podría alterar un registro ya guardado.
- Mitigación aplicada: `consultarHistorial()` devuelve una **copia** de la lista (`new ArrayList<>(this.registros)`)
  para que no se modifique la colección interna desde afuera.

---

[Volver al índice](../README.md) · [← Anterior: Bibliografía](08-bibliografia.md)
