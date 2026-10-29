package Frontent;

import Backend.Analizadores.AnalizadorLexico;
import Backend.Analizadores.AnalizadorSemantico;
import Backend.Analizadores.AnalizadorSintactico2;
import Backend.ListaEnlazadaGenerica.ListaEnlazada;
import Backend.ListaEnlazadaGenerica.ListaEnlazadaException;
import Backend.Reportes.ReporteTabla;
import Backend.Token;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import Backend.Reportes.ReporteErroresLexicos;
import Backend.Reportes.ReporteErroresSemanticos;

public class IDE2 extends JFrame {

    private static final Color COLOR_FONDO = new Color(24, 24, 24);
    private static final Color COLOR_PANEL = new Color(30, 30, 30);
    private static final Color COLOR_PANEL_2 = new Color(37, 37, 38);
    private static final Color COLOR_EDITOR = new Color(25, 25, 25);
    private static final Color COLOR_BORDE = new Color(55, 55, 55);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(150, 150, 150);
    private static final Color COLOR_AZUL = new Color(0, 122, 204);
    private static final Color COLOR_VERDE = new Color(45, 125, 75);
    private static final Color COLOR_MORADO = new Color(85, 70, 145);
    private static final Color COLOR_BLANCO = new Color(255, 255, 255);

    private JTextPane editor;
    private JTextArea consola;
    private JTextArea tablaTokens;
    private JTextArea tablaErroresLexicos;
    private JTextArea tablaErroresSintacticos;
    private JTextArea tablaErroresSemanticos;
    private JTextArea promptGenerado;
    private JLabel lblArchivo;
    private JLabel lblEstado;
    private JLabel lblPosicion;
    private JLabel lblEstadoLexico;
    private JLabel lblEstadoSintactico;
    private JLabel lblEstadoSemantico;
    private JLabel lblEstadoGeneracion;
    private JTabbedPane tabsInferior;
    private File archivoActual;
    private boolean archivoModificado = false;
    private ListaEnlazada<Token> tokensReconocidos;

    public IDE2() {
        configurarVentana();
        crearInterfaz();
        crearAtajos();
        agregarEventos();
        nuevoArchivo();

    }

    private void configurarVentana() {
        setTitle("PromptZal IDE");
        setSize(1450, 900);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void crearInterfaz() {

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(COLOR_FONDO);
        setContentPane(principal);
        principal.add(crearMenuBar(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(COLOR_FONDO);
        centro.add(crearBarraHerramientas(), BorderLayout.NORTH);
        centro.add(crearCuerpoIDE(), BorderLayout.CENTER);
        centro.add(crearBarraEstado(), BorderLayout.SOUTH);
        principal.add(centro, BorderLayout.CENTER);
    }

    //Menu opciones
    private JMenuBar crearMenuBar() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(COLOR_PANEL_2);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        //Menu abrir
        JMenu archivo = crearMenu("Archivo");
        JMenuItem nuevo = crearItem("Nuevo", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        nuevo.addActionListener(e -> nuevoArchivo());

        JMenuItem abrir = crearItem("Abrir archivo (.pz)", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        abrir.addActionListener(e -> abrirArchivo());

        JMenuItem guardar = crearItem("Guardar archivo (.pz)", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        guardar.addActionListener(e -> guardarArchivo());

        JMenuItem guardarComo = crearItem("Guardar como...", null);
        guardarComo.addActionListener(e -> guardarComo());

        JMenuItem salir = crearItem("Salir", null);
        salir.addActionListener(e -> this.dispose());

        archivo.add(nuevo);
        archivo.add(abrir);
        archivo.add(guardar);
        archivo.add(guardarComo);
        archivo.addSeparator();
        archivo.add(salir);

        //Menu editar
        JMenu editar = crearMenu("Editar");
        JMenuItem copiar = crearItem("Copiar", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copiar.addActionListener(e -> editor.copy());

        JMenuItem pegar = crearItem("Pegar", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pegar.addActionListener(e -> editor.paste());

        JMenuItem seleccionar = crearItem("Seleccionar todo", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));

        seleccionar.addActionListener(e -> editor.selectAll());

        editar.add(copiar);
        editar.add(pegar);
        editar.addSeparator();
        editar.add(seleccionar);

        //Menu ayuda
        JMenu ayuda = crearMenu("Ayuda");
        JMenuItem acerca = crearItem("Acerca de PromptZal", null);
        acerca.addActionListener(e -> mostrarAcercaDe());
        ayuda.add(acerca);

        barra.add(archivo);
        barra.add(editar);
        barra.add(ayuda);

        return barra;
    }

    //barra Herramientas
    private JPanel crearBarraHerramientas() {

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 6));

        barra.setBackground(COLOR_PANEL_2);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JButton nuevo = new JButton("Nuevo");
        nuevo.addActionListener(e -> nuevoArchivo());
        nuevo.setBackground(COLOR_BLANCO);

        JButton abrir = new JButton("Abrir .pz");
        abrir.addActionListener(e -> abrirArchivo());
        abrir.setBackground(COLOR_BLANCO);

        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> guardarArchivo());
        guardar.setBackground(COLOR_BLANCO);

        JSeparator separador = new JSeparator(JSeparator.VERTICAL);
        separador.setPreferredSize(new Dimension(2, 30));
        separador.setForeground(COLOR_BORDE);

        JButton analizar = new JButton("▶ Analizar");
        analizar.setBackground(COLOR_VERDE);
        analizar.addActionListener(e -> analizarArchivo());

        JButton compilar = new JButton("COMPILAR");
        compilar.setBackground(COLOR_AZUL);
        compilar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        compilar.addActionListener(e -> compilarArchivo());

        barra.add(nuevo);
        barra.add(abrir);
        barra.add(guardar);
        barra.add(separador);
        barra.add(analizar);
        barra.add(compilar);

        return barra;
    }

    private JSplitPane crearCuerpoIDE() {
        JPanel explorador = crearExplorador();

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBackground(COLOR_EDITOR);
        derecha.add(crearEditor(), BorderLayout.CENTER);
        derecha.add(crearPanelInferior(), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, explorador, derecha);
        split.setDividerLocation(230);
        split.setDividerSize(3);
        split.setBorder(null);
        return split;
    }

    private JPanel crearExplorador() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDE));

        JLabel titulo = new JLabel("EXPLORADOR");
        titulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titulo.setBorder(new EmptyBorder(10, 5, 10, 5));

        panel.add(titulo, BorderLayout.NORTH);
        JPanel archivos = new JPanel();

        archivos.setLayout(new BoxLayout(archivos, BoxLayout.Y_AXIS));
        archivos.setBackground(COLOR_PANEL);

        JLabel proyecto = new JLabel( "PROMPTZAL");
        proyecto.setForeground(COLOR_TEXTO);
        proyecto.setFont(new Font("Segoe UI", Font.BOLD, 13));

        proyecto.setBorder(new EmptyBorder(8, 15, 8, 5));

        lblArchivo = new JLabel("   📄  Sin título.pz");
        lblArchivo.setForeground(COLOR_TEXTO);
        lblArchivo.setBorder(new EmptyBorder(8, 15, 8, 5));

        archivos.add(proyecto);
        archivos.add(lblArchivo);

        panel.add(archivos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearEditor() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(COLOR_EDITOR);

        JPanel pestaña = new JPanel(new BorderLayout());
        pestaña.setBackground(COLOR_PANEL_2);
        pestaña.setPreferredSize(new Dimension(0, 38));

        JLabel nombreArchivo = new JLabel("  📄 Sin título.pz");
        nombreArchivo.setForeground(COLOR_TEXTO);

        pestaña.add(nombreArchivo, BorderLayout.WEST);
        panel.add(pestaña, BorderLayout.NORTH);

        //editor de texto
        editor = new JTextPane();
        editor.setBackground(COLOR_EDITOR);
        editor.setForeground(COLOR_TEXTO);
        editor.setCaretColor(Color.WHITE);
        editor.setSelectionColor(new Color(45, 90, 130));
        editor.setSelectedTextColor(Color.WHITE);
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        editor.setMargin(new Insets(10, 12, 10, 12));

        TextLineNumber lineas = new TextLineNumber(editor);
        JScrollPane scroll = new JScrollPane(editor);
        scroll.setRowHeaderView(lineas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_EDITOR);
        scroll.getVerticalScrollBar().setBackground(COLOR_PANEL);
        scroll.getHorizontalScrollBar().setBackground(COLOR_PANEL);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // Panel inferior
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setPreferredSize(new Dimension(0, 280));

        tabsInferior = new JTabbedPane();
        tabsInferior.setBackground(COLOR_PANEL);
        tabsInferior.setForeground(Color.WHITE);
        tabsInferior.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsInferior.setBorder(null);

        consola = crearAreaTexto();
        consola.setText("> PromptZal IDE iniciado.\n" + "> Listo para trabajar.\n");

        tablaTokens = crearAreaTexto();
        tablaTokens.setText(encabezadoTablaTokens());

        tablaErroresLexicos = crearAreaTexto();
        tablaErroresLexicos.setText("No hay errores Lexicos.\n");

        tablaErroresSintacticos = crearAreaTexto();
        tablaErroresSintacticos.setText("No hay errores sintacticos.\n");

        tablaErroresSemanticos = crearAreaTexto();
        tablaErroresSemanticos.setText("No hay errores Semanticos.\n");

        JPanel panelPrompt = crearPanelPrompt();

        tabsInferior.addTab("CONSOLA", new JScrollPane(consola));
        tabsInferior.addTab("TOKENS", new JScrollPane(tablaTokens));
        tabsInferior.addTab("ERRORES LEXICOS", new JScrollPane(tablaErroresLexicos));
        tabsInferior.addTab("ERRORES SINTACTICOS", new JScrollPane(tablaErroresSintacticos));
        tabsInferior.addTab("ERRORES SEMANTICOS", new JScrollPane(tablaErroresSemanticos));
        tabsInferior.addTab("PROMPTZAL GENERADO", panelPrompt);

        panel.add(tabsInferior, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelPrompt() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_EDITOR);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
        barra.setBackground(COLOR_PANEL_2);

        JLabel titulo = new JLabel("PROMPTZAL GENERADO");

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton guardarTXT = new JButton("Guardar .txt");
        guardarTXT.setBackground(COLOR_VERDE);
        guardarTXT.addActionListener(e -> guardarPromptTXT());
        JButton copiar = new JButton("Copiar");
        copiar.setBackground(COLOR_MORADO);

        copiar.addActionListener(
                e -> {
                    promptGenerado.selectAll();
                    promptGenerado.copy();
                    promptGenerado.setCaretPosition(0);
                    actualizarEstado("Prompt copiado");
                }
        );

        barra.add(titulo);
        barra.add(Box.createHorizontalStrut(20));
        barra.add(guardarTXT);
        barra.add(copiar);

        panel.add(barra, BorderLayout.NORTH);

        promptGenerado = new JTextArea();
        promptGenerado.setBackground(COLOR_EDITOR);
        promptGenerado.setForeground(new Color(205, 225, 205));
        promptGenerado.setCaretColor(Color.WHITE);
        promptGenerado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        promptGenerado.setMargin(new Insets(10, 12, 10, 12));
        promptGenerado.setEditable(false);
        promptGenerado.setText("El PromptZal generado aparecerá aquí...");

        panel.add(new JScrollPane(promptGenerado), BorderLayout.CENTER);

        return panel;
    }

    //barra de estados del ide
    private JPanel crearBarraEstado() {

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(COLOR_AZUL);
        barra.setPreferredSize(new Dimension(0, 28));

        lblEstado = new JLabel("  Listo");
        lblEstado.setForeground(Color.WHITE);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        derecha.setOpaque(false);

        lblEstadoLexico = cambiarEstado("Léxico: —");
        lblEstadoSintactico = cambiarEstado("Sintáctico: —");
        lblEstadoSemantico = cambiarEstado("Semántico: —");
        lblEstadoGeneracion = cambiarEstado("PromptZal: —");

        lblPosicion = cambiarEstado("Ln 1, Col 1");

        derecha.add(lblEstadoLexico);
        derecha.add(lblEstadoSintactico);
        derecha.add(lblEstadoSemantico);
        derecha.add(lblEstadoGeneracion);
        derecha.add(lblPosicion);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    private JLabel cambiarEstado(String texto) {
        JLabel label = new JLabel(texto + "    ");
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextArea crearAreaTexto() {

        JTextArea area = new JTextArea();
        area.setBackground(COLOR_EDITOR);
        area.setForeground(COLOR_TEXTO);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setEditable(false);

        return area;
    }

    //Encabezado de la tabla tokens
    private String encabezadoTablaTokens() {
        return String.format("%-5s %-75s %-25s %-8s %-8s%n", "No.", "Lexema", "Tipo", "Fila", "Col.")
                + "---------------------------------------------------------------------------------------------------------------------------\n";
    }

    //Menu
    private JMenu crearMenu(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setForeground(COLOR_TEXTO);
        return menu;
    }

    private JMenuItem crearItem(String texto, KeyStroke tecla) {
        JMenuItem item = new JMenuItem(texto);

        if (tecla != null) {
            item.setAccelerator(tecla);
        }

        return item;
    }

    //crear un nuevo archivo
    private void nuevoArchivo() {
        consola.setText("""
                       > PromptZal IDE iniciado. \n
                       > Listo para trabajar.""");
        
        if (archivoModificado) {
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "Hay cambios sin guardar.\n"
                    + "¿Desea guardarlos?",
                    "Cambios sin guardar",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (respuesta == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (respuesta == JOptionPane.YES_OPTION) {
                guardarArchivo();
            }
        }
        editor.setText("");
        archivoActual = null;
        archivoModificado = false;
        limpiarResultados();
        actualizarNombreArchivo();
        actualizarEstado("Nuevo archivo");
    }

    //abrir un archivo
    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir archivo PromptZal");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PromptZal (*.pz)", "pz"));

        int resultado = chooser.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();

        try {
            String contenido = Files.readString(archivo.toPath(), StandardCharsets.UTF_8);
            archivoActual = archivo;
            archivoModificado = false;
            limpiarResultados();
            actualizarNombreArchivo();
            consola.append("> Archivo abierto: " + archivo.getName() + "\n");
            editor.setText(contenido);
            actualizarEstado("Archivo abierto");

        } catch (IOException ex) {
            mostrarError("No se pudo abrir el archivo.\n");
        }
    }

    //Guardar Archivo
    private void guardarArchivo() {
        if (archivoActual == null) {
            guardarComo();
            return;
        }

        try {
            Files.writeString(archivoActual.toPath(), editor.getText(), StandardCharsets.UTF_8);
            archivoModificado = false;
            actualizarNombreArchivo();
            actualizarEstado("Archivo guardado");

            consola.append(
                    "> Archivo guardado: "
                    + archivoActual.getAbsolutePath()
                    + "\n"
            );

        } catch (IOException ex) {
            mostrarError("No se pudo guardar el archivo.\n" + ex.getMessage()
            );
        }
    }

    //guardar 
    private void guardarComo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo .pz");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PromptZal (*.pz)", "pz"));
        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pz")) {
            archivo = new File(archivo.getAbsolutePath() + ".pz");
        }
        archivoActual = archivo;
        guardarArchivo();
    }

    // analizar archivo
    private void analizarArchivo() {
        tablaTokens.setText(encabezadoTablaTokens());
        String codigo = editor.getText();

        if (codigo.trim().isEmpty()) {
            mostrarError("No hay contenido para analizar.");
            return;
        }

        tablaTokens.setText(encabezadoTablaTokens());
        tablaErroresLexicos.setText("");

        consola.append("\n");
        consola.append("============================================\n");
        consola.append("           ANÁLISIS LÉXICO\n");
        consola.append("============================================\n");

        ejecutarAnalisisLexico();

        lblEstadoLexico.setText("Léxico: ✓    ");
        consola.append("> Análisis léxico terminado.\n");
        actualizarEstado("Análisis léxico terminado");

    }

    /*
    private void colorearTexto(String contenido) {
        ListaEnlazada<Token> tokensColorear = new ListaEnlazada<>();

        try {
            Reader reader = new StringReader(contenido);
            Colorear analizador = new Colorear(reader);
            Token tokenActual;
            while ((tokenActual = analizador.yylex()) != null) {
                tokensColorear.agregarNuevo(tokenActual);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: ");
        }

        StyledDocument documento = editor.getStyledDocument();

        try {
            documento.remove(0, documento.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        Style estiloDirectiva = editor.addStyle("Directiva", null);
        StyleConstants.setForeground(estiloDirectiva, new Color(0, 139, 139));
        StyleConstants.setBold(estiloDirectiva, true);

        Style estiloComentario = editor.addStyle("Comentario", null);
        StyleConstants.setForeground(estiloComentario, new Color(63, 127, 95));
        StyleConstants.setBold(estiloComentario, true);

        Style estiloCadena = editor.addStyle("Cadena", null);
        StyleConstants.setForeground(estiloCadena, Color.ORANGE);
        StyleConstants.setBold(estiloCadena, true);

        Style estiloReservadas = editor.addStyle("reservadas", null);
        StyleConstants.setForeground(estiloReservadas, Color.BLUE);
        StyleConstants.setBold(estiloReservadas, true);

        Style estiloComandosIA = editor.addStyle("reservadas", null);
        StyleConstants.setForeground(estiloComandosIA, Color.MAGENTA);
        StyleConstants.setBold(estiloComandosIA, true);

        Style estiloNumeros = editor.addStyle("numeros", null);
        StyleConstants.setForeground(estiloNumeros, new Color(36, 110, 18));
        StyleConstants.setBold(estiloNumeros, true);

        Style estiloError = editor.addStyle("numeros", null);
        StyleConstants.setForeground(estiloError, Color.RED);
        StyleConstants.setBold(estiloError, true);

        for (int i = 0; i < tokensColorear.getTamañoLista(); i++) {
            try {

                Token token = tokensColorear.obtenerDatos(i);
                System.out.println(tokensColorear.obtenerDatos(i).getTipo());
                int inicio = documento.getLength();
                String lexema = token.getLexema();

                documento.insertString(inicio, lexema, null);

                if (token.getTipo().equals("DIRECTIVA")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloDirectiva, true);
                } else if (token.getTipo().equals("COMENTARIO")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloComentario, true);
                } else if (token.getTipo().equals("CADENA")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloCadena, true);
                } else if (token.getTipo().equals("RESERVADAS")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloReservadas, true);
                } else if (token.getTipo().equals("COMANDO_IA")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloComandosIA, true);
                } else if (token.getTipo().equals("NUMERO")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloNumeros, true);
                } else if (token.getTipo().equals("ERROR")) {
                    documento.setCharacterAttributes(inicio, lexema.length(), estiloError, true);
                }

            } catch (ListaEnlazadaException | BadLocationException ex) {
                System.out.println("error al colorear");
            }
        }
    }
     */
    private void compilarArchivo() {
        consola.append("\n");
        consola.append("============================================\n");
        consola.append("           COMPILANDO \n");
        consola.append("============================================\n");
        
        ejecutarAnalisisLexico();
        ejecutarAnalisisSintactico();
        
        consola.append("> compilación finalizada");
    }

    private void ejecutarAnalisisLexico() {
        tablaTokens.setText(encabezadoTablaTokens());
        StringReader leer = new StringReader(editor.getText());
        AnalizadorLexico analizador = new AnalizadorLexico(leer);
        tokensReconocidos = new ListaEnlazada<>();
        try {
            Token tokenActual;
            while ((tokenActual = analizador.yylex()) != null) {
                if (!tokenActual.getTipo().equals("COMENTARIO")) {
                    tokensReconocidos.agregarNuevo(tokenActual);
                }

            }
        } catch (Exception e) {
            System.out.println("Error en el analisis Lexico: " + e.getMessage());
        }

        for (int i = 0; i < tokensReconocidos.getTamañoLista(); i++) {
            try {
                agregarToken(tokensReconocidos.obtenerDatos(i).getNoToken(),
                        tokensReconocidos.obtenerDatos(i).getLexema(),
                        tokensReconocidos.obtenerDatos(i).getTipo(),
                        tokensReconocidos.obtenerDatos(i).getFila(),
                        tokensReconocidos.obtenerDatos(i).getColumna());
            } catch (ListaEnlazadaException ex) {
                System.out.println("Error al mostrar tokens en consola");
            }
        }

        if (analizador.obtenerErrores().length() == 0) {
            tablaErroresLexicos.setText("No hay errores Lexicos");
        } else {
            tablaErroresLexicos.setText("ERRORES LEXICOS REGISTRADOS:" + "\n" + analizador.obtenerErrores());
        }
        ReporteErroresLexicos reporteErroresLexicos = new ReporteErroresLexicos();

        for (int i = 0; i < analizador.obtenerListaErrores().getTamañoLista(); i++) {
            try {
                reporteErroresLexicos.agregarError(
                        analizador.obtenerListaErrores().obtenerDatos(i).getLexema(),
                        analizador.obtenerListaErrores().obtenerDatos(i).getTipo(),
                        analizador.obtenerListaErrores().obtenerDatos(i).getFila(),
                        analizador.obtenerListaErrores().obtenerDatos(i).getColumna()
                        + 1);
            } catch (ListaEnlazadaException ex) {
                System.out.println("Error al crear reporte erroes lexicos");
            }

        }
        reporteErroresLexicos.generarReporte("reporteErroesLexicos.html");

        //colorearTexto(editor.getText());
    }

    private void ejecutarAnalisisSintactico() {
        ReporteTabla reporteTablaSimbolos = new ReporteTabla();
        tablaErroresSintacticos.setText("");
        AnalizadorSintactico2 analizador = new AnalizadorSintactico2(tokensReconocidos);
        analizador.analizar();
        ListaEnlazada<String> listadoErroresSintacticos = analizador.getListaErrores();
        tablaErroresSintacticos.append("ERRORES SINTACTICOS DETECTADOS: \n");

        if (listadoErroresSintacticos.getTamañoLista() > 0) {
            try {
                for (int i = 0; i < listadoErroresSintacticos.getTamañoLista(); i++) {
                    tablaErroresSintacticos.append(listadoErroresSintacticos.obtenerDatos(i) + "\n");
                }

            } catch (Exception e) {
                System.out.println("Error al registrar errores");
            }

        } else {
            tablaErroresSintacticos.setText("No hay errores Sintacticos.");
        }

        promptGenerado.setText(analizador.getPromptGenerado());

        try {
            for (int i = 0; i < analizador.getListadoSimbolos().getTamañoLista(); i++) {

                reporteTablaSimbolos.agregarDato(analizador.getListadoSimbolos().obtenerDatos(i).getNombre(),
                        analizador.getListadoSimbolos().obtenerDatos(i).getQueEs(),
                        analizador.getListadoSimbolos().obtenerDatos(i).getDondeExiste(),
                        analizador.getListadoSimbolos().obtenerDatos(i).getLinea());
            }
            reporteTablaSimbolos.guardarHTML("reporteTablaSimbolos.html");
        } catch (ListaEnlazadaException e) {
            System.out.println("Error al generar la tabla de simbolos");
        } catch (IOException ex) {
            System.out.println("Error al generar la tabla de simbolos");
        }

        
        ReporteErroresSemanticos reporteErroresSemanticos = new ReporteErroresSemanticos();
        
        AnalizadorSemantico analizadorSemantico = new AnalizadorSemantico(analizador.getListadoSimbolos(), analizador.getListadoNodos());
        analizadorSemantico.analizar();
        if (analizadorSemantico.getListaErrores().getTamañoLista() > 0) {
            tablaErroresSemanticos.setText("ERRORES SEMANTICOS DETECTADOS: \n");
            try {
                for (int i = 0; i < analizadorSemantico.getListaErrores().getTamañoLista(); i++) {
                    tablaErroresSemanticos.append(analizadorSemantico.getListaErrores().obtenerDatos(i) + "\n");
                    reporteErroresSemanticos.agregarError(analizadorSemantico.getListaErrores().obtenerDatos(i));
                }
            } catch (Exception e) {
            }
        }else {
            tablaErroresSemanticos.setText("No hay errores semanticos.");
        }
        reporteErroresSemanticos.generarReporte("reporteErroresSemanticos.html");
        

    }

    private void ejecutarAnalisisSemantico() {

    }

    private String generarPrompt() {
        return "";
    }

    private void agregarToken(int numero, String lexema, String tipo, int fila, int columna) {
        tablaTokens.append(String.format("%-5d %-75s %-25s %-8d %-8d%n", numero, lexema, tipo, fila, columna));
    }

    private void guardarPromptTXT() {
        guardarPrompt("Texto (*.txt)", "txt");
    }

    private void guardarPrompt(String descripcion, String extension) {

        if (promptGenerado == null || promptGenerado.getText().trim().isEmpty() || promptGenerado.getText().startsWith("El PromptZal generado")) {
            mostrarError("Primero debe compilar un archivo.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar PromptZal");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(descripcion, extension));

        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith("." + extension)) {
            archivo = new File(archivo.getAbsolutePath() + "." + extension);
        }

        try {

            Files.writeString(archivo.toPath(), promptGenerado.getText(), StandardCharsets.UTF_8);

            consola.append(
                    "> PromptZal guardado: "
                    + archivo.getAbsolutePath()
                    + "\n"
            );

            actualizarEstado("PromptZal guardado");

        } catch (IOException ex) {
            mostrarError("No se pudo guardar el PromptZal.\n" + ex.getMessage()
            );
        }
    }

    private void limpiarResultados() {

        if (tablaTokens != null) {
            tablaTokens.setText(encabezadoTablaTokens());
        }

        if (promptGenerado != null) {
            promptGenerado.setText("El PromptZal generado aparecerá aquí...");
        }

        if (lblEstadoLexico != null) {
            lblEstadoLexico.setText("Léxico: —    ");
            lblEstadoSintactico.setText("Sintáctico: —    ");
            lblEstadoSemantico.setText("Semántico: —    ");
            lblEstadoGeneracion.setText("PromptZal: —    ");
        }
    }

    private void posicionFilaYColumna() {

        if (editor == null || lblPosicion == null) {
            return;
        }

        try {
            int posicion = editor.getCaretPosition();
            int linea = editor.getDocument().getDefaultRootElement().getElementIndex(posicion) + 1;
            int inicio = editor.getDocument().getDefaultRootElement().getElement(linea - 1).getStartOffset();
            int columna = posicion - inicio + 1;
            lblPosicion.setText(
                    "Ln "
                    + linea
                    + ", Col "
                    + columna
                    + "    "
            );

        } catch (Exception ignored) {
            System.out.println("Error al obtener linae y columna del editor");
        }
    }

    private void actualizarNombreArchivo() {

        String nombre;

        if (archivoActual == null) {
            nombre = "Sin título.pz";
        } else {
            nombre = archivoActual.getName();
        }

        if (archivoModificado) {
            nombre = "● " + nombre;
        }
        if (lblArchivo != null) {
            lblArchivo.setText("   📄  " + nombre
            );
        }

        setTitle("PromptZal IDE - " + nombre);
    }

    private void agregarEventos() {

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                archivoModificado = true;
                actualizarNombreArchivo();
                posicionFilaYColumna();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                archivoModificado = true;
                actualizarNombreArchivo();
                posicionFilaYColumna();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                posicionFilaYColumna();
            }
        });
        editor.addCaretListener(e -> posicionFilaYColumna()
        );
    }

    private void crearAtajos() {
        InputMap mapa = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap acciones = editor.getActionMap();

        mapa.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "analizar");

        acciones.put("analizar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analizarArchivo();
            }
        });

        mapa.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "compilar");

        acciones.put("compilar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilarArchivo();
            }
        });
    }

    private void actualizarEstado(String texto) {

        if (lblEstado != null) {
            lblEstado.setText("  " + texto);
        }
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(this, mensaje, "PromptZal", JOptionPane.ERROR_MESSAGE);
        actualizarEstado("Error");
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
                this,
                "PROMPTZAL IDE\n\n"
                + "Compilador del lenguaje PromptZal\n"
                + "Laboratorio de Lenguajes Formales\n\n"
                + "Frontend desarrollado en Java Swing.",
                "Acerca de PromptZal",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ============================================================
    // NÚMEROS DE LÍNEA
    // ============================================================
    public static class TextLineNumber extends JPanel implements DocumentListener {

        private final JTextPane editor;
        private final Font fuente = new Font(Font.MONOSPACED, Font.PLAIN, 14);

        public TextLineNumber(JTextPane editor) {
            this.editor = editor;
            setBackground(new Color(35, 35, 35));
            setPreferredSize(new Dimension(55, 0));

            editor.getDocument().addDocumentListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(fuente);
            g.setColor(new Color(115, 115, 115));

            FontMetrics metricas = g.getFontMetrics();
            int altura = metricas.getHeight();
            int cantidad = editor.getDocument().getDefaultRootElement().getElementCount();

            for (int i = 0; i < cantidad; i++) {
                String numero = String.valueOf(i + 1);

                int y = ((i + 1) * altura) - 4;
                int x = getWidth() - 8 - metricas.stringWidth(numero);

                g.drawString(numero, x, y);
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            repaint();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            repaint();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            repaint();
        }
    }

}
