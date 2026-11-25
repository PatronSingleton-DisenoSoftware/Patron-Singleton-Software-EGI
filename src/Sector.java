public class Sector {
    private int idS;
    private String nombre;
    private NivelAcceso nivelRequerido;


    public Sector(int idS, String nombre, NivelAcceso nivelRequerido){
        this.idS = idS;
        this.nombre = (nombre != null) ? nombre.trim() : "";
        this.nivelRequerido = (nivelRequerido != null) ? nivelRequerido : NivelAcceso.NIVEL_1;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdS() {
        return idS;
    }

    public NivelAcceso getNivelRequerido() {
        return nivelRequerido;
    }

    public void setIdS(int idS) {
        if (idS <= 0) {
            System.out.println("Error: el ID del sector debe ser mayor a 0.");
            return;
        }
        this.idS = idS;
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
        return "Sector [ID: " + idS + ", Nombre: " + nombre + ", Nivel Requerido: " + nivelRequerido + "]";
    }
}
