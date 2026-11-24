import java.util.ArrayList;
import java.util.List;

public class ControlAcceso {
    // Atributos
    private List<Empleado> empleados;
    private List<Sector> sectores;
    private List<Permiso>permisos;
    private List <RegistroAcceso>registros;
    private static ControlAcceso instancia;

    // Constructor de acceso unico
    private ControlAcceso(){
        this.empleados = new ArrayList<>();
        this.sectores = new ArrayList<>();
        this.permisos = new ArrayList<>();
        this.registros = new ArrayList<>();
    }

    // Metodo de acceso unico
    public static ControlAcceso getInstance() {
        if (instancia == null){
            instancia = new ControlAcceso();
        }
        return instancia;
    }

    // Metodos publicos o privados
    public void registrarEmpleado(Empleado empleado){
        // Guarda: no se aceptan datos vacios
        if (empleado == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        // Un empleado no se puede dar de alta dos veces
        if (buscarEmpleado(empleado.getIdentificacion()) != null){
            System.out.println("Ya existe un empleado con identificacion " + empleado.getIdentificacion());
            return;   // no se agrega
        }
        empleados.add(empleado);
        System.out.println("Empleado registrado: " + empleado.getNombre() + " " + empleado.getApellido());
    }
    
    public void registrarSector(Sector sector){
        // Guarda: no se aceptan datos vacios
        if (sector == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        // Un sector no se puede dar de alta dos veces
        if (buscarSector(sector.getIdS()) != null){
            System.out.println("Ya existe un sector con identificacion " + sector.getIdS());
            return;   // no se agrega
        }
        sectores.add(sector);
        System.out.println("Sector registrado: " + sector.getNombre());
    }

    public void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso){
        // Guarda: no se aceptan datos vacios
        if (empleado == null || nivelAcceso == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        // Solo se le asigna nivel a un empleado ya registrado
        Empleado registrado = buscarEmpleado(empleado.getIdentificacion());
        if (registrado == null){
            System.out.println("No existe un empleado con identificacion " + empleado.getIdentificacion());
            return;
        }
        registrado.setNivelAcesso(nivelAcceso);
        System.out.println("Nivel de " + registrado.getNombre() + " actualizado a " + nivelAcceso);
    }

    public void concederPermiso(Permiso permiso){
        // Guarda: no se aceptan datos vacios
        if (permiso == null || permiso.getEmpleado() == null || permiso.getSector() == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        Empleado empleado = permiso.getEmpleado();
        Sector sector = permiso.getSector();
        // CP-12: el permiso necesita un empleado y un sector que ya existan
        if (buscarEmpleado(empleado.getIdentificacion()) == null){
            System.out.println("No existe el empleado con identificacion " + empleado.getIdentificacion()
                    + ". Permiso rechazado (CP-12)");
            return;
        }
        if (buscarSector(sector.getIdS()) == null){
            System.out.println("No existe el sector con identificacion " + sector.getIdS()
                    + ". Permiso rechazado (CP-12)");
            return;
        }
        // Un mismo empleado y sector no se cargan dos veces
        Permiso existente = buscarPermiso(empleado, sector);
        if (existente != null && existente.estaActivo()){
            System.out.println("Ya existe un permiso activo para ese empleado y sector");
            return;
        }
        // Si estaba revocado, se vuelve a habilitar en vez de duplicar
        if (existente != null){
            existente.setActivo(true);
            System.out.println("Permiso reactivado para " + empleado.getNombre() + " en " + sector.getNombre());
            return;
        }
        permisos.add(permiso);
        System.out.println("Permiso concedido a " + empleado.getNombre() + " en " + sector.getNombre());
    }

    public void revocarPermiso(Permiso permiso){
        // Guarda: no se aceptan datos vacios
        if (permiso == null || permiso.getEmpleado() == null || permiso.getSector() == null){
            System.out.println("Error, no puede cargar datos vacios");
            return;
        }
        // Se revoca el permiso guardado, no el objeto que llega por parametro
        Permiso existente = buscarPermiso(permiso.getEmpleado(), permiso.getSector());
        if (existente == null){
            System.out.println("No existe un permiso para ese empleado y sector");
            return;
        }
        if (!existente.estaActivo()){
            System.out.println("El permiso ya estaba revocado");
            return;
        }
        existente.revocar();
        System.out.println("Permiso revocado a " + permiso.getEmpleado().getNombre()
                + " en " + permiso.getSector().getNombre());
    }
    
    public boolean verificarAcceso(int numEmpleado, int numSector){
        Empleado empleado = buscarEmpleado(numEmpleado);
        Sector sector = buscarSector(numSector);

        //1. Validaciones previas
        if (empleado == null){
            String motivo = "Empleado no registrado";
            System.out.println("Acceso denegado: " + motivo);
            return false;
        }
        if (sector == null) {
            String motivo = "Sector inexistente";
            System.out.println("Acceso denegado: " + motivo);
            return false;
        }
        boolean autorizado = false;
        String motivo = null;

        // 2. Verificar jerarquía de niveles (RN-01)
        if (empleado.getNivelAcesso().alcanzaA(sector.getNivelRequerido())) {
            autorizado = true;
            motivo = "Acceso autorizado por nivel";
        }
        // 3. Si el nivel no alcanza, verificar permiso especial activo (RN-02)
        else if (tienePermisoEspecialActivo(empleado, sector)) {
            autorizado = true;
            motivo = "Acceso autorizado por permiso especial";
        }
        // 4. Rechazo por nivel insuficiente (RN-05)
        else {
            autorizado = false;
            motivo = "Nivel insuficiente";
            System.out.println("Acceso denegado a " + empleado.getNombre() + " al sector "
                    + sector.getNombre() + ". Motivo: " + motivo);
        }
        // 5. Registrar intento en el historial (RN-04)
        RegistroAcceso registro = new RegistroAcceso(empleado, sector, autorizado, motivo);
        this.registros.add(registro);

        if (autorizado) {
            System.out.println("Acceso permitido a " + empleado.getNombre() + " al sector " + sector.getNombre());
        }

        return autorizado;
    }


    public RegistroAcceso consultarHistorial(){
        // Esta en null a modo de ejemplo para que no de error
        return null;
    }

    // --- Ayudantes -------
    public Empleado buscarEmpleado(int identificacion){
        for (Empleado emp : this.empleados){
            if (emp.getIdentificacion() == identificacion){
                return emp;
            }
        }
        return null;
    }

    public Sector buscarSector(int idS){
        for (Sector sec : this.sectores){
            if (sec.getIdS() == idS){
                return sec;
            }
        }
        return null;
    }

    private Permiso buscarPermiso(Empleado empleado, Sector sector){
        for (Permiso permiso : permisos){
            if (permiso.getEmpleado() != null && permiso.getSector() != null
                    && permiso.getEmpleado().getIdentificacion() == empleado.getIdentificacion()
                    && permiso.getSector().getIdS() == sector.getIdS()){
                return permiso;
            }
        }
        return null;
    }

    private boolean tienePermisoEspecialActivo(Empleado empleado, Sector sector) {
        for (Permiso p : this.permisos) {
            // Coincide empleado y sector, es especial y sigue activo
            if (p.getEmpleado().getIdentificacion() == empleado.getIdentificacion()
                    && p.getSector().getIdS() == sector.getIdS()
                    && p.esEspecial()
                    && p.estaActivo()) {
                return true;
            }
        }
        return false;
    }
}
