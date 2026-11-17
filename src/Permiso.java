public class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;

    public Permiso(){

    }

    public Permiso(Empleado empleado, Sector sector, boolean especial){
        this.empleado = empleado;
        this.sector = sector;
        this.especial = especial;
        this.activo = true;
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
        return especial;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public void revocar(){
        this.activo = false;
    }
}
