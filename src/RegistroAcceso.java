import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistroAcceso {
    private Empleado empleado;
    private Sector sector;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivo;

    public  RegistroAcceso(){

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
}
