# Reglas Generales de Programación y Estilo
   Para mantener un código limpio, profesional y estandarizado en Java, se seguirán estrictamente las siguientes directrices:
***
- Lenguaje: Desarrollado íntegramente en Java.

- Archivos independientes: Cada clase y enumeración debe estar contenida en su propio archivo físico .java (por ejemplo, ControlAcceso.java, Empleado.java, etc.).

- Nomenclatura de Clases: Nombres en formato UpperCamelCase, donde la primera letra de cada palabra compuesta es mayúscula (Ej: ControlAcceso, RegistroAcceso).

- Nomenclatura de Atributos: Nombres en formato camelCase, comenzando con minúscula, y con nombres descriptivos que reflejen claramente el dato almacenado en memoria.

- Encapsulamiento: Todos los atributos de las clases deben ser private. El acceso a su lectura (get) y modificación (set) se realizará exclusivamente a través de métodos públicos (Getters y Setters), garantizando la integridad de los datos.
***
# Implementación de la Clase Singleton (ControlAcceso)
   La clase central del sistema aplicará el patrón de diseño Singleton para asegurar que exista una única instancia en memoria que administre la seguridad y evite la desincronización de datos entre los distintos puntos de acceso.

    Reglas específicas para la clase Singleton:

- Atributo estático privado: Debe contener una referencia estática y privada a sí misma (ej. private static ControlAcceso instancia;).

- Constructor privado: El constructor debe ser private para impedir la creación de instancias mediante el operador new desde fuera de la clase.

- Método de instancia público: Un método estático público (habitualmente llamado getInstancia()) que controla la creación de la instancia única (creándola solo si es nula) y la retorna.

- Atributos de colección privados: Almacenarán las listas de empleados, sectores, permisos e historial de accesos.

- Getters y Setters: Métodos de acceso controlados para gestionar los atributos internos según corresponda.
***