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

1. Completar el código (`Sector`, `Permiso`, `RegistroAcceso`, `NivelAcceso`,
   `TipoPermiso` y los métodos de `ControlAcceso`).
2. Completar `Main` con el flujo simulado (misma instancia + permiso concedido y revocado).
3. Exportar `docs/assets/uml-clases.puml` a imagen e insertarla en la Sección II.
4. Publicar el repositorio y pegar la URL en la Sección III.
5. Completar los datos personales y exportar el documento a PDF.

La lista completa está en `docs/EGI-Patron-Singleton.md` (sección "Lista de control de la
consigna").

## Cómo ayudar: tutor

El agente trabaja como **tutor**. En la defensa final cada integrante tiene que explicar lo que hizo, así que:

- Escribí código en pasos chicos, una función o un bloque por vez, y explicá cada uno en lenguaje simple antes de seguir.
- Nombrá el CU, la RN o el diagrama que implementa cada función (por ejemplo, "implementa RN-04, ver SD-01 paso 3").
- Cuando el pedido es una tarjeta entera, proponé primero el plan de funciones y esperá el OK.
- Respondé en español.
## Reglas obligatorias de código

Fuente de verdad: [`Reglas-Proyecto.md`](Reglas-Proyecto.md). Resumen operativo:

| Regla | Detalle |
|-------|---------|
| Lenguaje | Java (sin frameworks, sin Maven/Gradle) |
| Una clase por archivo | El archivo se llama igual que la clase |
| Nombres de clase | Mayúscula inicial (`ControlAcceso`, `Empleado`) |
| Nombres de atributos | Minúscula inicial, descriptivos (`identificacion`, no `id`) |
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

JDK: 17 o superior (en este equipo, 23.0.1; `.idea/misc.xml` declara nivel 21).

## Estado real del código

No asumir que lo documentado está implementado. Estado actual:

- ✅ `ControlAcceso`: esqueleto correcto del Singleton (lazy, sin `synchronized`).
- ✅ `Empleado`: 4 atributos privados, constructor y getters. **Sin `nivelAcceso`, sin setters.**
- ⚠️ `Sector`, `Permiso`, `RegistroAcceso`: clases vacías.
- ❌ `NivelAcceso` (enum): no existe todavía.
- ⚠️ `Main`: `main` vacío, sin demostración del patrón.
- ✅ El proyecto compila sin errores.

El **diseño** (Sección 1 y 2) ya está documentado y es más amplio que el código:
`docs/informe/04-diseno.md` define las siete clases/enums, sus atributos y
responsabilidades.

Decisiones de diseño **ya tomadas** (no reabrir sin actualizar
`docs/informe/09-decisiones.md`):

1. `NivelAcceso` y `TipoPermiso` son enums; `nivelAcceso`/`nivelRequerido` no son `int`.
2. `RegistroAcceso.fechaHora` es `LocalDateTime`, asignado por el propio registro.
3. `Sector.identificacion` (no `idS`).
4. La colección `permisos` vive en `ControlAcceso`.
5. `verificarAcceso()` no registra; `registrarIntento()` sí.
6. Setters en todas las clases, incluida `RegistroAcceso` (regla de la cátedra).

Mantener actualizado `docs/informe/07-conclusiones.md` y el
[índice de documentación](docs/README.md) cuando cambie el estado.

## Convenciones de documentación

- Idioma: **español** (código, comentarios, docs y mensajes de commit).
- Formato: Markdown UTF-8.
- Diagramas: Mermaid embebido en los `.md`; no introducir PlantUML ni imágenes binarias
  si un diagrama de texto alcanza.
- Imágenes: `docs/assets/`, referenciadas con rutas relativas.
- No duplicar contenido: el informe referencia el código, no lo copia entero.

## Git

- Una rama por tarjeta de Trello: `impl-05-reservar`, `docs-rn23`, etc.
- PR a `main` con la revisión de al menos un compañero antes de mergear. GitHub no lo exige: es un acuerdo del grupo.
- Mensajes de commit en español, en presente: "Agrega registro de intento de acceso".
- La EGI es **grupal**: la revisión de un compañero antes del merge es parte del acuerdo del grupo.

## Seguridad y secretos

- No incluir credenciales, tokens ni datos personales reales en el repositorio ni en la
  documentación. Usar datos ficticios en ejemplos (`Ana Gómez`, DNI `30111222`).
- No subir archivos de `out/` ni configuración local de IDE más allá de `.idea/` ya versionada.
