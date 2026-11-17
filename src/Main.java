import java.util.Scanner;

public class Main {

    // Método auxiliar para registrar empleados
    public static void funcionRegistrarEmpleado(Scanner entradaDatos, ControlAcceso control) {
        System.out.print("Identificacion: ");
        int identificacion = entradaDatos.nextInt();
        System.out.print("DNI: ");
        int dni = entradaDatos.nextInt();
        entradaDatos.nextLine();          // limpia el salto pendiente

        System.out.print("Nombre: ");
        String nombre = entradaDatos.nextLine();
        System.out.print("Apellido: ");
        String apellido = entradaDatos.nextLine();

        System.out.print("Nivel (1 a 5): ");
        int nivel = entradaDatos.nextInt();
        NivelAcceso nivelAcceso = NivelAcceso.values()[nivel - 1];

        Empleado empleado = new Empleado(identificacion, dni, nombre, apellido, nivelAcceso);
        control.registrarEmpleado(empleado);   // acá actúa RN-08
    }

    public static void main(String[] args) {
        Scanner entradaDatos = new Scanner(System.in);
        ControlAcceso control = ControlAcceso.getInstance();
        boolean salir = false;

        while (!salir) {
            System.out.println("\n1 - Agregar empleado");
            System.out.println("0 - Salir");
            System.out.print("Opcion: ");
            int opcion = entradaDatos.nextInt();

            switch (opcion) {
                case 1:
                    funcionRegistrarEmpleado(entradaDatos, control);
                    break;

                case 0:
                    salir = true;
                    break;

                default:
                    System.out.println("Opcion invalida");
            }
        }
        entradaDatos.close();
    } // Cierre del main
} // Cierre de la clase Main



