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

El diseño está cerrado y las Secciones I, II y IV del documento de entrega están
completas. Quedan **tres bloqueantes** para poder entregar:

1. el código no está completo (faltan clases, enums y métodos de `ControlAcceso`);
2. la clase de prueba no ejecuta todavía el flujo completo;
3. falta publicar el repositorio y pegar el enlace en la Sección III.

El detalle está en la
[lista de control](EGI-Patron-Singleton.md#lista-de-control-de-la-consigna).

## Convenciones

- Idioma: español.
- Formato: Markdown (`.md`), codificación UTF-8.
- Nombres de archivo en minúsculas, con prefijo numérico para mantener el orden de lectura.
- Diagramas: [Mermaid](https://mermaid.js.org/) embebido en los `.md` (IntelliJ lo renderiza en la vista previa, sin plugins). Para el UML de la entrega, exportar `assets/uml-clases.puml` a PNG con [plantuml.com](https://www.plantuml.com/plantuml/uml/) o draw.io e insertar la imagen en el PDF.
- Imágenes y capturas: carpeta `docs/assets/` y referencia relativa desde el documento que las usa.
- Código citado: bloques ```` ```java ```` con el lenguaje indicado.
