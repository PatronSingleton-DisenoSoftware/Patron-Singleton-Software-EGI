public class Sector {
    private int identificacion;
    private String nombre;
    private NivelAcceso nivelRequerido;

    public Sector(int identificacion, String nombre, NivelAcceso nivelRequerido){
        // Se parte de valores seguros y se delega en los setters,
        // asi el constructor y los setters validan siempre igual.
        this.identificacion = 0;
        this.nombre = "";
        this.nivelRequerido = NivelAcceso.NIVEL_1;
        setIdentificacion(identificacion);
        setNombre(nombre);
        setNivelRequerido(nivelRequerido);
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdentificacion() {
        return identificacion;
    }

    public NivelAcceso getNivelRequerido() {
        return nivelRequerido;
    }

    public void setIdentificacion(int identificacion) {
        if (identificacion <= 0) {
            System.out.println("Error: la identificacion del sector debe ser mayor a 0.");
            return;
        }
        this.identificacion = identificacion;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().equals("")) {
            System.out.println("Error: el nombre del sector no puede estar vacio.");
            return;
        }
        this.nombre = nombre.trim();
    }

    public void setNivelRequerido(NivelAcceso nivelRequerido) {
        if (nivelRequerido == null) {
            System.out.println("Error: el nivel requerido no puede ser nulo.");
            return;
        }
        this.nivelRequerido = nivelRequerido;
    }

    @Override
    public String toString() {
        return "Sector [ID: " + identificacion + " | Nombre: " + nombre + " | Nivel Requerido: " + nivelRequerido + "]";
    }
}
