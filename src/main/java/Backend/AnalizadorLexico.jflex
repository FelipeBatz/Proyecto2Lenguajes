package Backend;
import Backend.Token;

%%

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
    private StringBuilder comentario = new StringBuilder();
    private StringBuilder cadenaCompleta = new StringBuilder();
    private int columnaCadena;
    private int columnaComentario;

    public Token crearToken(String tipo) {
        noTokens++;
        Token token = new Token(noTokens, yytext(), tipo, yyline + 1, yycolumn + 1 );
        return token;
    }
    
    
%}


ESPACIO = [ \t\n\r]+

LETRAS = [a-zA-Z]
DIGITOS = [0-9]
OPERADORES = "+" | "="
DELIMITADORES = "(" | ")" | "{" | "}"
FLECHA = "->"
FUNCIONES = "CARGAR"
DIRECTIVAS = "@modelo" | "@rol" | "@formato"
PALABRAS_RESERVADAS = "AGENTE" | "contexto" | "variable" | "EJECUTAR" | "EXPORTAR"
COMANDOS_IA = "PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER"
CONECTORES = "SOBRE" | "DESDE" | "EN" | "COMO"



Identificador = ({LETRAS}|"_")({LETRAS}|"_"|{DIGITOS})*
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
        return crearToken("Comentario");
    }

    //-----------------------------Deteccion de cadenas---------------------------
    "\"" {
        cadenaCompleta.setLength(0);
        cadenaCompleta.append(yytext());
        columnaCadena = yycolumn + 1;
        yybegin(cadena);
    }

    //-----------------------------Deteccion de operadores---------------------------
    {OPERADORES} {
        return crearToken("Operador");
    }

    //-----------------------------Deteccion de Delimitadores---------------------------
    {DELIMITADORES} {
        return crearToken("Delimitador");
    }

    //-----------------------------Deteccion de numeros---------------------------
    {Numero} {
        return crearToken("Numero");
    }

    {NumeroDecimal} {
        return crearToken("Decimal");

    }


    

    {FUNCIONES} {
        return crearToken("Funcion");
    }

    {DIRECTIVAS} {
        return crearToken("Directiva");
    }

    {PALABRAS_RESERVADAS} {
        return crearToken("Palabra Reservada");
    }

    {COMANDOS_IA} {
        return crearToken("Comando de IA");
    }

    {CONECTORES} {
        return crearToken("Conector");
    }

    {FLECHA} {
        return crearToken("Flecha");
    }

    //-----------------------------Deteccion de Identificadores---------------------------
    {Identificador} {
        return crearToken("Identificador");
    }

    
    

    
}


//-----------------------cadena--------------------------------------------
<cadena> "\"" {
    cadenaCompleta.append(yytext());
    noTokens++;
    Token token = new Token(noTokens, cadenaCompleta.toString(), "Cadena", yyline + 1, columnaCadena);
    yybegin(YYINITIAL);
    return token;
    

}

<cadena> [^\"]+ { 
    cadenaCompleta.append(yytext());
}

<cadena> <<EOF>> {
    System.err.println("Error léxico: cadena sin cerrar " + yytext());
    yybegin(YYINITIAL);
}    
    
//-----------------------------Comentario de bloque-----------------------------------    
<comentarioDeBloque> "*/" {
    comentario.append(yytext());
    noTokens++;
    Token token = new Token(noTokens, comentario.toString(), "Comentario", yyline + 1, columnaComentario);
    yybegin(YYINITIAL);
    return token;
}

<comentarioDeBloque> [^*]+ { 
    comentario.append(yytext());
}


<comentarioDeBloque> <<EOF>> {
    System.err.println("Error léxico: comentario de bloque sin cerrar " + yytext());
    yybegin(YYINITIAL);
}


. {
    System.out.println("Error detectado: " + yytext());
    return crearToken("caracter no reconocido");
}