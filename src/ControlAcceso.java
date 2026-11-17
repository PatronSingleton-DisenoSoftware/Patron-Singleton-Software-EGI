public class ControlAcceso {
    // Atributos
    private static ControlAcceso instancia;
    // Constructor de acceso unico
    private ControlAcceso(){
        // Logica del constructor
    }
    // Getters y Setters

// Metodo de acceso unico
    public static ControlAcceso getInstancia() {
        if (instancia == null){
            instancia = new ControlAcceso();
        }
        return instancia;
    }
}
// Metodos publicos o privados