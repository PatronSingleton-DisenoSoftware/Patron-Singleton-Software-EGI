# Instalación del entorno

## 1. Requisitos

| Componente | Versión usada | Notas |
|------------|---------------|-------|
| JDK | 17 o superior | En este equipo hay JDK 23.0.1; el proyecto declara nivel de lenguaje 21 en `.idea/misc.xml`. |
| IntelliJ IDEA | Community Edition 2025.2.6.3 | La edición Community alcanza: no se usan frameworks de Enterprise. |
| Git | Cualquiera reciente | Opcional, solo para versionado. |

Verificar el JDK desde la terminal:

```powershell
java -version
javac -version
```

Ambos comandos deben responder con la misma versión.

## 2. Abrir el proyecto

1. Abrir IntelliJ IDEA.
2. **File → Open…** y seleccionar la **carpeta raíz** del repositorio
   (`Patron-Singleton-Software-EGI`), no un archivo suelto.
3. IntelliJ detecta el archivo `Patron-Singleton-D.S-EGI.iml` y lo trata como proyecto
   Java existente. No hace falta importar como Gradle/Maven.

## 3. Configurar el JDK

Si al abrir aparece un aviso de "Project SDK is not defined":

1. **File → Project Structure…** (`Ctrl+Alt+Shift+S`).
2. **Project → SDK → Add SDK → JDK…**
3. Elegir el JDK instalado (por ejemplo, `C:\Program Files\Java\jdk-23`).
4. Verificar **Language level**: 21 o el que corresponda al JDK elegido.

> El proyecto no usa Maven ni Gradle: se compila con el compilador propio de IntelliJ y
> el resultado va a `out/`, carpeta ya ignorada por `.gitignore`.

## 4. Verificar la estructura

En el panel **Project** debería verse:

```
Patron-Singleton-Software-EGI
├── src                ← marcada como carpeta de fuentes (azul)
│   ├── ControlAcceso.java
│   ├── Empleado.java
│   ├── Main.java
│   ├── Permiso.java
│   ├── RegistroAcceso.java
│   └── Sector.java
├── docs
├── Reglas-Proyecto.md
└── Patron-Singleton-D.S-EGI.iml
```

Si `src` aparece como carpeta común (gris), marcarla: clic derecho →
**Mark Directory as → Sources Root**.

## 5. Compilar y ejecutar

- **Build → Build Project** (`Ctrl+F9`).
- Abrir `src/Main.java` y usar el triángulo verde junto a `main` para ejecutar.

El proyecto compila sin errores en el estado actual.

## 6. Ver la documentación

Los `.md` de `docs/` se abren directamente en el editor. Para la vista renderizada, usar
el ícono de vista previa (esquina superior derecha del editor) o `Ctrl+Shift+P` según el
keymap. Mermaid se renderiza sin plugins adicionales.

---

[Volver al índice](../README.md) · [Siguiente: Guía de uso →](guia-uso.md)
