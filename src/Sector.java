public class Sector {
    private int idS;
    private String nombre;
    private NivelAcceso nivelRequerido;


    public Sector(int idS, String nombre, NivelAcceso nivelRequerido){
        this.idS = idS;
        this.nombre = nombre;
        this.nivelRequerido = nivelRequerido;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdS() {
        return idS;
    }

    public NivelAcceso getNivelRequerido() {
        return nivelRequerido;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNivelRequerido(NivelAcceso nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }
}
