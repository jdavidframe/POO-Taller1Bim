package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana principal del sistema. Solo navega entre módulos.
 */
public class MenuPrincipal extends JFrame {

    private controlador.ServicioAlumno servicio;

    public MenuPrincipal() {
        servicio = new controlador.ServicioAlumno();
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión Académica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 60, 114));

        // Título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(30, 60, 114));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel lblTitulo = new JLabel("SISTEMA ACADÉMICO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Gestión de Matrículas y Becas", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(180, 210, 255));

        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        panelTitulo.add(lblSubtitulo, BorderLayout.SOUTH);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 12));
        panelBotones.setBackground(new Color(30, 60, 114));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));

        JButton btnRegistrar    = crearBoton("👤  Registrar Alumno",    new Color(41, 128, 185));
        JButton btnHistorial    = crearBoton("📋  Historial",            new Color(39, 174, 96));
        JButton btnPagos        = crearBoton("💳  Gestor de Pagos",      new Color(142, 68, 173));
        JButton btnSalir        = crearBoton("✖  Salir",                 new Color(192, 57, 43));

        btnRegistrar.addActionListener(e -> abrirRegistro());
        btnHistorial.addActionListener(e -> abrirHistorial());
        btnPagos.addActionListener(e -> abrirPagos());
        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnPagos);
        panelBotones.add(btnSalir);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 44));

        btn.addMouseListener(new MouseAdapter() {
            Color original = color;
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(original.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });
        return btn;
    }

    private void abrirRegistro() {
        new RegistroAlumno(this, servicio).setVisible(true);
        setVisible(false);
    }

    private void abrirHistorial() {
        new Historial(this, servicio).setVisible(true);
        setVisible(false);
    }

    private void abrirPagos() {
        new GestionPagos(this, servicio).setVisible(true);
        setVisible(false);
    }
}
