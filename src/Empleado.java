public class Empleado {
    private int identificacion;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcceso;
    private boolean registroValido;

    public Empleado(int identificacion, int dni, String nombre, String apellido, NivelAcceso nivelAcceso) {
        boolean nombreValido = (nombre != null && !nombre.trim().equals(""));
        boolean apellidoValido = (apellido != null && !apellido.trim().equals(""));
        boolean dniValido = esDniValido(dni);

        if (!nombreValido || !apellidoValido) {
            this.registroValido = false;
            System.out.println("No se puede registrar este empleado porque no tiene nombre o apellido.");
            return;
        }

        if (!dniValido) {
            this.registroValido = false;
            System.out.println("No se puede registrar este empleado porque el DNI no tiene la cantidad de digitos valida (7 u 8 digitos).");
            return;
        }

        if (identificacion <= 0) {
            this.registroValido = false;
            System.out.println("No se puede registrar este empleado porque la identificacion debe ser mayor a 0.");
            return;
        }

        this.registroValido = true;
        this.identificacion = identificacion;
        this.dni = dni;
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();

        if (nivelAcceso == null) {
            this.nivelAcceso = NivelAcceso.NIVEL_1;
        } else {
            this.nivelAcceso = nivelAcceso;
        }
    }

    public static boolean esDniValido(int dni) {
        if (dni <= 0) {
            return false;
        }
        String dniTexto = String.valueOf(dni).trim();
        return dniTexto.length() == 7 || dniTexto.length() == 8;
    }

    public boolean isRegistroValido() {
        return registroValido;
    }

    public void setRegistroValido(boolean registroValido) {
        this.registroValido = registroValido;
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

    public int getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(int identificacion) {
        if (identificacion <= 0) {
            System.out.println("Error: la identificacion debe ser mayor a 0.");
            return;
        }
        this.identificacion = identificacion;
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
        return "Empleado [ID: " + identificacion + " | DNI: " + dni + " | Nombre: " + nombre + " " + apellido + " | Nivel: " + nivelAcceso + "]";
    }
}