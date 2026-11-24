import java.util.List;
import java.util.Scanner;

public class Main {

    // Lee un entero como línea completa para poder rechazar entradas mezcladas o inválidas.
    private static int leerEntero(Scanner entradaDatos, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = entradaDatos.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException error) {
                System.out.println("El tipo de dato no coincide. Ingrese un numero entero.");
            }
        }
    }

    // Los nombres aceptan letras Unicode, espacios, guiones y apostrofes.
    private static String leerNombre(Scanner entradaDatos, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = entradaDatos.nextLine().trim();
            if (entrada.matches("(?U)[\\p{L}]+(?:[ '\\-][\\p{L}]+)*")) {
                return entrada;
            }
            System.out.println("El tipo de dato no coincide. Ingrese un nombre con letras.");
        }
    }

    // El nombre del sector puede incluir numeros, por ejemplo "Piso 2", pero debe tener letras.
    private static String leerNombreSector(Scanner entradaDatos, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = entradaDatos.nextLine().trim();
            if (!entrada.isEmpty() && entrada.matches("(?s).*\\p{L}.*")) {
                return entrada;
            }
            System.out.println("El tipo de dato no coincide. Ingrese un texto con al menos una letra.");
        }
    }

    // Metodo auxiliar para leer y validar un NivelAcceso por consola
    private static NivelAcceso leerNivelAcceso(Scanner entradaDatos) {
        System.out.println("Nivel 1 - Acceso General");
        System.out.println("Nivel 2 - Acceso Interno");
        System.out.println("Nivel 3 - Acceso Restringido");
        System.out.println("Nivel 4 - Acceso Tecnico");
        System.out.println("Nivel 5 - Acceso Directivo");

        int nivel;
        do {
            nivel = leerEntero(entradaDatos, "Seleccione nivel (1 a 5): ");
            if (nivel < 1 || nivel > 5) {
                System.out.println("Nivel invalido. Debe ser un numero entre 1 y 5.");
            }
        } while (nivel < 1 || nivel > 5);

        return NivelAcceso.values()[nivel - 1];
    }

    // 1 - Registrar empleado
    public static void funcionRegistrarEmpleado(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Empleado ---");
        int identificacion = leerEntero(entradaDatos, "Identificacion (ID numerico): ");
        int dni = leerEntero(entradaDatos, "DNI: ");
        String nombre = leerNombre(entradaDatos, "Nombre: ");
        String apellido = leerNombre(entradaDatos, "Apellido: ");

        NivelAcceso nivelAcceso = leerNivelAcceso(entradaDatos);

        Empleado empleado = new Empleado(identificacion, dni, nombre, apellido, nivelAcceso);
        control.registrarEmpleado(empleado);
    }

    // 2 - Registrar sector
    public static void funcionRegistrarSector(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Sector ---");
        int idS = leerEntero(entradaDatos, "ID del Sector: ");
        String nombre = leerNombreSector(entradaDatos, "Nombre del Sector: ");

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
        int idEmpleado = leerEntero(entradaDatos, "ID del Empleado: ");
        int idSector = leerEntero(entradaDatos, "ID del Sector: ");

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
        int idEmpleado = leerEntero(entradaDatos, "ID del Empleado: ");
        int idSector = leerEntero(entradaDatos, "ID del Sector: ");

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
        int idEmpleado = leerEntero(entradaDatos, "Ingrese ID del Empleado: ");
        int idSector = leerEntero(entradaDatos, "Ingrese ID del Sector: ");

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
            System.out.println("0 - Salir");
            int opcion = leerEntero(entradaDatos, "Seleccione una opcion: ");

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
