public class Empleado {
    private int identificacion;
    private int dni;
    private String nombre;
    private String apellido;

    public Empleado(int identificacion, int dni, String nombre, String apellido){
        this.identificacion = identificacion;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
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

}

