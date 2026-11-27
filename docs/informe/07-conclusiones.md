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
| `ControlAcceso` | ✅ Singleton completo, validaciones, verificación de accesos, historial y colecciones con copia defensiva |
| `Empleado` | ✅ 6 atributos (incluye `registroValido`), constructor con validación, `esDniValido()`, `datosValidos()`, getters, setters y `toString()` |
| `Sector` | ✅ 3 atributos, constructor que valida vía setters, `datosValidos()`, getters, setters y `toString()`; `idSector` (ADR-04) |
| `Permiso` | ✅ 4 atributos, constructor, getters, setters con validación de nulos y `revocar()` funcional |
| `RegistroAcceso` | ✅ 5 atributos, constructor con `LocalDateTime.now()`, getters, setters con validación de fecha y motivo, y `toString()` |
| `NivelAcceso` (enum) | ✅ 5 niveles con valor numérico y `alcanzaA()` para jerarquía acumulativa |
| `TipoPermiso` (enum) | ❌ No existe: se utiliza `boolean especial` |
| `Main` | ✅ Interactivo por consola, con demostración automática idempotente |
| Clases del dominio | ✅ `final` en las cinco clases (ADR-09) |
| Compilación | ✅ `javac -Xlint:all` sin errores ni advertencias |

En consecuencia, tanto la **estructura del patrón** como la **lógica de negocio y auditoría**
se encuentran implementadas y operativas en el proyecto.

## 7.3. Sobre el diseño

El diseño se registró antes de programar con nueve decisiones en
[Decisiones de diseño](09-decisiones.md):

| ADR | Decisión | Por qué | Estado en el código |
|-----|----------|---------|---------------------|
| ADR-01 | `NivelAcceso` como enum | Evita niveles inválidos y centraliza el "alcanza a". | ✅ Aplicado: enum con valor numérico y `alcanzaA()` |
| ADR-02 | `TipoPermiso` como enum | `tipo` no tenía tipo y aceptaba cualquier valor. | ❌ No aplicado: `boolean especial` |
| ADR-03 | `fechaHora` como `LocalDateTime` | Permite ordenar y filtrar el historial. | ✅ Aplicado: asignado en constructor |
| ADR-04 | `idEmpleado` / `idSector` | Nombres explícitos por entidad, en camelCase. | ✅ Aplicado: renombrado en código y UML |
| ADR-05 | `permisos` en `ControlAcceso` | Sin esa colección la revocación no tendría dónde vivir. | ✅ Aplicado |
| ADR-06 | Separar verificar de registrar | Evita mezclar consulta con efecto secundario. | ❌ No aplicado: flujo unificado en `verificarAcceso()` |
| ADR-07 | Singleton lazy sin `synchronized` | Claridad didáctica; riesgo documentado. | ✅ Aplicado |
| ADR-08 | Setters en todas las clases | Cumplimiento de `Reglas-Proyecto.md`. | ✅ Aplicado: setters completos y copia defensiva |
| ADR-09 | Clases `final` y validez calculada | Evita herencia no prevista y estados inválidos forzados. | ✅ Aplicado: cinco clases `final`; `datosValidos()` y `setRegistroValido()` |

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
4. Agregar una opción de menú para `asignarNivel()` y copias profundas en los getters de colecciones.
5. Exportar el documento de entrega a PDF.

---

[Volver al índice](../README.md) · [← Anterior: Pruebas](06-pruebas.md) · [Siguiente: Bibliografía →](08-bibliografia.md)
