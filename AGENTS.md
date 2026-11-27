# AGENTS.md

Guía para agentes de IA (y para cualquier colaborador) que trabajen en este repositorio.
`AGENTS.md` es un **archivo** en la raíz del proyecto, no una carpeta: así lo leen las
herramientas de asistencia automáticamente.

## Qué es este proyecto

Trabajo práctico de **diseño de software**: implementación en **Java** del patrón
**Singleton** aplicado a un sistema de control de acceso.

- Punto de entrada: `src/Main.java`
- Pieza central: `src/ControlAcceso.java` (el Singleton)
- Dominio: `Empleado`, `Sector`, `Permiso`, `RegistroAcceso`

## Consigna de la EGI

El trabajo se rige por las consignas de la Evaluación Global Integradora (Diseño de
Software, ITU UNCuyo Sede Este).

- **Modalidad grupal.** La consigna publicada dice "individual" por error; la modalidad
  acordada es grupal. No individualizar la documentación ni la carátula.
- **Entrega:** un único documento en PDF/Markdown con **cuatro secciones en orden**:
  1. Planteamiento y justificación del problema (por qué el patrón frente a una solución
     convencional: acoplamiento, rigidez, duplicación, recursos).
  2. Diseño arquitectónico: UML formal con visibilidad (`+`, `-`, `#`) y roles del patrón.
  3. Implementación práctica: código **completo y autoejecutable**, clase de prueba
     propia, versión del lenguaje y enlace al GitHub personal.
  4. Análisis crítico y SOLID: beneficios y tensiones con referencias a las clases y
     métodos propios, más ventajas y desventajas (multihilo, testing, memoria).
- **Documento de entrega:** `docs/EGI-Patron-Singleton.md`. Los capítulos de
  `docs/informe/` son la versión extendida de trabajo; si hay diferencias, manda el
  documento de entrega.
- **No se requiere interfaz gráfica.** La prueba se ejecuta por consola.
- **Singleton:** constructor `private`, atributo `private static` y método
  `public static` de acceso; el cliente no debe poder hacer `new ControlAcceso()`, y la
  clase de prueba debe demostrar que dos llamadas devuelven la misma instancia.

### Bloqueantes antes de entregar

1. Exportar `docs/EGI-Patron-Singleton.md` a PDF y subirlo al campus virtual.

Las brechas de los ADR-02 y 06 (`boolean especial`, `verificarAcceso` que registra) están
**aceptadas y documentadas** como decisiones de diseño; no bloquean la entrega. Los datos
de los integrantes ya están cargados.

El repositorio ya es público: la URL figura en la Sección III del documento de entrega.
La lista completa está en `docs/EGI-Patron-Singleton.md` (sección "Lista de control de la
consigna").

## Cómo ayudar: tutor

El agente trabaja como **tutor**. En la defensa final cada integrante tiene que explicar lo que hizo, así que:

- Escribí código en pasos chicos, una función o un bloque por vez, y explicá cada uno en lenguaje simple antes de seguir.
- Cuando el pedido es una tarjeta entera, proponé primero el plan de funciones y esperá el OK.
- Respondé en español.
## Reglas obligatorias de código

Fuente de verdad: [`Reglas-Proyecto.md`](Reglas-Proyecto.md). Resumen operativo:

| Regla | Detalle |
|-------|---------|
| Lenguaje | Java (sin frameworks, sin Maven/Gradle) |
| Una clase por archivo | El archivo se llama igual que la clase |
| Nombres de clase | Mayúscula inicial (`ControlAcceso`, `Empleado`) |
| Nombres de atributos | Minúscula inicial, descriptivos (`idEmpleado`, no `id`) |
| Encapsulamiento | Todos los atributos `private` |
| Acceso a atributos | Siempre por getters y setters, nunca acceso directo |
| Singleton | Atributo `private static`, constructor `private`, método `public static` de acceso |

Cualquier cambio debe respetar estas reglas. Si una regla y una petición entran en
conflicto, señalarlo antes de implementar.

## Estructura

```
src/          Código fuente (Sources Root)
docs/         Documentación (informe, diagramas, manual)
docs/informe/ Capítulos del informe académico, numerados
docs/manual/  Instalación y guía de uso
docs/assets/  Imágenes y capturas
out/          Salida de compilación (ignorada por git)
```

## Compilar y ejecutar

```powershell
javac -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

En IntelliJ IDEA: abrir `src/Main.java` y usar **Run** (`Ctrl+Shift+F10`), o
**Build → Build Project** (`Ctrl+F9`).

JDK: 17 o superior (en este equipo, 23.0.1; `.idea/misc.xml` declara `JDK_23`).

## Estado real del código

No asumir que lo documentado está implementado. Estado actual (alineado con `main` el
2026-11-27):

- ✅ `ControlAcceso`: Singleton correcto (atributo `private static`, constructor privado,
  `getInstance()` lazy sin `synchronized`), cuatro listas `final` inicializadas, lógica
  completa de altas (validación de datos y duplicados por ID y DNI), niveles, permisos
  (con reactivación de permisos revocados), `verificarAcceso` (evalúa y registra),
  `consultarHistorial` y getters con copia defensiva, y `mostrarMensajesRegistro` para
  silenciar la carga inicial.
- ✅ `Empleado`: 6 atributos privados (incluye `registroValido`), constructor con
  validación, `esDniValido()`, `isRegistroValido()`, `setRegistroValido()`,
  `datosValidos()`, getters, setters y `toString()`.
- ✅ `Sector`: 3 atributos privados (`idSector`, `nombre`, `nivelRequerido`),
  constructor que delega en los setters, `datosValidos()`, getters, setters y `toString()`.
- ✅ `Permiso`: `empleado`, `sector`, `activo`, `especial`, getters, setters con validación
  de nulos y `revocar()` funcional.
- ✅ `RegistroAcceso`: constructor con `LocalDateTime.now()`, getters, setters con
  validación de fecha y motivo, y `toString()` legible.
- ✅ `NivelAcceso`: enum `NIVEL_1` … `NIVEL_5` con valor numérico y `alcanzaA()`.
- ❌ `TipoPermiso`: no existe (se usa `boolean especial`).
- ✅ `Main`: menú interactivo por consola con demostración automática idempotente y
  opción 10 para reasignar el nivel de un empleado registrado.
- ✅ Las cinco clases del dominio son `final` (ADR-09).
- ✅ El proyecto compila con `javac -Xlint:all` sin errores ni advertencias.

El **diseño documentado** está alineado al código: `docs/informe/04-diseno.md` describe
la estructura real y `05-implementacion.md` registra la brecha exacta.

Decisiones registradas en `docs/informe/09-decisiones.md`: el código aplicó **ADR-01**,
**ADR-03**, **ADR-04**, **ADR-05**, **ADR-07**, **ADR-08** y **ADR-09**; quedan sin aplicar
**ADR-02** (`TipoPermiso`) y **ADR-06** (separar verificar de registrar). No cambiar nombres
ni tipos sin actualizar esa tabla y el UML.

Mantener actualizado `docs/informe/07-conclusiones.md` y el
[índice de documentación](docs/README.md) cuando cambie el estado.

## Convenciones de documentación

- Idioma: **español** (código, comentarios, docs y mensajes de commit).
- Formato: Markdown UTF-8.
- Diagramas: Mermaid embebido en los `.md` del informe de trabajo. Las figuras del documento
  de entrega se mantienen en `docs/assets/uml-clases.puml` y `docs/assets/flujo-acceso.puml`,
  se renderizan a PNG y se embeben como imágenes (ver `docs/README.md`).
- Imágenes: `docs/assets/`, referenciadas con rutas relativas.
- No duplicar contenido: el informe referencia el código, no lo copia entero. **Excepción:**
  el documento de entrega incluye el Anexo B con las siete clases completas de `src/`. Si
  cambia una clase, actualizar también ese anexo para que no se desincronice.

## Git

- Una rama por tarjeta de Trello: `impl-05-reservar`, `docs-rn23`, etc.
- PR a `main` con la revisión de al menos un compañero antes de mergear. GitHub no lo exige: es un acuerdo del grupo.
- Mensajes de commit en español, en presente: "Agrega registro de intento de acceso".
- La EGI es **grupal**: la revisión de un compañero antes del merge es parte del acuerdo del grupo.

## Seguridad y secretos

- No incluir credenciales, tokens ni datos personales reales en el repositorio ni en la
  documentación. Usar datos ficticios en ejemplos (`Ana Gómez`, DNI `30111222`).
- No subir archivos de `out/` ni configuración local de IDE más allá de `.idea/` ya versionada.
