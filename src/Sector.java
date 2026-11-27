public class Sector {
    private int idSector;
    private String nombre;
    private NivelAcceso nivelRequerido;

    public Sector(int idSector, String nombre, NivelAcceso nivelRequerido){
        // Se parte de valores seguros y se delega en los setters,
        // asi el constructor y los setters validan siempre igual.
        this.idSector = 0;
        this.nombre = "";
        this.nivelRequerido = NivelAcceso.NIVEL_1;
        setIdSector(idSector);
        setNombre(nombre);
        setNivelRequerido(nivelRequerido);
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdSector() {
        return idSector;
    }

    public NivelAcceso getNivelRequerido() {
        return nivelRequerido;
    }

    public void setIdSector(int idSector) {
        if (idSector <= 0) {
            System.out.println("Error: el ID del sector debe ser mayor a 0.");
            return;
        }
        this.idSector = idSector;
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
        return "Sector [ID: " + idSector + " | Nombre: " + nombre + " | Nivel Requerido: " + nivelRequerido + "]";
    }
}
