package Backend;

public class NodoAST {

    private String tipo;
    private String valor;
    private String lexema;
    private int fila;
    private final ListaEnlazada<NodoAST> hijos;

    public NodoAST(String tipo, String lexema, String valor, int fila) {
        this.tipo = tipo;
        this.valor = valor;
        this.fila = fila;
        this.hijos = new ListaEnlazada<>();
        this.lexema = lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public ListaEnlazada<NodoAST> getHijos() {
        return hijos;
    }

    public void agregarHijo(NodoAST hijo) {
        if (hijo != null) {
            hijos.agregarNuevo(hijo);
        }
    }

    public boolean tieneHijos() {
        return hijos.getTamañoLista() > 0;
    }

    public String getLexema() {
        return lexema;
    }

    public static class PreguntarNodo extends NodoAST {

        private final NodoAST expresion;
        private final NodoAST conectores;
        private final String destino;

        public PreguntarNodo(
                NodoAST expresion,
                NodoAST conectores,
                String destino) {

            super("Preguntar", "asdf", "COMANDO_IA", 1);

            this.expresion = expresion;
            this.conectores = conectores;
            this.destino = destino;

            agregarHijo(expresion);
            agregarHijo(conectores);

            agregarHijo(new NodoAST("Destino", "asdfasd", destino, 1));
        }

        public NodoAST getExpresion() {
            return expresion;
        }

        public NodoAST getConectores() {
            return conectores;
        }

        public String getDestino() {
            return destino;
        }
    }
}
