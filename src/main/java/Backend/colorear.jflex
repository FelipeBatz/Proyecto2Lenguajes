package Backend;
import Backend.Token;

%%
%public
%class Colorear
%unicode
%line
%column
%type Token
%xstate comentarioDeBloque
%xstate cadena


%{
     // contador para controlar la cantidad de tokens reconocidos
    private int noTokens;  
    private StringBuilder comentario = new StringBuilder();
    private StringBuilder cadenaCompleta = new StringBuilder();
    private int columnaCadena;
    private int columnaComentario;

    public Token crearToken(String tipo) {
        noTokens++;
        Token token = new Token(noTokens, yytext(), tipo, yyline + 1, yycolumn + 1 );
        return token;
    }

    public Token reportarError(String tipo, String lexema) {
        noTokens++;
        Token token = new Token(noTokens, lexema, tipo, yyline + 1, yycolumn + 1 );
        return token;
    }
    
%}


ESPACIO = [ \t\n\r]+

LETRAS = [a-zA-Z]
DIGITOS = [0-9]

FLECHA = "->"
DIRECTIVAS = "@modelo" | "@rol" | "@formato"
COMANDOS_IA = "PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER"
CONECTORES = "SOBRE" | "DESDE" | "EN" | "COMO"
Reservadas = "AGENTE" | "contexto" | "variable" | "EJECUTAR" | "EXPORTAR"  | "CARGAR"
ID = ({LETRAS}|"_")({LETRAS}|"_"|{DIGITOS})*
Numero = {DIGITOS}+
NumeroDecimal = {DIGITOS}+ "." {DIGITOS}+
comentarioDeLinea = "//" .*








%%

<YYINITIAL> {
    //----------------------------Ignorar espacios-------------------------------
    {ESPACIO} { 
        return crearToken("ESPACIO");
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

    {Reservadas} {

        return crearToken("RESERVADAS");
    }


    {COMANDOS_IA} {
        return crearToken("COMANDO_IA");
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
    String lexema = cadenaCompleta.toString() + "\n";

    cadenaCompleta.setLength(0);
    yybegin(YYINITIAL);

    return reportarError("ERROR", lexema);
}
<cadena> <<EOF>> {
    String lexema = cadenaCompleta.toString();

    cadenaCompleta.setLength(0);
    yybegin(YYINITIAL);

    return reportarError("Cadena sin cerrar", lexema);
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
    
    yybegin(YYINITIAL);
    return crearToken("ERROR");
}


. {
    return crearToken("ERROR");
}

