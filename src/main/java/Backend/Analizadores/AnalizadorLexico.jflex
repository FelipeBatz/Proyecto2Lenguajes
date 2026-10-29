package Backend.AnalizadorLexico;
import Backend.Token;
import Backend.ListaEnlazadaGenerica.ListaEnlazada;


%%
%public
%class AnalizadorLexico
%unicode
%line
%column
%type Token
%xstate comentarioDeBloque
%xstate cadena


%{
     // contador para controlar la cantidad de tokens reconocidos
    private int noTokens;  
    private int noErrores;  
    private StringBuilder comentario = new StringBuilder();
    private StringBuilder cadenaCompleta = new StringBuilder();
    private int columnaCadena;
    private int columnaComentario;
    private StringBuilder  errores = new StringBuilder();
    private ListaEnlazada<Token> erroresLista = new ListaEnlazada<>();

    public Token crearToken(String tipo) {
        noTokens++;
        Token token = new Token(noTokens, yytext(), tipo, yyline + 1, yycolumn + 1 );
        return token;
    }

    public void reportarError(String tipo, String lexema){
        
        noErrores++;
        errores.append(noErrores + " | " + "Error, " + tipo + ", Lexema: " + lexema + ", Fila: " + (yyline + 1 ) + ", Columna: " + (yycolumn + 1) + "\n"); 
        Token error = new Token(noErrores,lexema, tipo, (yyline + 1 ), yycolumn);
        erroresLista.agregarNuevo(error);
    }

    public String obtenerErrores(){
        System.out.println(errores.length());
        return errores.toString();
    }

    public ListaEnlazada<Token> obtenerListaErrores() {

        return erroresLista;
    }
    
    
%}


ESPACIO = [ \t\n\r]+

LETRAS = [a-zA-Z]
DIGITOS = [0-9]

FLECHA = "->"
DIRECTIVAS = "@modelo" | "@rol" | "@formato"
COMANDOS_IA = "PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER"
CONECTORES = "SOBRE" | "DESDE" | "EN" | "COMO"
ID = ({LETRAS}|"_")({LETRAS}|"_"|{DIGITOS})*
Numero = {DIGITOS}+
NumeroDecimal = {DIGITOS}+ "." {DIGITOS}+
comentarioDeLinea = "//" .*








%%

<YYINITIAL> {
    //----------------------------Ignorar espacios-------------------------------
    {ESPACIO} { 
       
    }

    //-----------------------------Deteccion de comentarios---------------------------
    "/*" {
        comentario.setLength(0);
        comentario.append(yytext());
        columnaComentario = yycolumn + 1;
        yybegin(comentarioDeBloque);
        
    }

    {comentarioDeLinea} {
        return crearToken("COMENTARIO");
    }

    //-----------------------------Deteccion de cadenas---------------------------
    "\"" {
        cadenaCompleta.setLength(0);
        cadenaCompleta.append(yytext());
        columnaCadena = yycolumn + 1;
        yybegin(cadena);
    }

    
    //-----------------------------Deteccion de numeros---------------------------
    {Numero} {
        return crearToken("NUMERO");
    }

    {NumeroDecimal} {
        return crearToken("NUMERO");

    }

    {DIRECTIVAS} {
        return crearToken("DIRECTIVA");
    }

    "AGENTE" {
        return crearToken("AGENTE");
    }

    "contexto" {
        return crearToken("CONTEXTO");
    }

    "variable" {
        return crearToken("VARIABLE");
    }

    "EJECUTAR" {
        return crearToken("EJECUTAR");
    }

    "EXPORTAR" {
        return crearToken("EXPORTAR");
    }

    {COMANDOS_IA} {
        return crearToken("COMANDO_IA");
    }

    "CARGAR" {
        return crearToken("CARGAR");
    }

    {CONECTORES} {
        return crearToken("CONECTOR");
    }

    //-----------------------------Deteccion de Identificadores---------------------------
    {ID} {
        return crearToken("ID");
    }

    {FLECHA} {
        return crearToken("FLECHA");
    }

    "=" {
        return crearToken("IGUAL");
    }

    "+" {
        return crearToken("MAS");
    }

    "{" {
        return crearToken("LLAVE_A");
    }

    "}" {
        return crearToken("LLAVE_C");
    }

    "(" {
        return crearToken("PAR_A");
    }

    ")" {
        return crearToken("PAR_C");
    }

    "," {
        return crearToken("COMA");
    }
    
}


//-----------------------cadena--------------------------------------------
<cadena> "\"" {
    cadenaCompleta.append(yytext());

    noTokens++;
    Token token = new Token(
        noTokens,
        cadenaCompleta.toString(),
        "CADENA",
        yyline + 1,
        columnaCadena
    );

    cadenaCompleta.setLength(0);
    yybegin(YYINITIAL);
    return token;
}

<cadena> [^\r\n\"]+ {
    cadenaCompleta.append(yytext());
}

<cadena> \r\n|\r|\n {
    String lexema = cadenaCompleta.toString();

    reportarError("Cadena sin cerrar", lexema);

    cadenaCompleta.setLength(0);
    yybegin(YYINITIAL);
}

<cadena> <<EOF>> {
    String lexema = cadenaCompleta.toString();

    reportarError("Cadena sin cerrar", lexema);

    cadenaCompleta.setLength(0);
    yybegin(YYINITIAL);
} 
    
//-----------------------------Comentario de bloque-----------------------------------    
<comentarioDeBloque> "*/" {
    comentario.append(yytext());
    noTokens++;
    Token token = new Token(noTokens, comentario.toString(), "COMENTARIO", yyline + 1, columnaComentario);
    yybegin(YYINITIAL);
    return token;
}

<comentarioDeBloque> [^*]+ { 
    comentario.append(yytext());
}


<comentarioDeBloque> <<EOF>> {
    reportarError("comentario sin cerrar", yytext());
    yybegin(YYINITIAL);
    
    
}


. {
    reportarError("caracter no reconocido", yytext());
}

