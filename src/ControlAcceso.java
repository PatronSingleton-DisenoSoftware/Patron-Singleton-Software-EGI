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
    // Getters y Setters

// Metodo de acceso unico
    public static ControlAcceso getInstance() {
        if (instancia == null){
            instancia = new ControlAcceso();
        }
        return instancia;
    }
    // Metodos publicos o privados
    public void registrarEmpleado(Empleado empleado){
        for (Empleado existente : empleados){
            if (existente.getIdentificacion() == empleado.getIdentificacion()){
                System.out.println("Ya existe un empleado con identificacion " + empleado.getIdentificacion());
                break;   // corta acá: no se agrega
            }
        }
        empleados.add(empleado);   // recién acá, fuera del for
        System.out.println("Empleado registrado: " + empleado.getNombre() + " " + empleado.getApellido());
    }

    public void registrarSector(Sector sector){
        // Logica
    }
    public void asignarNivel(Empleado empleado, NivelAcceso nivelAcceso){
        // Logica
    }
    public void concederPermiso(Permiso permiso){
        //Logica
    }
    public void revocarPermiso(Permiso permiso){
        // Logica
    }
    public boolean verificarAcceso(int numEmpleado, int numSector){
        // Retorno de ejemplo para que no marque el error
        boolean ejemplo = false;
        return ejemplo;
    }
    public RegistroAcceso consultarHistorial(){
        // Esta en null a modo de ejemplo para que no de error
        return null;
    }
}
