# 2. Objetivos

## 2.1. Objetivo general

Implementar en Java un sistema de control de acceso para la empresa ENVIDIA que
centralice la gestión de empleados, sectores y permisos mediante el patrón Singleton,
garantizando que todos los puntos de acceso trabajen sobre una única fuente de verdad.

## 2.2. Objetivos específicos

1. Demostrar, en el planteamiento del problema, por qué múltiples instancias del gestor
   producen desincronización de permisos y una falla de seguridad.
2. Modelar las entidades del dominio: `Empleado`, `Sector`, `Permiso` y `RegistroAcceso`.
3. Representar los niveles de seguridad con la enumeración `NivelAcceso`
   (`NIVEL_1` a `NIVEL_5`, acumulativos).
4. Implementar `ControlAcceso` como Singleton:
   - atributo estático y privado con la instancia,
   - constructor privado,
   - método público y estático de acceso (`getInstance()`),
   - colecciones de empleados, sectores, permisos y registros.
5. Implementar la verificación de acceso combinando **nivel acumulativo** y
   **permiso especial activo**.
6. Registrar todo intento de ingreso, autorizado o rechazado, con fecha/hora y motivo.
7. Verificar que la revocación de un permiso impacta en todos los puntos de acceso.

## 2.3. Requisitos verificables

| ID  | Requisito | Cómo se verifica | Estado |
|-----|-----------|------------------|--------|
| R1  | Existe una sola instancia de `ControlAcceso` | `a == b` es `true` en `Main` | ✅ Implementado (CP-01) |
| R2  | El constructor no es accesible desde fuera | `new ControlAcceso()` no compila | ✅ Por inspección |
| R3  | Los atributos son privados | Inspección del código | ✅ |
| R4  | Cada clase en su propio archivo | Estructura de `src/` | ✅ |
| R5  | El proyecto compila con `javac` | Compilación de todo `src/` | ✅ |
| R6  | Un nivel N habilita los niveles 1..N | Caso de prueba con nivel 3 | ✅ Implementado (CP-05 a CP-07) |
| R7  | Un permiso especial activo habilita el ingreso aunque falte nivel | Caso de prueba | ✅ Implementado (CP-08) |
| R8  | Un permiso revocado deniega el ingreso | Caso de prueba | ✅ Implementado (CP-09 y CP-13) |
| R9  | Todo intento queda registrado con fecha/hora y motivo | Consultar historial | ✅ Implementado (CP-10, CP-11 y CP-20) |

El detalle de cada caso está en [Pruebas](06-pruebas.md).

---

[Volver al índice](../README.md) · [← Anterior: Planteamiento](01-introduccion.md) · [Siguiente: Marco teórico →](03-marco-teorico.md)
