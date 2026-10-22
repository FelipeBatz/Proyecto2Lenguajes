package Backend;

public class AnalizadorSintactico2 {

    private ListaEnlazada<Token> listaTokens;
    private ListaEnlazada<String> listaErrores;
    private int posicionListaTokens;

    private final String[] TOKENS_SINCRONIZACION = {
        "AGENTE",
        "LLAVE_C",
        "COMANDO_IA",
        "VARIABLE",
        "EJECUTAR",
        "EXPORTAR",
        "EOF"
    };

    public AnalizadorSintactico2(ListaEnlazada<Token> listaTokens) {
        this.listaTokens = listaTokens;
        this.listaErrores = new ListaEnlazada<>();
        this.posicionListaTokens = 0;
    }

    private boolean esTokenSincronizacion(String tipo) {
        for (String token : TOKENS_SINCRONIZACION) {
            if (token.equals(tipo)) {
                return true;
            }
        }

        return false;
    }

    private void reportarError(Token tokenActual, String tipo) {
        String error = "Se esperaba: " + tipo
                + ", pero llegó: " + tokenActual.getTipo()
                + ", lexema: " + tokenActual.getLexema()
                + ", columna: " + tokenActual.getColumna()
                + ", fila: " + tokenActual.getFila();

        listaErrores.agregarNuevo(error);
    }

    private Token tokenActual() {
        if (posicionListaTokens < listaTokens.getTamañoLista()) {
            try {
                return listaTokens.obtenerDatos(posicionListaTokens);
            } catch (ListaEnlazadaException ex) {
                System.out.println(
                        "Error al obtener el token actual."
                );
            }
        }

        return new Token(0, "EOF", "EOF", 0, 0);
    }

    private boolean verificarToken(String tipo) {
        return tokenActual().getTipo().equals(tipo);
    }

    private void avanzar() {
        if (!verificarToken("EOF")) {
            posicionListaTokens++;
        }
    }

    /*
     * Recuperación por pánico:
     * avanza hasta encontrar el token esperado,
     * un token de sincronización o EOF.
     */
    private void recuperarError(String tipoEsperado) {

        while (!verificarToken("EOF")) {

            // Se encontró el token esperado.
            if (verificarToken(tipoEsperado)) {
                avanzar();
                return;
            }

            // Se encontró un punto de sincronización.
            if (esTokenSincronizacion(tokenActual().getTipo())) {
                return;
            }

            avanzar();
        }
    }

    /*
     * Consume el token si es correcto.
     * En caso contrario, reporta el error y recupera.
     */
    private void esperar(String tipo) {

        if (verificarToken(tipo)) {
            avanzar();
        } else {
            reportarError(tokenActual(), tipo);
            recuperarError(tipo);
        }
    }

    public void analizar() {
        posicionListaTokens = 0;
        programa();

        if (listaErrores.getTamañoLista() > 0) {
            System.out.println("\nErrores sintácticos:");

            for (int i = 0;
                    i < listaErrores.getTamañoLista();
                    i++) {
                try {
                    System.out.println(
                            listaErrores.obtenerDatos(i)
                    );
                } catch (ListaEnlazadaException ex) {
                    System.out.println(
                            "Error al mostrar errores."
                    );
                }
            }
        } else {
            System.out.println(
                    "No se encontraron errores sintácticos."
            );
        }

        System.out.println("Análisis finalizado.");
    }

    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        esperar("EOF");
    }

    private void directivas() {
        while (verificarToken("DIRECTIVA")) {
            esperar("DIRECTIVA");
            esperar("CADENA");
        }
    }

    private void agentes() {
        agente();
        mas_agentes();
    }

    private void mas_agentes() {
        while (verificarToken("AGENTE")) {
            int posicionAnterior = posicionListaTokens;

            agente();

            // Evita ciclos infinitos.
            if (posicionListaTokens == posicionAnterior) {
                avanzar();
            }
        }
    }

    private void agente() {
        esperar("AGENTE");
        esperar("ID");
        esperar("LLAVE_A");
        contexto();
        variables();
        comandos();
        esperar("LLAVE_C");
    }

    private void contexto() {
        esperar("CONTEXTO");
        esperar("IGUAL");
        esperar("CADENA");
    }

    private void variables() {
        while (verificarToken("VARIABLE")) {
            int posicionAnterior = posicionListaTokens;

            esperar("VARIABLE");
            esperar("ID");
            esperar("IGUAL");
            expresion();

            if (posicionListaTokens == posicionAnterior) {
                avanzar();
            }
        }
    }

    private void comandos() {
        comando();
        mas_comandos();
    }

    private void mas_comandos() {
        while (verificarToken("COMANDO_IA")) {
            int posicionAnterior = posicionListaTokens;

            comando();

            if (posicionListaTokens == posicionAnterior) {
                avanzar();
            }
        }
    }

    public void comando() {
        esperar("COMANDO_IA");
        expresion();
        conectores();
        esperar("FLECHA");
        esperar("ID");
    }

    public void conectores() {
        while (verificarToken("CONECTOR")) {
            esperar("CONECTOR");
            expresion();
        }
    }

    public void expresion() {
        termino();
        mas_terminos();
    }

    public void mas_terminos() {
        while (verificarToken("MAS")) {
            esperar("MAS");
            termino();
        }
    }

    public void termino() {
        if (verificarToken("CADENA")) {
            esperar("CADENA");

        } else if (verificarToken("ID")) {
            esperar("ID");

        } else if (verificarToken("NUMERO")) {
            esperar("NUMERO");
            unidad();

        } else if (verificarToken("CARGAR")) {
            cargar();

        } else {
            reportarError(
                    tokenActual(),
                    "CADENA, ID, NUMERO o CARGAR"
            );

            recuperarTermino();
        }
    }

    /*
     * Recuperación específica para expresiones.
     * Conserva operadores y tokens que pueden indicar
     * dónde debe continuar la expresión.
     */
    private void recuperarTermino() {
        while (!verificarToken("EOF")
                && !verificarToken("MAS")
                && !verificarToken("FLECHA")
                && !verificarToken("CONECTOR")
                && !verificarToken("LLAVE_C")
                && !verificarToken("VARIABLE")
                && !verificarToken("COMANDO_IA")
                && !verificarToken("EJECUTAR")
                && !verificarToken("EXPORTAR")) {

            avanzar();
        }
    }

    public void unidad() {
        if (verificarToken("ID")) {
            esperar("ID");
        }
    }

    public void cargar() {
        esperar("CARGAR");
        esperar("PAR_A");
        esperar("CADENA");
        esperar("PAR_C");
    }

    public void ejecuciones() {
        esperar("EJECUTAR");
        esperar("ID");
        mas_ejecuciones();
    }

    public void mas_ejecuciones() {
        while (verificarToken("EJECUTAR")) {
            int posicionAnterior = posicionListaTokens;

            esperar("EJECUTAR");
            esperar("ID");

            if (posicionListaTokens == posicionAnterior) {
                avanzar();
            }
        }
    }

    public void exportar() {
        esperar("EXPORTAR");
        esperar("ID");
        mas_ids();
    }

    public void mas_ids() {
        while (verificarToken("COMA")) {
            esperar("COMA");
            esperar("ID");
        }
    }
}
