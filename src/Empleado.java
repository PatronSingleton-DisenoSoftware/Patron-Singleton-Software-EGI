public class Empleado {
    private int identificacion;
    private int dni;
    private String nombre;
    private String apellido;
    private NivelAcceso nivelAcesso;

    public Empleado(int identificacion, int dni, String nombre, String apellido, NivelAcceso nivelAcceso){
        this.identificacion = identificacion;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nivelAcesso = nivelAcceso;
    }

    public int getDni() {
        return dni;
    }

    public int getIdentificacion() {
        return identificacion;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public NivelAcceso getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(NivelAcceso nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    @Override
    public String toString() {
        return "Empleado [ID: " + identificacion + ", Nombre: " + nombre + " " + apellido + ", DNI: " + dni + ", Nivel: " + nivelAcesso + "]";
    }
}
