
package Backend;

public class AnalizadorSin {

    private ListaEnlazada<Token> listaTokens;
    private ListaEnlazada<String> listaErrores;
    private int posicionListaTokens;

    // Tokens donde puede continuar el análisis después de un error.
    private final String[] TOKENS_SINCRONIZACION = {
        "AGENTE",
        "LLAVE_C",
        "COMANDO_IA",
        "VARIABLE",
        "EJECUTAR",
        "EXPORTAR",
        "EOF"
    };

    public AnalizadorSin(ListaEnlazada<Token> listaTokens) {
        this.listaTokens = listaTokens;
        this.listaErrores = new ListaEnlazada<>();
        this.posicionListaTokens = 0;
    }

    // =====================================================
    // MANEJO DE TOKENS
    // =====================================================

    private Token tokenActual() {
        if (posicionListaTokens < listaTokens.getTamañoLista()) {
            try {
                return listaTokens.obtenerDatos(posicionListaTokens);
            } catch (ListaEnlazadaException ex) {
                System.out.println("Error al obtener el token.");
            }
        }

        return new Token(0, "EOF", "EOF", 0, 0);
    }

    private boolean verificarTipoToken(String tipoToken) {
        return tokenActual().getTipo().equals(tipoToken);
    }

    private boolean esSincronizacion(String tipo) {
        for (String token : TOKENS_SINCRONIZACION) {
            if (token.equals(tipo)) {
                return true;
            }
        }

        return false;
    }

    private void avanzar() {
        if (!verificarTipoToken("EOF")
                && posicionListaTokens < listaTokens.getTamañoLista()) {
            posicionListaTokens++;
        }
    }

    // =====================================================
    // REPORTE DE ERRORES Y RECUPERACIÓN EN MODO PÁNICO
    // =====================================================

    private void reportarError(String esperado) {
        Token encontrado = tokenActual();

        String mensaje = "Error sintáctico en fila "
                + encontrado.getFila()
                + ", columna " + encontrado.getColumna()
                + ": se esperaba " + esperado
                + ", pero se encontró "
                + encontrado.getTipo()
                + " (" + encontrado.getLexema() + ").";

        listaErrores.agregarNuevo(mensaje);
        System.out.println(mensaje);
    }

    private void sincronizar() {
        // Descarta tokens hasta llegar a un punto seguro.
        // No consume el token de sincronización: lo deja
        // disponible para que la gramática continúe.
        while (!verificarTipoToken("EOF")
                && !esSincronizacion(tokenActual().getTipo())) {
            avanzar();
        }
    }

    private boolean esperar(String tipo) {
        if (verificarTipoToken(tipo)) {
            if (!tipo.equals("EOF")) {
                avanzar();
            }

            return true;
        }

        reportarError(tipo);
        sincronizar();

        return false;
    }

    // =====================================================
    // ANÁLISIS PRINCIPAL
    // =====================================================

    public void analizar() {
        posicionListaTokens = 0;
        listaErrores = new ListaEnlazada<>();

        int posicionAnterior;

        do {
            posicionAnterior = posicionListaTokens;

            programa();

            // Protección contra ciclos infinitos:
            // si el parser no avanzó, descarta un token.
            if (posicionListaTokens == posicionAnterior
                    && !verificarTipoToken("EOF")) {
                avanzar();
            }

        } while (!verificarTipoToken("EOF"));

        if (listaErrores.getTamañoLista() == 0) {
            System.out.println("Análisis sintáctico correcto.");
        } else {
            System.out.println(
                    "Análisis sintáctico finalizado con "
                    + listaErrores.getTamañoLista()
                    + " errores."
            );
        }
    }

    public ListaEnlazada<String> getErrores() {
        return listaErrores;
    }

    // =====================================================
    // GRAMÁTICA
    // =====================================================

    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        esperar("EOF");
    }

    private void directivas() {
        while (verificarTipoToken("DIRECTIVA")) {
            if (!esperar("DIRECTIVA")) {
                return;
            }

            if (!esperar("CADENA")) {
                return;
            }
        }
    }

    private void agentes() {
        if (verificarTipoToken("AGENTE")) {
            agente();
            masAgentes();
        } else {
            esperar("AGENTE");
        }
    }

    private void masAgentes() {
        while (verificarTipoToken("AGENTE")) {
            int anterior = posicionListaTokens;
            agente();

            if (posicionListaTokens == anterior) {
                break;
            }
        }
    }

    private void agente() {
        if (!esperar("AGENTE")) {
            return;
        }

        if (!esperar("ID")) {
            return;
        }

        if (!esperar("LLAVE_A")) {
            return;
        }

        contexto();
        variables();
        comandos();

        // Si aparece el inicio de otro agente antes de cerrar
        // el actual, no se consume ese AGENTE.
        if (verificarTipoToken("AGENTE")) {
            reportarError("LLAVE_C");
            return;
        }

        esperar("LLAVE_C");
    }

    private void contexto() {
        if (!esperar("CONTEXTO")) {
            return;
        }

        if (!esperar("IGUAL")) {
            return;
        }

        esperar("CADENA");
    }

    private void variables() {
        while (verificarTipoToken("VARIABLE")) {
            if (!esperar("VARIABLE")) {
                return;
            }

            if (!esperar("ID")) {
                return;
            }

            if (!esperar("IGUAL")) {
                return;
            }

            int anterior = posicionListaTokens;
            expresion();

            if (posicionListaTokens == anterior
                    && !verificarTipoToken("EOF")) {
                avanzar();
            }
        }
    }

    private void comandos() {
        if (verificarTipoToken("COMANDO_IA")) {
            comando();
            masComandos();
        }
    }

    private void masComandos() {
        while (verificarTipoToken("COMANDO_IA")) {
            int anterior = posicionListaTokens;
            comando();

            if (posicionListaTokens == anterior) {
                break;
            }
        }
    }

    private void comando() {
        if (!esperar("COMANDO_IA")) {
            return;
        }

        int anterior = posicionListaTokens;
        expresion();

        if (posicionListaTokens == anterior) {
            return;
        }

        conectores();

        if (!esperar("FLECHA")) {
            return;
        }

        esperar("ID");
    }

    private void conectores() {
        while (verificarTipoToken("CONECTOR")) {
            if (!esperar("CONECTOR")) {
                return;
            }

            int anterior = posicionListaTokens;
            expresion();

            if (posicionListaTokens == anterior) {
                return;
            }
        }
    }

    private void expresion() {
        termino();

        while (verificarTipoToken("MAS")) {
            if (!esperar("MAS")) {
                return;
            }

            int anterior = posicionListaTokens;
            termino();

            if (posicionListaTokens == anterior) {
                return;
            }
        }
    }

    private void termino() {
        if (verificarTipoToken("CADENA")) {
            esperar("CADENA");

        } else if (verificarTipoToken("ID")) {
            esperar("ID");

        } else if (verificarTipoToken("NUMERO")) {
            esperar("NUMERO");
            unidad();

        } else if (verificarTipoToken("CARGAR")) {
            cargar();

        } else {
            reportarError("CADENA, ID, NUMERO o CARGAR");
            sincronizar();
        }
    }

    private void unidad() {
        if (verificarTipoToken("ID")) {
            esperar("ID");
        }
    }

    private void cargar() {
        if (!esperar("CARGAR")) {
            return;
        }

        if (!esperar("PAR_A")) {
            return;
        }

        if (!esperar("CADENA")) {
            return;
        }

        esperar("PAR_C");
    }

    private void ejecuciones() {
        if (!esperar("EJECUTAR")) {
            return;
        }

        if (!esperar("ID")) {
            return;
        }

        masEjecuciones();
    }

    private void masEjecuciones() {
        while (verificarTipoToken("EJECUTAR")) {
            if (!esperar("EJECUTAR")) {
                return;
            }

            if (!esperar("ID")) {
                return;
            }
        }
    }

    private void exportar() {
        if (!esperar("EXPORTAR")) {
            return;
        }

        if (!esperar("ID")) {
            return;
        }

        masIds();
    }

    private void masIds() {
        while (verificarTipoToken("COMA")) {
            if (!esperar("COMA")) {
                return;
            }

            if (!esperar("ID")) {
                return;
            }
        }
    }
}
