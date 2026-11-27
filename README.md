# Sistema de Control de Acceso — Patrón Singleton

Trabajo práctico de **Diseño de Software** (ITU — UNCuyo, Sede Este): implementación en
**Java** del patrón **Singleton** aplicado a un sistema de control de acceso para la
empresa ficticia ENVIDIA.

- **Documento de entrega:** [`docs/EGI-Patron-Singleton.md`](docs/EGI-Patron-Singleton.md)
  (cuatro secciones: planteamiento, diseño UML, implementación y análisis SOLID).
- **Índice de documentación:** [`docs/README.md`](docs/README.md).
- **Pieza central:** [`src/ControlAcceso.java`](src/ControlAcceso.java) (el Singleton).
- **Punto de entrada:** [`src/Main.java`](src/Main.java) (menú por consola y demostración).

## Requisitos

- JDK 17 o superior (probado con JDK 23.0.1).
- No se usan frameworks, Maven ni Gradle.

## Compilar y ejecutar

```powershell
javac -Xlint:all -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

En IntelliJ IDEA: abrir la carpeta raíz del repositorio, marcar `src/` como *Sources Root*
si hace falta y ejecutar `src/Main.java` con **Run**.

## Estructura

```
src/          Código fuente (Java, una clase por archivo)
docs/         Documento de entrega, informe extendido, manuales y UML
docs/assets/  Diagrama de clases (.puml y .png)
out/          Salida de compilación (ignorada por git)
```

## Estado

El código compila sin errores ni advertencias con `javac -Xlint:all`. La lógica de altas,
niveles, permisos, verificación de accesos y auditoría está implementada. Queda como
pendiente de entrega únicamente la exportación del documento a PDF.

Las reglas de código del proyecto están en [`Reglas-Proyecto.md`](Reglas-Proyecto.md) y la
guía para colaboradores y agentes en [`AGENTS.md`](AGENTS.md).
