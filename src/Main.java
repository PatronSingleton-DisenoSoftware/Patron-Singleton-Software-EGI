import java.util.List;
import java.util.Scanner;

public class Main {

    // Metodo auxiliar para leer y validar un NivelAcceso por consola
    private static NivelAcceso leerNivelAcceso(Scanner entradaDatos) {
        System.out.println("Nivel 1 - Acceso General");
        System.out.println("Nivel 2 - Acceso Interno");
        System.out.println("Nivel 3 - Acceso Restringido");
        System.out.println("Nivel 4 - Acceso Tecnico");
        System.out.println("Nivel 5 - Acceso Directivo");

        int nivel;
        do {
            System.out.print("Seleccione nivel (1 a 5): ");
            nivel = entradaDatos.nextInt();
            entradaDatos.nextLine(); // limpia el salto de linea
            if (nivel < 1 || nivel > 5) {
                System.out.println("Nivel invalido. Debe ser un numero entre 1 y 5.");
            }
        } while (nivel < 1 || nivel > 5);

        return NivelAcceso.values()[nivel - 1];
    }

    // 1 - Registrar empleado
    public static void funcionRegistrarEmpleado(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Empleado ---");
        System.out.print("Identificacion (ID numerico): ");
        int identificacion = entradaDatos.nextInt();
        System.out.print("DNI: ");
        int dni = entradaDatos.nextInt();
        entradaDatos.nextLine(); // limpia el salto pendiente

        System.out.print("Nombre: ");
        String nombre = entradaDatos.nextLine();
        System.out.print("Apellido: ");
        String apellido = entradaDatos.nextLine();

        NivelAcceso nivelAcceso = leerNivelAcceso(entradaDatos);

        Empleado empleado = new Empleado(identificacion, dni, nombre, apellido, nivelAcceso);
        control.registrarEmpleado(empleado);
    }

    // 2 - Registrar sector
    public static void funcionRegistrarSector(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Sector ---");
        System.out.print("ID del Sector: ");
        int idS = entradaDatos.nextInt();
        entradaDatos.nextLine();

        System.out.print("Nombre del Sector: ");
        String nombre = entradaDatos.nextLine();

        System.out.println("Nivel minimo requerido para ingresar al sector:");
        NivelAcceso nivelRequerido = leerNivelAcceso(entradaDatos);

        Sector sector = new Sector(idS, nombre, nivelRequerido);
        control.registrarSector(sector);
    }

    // 3 - Listar empleados
    public static void funcionListarEmpleados(ControlAcceso control) {
        System.out.println("\n--- Lista de Empleados Registrados ---");
        List<Empleado> empleados = control.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados en el sistema.");
            return;
        }
        for (Empleado emp : empleados) {
            System.out.println("• " + emp);
        }
    }

    // 4 - Listar sectores
    public static void funcionListarSectores(ControlAcceso control) {
        System.out.println("\n--- Lista de Sectores Registrados ---");
        List<Sector> sectores = control.getSectores();
        if (sectores.isEmpty()) {
            System.out.println("No hay sectores registrados en el sistema.");
            return;
        }
        for (Sector sec : sectores) {
            System.out.println("• " + sec);
        }
    }

    // 5 - Conceder permiso especial
    public static void funcionConcederPermiso(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Conceder Permiso Especial ---");
        System.out.print("ID del Empleado: ");
        int idEmpleado = entradaDatos.nextInt();
        System.out.print("ID del Sector: ");
        int idSector = entradaDatos.nextInt();
        entradaDatos.nextLine();

        Empleado emp = control.buscarEmpleado(idEmpleado);
        Sector sec = control.buscarSector(idSector);

        if (emp == null) {
            System.out.println("Error: No se encontro ningun empleado con ID " + idEmpleado);
            return;
        }
        if (sec == null) {
            System.out.println("Error: No se encontro ningun sector con ID " + idSector);
            return;
        }

        Permiso permiso = new Permiso(emp, sec, true);
        control.concederPermiso(permiso);
    }

    // 6 - Revocar permiso
    public static void funcionRevocarPermiso(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Revocar Permiso ---");
        System.out.print("ID del Empleado: ");
        int idEmpleado = entradaDatos.nextInt();
        System.out.print("ID del Sector: ");
        int idSector = entradaDatos.nextInt();
        entradaDatos.nextLine();

        Empleado emp = control.buscarEmpleado(idEmpleado);
        Sector sec = control.buscarSector(idSector);

        if (emp == null || sec == null) {
            System.out.println("Error: Empleado o Sector inexistente.");
            return;
        }

        Permiso permiso = new Permiso(emp, sec, true);
        control.revocarPermiso(permiso);
    }

    // 7 - Verificar acceso
    public static void funcionVerificarAcceso(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Punto de Acceso (Lector de Credencial) ---");
        System.out.print("Ingrese ID del Empleado: ");
        int idEmpleado = entradaDatos.nextInt();
        System.out.print("Ingrese ID del Sector: ");
        int idSector = entradaDatos.nextInt();
        entradaDatos.nextLine();

        boolean autorizado = control.verificarAcceso(idEmpleado, idSector);
        if (autorizado) {
            System.out.println(">>> RESULTADO: ACCESO PERMITIDO <<<");
        } else {
            System.out.println(">>> RESULTADO: ACCESO DENEGADO <<<");
        }
    }

    // 8 - Consultar historial
    public static void funcionConsultarHistorial(ControlAcceso control) {
        System.out.println("\n--- Auditoria / Historial de Intentos de Acceso ---");
        List<RegistroAcceso> historial = control.consultarHistorial();
        if (historial.isEmpty()) {
            return;
        }
        for (RegistroAcceso reg : historial) {
            System.out.println("• " + reg);
        }
    }

    // 9 - Demostracion automatica del patron Singleton y reglas de negocio
    public static void funcionDemostracionAutomatica(ControlAcceso control) {
        System.out.println("\n========================================================");
        System.out.println("         DEMOSTRACION AUTOMATICA DEL SISTEMA            ");
        System.out.println("========================================================");

        // CP-01 y CP-02: Demostracion de unicidad de instancia
        System.out.println("\n[1] Verificando unicidad del Singleton:");
        ControlAcceso puerta1 = ControlAcceso.getInstance();
        ControlAcceso puerta2 = ControlAcceso.getInstance();
        System.out.println("    Puerta 1 referencia: " + puerta1);
        System.out.println("    Puerta 2 referencia: " + puerta2);
        System.out.println("    ¿Son la misma instancia (puerta1 == puerta2)? " + (puerta1 == puerta2));

        // Registro de datos de prueba
        System.out.println("\n[2] Registrando sectores y empleados de prueba:");
        Sector recepcion = new Sector(10, "Recepcion", NivelAcceso.NIVEL_1);
        Sector servidores = new Sector(20, "Sala de Servidores", NivelAcceso.NIVEL_4);
        control.registrarSector(recepcion);
        control.registrarSector(servidores);

        Empleado ana = new Empleado(1, 40111222, "Ana", "Gomez", NivelAcceso.NIVEL_2);
        Empleado carlos = new Empleado(2, 35333444, "Carlos", "Lopez", NivelAcceso.NIVEL_4);
        control.registrarEmpleado(ana);
        control.registrarEmpleado(carlos);

        // Verificaciones por nivel
        System.out.println("\n[3] Prueba de acceso por nivel acumulativo:");
        System.out.println("    • Ana (NIVEL_2) intenta entrar a Recepcion (NIVEL_1):");
        control.verificarAcceso(1, 10);

        System.out.println("    • Ana (NIVEL_2) intenta entrar a Sala de Servidores (NIVEL_4):");
        control.verificarAcceso(1, 20);

        System.out.println("    • Carlos (NIVEL_4) intenta entrar a Sala de Servidores (NIVEL_4):");
        control.verificarAcceso(2, 20);

        // Permiso especial y revocacion
        System.out.println("\n[4] Prueba de permiso especial y revocacion:");
        System.out.println("    • Concediendo permiso especial a Ana para Sala de Servidores...");
        control.concederPermiso(new Permiso(ana, servidores, true));
        System.out.println("    • Ana intenta entrar nuevamente a Sala de Servidores:");
        control.verificarAcceso(1, 20);

        System.out.println("    • Revocando permiso especial de Ana...");
        control.revocarPermiso(new Permiso(ana, servidores, true));
        System.out.println("    • Ana intenta entrar tras la revocacion:");
        control.verificarAcceso(1, 20);

        // Historial centralizado
        System.out.println("\n[5] Auditoria del historial centralizado:");
        for (RegistroAcceso reg : control.consultarHistorial()) {
            System.out.println("    " + reg);
        }
        System.out.println("========================================================\n");
    }

    public static void main(String[] args) {
        Scanner entradaDatos = new Scanner(System.in);
        ControlAcceso control = ControlAcceso.getInstance();
        boolean salir = false;

        while (!salir) {
            System.out.println("\n========== SISTEMA DE CONTROL DE ACCESO ==========");
            System.out.println("1 - Agregar empleado");
            System.out.println("2 - Agregar sector");
            System.out.println("3 - Listar empleados");
            System.out.println("4 - Listar sectores");
            System.out.println("5 - Conceder permiso especial");
            System.out.println("6 - Revocar permiso especial");
            System.out.println("7 - Simular punto de acceso (verificar acceso)");
            System.out.println("8 - Consultar historial de accesos");
            System.out.println("9 - Ejecutar demostracion automatica completa");
            System.out.println("0 - Salir");
            System.out.print("Seleccione una opcion: ");

            int opcion = -1;
            if (entradaDatos.hasNextInt()) {
                opcion = entradaDatos.nextInt();
                entradaDatos.nextLine(); // limpia el salto
            } else {
                entradaDatos.nextLine(); // descarta entrada no numerica
                System.out.println("Por favor ingrese un numero valido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    funcionRegistrarEmpleado(entradaDatos, control);
                    break;
                case 2:
                    funcionRegistrarSector(entradaDatos, control);
                    break;
                case 3:
                    funcionListarEmpleados(control);
                    break;
                case 4:
                    funcionListarSectores(control);
                    break;
                case 5:
                    funcionConcederPermiso(entradaDatos, control);
                    break;
                case 6:
                    funcionRevocarPermiso(entradaDatos, control);
                    break;
                case 7:
                    funcionVerificarAcceso(entradaDatos, control);
                    break;
                case 8:
                    funcionConsultarHistorial(control);
                    break;
                case 9:
                    funcionDemostracionAutomatica(control);
                    break;
                case 0:
                    salir = true;
                    System.out.println("Cerrando sistema de control de acceso. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
            }
        }
        entradaDatos.close();
    }
}
