public class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;

    public Permiso(){

    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Sector getSector() {
        return sector;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean esEspecial(){
        return  especial;
    }

    public void revocar(){

    }
}