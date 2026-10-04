package Backend;
import Backend.Token;
%%

%class AnalizadorLexico
%unicode
%public
%type Token
%line
%column

%{
    // contador para controlar la cantidad de tokens reconocidos
    int noTokens;  

    public Token crearToken(String tipo) {
        noTokens++;
        Token token = new Token(noTokens, yytext(), tipo, yyline, yycolumn);
        return token;
    }

%}

Espacio = [ \t\r\n]+

Numero = [0-9]+

Letra = [a-zA-Z]

NumeroDecimal = {Numero}\.{Numero}

Directivas = "@modelo" | "@rol" | "@formato"

Palabras_Reservadas_De_Estructura = "AGENTE" | "contexto" | "variable" | "EJECUTAR" | "EXPORTAR"

Comandos_De_IA = "PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER"

Funcion = "CARGAR"

Conectores = "SOBRE" | "DESDE" | "EN" | "COMO"

Cadena = \"[^\"]*\"

Operadores = "=" | "+"

Delimitadores = "{" | "}" | "(" | ")" | ","

Identificador = ({Letra}|"_")({Letra}|{Numero}|"_")*

Comentario = "/*"([^*]|\*+[^*/])*\*+"/"

DirectivaNoReconocida = "@"+({Letra}|{Numero}|"_")+

ComentarioDeLinea = "//"[^\r\n]*

CadenaNoCerrada = \"[^\"]*

%%


{Espacio} {
    /* ignora espacios, tabulaciones, saltos de linea y comentarios */
}

//====================Comentarios===========================

{Comentario} {
    return crearToken("Comentario");
}

{ComentarioDeLinea} {
    return crearToken("Comentario");
}

//====================cadena===========================

{Cadena} {
    return crearToken("Literal");
}


//====================Delimitadores===========================

{Delimitadores} {
    return crearToken("Delimitador");
}

//====================Operadores===========================

{Operadores} {
    return crearToken("Operador");
}

//====================Numeros===========================

{NumeroDecimal} {
    return crearToken("Literal");

}

{Numero} {
    return crearToken("Literal");
}

//====================Directivas===========================
{Directivas} {

    return crearToken("Directiva");

}

//====================Palabras reservadas de estructura===========================

{Palabras_Reservadas_De_Estructura} {
    return crearToken("Palabra Resercada");
}

//====================Comandos de IA===========================
{Comandos_De_IA} {
    return crearToken("Comando de IA");
}

//====================Funcion===========================
{Funcion} {
    return crearToken("Funcion");
}

//====================Conectores===========================
{Conectores} {
    return crearToken("Conector");
}

//====================Identificador===========================
{Identificador} {
    return crearToken("Identificador");
}


//====================FLECHA===========================

"->" {
    return crearToken("Flecha");
}



//====================Manuero de erroes===========================

{CadenaNoCerrada} {
    
    return crearToken("Error cadena no cerrada");
}

{DirectivaNoReconocida} {

    return crearToken("Directiva no reconocida");
}


. {

    return crearToken("Caracter no reconocido");
}