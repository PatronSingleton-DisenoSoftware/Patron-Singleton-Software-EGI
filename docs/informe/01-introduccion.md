# 1. Planteamiento y solución del problema

## 1.1. Contexto

La empresa **ENVIDIA** dispone de un edificio con diferentes sectores de acceso
restringido: oficinas administrativas, salas de servidores y depósitos. La empresa
necesita un sistema que **centralice el control de acceso** de los empleados.

Cada vez que una persona intenta ingresar, el sistema debe:

1. verificar su identificación,
2. consultar sus permisos,
3. registrar el intento de acceso.

## 1.2. El problema

Si cada punto de entrada administrara sus propias reglas de seguridad y sus propios
registros, aparecerían inconsistencias. El caso crítico es el siguiente:

> Un empleado al que se le revocó el permiso podría seguir teniendo autorización en otro
> punto de acceso que todavía no actualizó sus datos.

Es decir, el problema no es solo de duplicación de información, sino de **seguridad**:
una revocación que no llega a todos los sectores deja el sistema de control inutilizado.

## 1.3. Enfoque convencional (sin Singleton)

Sin un punto único de control en memoria, cada punto de acceso crea y maneja su propia
copia local de la lista de permisos. Las consecuencias son:

| Consecuencia | Descripción |
|--------------|-------------|
| **Copias independientes** | La puerta principal, la sala de servidores y el depósito mantienen cada uno su propia versión de los permisos, de forma aislada. |
| **Inconsistencia ante cambios** | Si Recursos Humanos revoca un acceso por motivos de seguridad, la modificación se registra solo en la instancia donde se realizó la acción. |
| **Falla de seguridad** | Las demás puertas siguen operando con copias desactualizadas: la persona puede ingresar por cualquier otro sector, anulando el propósito del control centralizado. |

En síntesis: el enfoque convencional produce **fragmentación y desincronización del
estado**, lo que se traduce en una vulnerabilidad crítica.

## 1.4. Solución propuesta

Se propone aplicar el patrón de diseño **Singleton** en la clase `ControlAcceso`, porque
el sistema necesita un único punto centralizado que administre los permisos de los
empleados.

Con una única instancia:

- todos los sectores del edificio consultan **la misma información actualizada** sobre
  autorizaciones;
- cualquier modificación de permisos —una autorización nueva o una revocación— queda
  **disponible para todo el sistema** de inmediato;
- todos los accesos se gestionan bajo los **mismos criterios**.

## 1.5. Alcance

| Dentro del alcance | Fuera del alcance (esta entrega) |
|--------------------|----------------------------------|
| Modelo de dominio: `Empleado`, `Sector`, `Permiso`, `RegistroAcceso` | Persistencia en base de datos |
| `ControlAcceso` como Singleton | Interfaz gráfica |
| Niveles de acceso acumulativos y permisos especiales | Integración con hardware de puertas/lectores |
| Registro de intentos autorizados y rechazados | Autenticación real de usuarios |

El estado de implementación de cada punto se detalla en
[Implementación](05-implementacion.md) y [Conclusiones](07-conclusiones.md).

---

[Volver al índice](../README.md) · [Siguiente: Objetivos →](02-objetivos.md)
