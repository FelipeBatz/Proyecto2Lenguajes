/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Frontent;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

/**
 *
 * @author felip
 */
public class IDE extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(IDE.class.getName());

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

    private JTextPane editor;
    private JTextArea consola;
    private JTextArea tablaTokens;
    private JTextArea tablaErrores;
    private JLabel lblArchivo;
    private JLabel lblEstado;
    private JLabel lblPosicion;
    private JLabel lblCodificacion;
    private JLabel lblLineaSeleccionada;
    private JPanel panelExplorador;
    private JPanel panelInferior;
    private JTabbedPane tabsInferior;
    private File archivoActual;
    private boolean archivoModificado = false;

    public IDE() {
        initComponents();

    }

    private void configurarVentana() {
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarPrograma();
            }

        });
    }

    private void cerrarPrograma() {

        if (archivoModificado) {

            int respuesta = JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar.\n" + "¿Desea guardar antes de salir?", "Salir", JOptionPane.YES_NO_CANCEL_OPTION);
            if (respuesta == JOptionPane.CANCEL_OPTION) {
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

            consola.append("> Archivo guardado: " + archivoActual.getAbsolutePath() + "\n");

        } catch (IOException ex) {

            mostrarError("No se pudo guardar el archivo.\n" + ex.getMessage());
        }
    }

    private void guardarComo() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo PromptZal");

        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PromptZal (*.pz)", "pz"));

        int resultado = chooser.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();

        String nombre = archivo.getName();

        if (!nombre.toLowerCase().endsWith(".pz")) {

            archivo = new File(archivo.getAbsolutePath() + ".pz");
        }

        archivoActual = archivo;

        guardarArchivo();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "PromptZal", JOptionPane.ERROR_MESSAGE);

        actualizarEstado("Error");
    }

    private void actualizarEstado(String mensaje) {
        lblEstado.setText("  " + mensaje);
    }

    private void actualizarNombreArchivo() {
        if (lblArchivo == null) {
            return;
        }
        String nombre = obtenerNombreArchivo();

        if (archivoModificado) {
            lblArchivo.setText("  ● " + nombre);
        } else {
            lblArchivo.setText("  " + nombre);
        }
        setTitle("PromptZal - " + nombre);
    }

    private String obtenerNombreArchivo() {
        if (archivoActual == null) {
            return "Sin título.pz";
        }
        return archivoActual.getName();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("IDE");
        setMinimumSize(new java.awt.Dimension(1000, 650));
        setSize(new java.awt.Dimension(1400, 850));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1050, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
