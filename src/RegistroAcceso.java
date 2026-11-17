import java.time.LocalDateTime;

public class RegistroAcceso {
    private Empleado empleado;
    private Sector sector;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivo;

    public RegistroAcceso(){

    }

    public RegistroAcceso(Empleado empleado, Sector sector, boolean autorizado, String motivo){
        this.empleado = empleado;
        this.sector = sector;
        this.autorizado = autorizado;
        this.motivo = motivo;
        this.fechaHora = LocalDateTime.now();
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Sector getSector() {
        return sector;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
