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
| `ControlAcceso` | ✅ Estructura Singleton completa, lógica de validaciones, verificación de accesos e historial con copia defensiva |
| `Empleado` | ✅ 5 atributos, constructor, getters, setter de nivel y `toString()`; typo `nivelAcesso` conservado |
| `Sector` | ✅ 3 atributos, constructor parametrizado, getters y setters |
| `Permiso` | ✅ 4 atributos, constructor, getters, setters y `revocar()` funcional |
| `RegistroAcceso` | ✅ 5 atributos, constructor con `LocalDateTime.now()`, getters, setters y `toString()` |
| `NivelAcceso` (enum) | ✅ 5 niveles evaluados mediante `ordinal()` para jerarquía acumulativa |
| `TipoPermiso` (enum) | ❌ No existe: se utiliza `boolean especial` |
| `Main` | ✅ Interactivo por consola, conectado a la instancia Singleton |
| Compilación | ✅ Sin errores ni advertencias |

En consecuencia, tanto la **estructura del patrón** como la **lógica de negocio y auditoría**
se encuentran implementadas y operativas en el proyecto.

## 7.3. Sobre el diseño

El diseño se registró antes de programar con ocho decisiones en
[Decisiones de diseño](09-decisiones.md):

| ADR | Decisión | Por qué | Estado en el código |
|-----|----------|---------|---------------------|
| ADR-01 | `NivelAcceso` como enum | Evita niveles inválidos y centraliza el "alcanza a". | ⚠️ Parcial: evaluado por `ordinal()` |
| ADR-02 | `TipoPermiso` como enum | `tipo` no tenía tipo y aceptaba cualquier valor. | ❌ No aplicado: `boolean especial` |
| ADR-03 | `fechaHora` como `LocalDateTime` | Permite ordenar y filtrar el historial. | ✅ Aplicado: asignado en constructor |
| ADR-04 | `Sector.identificacion` | Consistencia con `Empleado` y con las reglas. | ❌ No aplicado: sigue `idS` |
| ADR-05 | `permisos` en `ControlAcceso` | Sin esa colección la revocación no tendría dónde vivir. | ✅ Aplicado |
| ADR-06 | Separar verificar de registrar | Evita mezclar consulta con efecto secundario. | ❌ No aplicado: flujo unificado en `verificarAcceso()` |
| ADR-07 | Singleton lazy sin `synchronized` | Claridad didáctica; riesgo documentado. | ✅ Aplicado |
| ADR-08 | Setters en todas las clases | Cumplimiento de `Reglas-Proyecto.md`. | ✅ Aplicado: setters completos y copia defensiva |

## 7.4. Sobre la concurrencia

La implementación lazy sin sincronización puede crear dos instancias si dos hilos evalúan
`instancia == null` al mismo tiempo. En un edificio real con varios puntos de acceso
operando en simultáneo esto es relevante. Alternativas, de menor a mayor robustez:

- `synchronized` en `getInstance()`: simple, con costo por llamada.
- Inicialización anticipada (`private static final`): simple y segura.
- `enum` de una sola constante: la más robusta frente a reflexión y serialización.
- Double-checked locking con `volatile`: eficiente pero más difícil de explicar.

## 7.5. Trabajo futuro

1. Incorporar pruebas automáticas con JUnit 5.
2. Evaluar una variante thread-safe del Singleton (ADR-07).
3. Desarrollar persistencia externa (base de datos o archivos) para mantener el historial entre ejecuciones.
4. Extender la interfaz de usuario por consola para abarcar la verificación de accesos y visualización directa del historial.

---

[Volver al índice](../README.md) · [← Anterior: Pruebas](06-pruebas.md) · [Siguiente: Bibliografía →](08-bibliografia.md)
