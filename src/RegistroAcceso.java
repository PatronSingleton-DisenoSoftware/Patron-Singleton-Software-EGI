import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroAcceso {
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Empleado empleado;
    private Sector sector;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivo;


    public RegistroAcceso(Empleado empleado, Sector sector, boolean autorizado, String motivo){
        // empleado y sector pueden ser null si el intento se hizo con una
        // ID que no existe; el motivo explica el rechazo.
        this.empleado = empleado;
        this.sector = sector;
        this.autorizado = autorizado;
        this.fechaHora = LocalDateTime.now();
        setMotivo(motivo);   // valida que el motivo no este vacio (CP-11)
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
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora del registro no puede ser nula");
        }
        this.fechaHora = fechaHora;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public void setMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo del registro no puede estar vacio");
        }
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        // Si el intento vino con datos inexistentes, se muestra el texto en vez de un null
        String nombreEmpleado = (getEmpleado() == null)
                ? "Empleado no registrado"
                : getEmpleado().getNombre() + " " + getEmpleado().getApellido()
                        + " (ID " + getEmpleado().getIdEmpleado() + ")";
        String nombreSector = (getSector() == null)
                ? "Sector inexistente"
                : getSector().getNombre() + " (ID " + getSector().getIdSector() + ")";
        String resultado = isAutorizado() ? "AUTORIZADO" : "DENEGADO";

        return getFechaHora().format(FORMATO_FECHA_HORA) + " | "
                + nombreEmpleado + " | "
                + nombreSector + " | "
                + resultado + " | "
                + getMotivo();
    }
}
