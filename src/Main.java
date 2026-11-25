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

        if (identificacion <= 0) {
            System.out.println("Error: La identificacion debe ser mayor a 0.");
            return;
        }
        // Evitar que coincida con el ID de un empleado
        if (control.buscarEmpleado(identificacion) != null) {
            System.out.println("Error: Ya existe un empleado con esa identificacion.");
            return;
        }


        System.out.print("DNI: ");
        String dniTexto = entradaDatos.nextLine().trim();

        boolean soloNumeros = !dniTexto.equals("");
        for (int i = 0; i < dniTexto.length(); i++) {
            if (!Character.isDigit(dniTexto.charAt(i))) {
                soloNumeros = false;
                break;
            }
        }

        if (!soloNumeros || (dniTexto.length() != 7 && dniTexto.length() != 8)) {
            System.out.println("DNI invalido. Debe tener 7 u 8 digitos numericos.");
            return;
        }

        int dni = Integer.parseInt(dniTexto);
        // Evitar que se repita el DNI de otro empleado
        if (control.buscarEmpleadoPorDni(dni) != null) {
            System.out.println("Error: Ya existe un empleado con ese DNI.");
            return;
        }

        String nombre = leerNombre(entradaDatos, "Nombre: ");
        String apellido = leerNombre(entradaDatos, "Apellido: ");

        boolean nombreValido = (nombre != null && !nombre.trim().equals(""));
        boolean apellidoValido = (apellido != null && !apellido.trim().equals(""));

        if (!nombreValido || !apellidoValido) {
            System.out.println("No se puede registrar este empleado porque no tiene nombre o apellido.");
            return;
        }

        NivelAcceso nivelAcceso = leerNivelAcceso(entradaDatos);

        Empleado empleado = new Empleado(identificacion, dni, nombre.trim(), apellido.trim(), nivelAcceso);
        if (empleado.isRegistroValido()) {
            control.registrarEmpleado(empleado);
        }
    }

    // 2 - Registrar sector
    public static void funcionRegistrarSector(Scanner entradaDatos, ControlAcceso control) {
        System.out.println("\n--- Alta de Sector ---");
        int idS = leerEntero(entradaDatos, "ID del Sector: ");

        if (idS <= 0) {
            System.out.println("Error: El ID del sector debe ser mayor a 0.");
            return;
        }

        // Evitar que se repita el ID de otro sector
        if (control.buscarSector(idS) != null) {
            System.out.println("Error: Ya existe un sector con ese ID.");
            return;
        }

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

    // 9 - Demostracion automatica del patron Singleton y reglas de negocio
    public static void funcionDemostracionAutomatica(ControlAcceso control) {
        System.out.println("\n========================================================");
        System.out.println("         DEMOBRACION AUTOMATICA DEL SISTEMA            ");
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

        // 5 Empleados por defecto
        Empleado e1 = new Empleado(1, 45123456, "Juan", "Perez", NivelAcceso.NIVEL_1);
        Empleado e2 = new Empleado(2, 42345678, "Maria", "Gonzalez", NivelAcceso.NIVEL_2);
        Empleado e3 = new Empleado(3, 39876543, "Lucas", "Fernandez", NivelAcceso.NIVEL_3);
        Empleado e4 = new Empleado(4, 35678901, "Sofia", "Martinez", NivelAcceso.NIVEL_2);
        Empleado e5 = new Empleado(5, 47890123, "Pedro", "Rodriguez", NivelAcceso.NIVEL_1);

        control.registrarEmpleado(e1);
        control.registrarEmpleado(e2);
        control.registrarEmpleado(e3);
        control.registrarEmpleado(e4);
        control.registrarEmpleado(e5);

        // 5 Sectores por defecto
        Sector s1 = new Sector(1, "Recepcion", NivelAcceso.NIVEL_1);
        Sector s2 = new Sector(2, "Administracion", NivelAcceso.NIVEL_2);
        Sector s3 = new Sector(3, "Oficinas", NivelAcceso.NIVEL_3);
        Sector s4 = new Sector(4, "Sala de Servidores", NivelAcceso.NIVEL_4);
        Sector s5 = new Sector(5, "Direccion", NivelAcceso.NIVEL_5);

        control.registrarSector(s1);
        control.registrarSector(s2);
        control.registrarSector(s3);
        control.registrarSector(s4);
        control.registrarSector(s5);
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
