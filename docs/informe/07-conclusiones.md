# 7. Conclusiones

## 7.1. Sobre el problema y el patrón

El análisis del caso ENVIDIA muestra que el riesgo no está en la cantidad de código sino
en **dónde vive el estado**. Con instancias separadas, cada punto de acceso tiene su copia
de los permisos y una revocación queda sin efecto en el resto del edificio: el sistema de
seguridad falla aunque cada puerta "funcione".

El patrón Singleton ataca exactamente esa causa: una única instancia de `ControlAcceso`
significa una única lista de empleados, sectores, permisos y registros, y por lo tanto una
única decisión de autorización para todo el edificio.

## 7.2. Sobre la implementación actual

Estado al cierre de esta versión:

| Componente | Estado |
|------------|--------|
| `ControlAcceso` | ✅ Estructura del Singleton (lazy, sin `synchronized`, ADR-07); ❌ métodos sin lógica |
| `Empleado` | ✅ 5 atributos, constructor, getters, setter de nivel y `toString()`; ⚠️ typo `nivelAcesso` |
| `Sector` | ⚠️ 3 atributos y getters; sin setters ni constructor con parámetros |
| `Permiso` | ⚠️ 4 atributos y getters; `revocar()` vacío |
| `RegistroAcceso` | ⚠️ 5 atributos y 3 getters; `fechaHora` sin asignar |
| `NivelAcceso` (enum) | ✅ Existe con `NIVEL_1`…`NIVEL_5`; ⚠️ sin comparación acumulativa |
| `TipoPermiso` (enum) | ❌ No existe: se usa `boolean especial` |
| `Main` | ❌ Vacío, sin demostración |
| Compilación | ✅ Sin errores |

En consecuencia: la **estructura del patrón** está verificada por inspección (CP-02), pero
la **demostración en ejecución** (CP-01) y toda la lógica de negocio siguen pendientes.

## 7.3. Sobre el diseño

El diseño se registró **antes de programar**, con ocho decisiones en
[Decisiones de diseño](09-decisiones.md). El código posterior aplicó algunas y dejó otras
pendientes:

| ADR | Decisión | Por qué | Estado en el código |
|-----|----------|---------|---------------------|
| ADR-01 | `NivelAcceso` como enum | Evita niveles inválidos y centraliza el "alcanza a". | ⚠️ Parcial: sin valor ni comparación |
| ADR-02 | `TipoPermiso` como enum | `tipo` no tenía tipo y aceptaba cualquier valor. | ❌ No aplicado: `boolean especial` |
| ADR-03 | `fechaHora` como `LocalDateTime` | Permite ordenar y filtrar el historial. | ⚠️ Parcial: tipo correcto, sin asignación |
| ADR-04 | `Sector.identificacion` | Consistencia con `Empleado` y con las reglas. | ❌ No aplicado: sigue `idS` |
| ADR-05 | `permisos` en `ControlAcceso` | Sin esa colección la revocación no tendría dónde vivir. | ✅ Aplicado |
| ADR-06 | Separar verificar de registrar | Evita mezclar consulta con efecto secundario. | ❌ No aplicado: no existe `registrarIntento()` |
| ADR-07 | Singleton lazy sin `synchronized` | Claridad didáctica; riesgo documentado. | ✅ Aplicado |
| ADR-08 | Setters en todas las clases | Cumplimiento de `Reglas-Proyecto.md`. | ⚠️ Parcial: solo `Empleado` |

La lección es concreta: las decisiones de tipo y de nombre **no se sostienen solas** si el
código se escribe en paralelo sin revisarlas. La brecha está registrada en
[Implementación §5.5](05-implementacion.md#55-brecha-entre-diseño-e-implementación).

## 7.4. Sobre la concurrencia

La implementación lazy sin sincronización puede crear dos instancias si dos hilos evalúan
`instancia == null` al mismo tiempo. En un edificio real con varios puntos de acceso
operando en simultáneo esto es relevante. Alternativas, de menor a mayor robustez:

- `synchronized` en `getInstance()`: simple, con costo por llamada.
- Inicialización anticipada (`private static final`): simple y segura.
- `enum` de una sola constante: la más robusta frente a reflexión y serialización.
- Double-checked locking con `volatile`: eficiente pero más difícil de explicar.

## 7.5. Trabajo futuro

1. Completar la lógica de `ControlAcceso` (altas, búsquedas, permisos y verificación).
2. Definir e implementar el registro del intento en `RegistroAcceso` y en el gestor.
3. Completar `Permiso.revocar()` y los setters de `Sector` y `RegistroAcceso`.
4. Implementar el `main` de demostración y ejecutar CP-01 a CP-13.
5. Resolver las brechas de los ADR-01, 02, 04, 06 y 08, o registrar formalmente su
   reemplazo.
6. Migrar las pruebas a JUnit 5.
7. Evaluar una variante segura frente a hilos (ADR-07).

---

[Volver al índice](../README.md) · [← Anterior: Pruebas](06-pruebas.md) · [Siguiente: Bibliografía →](08-bibliografia.md)
