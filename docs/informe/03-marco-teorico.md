# 3. Marco teórico

## 3.1. Patrones de diseño

Un patrón de diseño es una solución general y reutilizable a un problema que aparece
repetidamente en el diseño de software. No es código listo para copiar, sino una
**plantilla** que se adapta al contexto.

El patrón Singleton pertenece al grupo de los **patrones creacionales**: se ocupa de
cómo se crean los objetos.

## 3.2. Singleton

### Definición

> El patrón Singleton garantiza que una clase tenga una única instancia y proporciona
> un punto de acceso global a ella.

### Problema que resuelve

Hay situaciones donde tener más de un objeto de cierta clase produce inconsistencias:

- Un registro central de accesos: dos registros distintos = información duplicada o perdida.
- Un gestor de configuración: dos copias = dos configuraciones distintas en el mismo programa.
- Un pool de conexiones o un logger global.

### Estructura

| Elemento | Rol |
|----------|-----|
| Atributo `private static` | Guarda la única instancia. Es `static` porque pertenece a la clase, no a un objeto. |
| Constructor `private` | Impide que otras clases hagan `new`. |
| Método `public static` de acceso | Crea la instancia la primera vez y siempre devuelve la misma. |

### Variantes de implementación en Java

| Variante | Descripción | Ventaja | Riesgo |
|----------|-------------|---------|--------|
| **Lazy** (perezosa) | La instancia se crea en el primer `getInstancia()`. | No se crea si nunca se usa. | No es segura frente a hilos sin sincronización. |
| **Eager** (anticipada) | `private static final X instancia = new X();` | Simple y segura frente a hilos. | Se crea siempre, aunque no se use. |
| **Sincronizada** | `getInstancia()` marcado `synchronized`. | Segura frente a hilos. | Costo de sincronización en cada llamada. |
| **Double-checked locking** | Verificación doble con `volatile`. | Segura y eficiente. | Más compleja de leer. |
| **Enum** | `enum X { INSTANCIA; }` | La más robusta: resiste reflexión y serialización. | Menos flexible si se necesita herencia. |

En este trabajo se usa la variante **lazy** sin sincronización, porque el objetivo
pedagógico es mostrar la estructura clásica del patrón y el programa es de un solo hilo.
La sección de [conclusiones](07-conclusiones.md) analiza qué habría que cambiar en un
entorno concurrente.

### Ventajas

- Control estricto sobre la creación de la instancia.
- Acceso simple y global sin pasar el objeto por parámetro.
- Ahorro de recursos cuando el objeto es costoso de crear.

### Desventajas y críticas

- **Estado global:** dificulta el testing (no se puede sustituir por un doble de prueba fácilmente).
- **Acoplamiento:** el código que lo usa depende de una referencia concreta.
- **Concurrencia:** una implementación naive no es segura frente a múltiples hilos.
- **Serialización/reflexión:** pueden romper la unicidad si no se protege explícitamente.
- Se lo considera frecuentemente un **anti-patrón** cuando se usa solo como "variable global con esteroides".

## 3.3. Aplicación al control de acceso

En el dominio del problema, `ControlAcceso` concentra el estado compartido del sistema:
los empleados habilitados, los sectores, los permisos y los registros de ingreso. Si
existieran varias instancias:

- un empleado dado de alta en una instancia no existiría en la otra;
- una revocación de permiso quedaría registrada solo en una copia, y el resto de los
  puntos de acceso seguiría autorizando el ingreso;
- los registros de ingreso quedarían repartidos y las consultas darían resultados
  distintos según la puerta consultada.

Por eso la unicidad **no es un capricho técnico**: es un requisito de seguridad del
dominio. El sistema además combina esa fuente única de verdad con dos mecanismos de
autorización —niveles acumulativos (1 a 5) y permisos especiales activos—, ambos
evaluados en el mismo lugar.

---

[Volver al índice](../README.md) · [← Anterior: Objetivos](02-objetivos.md) · [Siguiente: Diseño →](04-diseno.md)
