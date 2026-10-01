package Frontent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * IDE frontend para PromptZal
 *
 * Funciones: - Nuevo archivo - Abrir archivo .pz - Guardar archivo .pz -
 * Guardar como - Editar - Analizar - Compilar - Consola - Panel de tokens -
 * Panel de errores - Explorador de archivos - Números de línea - Tema oscuro
 */
public class FramePrincipal extends JFrame {

    //Colores ============================================================
    private final Color COLOR_FONDO = new Color(30, 30, 30);
    private final Color COLOR_PANEL = new Color(37, 37, 38);
    private final Color COLOR_PANEL_2 = new Color(45, 45, 48);
    private final Color COLOR_EDITOR = new Color(30, 30, 30);
    private final Color COLOR_BORDE = new Color(60, 60, 60);
    private final Color COLOR_TEXTO = new Color(220, 220, 220);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(150, 150, 150);
    private final Color COLOR_AZUL = new Color(0, 122, 204);
    private final Color COLOR_VERDE = new Color(78, 201, 176);
    private final Color COLOR_ROJO = new Color(244, 71, 71);
    private final Color COLOR_AMARILLO = new Color(220, 220, 170);

    //Componentes ============================================================
    private JTextPane editor;
    private JTextArea consola;
    private JTextArea tablaTokens;
    private JTextArea tablaErrores;
    private JLabel lblArchivo;
    private JLabel lblEstado;
    private JLabel lblPosicion;
    private JLabel lblCodificacion;
    private JPanel panelExplorador;
    private JPanel panelInferior;
    private JTabbedPane tabsInferior;
    private File archivoActual;
    private boolean archivoModificado = false;

    //Constructor ============================================================
    public FramePrincipal() {

        configurarVentana();

        crearInterfaz();

        crearAtajos();

        agregarEventos();

        nuevoArchivo();
    }

    //Configuracion frame principal ============================================================
    private void configurarVentana() {
        setTitle("PromptZal");
        setSize(1400, 850);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarPrograma();
            }
        });
    }

    // ============================================================
    // CREAR INTERFAZ
    // ============================================================
    private void crearInterfaz() {

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(COLOR_FONDO);
        setContentPane(principal);

        // MENÚ
        principal.add(crearMenuBar(), BorderLayout.NORTH);

        // TOOLBAR
        JPanel barraHerramientas = crearToolBar();
        principal.add(barraHerramientas, BorderLayout.CENTER);

        // Por eso utilizamos un contenedor principal.
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);

        contenido.add(barraHerramientas, BorderLayout.NORTH);

        JSplitPane centro = crearCentro();
        contenido.add(centro,BorderLayout.CENTER);
        JPanel estado = crearBarraEstado();
        contenido.add(estado,BorderLayout.SOUTH);
        principal.removeAll();
        principal.add(crearMenuBar(),BorderLayout.NORTH);
        principal.add(contenido,BorderLayout.CENTER);
    }


    //Menu ============================================================
    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_PANEL);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,COLOR_BORDE));

      
        // ARCHIVO
        // --------------------------------------------------------
        JMenu menuArchivo = crearMenu("Archivo");

        JMenuItem nuevo = crearMenuItem(
                "Nuevo",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_N,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        nuevo.addActionListener(e -> nuevoArchivo());

        JMenuItem abrir = crearMenuItem(
                "Abrir archivo (.pz)",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        abrir.addActionListener(e -> abrirArchivo());

        JMenuItem guardar = crearMenuItem(
                "Guardar archivo (.pz)",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_S,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        guardar.addActionListener(e -> guardarArchivo());

        JMenuItem guardarComo = crearMenuItem(
                "Guardar como...",
                null
        );

        guardarComo.addActionListener(
                e -> guardarComo()
        );

        JMenuItem salir = crearMenuItem(
                "Salir",
                null
        );

        salir.addActionListener(
                e -> cerrarPrograma()
        );

        menuArchivo.add(nuevo);
        menuArchivo.add(abrir);
        menuArchivo.add(guardar);
        menuArchivo.add(guardarComo);

        menuArchivo.addSeparator();

        menuArchivo.add(salir);

        // --------------------------------------------------------
        // EDITAR
        // --------------------------------------------------------
        JMenu menuEditar = crearMenu("Editar");

        JMenuItem deshacer = crearMenuItem(
                "Deshacer",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_Z,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        deshacer.addActionListener(
                e -> editor.getActionMap()
                        .get("undo")
        );

        JMenuItem cortar = crearMenuItem(
                "Cortar",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_X,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        cortar.addActionListener(
                e -> editor.cut()
        );

        JMenuItem copiar = crearMenuItem(
                "Copiar",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_C,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        copiar.addActionListener(
                e -> editor.copy()
        );

        JMenuItem pegar = crearMenuItem(
                "Pegar",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_V,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        pegar.addActionListener(
                e -> editor.paste()
        );

        JMenuItem seleccionarTodo = crearMenuItem(
                "Seleccionar todo",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_A,
                        InputEvent.CTRL_DOWN_MASK
                )
        );

        seleccionarTodo.addActionListener(
                e -> editor.selectAll()
        );

        menuEditar.add(deshacer);
        menuEditar.add(cortar);
        menuEditar.add(copiar);
        menuEditar.add(pegar);

        menuEditar.addSeparator();

        menuEditar.add(seleccionarTodo);

        // --------------------------------------------------------
        // ANALIZAR
        // --------------------------------------------------------
        JMenu menuAnalizar = crearMenu("Analizar");

        JMenuItem analizar = crearMenuItem(
                "Analizar archivo",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_F5,
                        0
                )
        );

        analizar.addActionListener(
                e -> analizarArchivo()
        );

        menuAnalizar.add(analizar);

        // --------------------------------------------------------
        // COMPILAR
        // --------------------------------------------------------
        JMenu menuCompilar = crearMenu("Compilar");

        JMenuItem compilar = crearMenuItem(
                "Compilar PromptZal",
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_F6,
                        0
                )
        );

        compilar.addActionListener(
                e -> compilarArchivo()
        );

        menuCompilar.add(compilar);

        // --------------------------------------------------------
        // AYUDA
        // --------------------------------------------------------
        JMenu menuAyuda = crearMenu("Ayuda");

        JMenuItem acerca = crearMenuItem(
                "Acerca de PromptZal",
                null
        );

        acerca.addActionListener(
                e -> mostrarAcercaDe()
        );

        menuAyuda.add(acerca);

        // Agregar menús
        menuBar.add(menuArchivo);
        menuBar.add(menuEditar);
        menuBar.add(menuAnalizar);
        menuBar.add(menuCompilar);
        menuBar.add(menuAyuda);

        return menuBar;
    }

    // ============================================================
    // TOOLBAR
    // ============================================================
    private JPanel crearToolBar() {

        JPanel barra = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        5,
                        5
                )
        );

        barra.setBackground(COLOR_PANEL);

        barra.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0,
                        COLOR_BORDE
                )
        );

        JButton btnNuevo = crearBoton(
                "Nuevo",
                "Ctrl + N"
        );

        btnNuevo.addActionListener(
                e -> nuevoArchivo()
        );

        JButton btnAbrir = crearBoton(
                "Abrir",
                "Ctrl + O"
        );

        btnAbrir.addActionListener(
                e -> abrirArchivo()
        );

        JButton btnGuardar = crearBoton(
                "Guardar",
                "Ctrl + S"
        );

        btnGuardar.addActionListener(
                e -> guardarArchivo()
        );

        JSeparator separador = new JSeparator(
                JSeparator.VERTICAL
        );

        separador.setPreferredSize(
                new Dimension(1, 25)
        );

        JButton btnAnalizar = crearBoton(
                "▶ Analizar",
                "F5"
        );

        btnAnalizar.setBackground(
                new Color(40, 100, 70)
        );

        btnAnalizar.addActionListener(
                e -> analizarArchivo()
        );

        JButton btnCompilar = crearBoton(
                "⚙ Compilar",
                "F6"
        );

        btnCompilar.setBackground(
                new Color(60, 70, 120)
        );

        btnCompilar.addActionListener(
                e -> compilarArchivo()
        );

        barra.add(btnNuevo);
        barra.add(btnAbrir);
        barra.add(btnGuardar);

        barra.add(separador);

        barra.add(btnAnalizar);
        barra.add(btnCompilar);

        return barra;
    }

    // ============================================================
    // CENTRO
    // ============================================================
    private JSplitPane crearCentro() {
        // --------------------------------------------------------
        // EDITOR
        // --------------------------------------------------------
        JPanel panelEditor = crearEditor();

        // --------------------------------------------------------
        // PARTE DERECHA
        // --------------------------------------------------------
        JPanel derecha = new JPanel(
                new BorderLayout()
        );

        derecha.setBackground(
                COLOR_EDITOR
        );

        derecha.add(
                panelEditor,
                BorderLayout.CENTER
        );

        panelInferior = crearPanelInferior();

        derecha.add(
                panelInferior,
                BorderLayout.SOUTH
        );

        // --------------------------------------------------------
        // SPLIT EXPLORADOR / EDITOR
        // --------------------------------------------------------
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelExplorador,
                derecha
        );

        split.setDividerLocation(230);

        split.setDividerSize(3);

        split.setBorder(null);

        return split;
    }

   
    

    // ============================================================
    // EDITOR
    // ============================================================
    private JPanel crearEditor() {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(
                COLOR_EDITOR
        );

        // --------------------------------------------------------
        // PESTAÑA DEL ARCHIVO
        // --------------------------------------------------------
        JPanel pestaña = new JPanel(
                new BorderLayout()
        );

        pestaña.setBackground(
                COLOR_PANEL_2
        );

        pestaña.setPreferredSize(
                new Dimension(0, 38)
        );

        lblArchivo = new JLabel(
                "  ● Sin guardar"
        );

        lblArchivo.setForeground(
                COLOR_TEXTO
        );

        lblArchivo.setBorder(
                new EmptyBorder(
                        0, 10, 0, 10
                )
        );

        pestaña.add(
                lblArchivo,
                BorderLayout.WEST
        );

        panel.add(
                pestaña,
                BorderLayout.NORTH
        );

        // --------------------------------------------------------
        // EDITOR
        // --------------------------------------------------------
        editor = new JTextPane();

        editor.setBackground(
                COLOR_EDITOR
        );

        editor.setForeground(
                COLOR_TEXTO
        );

        editor.setCaretColor(
                Color.WHITE
        );

        editor.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        15
                )
        );

        editor.setMargin(
                new Insets(
                        8, 10, 8, 10
                )
        );

        // --------------------------------------------------------
        // NÚMEROS DE LÍNEA
        // --------------------------------------------------------
        TextLineNumber lineNumber
                = new TextLineNumber(editor);

        JScrollPane scroll = new JScrollPane(
                editor
        );

        scroll.setRowHeaderView(
                lineNumber
        );

        scroll.setBorder(null);

        scroll.getViewport()
                .setBackground(COLOR_EDITOR);

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ============================================================
    // PANEL INFERIOR
    // ============================================================
    private JPanel crearPanelInferior() {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(
                COLOR_PANEL
        );

        panel.setPreferredSize(
                new Dimension(
                        0,
                        190
                )
        );

        tabsInferior = new JTabbedPane();

        tabsInferior.setBackground(
                COLOR_PANEL
        );

        tabsInferior.setForeground(
                COLOR_TEXTO
        );

        // --------------------------------------------------------
        // CONSOLA
        // --------------------------------------------------------
        consola = crearAreaTexto();

        consola.setText(
                "> PromptZal IDE iniciado.\n"
                + "> Listo para trabajar.\n"
        );

        // --------------------------------------------------------
        // TOKENS
        // --------------------------------------------------------
        tablaTokens = crearAreaTexto();

        tablaTokens.setText(
                String.format(
                        "%-5s %-20s %-20s %-8s %-8s%n",
                        "No.",
                        "Lexema",
                        "Tipo",
                        "Fila",
                        "Col."
                )
        );

        // --------------------------------------------------------
        // ERRORES
        // --------------------------------------------------------
        tablaErrores = crearAreaTexto();

        tablaErrores.setText(
                "No se han encontrado errores.\n"
        );

        tabsInferior.addTab(
                "CONSOLA",
                new JScrollPane(consola)
        );

        tabsInferior.addTab(
                "TOKENS",
                new JScrollPane(tablaTokens)
        );

        tabsInferior.addTab(
                "ERRORES",
                new JScrollPane(tablaErrores)
        );

        panel.add(
                tabsInferior,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ============================================================
    // BARRA DE ESTADO
    // ============================================================
    private JPanel crearBarraEstado() {

        JPanel barra = new JPanel(
                new BorderLayout()
        );

        barra.setBackground(
                COLOR_AZUL
        );

        barra.setPreferredSize(
                new Dimension(
                        0,
                        27
                )
        );

        lblEstado = new JLabel(
                "  Listo"
        );

        lblEstado.setForeground(
                Color.WHITE
        );

        lblPosicion = new JLabel(
                "Ln 1, Col 1"
        );

        lblPosicion.setForeground(
                Color.WHITE
        );

        lblCodificacion = new JLabel(
                "UTF-8   "
        );

        lblCodificacion.setForeground(
                Color.WHITE
        );

        barra.add(
                lblEstado,
                BorderLayout.WEST
        );

        JPanel derecha = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        0,
                        4
                )
        );

        derecha.setOpaque(false);

        derecha.add(lblPosicion);
        derecha.add(lblCodificacion);

        barra.add(
                derecha,
                BorderLayout.EAST
        );

        return barra;
    }

    // ============================================================
    // CREAR BOTÓN
    // ============================================================
    private JButton crearBoton(
            String texto,
            String tooltip
    ) {

        JButton boton = new JButton(
                texto
        );

        boton.setForeground(
                COLOR_TEXTO
        );

        boton.setBackground(
                COLOR_PANEL_2
        );

        boton.setFocusPainted(false);

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        new EmptyBorder(
                                5, 10, 5, 10
                        )
                )
        );

        boton.setToolTipText(
                tooltip
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return boton;
    }

    // ============================================================
    // MENÚ
    // ============================================================
    private JMenu crearMenu(
            String texto
    ) {

        JMenu menu = new JMenu(
                texto
        );

        menu.setForeground(
                COLOR_TEXTO
        );

        return menu;
    }

    private JMenuItem crearMenuItem(
            String texto,
            KeyStroke tecla
    ) {

        JMenuItem item = new JMenuItem(
                texto
        );

        item.setAccelerator(tecla);

        return item;
    }

    // ============================================================
    // ÁREA DE TEXTO
    // ============================================================
    private JTextArea crearAreaTexto() {

        JTextArea area = new JTextArea();

        area.setEditable(false);

        area.setBackground(
                COLOR_EDITOR
        );

        area.setForeground(
                COLOR_TEXTO
        );

        area.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        area.setMargin(
                new Insets(
                        8, 10, 8, 10
                )
        );

        return area;
    }

    // ============================================================
    // EVENTOS
    // ============================================================
    private void agregarEventos() {

        editor.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                    @Override
                    public void insertUpdate(
                            DocumentEvent e
                    ) {

                        archivoModificado = true;

                        actualizarNombreArchivo();

                        actualizarPosicion();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent e
                    ) {

                        archivoModificado = true;

                        actualizarNombreArchivo();

                        actualizarPosicion();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent e
                    ) {

                        actualizarPosicion();
                    }
                }
                );

        editor.addCaretListener(
                e -> actualizarPosicion()
        );
    }

    // ============================================================
    // ATAJOS
    // ============================================================
    private void crearAtajos() {

        InputMap inputMap
                = editor.getInputMap(
                        JComponent.WHEN_IN_FOCUSED_WINDOW
                );

        ActionMap actionMap
                = editor.getActionMap();

        // F5 = Analizar
        inputMap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_F5,
                        0
                ),
                "analizar"
        );

        actionMap.put(
                "analizar",
                new AbstractAction() {

            @Override
            public void actionPerformed(
                    ActionEvent e
            ) {

                analizarArchivo();
            }
        }
        );

        // F6 = Compilar
        inputMap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_F6,
                        0
                ),
                "compilar"
        );

        actionMap.put(
                "compilar",
                new AbstractAction() {

            @Override
            public void actionPerformed(
                    ActionEvent e
            ) {

                compilarArchivo();
            }
        }
        );
    }

    // ============================================================
    // NUEVO
    // ============================================================
    private void nuevoArchivo() {

        if (archivoModificado) {

            int respuesta = JOptionPane.showConfirmDialog(this,
                            "El archivo tiene cambios sin guardar.\n"
                            + "¿Desea guardarlos?",
                            "Cambios sin guardar", JOptionPane.YES_NO_CANCEL_OPTION);

            if (respuesta == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (respuesta == JOptionPane.YES_OPTION) {
                guardarArchivo();
            }
        }
        archivoActual = null;
        archivoModificado = false;
        actualizarNombreArchivo();
        actualizarEstado("Nuevo archivo");
    }

    // ============================================================
    // ABRIR
    // ============================================================
    private void abrirArchivo() {

        JFileChooser chooser
                = new JFileChooser();

        chooser.setDialogTitle(
                "Abrir archivo PromptZal"
        );

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Archivos PromptZal (*.pz)",
                        "pz"
                )
        );

        int resultado
                = chooser.showOpenDialog(this);

        if (resultado
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        File archivo
                = chooser.getSelectedFile();

        try {

            String contenido
                    = Files.readString(
                            archivo.toPath(),
                            StandardCharsets.UTF_8
                    );

            editor.setText(
                    contenido
            );

            archivoActual = archivo;

            archivoModificado = false;

            actualizarNombreArchivo();

            actualizarEstado(
                    "Archivo abierto correctamente"
            );

            consola.append(
                    "> Archivo abierto: "
                    + archivo.getName()
                    + "\n"
            );

        } catch (IOException ex) {

            mostrarError(
                    "No se pudo abrir el archivo.\n"
                    + ex.getMessage()
            );
        }
    }

    // ============================================================
    // GUARDAR
    // ============================================================
    private void guardarArchivo() {

        if (archivoActual == null) {

            guardarComo();

            return;
        }

        try {

            Files.writeString(
                    archivoActual.toPath(),
                    editor.getText(),
                    StandardCharsets.UTF_8
            );

            archivoModificado = false;

            actualizarNombreArchivo();

            actualizarEstado(
                    "Archivo guardado"
            );

            consola.append(
                    "> Archivo guardado: "
                    + archivoActual.getAbsolutePath()
                    + "\n"
            );

        } catch (IOException ex) {

            mostrarError(
                    "No se pudo guardar el archivo.\n"
                    + ex.getMessage()
            );
        }
    }

    // ============================================================
    // GUARDAR COMO
    // ============================================================
    private void guardarComo() {

        JFileChooser chooser
                = new JFileChooser();

        chooser.setDialogTitle(
                "Guardar archivo PromptZal"
        );

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Archivos PromptZal (*.pz)",
                        "pz"
                )
        );

        int resultado
                = chooser.showSaveDialog(this);

        if (resultado
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        File archivo
                = chooser.getSelectedFile();

        String nombre
                = archivo.getName();

        if (!nombre.toLowerCase()
                .endsWith(".pz")) {

            archivo
                    = new File(
                            archivo.getAbsolutePath()
                            + ".pz"
                    );
        }

        archivoActual = archivo;

        guardarArchivo();
    }

    // ============================================================
    // ANALIZAR
    // ============================================================
    private void analizarArchivo() {

        if (editor.getText().trim().isEmpty()) {

            mostrarError(
                    "No hay contenido para analizar."
            );

            return;
        }

        tabsInferior.setSelectedIndex(0);

        consola.append(
                "\n> ========================================\n"
        );

        consola.append(
                "> INICIANDO ANÁLISIS LÉXICO\n"
        );

        consola.append(
                "> ========================================\n"
        );

        consola.append(
                "> Archivo: "
                + obtenerNombreArchivo()
                + "\n"
        );

        consola.append(
                "> Caracteres: "
                + editor.getText().length()
                + "\n"
        );

        /*
         * ========================================================
         * AQUÍ CONECTAREMOS TU JFLEX
         * ========================================================
         *
         * Ejemplo posteriormente:
         *
         * Reader reader =
         *     new StringReader(editor.getText());
         *
         * Lexer lexer =
         *     new Lexer(reader);
         *
         * lexer.yylex();
         *
         * ========================================================
         */
        tablaTokens.setText("");

        tablaTokens.append(
                String.format(
                        "%-5s %-20s %-20s %-8s %-8s%n",
                        "No.",
                        "Lexema",
                        "Tipo",
                        "Fila",
                        "Col."
                )
        );

        tablaTokens.append(
                "----------------------------------------------------------\n"
        );

        // Análisis visual temporal
        String[] lineas
                = editor.getText()
                        .split("\\R");

        int numeroToken = 1;

        for (int i = 0;
                i < lineas.length;
                i++) {

            String linea
                    = lineas[i].trim();

            if (linea.startsWith("@modelo")) {

                agregarToken(
                        numeroToken++,
                        "@modelo",
                        "DIRECTIVA",
                        i + 1,
                        1
                );
            }

            if (linea.startsWith("@rol")) {

                agregarToken(
                        numeroToken++,
                        "@rol",
                        "DIRECTIVA",
                        i + 1,
                        1
                );
            }

            if (linea.startsWith("@formato")) {

                agregarToken(
                        numeroToken++,
                        "@formato",
                        "DIRECTIVA",
                        i + 1,
                        1
                );
            }

            if (linea.contains("{")) {

                agregarToken(
                        numeroToken++,
                        "{",
                        "DELIMITADOR",
                        i + 1,
                        linea.indexOf("{") + 1
                );
            }

            if (linea.contains("}")) {

                agregarToken(
                        numeroToken++,
                        "}",
                        "DELIMITADOR",
                        i + 1,
                        linea.indexOf("}") + 1
                );
            }
        }

        tablaErrores.setText(
                "No se encontraron errores léxicos.\n"
        );

        consola.append(
                "> Tokens encontrados: "
                + (numeroToken - 1)
                + "\n"
        );

        consola.append(
                "> Errores: 0\n"
        );

        consola.append(
                "> Análisis terminado correctamente.\n"
        );

        actualizarEstado(
                "Análisis completado"
        );

        tabsInferior.setSelectedIndex(1);
    }

    // ============================================================
    // AGREGAR TOKEN
    // ============================================================
    private void agregarToken(
            int numero,
            String lexema,
            String tipo,
            int fila,
            int columna
    ) {

        tablaTokens.append(
                String.format(
                        "%-5d %-20s %-20s %-8d %-8d%n",
                        numero,
                        lexema,
                        tipo,
                        fila,
                        columna
                )
        );
    }

    // ============================================================
    // COMPILAR
    // ============================================================
    private void compilarArchivo() {

        if (editor.getText().trim().isEmpty()) {

            mostrarError(
                    "No hay código para compilar."
            );

            return;
        }

        tabsInferior.setSelectedIndex(0);

        consola.append(
                "\n> ========================================\n"
        );

        consola.append(
                "> COMPILANDO PROMPTZAL\n"
        );

        consola.append(
                "> ========================================\n"
        );

        consola.append(
                "> Analizando archivo...\n"
        );

        // Primero analizamos
        analizarArchivo();

        consola.append(
                "> Verificando errores...\n"
        );

        consola.append(
                "> Generando salida...\n"
        );

        consola.append(
                "> Compilación finalizada.\n"
        );

        actualizarEstado(
                "Compilación finalizada"
        );
    }

    // ============================================================
    // POSICIÓN DEL CURSOR
    // ============================================================
    private void actualizarPosicion() {

        if (editor == null
                || lblPosicion == null) {

            return;
        }

        try {

            int posicion
                    = editor.getCaretPosition();

            int linea
                    = editor.getDocument()
                            .getDefaultRootElement()
                            .getElementIndex(
                                    posicion
                            ) + 1;

            int inicioLinea
                    = editor.getDocument()
                            .getDefaultRootElement()
                            .getElement(
                                    linea - 1
                            )
                            .getStartOffset();

            int columna
                    = posicion
                    - inicioLinea
                    + 1;

            lblPosicion.setText(
                    "Ln "
                    + linea
                    + ", Col "
                    + columna
            );

        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // NOMBRE ARCHIVO
    // ============================================================
    private void actualizarNombreArchivo() {

        if (lblArchivo == null) {
            return;
        }

        String nombre
                = obtenerNombreArchivo();

        if (archivoModificado) {

            lblArchivo.setText(
                    "  ● "
                    + nombre
            );

        } else {

            lblArchivo.setText(
                    "  "
                    + nombre
            );
        }

        setTitle(
                "PromptZal - "
                + nombre
        );
    }

    private String obtenerNombreArchivo() {

        if (archivoActual == null) {

            return "Sin título.pz";
        }

        return archivoActual.getName();
    }

    // ============================================================
    // ESTADO
    // ============================================================
    private void actualizarEstado(
            String mensaje
    ) {

        lblEstado.setText(
                "  "
                + mensaje
        );
    }

    // ============================================================
    // ACERCA DE
    // ============================================================
    private void mostrarAcercaDe() {

        JOptionPane.showMessageDialog(
                this,
                "PromptZal IDE\n\n"
                + "Lenguaje para generación de prompts\n"
                + "Universidad de San Carlos de Guatemala\n\n"
                + "Versión 1.0",
                "Acerca de PromptZal",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ============================================================
    // ERROR
    // ============================================================
    private void mostrarError(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "PromptZal",
                JOptionPane.ERROR_MESSAGE
        );

        actualizarEstado(
                "Error"
        );
    }

    // ============================================================
    // CERRAR
    // ============================================================
    private void cerrarPrograma() {

        if (archivoModificado) {

            int respuesta
                    = JOptionPane.showConfirmDialog(
                            this,
                            "Hay cambios sin guardar.\n"
                            + "¿Desea guardar antes de salir?",
                            "Salir",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );

            if (respuesta
                    == JOptionPane.CANCEL_OPTION) {

                return;
            }

            if (respuesta
                    == JOptionPane.YES_OPTION) {

                guardarArchivo();
            }
        }

        dispose();

        System.exit(0);
    }

    // ============================================================
    // MAIN
    // ============================================================
    public static void main(
            String[] args
    ) {

        try {

            UIManager.setLookAndFeel(
                    UIManager
                            .getSystemLookAndFeelClassName()
            );

        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(
                () -> {

                    FramePrincipal ventana
                    = new FramePrincipal();

                    ventana.setVisible(true);
                }
        );
    }

    // ============================================================
    // CLASE PARA NÚMEROS DE LÍNEA
    // ============================================================
    public static class TextLineNumber
            extends JPanel
            implements DocumentListener {

        private final JTextComponent textComponent;

        private final Font font
                = new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        14
                );

        public TextLineNumber(
                JTextComponent component
        ) {

            textComponent = component;

            setBackground(
                    new Color(
                            38,
                            38,
                            38
                    )
            );

            setPreferredSize(
                    new Dimension(
                            50,
                            0
                    )
            );

            component.getDocument()
                    .addDocumentListener(this);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(g);

            FontMetrics metrics
                    = g.getFontMetrics(font);

            int lineHeight
                    = metrics.getHeight();

            int start
                    = textComponent
                            .getInsets().top;

            int lineCount
                    = textComponent
                            .getDocument()
                            .getDefaultRootElement()
                            .getElementCount();

            g.setFont(font);

            g.setColor(
                    new Color(
                            120,
                            120,
                            120
                    )
            );

            for (int i = 0;
                    i < lineCount;
                    i++) {

                int y
                        = start
                        + ((i + 1)
                        * lineHeight)
                        - 4;

                String numero
                        = String.valueOf(
                                i + 1
                        );

                int ancho
                        = getWidth()
                        - 8
                        - metrics.stringWidth(
                                numero
                        );

                g.drawString(
                        numero,
                        ancho,
                        y
                );
            }
        }

        @Override
        public void insertUpdate(
                DocumentEvent e
        ) {

            repaint();
        }

        @Override
        public void removeUpdate(
                DocumentEvent e
        ) {

            repaint();
        }

        @Override
        public void changedUpdate(
                DocumentEvent e
        ) {

            repaint();
        }
    }
}
