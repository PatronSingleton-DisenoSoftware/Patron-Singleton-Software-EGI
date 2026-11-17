# 9. Decisiones de diseño (ADR)

Registro de las decisiones que modifican o completan el planteo original. Cada una sigue
el formato **Contexto → Decisión → Consecuencias → Alternativas descartadas**.

Las decisiones se tomaron **antes de implementar**: cambiar el tipo de un atributo
después obliga a tocar constructores, getters, setters y pruebas. Esta sección conserva
la decisión propuesta; la tabla siguiente indica si el código de `main` la aplicó.

## Estado de aplicación en el código

| ADR | Decisión | Estado | Qué hay en `src/` |
|-----|----------|--------|-------------------|
| ADR-01 | `NivelAcceso` como enum | ⚠️ Parcial | Enum `NIVEL_1`…`NIVEL_5`, sin valor numérico ni `alcanzaA()` |
| ADR-02 | `TipoPermiso` como enum | ❌ No aplicado | `Permiso.especial` es `boolean`; no existe `TipoPermiso.java` |
| ADR-03 | `fechaHora` como `LocalDateTime` | ⚠️ Parcial | El tipo es correcto; el constructor no asigna la fecha |
| ADR-04 | `Sector.identificacion` | ❌ No aplicado | El atributo sigue siendo `idS` |
| ADR-05 | `permisos` en `ControlAcceso` | ✅ Aplicado | `private List<Permiso> permisos` |
| ADR-06 | Separar verificar de registrar | ❌ No aplicado | Solo `verificarAcceso(int, int)`; no existe `registrarIntento()` |
| ADR-07 | Singleton lazy sin `synchronized` | ✅ Aplicado | `getInstance()` crea la instancia en la primera llamada |
| ADR-08 | Setters en todas las clases | ⚠️ Parcial | Solo `Empleado` tiene setters |

> Las brechas no se corrigen en esta rama de documentación: se registran para que el grupo
> decida si ajusta el código o si formaliza el cambio de decisión.

---

## ADR-01: `NivelAcceso` como enum

**Contexto.** El planteo define `Empleado.nivelAcceso` como `private int` y, por separado,
una enumeración `NivelAcceso` con los niveles 1 a 5. Son dos representaciones del mismo
concepto: un `int` permite `asignarNivel(99)` sin que nada lo impida.

**Decisión.** `Empleado.nivelAcceso` y `Sector.nivelRequerido` usan el tipo `NivelAcceso`.
El enum debía guardar su valor numérico y exponer `getNivel()` y
`alcanzaA(NivelAcceso)`.

**Estado actual.** ⚠️ Parcial: el enum existe con las constantes `NIVEL_1` … `NIVEL_5`,
pero no tiene valor numérico ni métodos. El atributo de `Empleado` se llama
`nivelAcesso`.

**Consecuencias.**

- El compilador impide niveles inventados.
- La comparación acumulativa queda en un solo lugar (`alcanzaA`), no dispersa en `if`.
- Los niveles son un conjunto cerrado: agregar uno nuevo es un cambio deliberado en el enum.
- Para mostrar el valor numérico en consola se usa `getNivel()`.

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
`esEspecial()` devuelve ese booleano y `TipoPermiso.java` no existe. Si el grupo mantiene
el `boolean`, este ADR debe marcarse como reemplazado.

**Consecuencias.**

- Un solo lugar define los tipos válidos.
- `Permiso.esEspecial()` se implementa como `tipo == TipoPermiso.ESPECIAL`.
- Si en el futuro aparecen tipos nuevos (por ejemplo, temporales), se agregan al enum.

**Alternativas descartadas.**

- `boolean especial`: simple, pero bloquea la extensión y no se lee igual en el historial.
- `String tipo`: acepta `"ESPECIAL"`, `"especial"` y `"Especial"` como valores distintos.

---

## ADR-03: `fechaHora` como `LocalDateTime`

**Contexto.** El planteo define `RegistroAcceso.fechaHora` como `String`.

**Decisión.** Se usa `java.time.LocalDateTime`. El valor lo asigna el propio registro en
el constructor (`LocalDateTime.now()`), no quien lo crea.

**Estado actual.** ⚠️ Parcial: el atributo es `LocalDateTime`, pero el constructor está
vacío y `fechaHora` queda en `null`.

**Consecuencias.**

- El historial se puede ordenar y filtrar por fecha sin parsear texto.
- No hay formatos inconsistentes (`"17/11/2026"` vs `"2026-11-17"`).
- El formato para mostrar se decide en la vista, no en el modelo.
- Queda registrado el momento real del intento, no el que declare el llamador.

**Alternativas descartadas.**

- `String`: obliga a parsear en cada consulta y permite datos inválidos.
- `Date`: API antigua, mutable y sin zona horaria clara.

---

## ADR-04: `identificacion` en lugar de `idS`

**Contexto.** `Sector.idS` rompe la convención del proyecto (`Empleado.identificacion`,
atributos descriptivos en minúscula) y mezcla abreviaturas con nombres completos.

**Decisión.** `Sector.identificacion`.

**Estado actual.** ❌ No aplicado: `Sector` conserva `private int idS` y su getter
`getIdS()`.

**Consecuencias.**

- Consistencia con `Empleado` y con `Reglas-Proyecto.md`.
- El código de búsqueda queda simétrico: `buscarEmpleado(int)` / `buscarSector(int)`.

---

## ADR-05: los permisos viven en `ControlAcceso`

**Contexto.** El planteo enumera para `ControlAcceso` solo `empleados`, `sectores` y
`registros`, pero le asigna la responsabilidad de conceder y revocar permisos. Sin una
colección de permisos, esas operaciones no tienen dónde persistir y cada punto de acceso
volvería a tener su propia copia: exactamente el problema que el Singleton evita.

**Decisión.** `ControlAcceso` mantiene `private List<Permiso> permisos`, junto con las
otras tres colecciones.

**Estado actual.** ✅ Aplicado: la lista existe y se inicializa en el constructor privado.

**Consecuencias.**

- La concesión y la revocación quedan centralizadas y son visibles para todos los sectores.
- `Empleado` sigue siendo una clase de datos, sin lógica de autorización.
- Crece la responsabilidad del gestor; si el sistema escala, se podría extraer un
  `RepositorioPermisos`, pero hoy sería sobre-diseño.

**Alternativas descartadas.**

- `List<Permiso>` dentro de `Empleado`: reparte el estado y complica la consulta
  "¿quién tiene acceso a este sector?".

---

## ADR-06: separar verificar de registrar

**Contexto.** El planteo pide "autorizar o rechazar" y "registrar el intento" como
responsabilidades distintas. Un único método que haga las dos cosas mezcla consulta con
efecto secundario y ensucia las pruebas.

**Decisión.**

- `boolean verificarAcceso(Empleado, Sector)`: evalúa RN-01 y RN-02, **no registra**.
- `RegistroAcceso registrarIntento(Empleado, Sector)`: llama a `verificarAcceso()`, arma
  el motivo, crea el registro, lo agrega a la colección y lo devuelve.

**Estado actual.** ❌ No aplicado: existe `verificarAcceso(int, int)`, que devuelve
`false` fijo, y `consultarHistorial()`, que devuelve `null`. No existe `registrarIntento`
ni una firma que reciba `Empleado` y `Sector`.

**Consecuencias.**

- Se puede consultar "¿puede entrar?" sin ensuciar el historial (por ejemplo, para una
  pantalla previa).
- Las pruebas de reglas de negocio no dependen del registro.
- `registrarIntento` es el único punto que escribe en `registros`.

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

**Estado actual.** ⚠️ Parcial: solo `Empleado` tiene setters (`setNivelAcesso`). `Sector`,
`Permiso` y `RegistroAcceso` no los tienen; además, sus constructores no reciben datos, así
que hoy no se pueden crear objetos válidos desde fuera.

**Consecuencias.**

- Cumplimiento explícito de la regla (se puede señalar en la defensa).
- Se pierde la inmutabilidad del historial: un setter podría alterar un registro ya
  guardado.
- Mitigación prevista: que `consultarHistorial()` devuelva una **copia** de la lista para
  que no se modifique la colección interna desde afuera. El código actual devuelve `null`.

---

[Volver al índice](../README.md) · [← Anterior: Bibliografía](08-bibliografia.md)
