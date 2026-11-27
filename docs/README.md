# Documentación — Sistema de Control de Acceso (Patrón Singleton)

Índice general de la documentación del proyecto. Todo está en Markdown para poder
leerse directamente desde IntelliJ IDEA (vista previa con el ícono de "ojito" en el
margen del editor) o desde GitHub.

## Documento de entrega

El documento que se entrega en el campus virtual es
[`EGI-Patron-Singleton.md`](EGI-Patron-Singleton.md): un único documento con las cuatro
secciones obligatorias de la consigna, en orden.

| Sección de la consigna | Dónde está |
|------------------------|------------|
| I. Planteamiento y justificación del problema | `EGI-Patron-Singleton.md` §I |
| II. Diseño arquitectónico (UML) | `EGI-Patron-Singleton.md` §II + `assets/uml-clases.puml` |
| III. Implementación práctica (código + prueba + GitHub) | `EGI-Patron-Singleton.md` §III + `src/` |
| IV. Análisis crítico y principios SOLID | `EGI-Patron-Singleton.md` §IV |

Los capítulos de `informe/` son la **versión extendida de trabajo** (más detalle del que
pide la consigna). Si hay diferencias, manda el documento de entrega.

## Índice de la documentación de trabajo

### Capítulos (versión extendida)

| #  | Documento | Contenido |
|----|-----------|-----------|
| 00 | [Carátula](informe/00-caratula.md) | Datos de la entrega grupal. |
| 01 | [Planteamiento y solución](informe/01-introduccion.md) | Caso ENVIDIA, enfoque convencional y solución propuesta. |
| 02 | [Objetivos](informe/02-objetivos.md) | Objetivo general, objetivos específicos y requisitos verificables. |
| 03 | [Marco teórico](informe/03-marco-teorico.md) | Qué es el patrón Singleton, cuándo usarlo, ventajas y riesgos. |
| 04 | [Diseño arquitectónico](informe/04-diseno.md) | Clases, atributos, responsabilidades, enums, relaciones, UML y ADR. |
| 05 | [Implementación](informe/05-implementacion.md) | Explicación del código clase por clase y reglas aplicadas. |
| 06 | [Pruebas](informe/06-pruebas.md) | Plan de pruebas, casos y cómo ejecutarlos. |
| 07 | [Conclusiones](informe/07-conclusiones.md) | Resultados, limitaciones y trabajo futuro. |
| 08 | [Bibliografía](informe/08-bibliografia.md) | Fuentes consultadas. |
| 09 | [Decisiones de diseño (ADR)](informe/09-decisiones.md) | Decisiones que completan o corrigen el planteo original, con su justificación. |

### Manual técnico

| Documento | Contenido |
|-----------|-----------|
| [Instalación del entorno](manual/instalacion-entorno.md) | JDK, IntelliJ IDEA, apertura y compilación del proyecto. |
| [Guía de uso](manual/guia-uso.md) | Cómo ejecutar el programa y qué esperar en cada etapa. |

### Reglas del proyecto

Las convenciones de código obligatorias viven en
[`Reglas-Proyecto.md`](../Reglas-Proyecto.md), en la raíz del repositorio.

## Estado frente a la consigna

El proyecto está **implementado y compila sin advertencias** con `javac -Xlint:all`. La
estructura de clases, el Singleton, la lógica de negocio, la auditoría y la demostración
automática están completos.

Queda **un solo bloqueante** para la entrega:

1. exportar `EGI-Patron-Singleton.md` a PDF y subirlo al campus virtual.

Las brechas de diseño que se mantienen de forma deliberada son las de los ADR-02
(`boolean especial` en lugar de `TipoPermiso`) y ADR-06 (`verificarAcceso` evalúa y
registra). Están aceptadas y documentadas en el [Anexo](EGI-Patron-Singleton.md#anexo-decisiones-de-diseño)
y en [Decisiones de diseño](informe/09-decisiones.md).

El repositorio ya es público y su enlace figura en la Sección III. El detalle está en la
[lista de control](EGI-Patron-Singleton.md#lista-de-control-de-la-consigna) y en
[Implementación §5.5](informe/05-implementacion.md#55-brecha-entre-diseño-e-implementación).

## Convenciones

- Idioma: español.
- Formato: Markdown (`.md`), codificación UTF-8.
- Nombres de archivo en minúsculas, con prefijo numérico para mantener el orden de lectura.
- Diagramas: [Mermaid](https://mermaid.js.org/) embebido en los `.md` del informe de trabajo (IntelliJ lo renderiza en la vista previa, sin plugins). Las figuras del documento de entrega están como imágenes: `assets/uml-clases.png` (clases) y `assets/flujo-acceso.png` (secuencia), generadas desde sus `.puml`; para regenerarlas, ver las instrucciones del final de este archivo.
- Imágenes y capturas: carpeta `docs/assets/` y referencia relativa desde el documento que las usa.
- Código citado: bloques ```` ```java ```` con el lenguaje indicado.

## Regenerar los diagramas (PNG)

Fuentes: `assets/uml-clases.puml` y `assets/flujo-acceso.puml`. Salidas: los PNG del mismo
nombre, ya incluidos en el documento de entrega.

### Opción A — IntelliJ IDEA (sin salir del IDE)

1. `Settings → Plugins → Marketplace` → instalar **PlantUML Integration**.
2. Abrir `docs/assets/uml-clases.puml` o `docs/assets/flujo-acceso.puml`.
3. Clic derecho en el editor → **Save diagram** (o el ícono de exportar de la barra de
   herramientas) → elegir PNG.

> Para el layout clásico con los miembros `static` subrayados, el plugin necesita
> Graphviz instalado y `GRAPHVIZ_DOT` apuntando a `dot.exe`.

### Opción B — Online (la más rápida)

1. Abrir [plantuml.com/plantuml/uml](https://www.plantuml.com/plantuml/uml/).
2. Pegar el contenido del `.puml`.
3. **Download PNG** (o usar [kroki.io](https://kroki.io/#/plantuml) con el mismo texto).

Sirve para regenerar sin instalar nada; el diagrama es público y no contiene datos
sensibles.

### Opción C — Local con `plantuml.jar`

```powershell
# descargar una vez
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/net/sourceforge/plantuml/plantuml/1.2024.7/plantuml-1.2024.7.jar" -OutFile plantuml.jar

# renderizar ambos (sin Graphviz, usa el layout Smetana)
java '-Djava.awt.headless=true' -jar plantuml.jar -tpng '-Playout=smetana' docs\assets\uml-clases.puml docs\assets\flujo-acceso.puml -o .
```

`-o .` deja el PNG al lado de cada `.puml`. Con Smetana el subrayado de `static` puede no
verse en el diagrama de clases; para el PNG final conviene Graphviz (opciones A o B).

> El nombre del PNG lo toma de la línea `@startuml …` de cada archivo.
