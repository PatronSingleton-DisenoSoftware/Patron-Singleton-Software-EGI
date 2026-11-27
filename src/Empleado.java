public final class Empleado {
    private int idEmpleado;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcceso;
    private boolean registroValido;

    public Empleado(int idEmpleado, int dni, String nombre, String apellido, NivelAcceso nivelAcceso) {
        // Se guardan los datos primero: si algo es invalido, el objeto conserva
        // lo que se intento cargar y resulta mas facil de inspeccionar.
        this.idEmpleado = idEmpleado;
        this.dni = dni;
        this.nombre = (nombre == null) ? null : nombre.trim();
        this.apellido = (apellido == null) ? null : apellido.trim();
        this.nivelAcceso = (nivelAcceso == null) ? NivelAcceso.NIVEL_1 : nivelAcceso;
        this.registroValido = false;

        if (this.nombre == null || this.nombre.isEmpty()
                || this.apellido == null || this.apellido.isEmpty()) {
            System.out.println("No se puede registrar este empleado porque no tiene nombre o apellido.");
            return;
        }

        if (!esDniValido(this.dni)) {
            System.out.println("No se puede registrar este empleado porque el DNI no tiene la cantidad de digitos valida (7 u 8 digitos).");
            return;
        }

        if (this.idEmpleado <= 0) {
            System.out.println("No se puede registrar este empleado porque el ID del empleado debe ser mayor a 0.");
            return;
        }

        this.registroValido = true;
    }

    public static boolean esDniValido(int dni) {
        if (dni <= 0) {
            return false;
        }
        String dniTexto = String.valueOf(dni).trim();
        return dniTexto.length() == 7 || dniTexto.length() == 8;
    }

    public boolean isRegistroValido() {
        return registroValido && datosValidos();
    }

    public void setRegistroValido(boolean registroValido) {
        // Un objeto solo queda marcado como valido si sus datos realmente lo son.
        this.registroValido = registroValido && datosValidos();
    }

    // Recalcula la validez a partir de los datos actuales.
    public boolean datosValidos() {
        return idEmpleado > 0
                && esDniValido(dni)
                && nombre != null && !nombre.trim().isEmpty()
                && apellido != null && !apellido.trim().isEmpty()
                && nivelAcceso != null;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        if (!esDniValido(dni)) {
            System.out.println("Error: el DNI debe tener 7 u 8 digitos validos.");
            return;
        }
        this.dni = dni;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        if (idEmpleado <= 0) {
            System.out.println("Error: el ID del empleado debe ser mayor a 0.");
            return;
        }
        this.idEmpleado = idEmpleado;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().equals("")) {
            System.out.println("Error: el apellido no puede estar vacio.");
            return;
        }
        this.apellido = apellido.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().equals("")) {
            System.out.println("Error: el nombre no puede estar vacio.");
            return;
        }
        this.nombre = nombre.trim();
    }

    public NivelAcceso getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(NivelAcceso nivelAcceso) {
        if (nivelAcceso == null) {
            System.out.println("Error: el nivel de acceso no puede ser nulo.");
            return;
        }
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String toString() {
        return "Empleado [ID: " + idEmpleado + " | DNI: " + dni + " | Nombre: " + nombre + " " + apellido + " | Nivel: " + nivelAcceso + "]";
    }
}