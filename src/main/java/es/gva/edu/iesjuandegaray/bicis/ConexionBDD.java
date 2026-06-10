package es.gva.edu.iesjuandegaray.bicis;

import java.sql.*;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;

public class ConexionBDD extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textFieldNEstaciones;
    private JTextArea textAreaDatos;

    private static Connection con;
    private static Statement s;
    private static DatosJSon dJSon;
    private static int numEst = 3;

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String user   = "admin";
    private static final String pass   = "123456789";
    private static final String url    =
        "jdbc:mysql://databasdmp6.cy5fb8q1fdr7.us-east-1.rds.amazonaws.com:3306/valenbicibd"
        + "?useSSL=true&requireSSL=false&serverTimezone=UTC";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try { new ConexionBDD().setVisible(true); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public ConexionBDD() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 520, 420);
        setTitle("ValenBisi AWS \u2014 Conexi\u00f3n RDS");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNEstaciones = new JLabel("N\u00ba estaciones:");
        lblNEstaciones.setBounds(10, 20, 120, 25);
        contentPane.add(lblNEstaciones);

        textFieldNEstaciones = new JTextField(String.valueOf(numEst));
        textFieldNEstaciones.setBounds(140, 20, 60, 25);
        contentPane.add(textFieldNEstaciones);

        JButton jButtonConectarBDD = new JButton("Conectar BD AWS");
        jButtonConectarBDD.setBounds(10, 55, 140, 30);
        contentPane.add(jButtonConectarBDD);
        jButtonConectarBDD.addActionListener(e -> conector());

        JButton jButtonObtenerDatos = new JButton("Obtener Datos API");
        jButtonObtenerDatos.setBounds(160, 55, 160, 30);
        contentPane.add(jButtonObtenerDatos);
        jButtonObtenerDatos.addActionListener(e -> obtenerDatos());

        JButton jButtonAnadirBDD = new JButton("A\u00f1adir a BDD");
        jButtonAnadirBDD.setBounds(330, 55, 140, 30);
        contentPane.add(jButtonAnadirBDD);
        jButtonAnadirBDD.addActionListener(e -> anadirBDD());

        JButton jButtonCerrar = new JButton("Cerrar Conexi\u00f3n");
        jButtonCerrar.setBounds(10, 95, 140, 30);
        contentPane.add(jButtonCerrar);
        jButtonCerrar.addActionListener(e -> cerrarConexion());

        textAreaDatos = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textAreaDatos);
        scrollPane.setBounds(10, 140, 480, 220);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(scrollPane);
    }

    private void conector() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pass);
            s   = con.createStatement();
            textAreaDatos.append("[OK] Conexi\u00f3n establecida con AWS RDS.\n");
        } catch (Exception ex) {
            textAreaDatos.append("[ERROR] " + ex.getMessage() + "\n");
        }
    }

    private void obtenerDatos() {
        try {
            numEst = Integer.parseInt(textFieldNEstaciones.getText().trim());
        } catch (NumberFormatException e) {
            textAreaDatos.append("[ERROR] N\u00famero inv\u00e1lido.\n");
            return;
        }
        dJSon = new DatosJSon(numEst);
        dJSon.mostrarDatos(numEst);
        textAreaDatos.append("--- Datos obtenidos de la API ---\n");
        if (dJSon.getDatos() != null && !dJSon.getDatos().isEmpty()) {
            textAreaDatos.append(dJSon.getDatos() + "\n");
        }
    }

    private void anadirBDD() {
        if (con == null || s == null) {
            textAreaDatos.append("[ERROR] Con\u00e9ctate primero a la BD.\n");
            return;
        }
        try {
            String[] values = dJSon != null ? dJSon.getValues() : null;
            if (values == null || values.length == 0) {
                textAreaDatos.append("[ERROR] No hay datos. Obt\u00e9n datos primero.\n");
                return;
            }
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null && !values[i].isEmpty()) {
                    s.execute(values[i]);
                    textAreaDatos.append("[OK] Insertado registro " + (i+1) + "\n");
                }
            }
        } catch (SQLException e) {
            textAreaDatos.append("[ERROR] " + e.getMessage() + "\n");
        }
    }

    private void cerrarConexion() {
        try {
            if (s != null) s.close();
            if (con != null) con.close();
            textAreaDatos.append("[OK] Conexi\u00f3n cerrada.\n");
        } catch (SQLException e) {
            textAreaDatos.append("[ERROR] " + e.getMessage() + "\n");
        }
    }
}
