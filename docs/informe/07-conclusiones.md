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
| `ControlAcceso` | ✅ Estructura del Singleton correcta (lazy, sin `synchronized`, ADR-07) |
| `Empleado` | ⚠️ 4 de 5 atributos, constructor y getters; falta `nivelAcceso`, setters y `toString()` |
| `Sector` | ❌ Vacía |
| `Permiso` | ❌ Vacía |
| `RegistroAcceso` | ❌ Vacía |
| `NivelAcceso` (enum) | ❌ No existe |
| `TipoPermiso` (enum) | ❌ No existe |
| `Main` | ❌ Vacío, sin demostración |
| Compilación | ✅ Sin errores |

En consecuencia: la **estructura del patrón** está verificada por inspección (CP-02), pero
la **demostración en ejecución** (CP-01) y toda la lógica de negocio siguen pendientes.

## 7.3. Sobre el diseño

El diseño quedó **cerrado antes de programar**, con ocho decisiones registradas en
[Decisiones de diseño](09-decisiones.md):

| ADR | Decisión | Por qué |
|-----|----------|---------|
| ADR-01 | `NivelAcceso` como enum | Evita niveles inválidos y centraliza el "alcanza a". |
| ADR-02 | `TipoPermiso` como enum | `tipo` no tenía tipo y aceptaba cualquier valor. |
| ADR-03 | `fechaHora` como `LocalDateTime` | Permite ordenar y filtrar el historial. |
| ADR-04 | `Sector.identificacion` | Consistencia con `Empleado` y con las reglas. |
| ADR-05 | `permisos` en `ControlAcceso` | Sin esa colección la revocación no tendría dónde vivir. |
| ADR-06 | Separar verificar de registrar | Evita mezclar consulta con efecto secundario. |
| ADR-07 | Singleton lazy sin `synchronized` | Claridad didáctica; riesgo documentado. |
| ADR-08 | Setters en todas las clases | Cumplimiento de `Reglas-Proyecto.md`. |

Tomar estas decisiones **antes** de escribir el dominio evita retrabajo: cambiar el tipo
de un atributo después obliga a tocar constructores, getters, setters y pruebas.

## 7.4. Sobre la concurrencia

La implementación lazy sin sincronización puede crear dos instancias si dos hilos evalúan
`instancia == null` al mismo tiempo. En un edificio real con varios puntos de acceso
operando en simultáneo esto es relevante. Alternativas, de menor a mayor robustez:

- `synchronized` en `getInstancia()`: simple, con costo por llamada.
- Inicialización anticipada (`private static final`): simple y segura.
- `enum` de una sola constante: la más robusta frente a reflexión y serialización.
- Double-checked locking con `volatile`: eficiente pero más difícil de explicar.

## 7.5. Trabajo futuro

1. Crear los enums `NivelAcceso` y `TipoPermiso` (ADR-01, ADR-02).
2. Completar `Empleado`: `nivelAcceso`, setters y `toString()`.
3. Implementar `Sector`, `Permiso` y `RegistroAcceso`.
4. Implementar en `ControlAcceso` el plan de métodos de
   [§5.6](05-implementacion.md#56-plan-de-métodos-de-controlacceso), en el orden sugerido.
5. Implementar el `main` de demostración y ejecutar CP-01 a CP-13.
6. Migrar las pruebas a JUnit 5.
7. Evaluar una variante segura frente a hilos (ADR-07).

---

[Volver al índice](../README.md) · [← Anterior: Pruebas](06-pruebas.md) · [Siguiente: Bibliografía →](08-bibliografia.md)
