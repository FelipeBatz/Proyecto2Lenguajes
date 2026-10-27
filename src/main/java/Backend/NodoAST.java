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

    public String getTipoNodo() {
        return "nodoNormal";
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

    public String getExpresion() {
       return "";
    }

    public String getConectores() {
      return "";
    }

    public String getDestino() {
        return "";
    }

    public static class PreguntarNodo extends NodoAST {

        private String tipoComando;
        private final String expresion;
        private final String conectores;
        private final String destino;

        public PreguntarNodo(String tipoComando, String expresion, String conectores, String destino, int fila) {
            super(tipoComando, conectores, destino, fila);
            this.expresion = expresion;
            this.conectores = conectores;
            this.destino = destino;
            this.tipoComando = tipoComando;

        }

        @Override
        public String getLexema() {
            return tipoComando;
        }

        @Override
        public String getTipoNodo() {
            return "nodoComando";
        }

        @Override
        public String getExpresion() {
            return expresion;
        }

        @Override
        public String getConectores() {
            return conectores;
        }
        @Override
        public String getDestino() {
            return destino;
        }
    }
}
