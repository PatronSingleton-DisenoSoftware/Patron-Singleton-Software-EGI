public final class Permiso {
    private Empleado empleado;
    private Sector sector;
    private boolean activo;
    private boolean especial;


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
        if (empleado == null) {
            System.out.println("Error: el empleado del permiso no puede ser nulo.");
            return;
        }
        this.empleado = empleado;
    }

    public void setSector(Sector sector) {
        if (sector == null) {
            System.out.println("Error: el sector del permiso no puede ser nulo.");
            return;
        }
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

    @Override
    public String toString() {
        String estado = activo ? "ACTIVO" : "REVOCADO";
        String tipo = especial ? "Especial" : "Regular";
        String emp = (empleado != null) ? empleado.getNombre() + " " + empleado.getApellido() : "Sin empleado";
        String sec = (sector != null) ? sector.getNombre() : "Sin sector";
        return "Permiso [" + estado + " | " + tipo + "] Empleado: " + emp + " -> Sector: " + sec;
    }
}
