public enum NivelAcceso {
    NIVEL_1(1, "Acceso General"),
    NIVEL_2(2, "Acceso Interno"),
    NIVEL_3(3, "Acceso Restringido"),
    NIVEL_4(4, "Acceso Tecnico"),
    NIVEL_5(5, "Acceso Directivo");

    private final int nivel;
    private final String descripcion;

    private NivelAcceso(int nivel, String descripcion) {
        this.nivel = nivel;
        this.descripcion = descripcion;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Devuelve true si este nivel alcanza al nivel requerido (RN-01)
    public boolean alcanzaA(NivelAcceso nivelRequerido) {
        if (nivelRequerido == null) {
            return false;
        }
        return this.nivel >= nivelRequerido.getNivel();
    }

    @Override
    public String toString() {
        return name() + " (" + descripcion + ")";
    }
}
