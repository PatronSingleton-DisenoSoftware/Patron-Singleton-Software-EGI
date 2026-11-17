public class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;

    public Permiso(Empleado empleado, Sector sector, boolean activo, boolean especial){
        this.empleado = empleado;
        this.sector = sector;
        this.activo = true;
        this.especial = false;
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

    public boolean isActivo() {
        return activo;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void revocar(){
        setActivo(false);
        if(!isActivo()){
            setActivo(true);
    }

    }
}