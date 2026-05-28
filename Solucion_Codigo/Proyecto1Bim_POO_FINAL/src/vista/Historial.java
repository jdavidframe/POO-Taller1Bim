package vista;

import controlador.ServicioAlumno;
import modelo.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Ventana de Historial.
 * Selección por ComboBox → muestra datos del alumno, becas y pensiones.
 */
public class Historial extends JFrame {

    private JFrame ventanaPadre;
    private ServicioAlumno servicio;

    private JComboBox<String> cmbAlumnos;

    // Info alumno
    private JLabel lblNombre, lblCedula, lblPromedio, lblNivel;

    // Info beca
    private JLabel lblBecas, lblDescuento, lblValorFinal;

    // Tabla pensiones
    private DefaultTableModel modeloTabla;
    private JTable tablaPensiones;

    public Historial(JFrame padre, ServicioAlumno servicio) {
        this.ventanaPadre = padre;
        this.servicio = servicio;
        initComponents();
        cargarCombo();
    }

    private void initComponents() {
        setTitle("Historial de Alumnos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(680, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { volverMenu(); }
        });

        JPanel contenedor = new JPanel(new BorderLayout(0, 0));
        contenedor.setBackground(new Color(245, 247, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(39, 174, 96));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("Historial de Alumnos");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.WEST);

        // Centro
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(new Color(245, 247, 250));
        centro.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        centro.add(crearSeccionSelector());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionInfoAlumno());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionBeca());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionPensiones());

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.setBackground(new Color(230, 234, 240));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 215)));

        JButton btnMostrar  = crearBoton("Mostrar información", new Color(39, 174, 96));
        JButton btnActualizar = crearBoton("Actualizar",        new Color(52, 152, 219));
        JButton btnVolver   = crearBoton("Volver al Menú",      new Color(127, 140, 141));

        btnMostrar.addActionListener(e -> mostrarInfoAlumno());
        btnActualizar.addActionListener(e -> { cargarCombo(); limpiarInfo(); });
        btnVolver.addActionListener(e -> volverMenu());

        panelBotones.add(btnMostrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVolver);

        contenedor.add(header, BorderLayout.NORTH);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(contenedor);
    }

    private JPanel crearSeccionSelector() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Selección de Alumno"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        cmbAlumnos = new JComboBox<>();
        cmbAlumnos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbAlumnos.addActionListener(e -> mostrarInfoAlumno());

        panel.add(new JLabel("Seleccionar alumno:"), BorderLayout.WEST);
        panel.add(cmbAlumnos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearSeccionInfoAlumno() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Información del Alumno"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        lblNombre  = new JLabel("—");
        lblCedula  = new JLabel("—");
        lblPromedio = new JLabel("—");
        lblNivel   = new JLabel("—");

        panel.add(new JLabel("Nombre completo:"));  panel.add(lblNombre);
        panel.add(new JLabel("Cédula:"));            panel.add(lblCedula);
        panel.add(new JLabel("Promedio:"));          panel.add(lblPromedio);
        panel.add(new JLabel("Nivel académico:"));   panel.add(lblNivel);
        return panel;
    }

    private JPanel crearSeccionBeca() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 6));
        panel.setBackground(new Color(248, 252, 248));
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Información de Becas y Matrícula"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        lblBecas     = new JLabel("—");
        lblDescuento = new JLabel("—");
        lblValorFinal = new JLabel("—");
        lblValorFinal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValorFinal.setForeground(new Color(30, 60, 114));

        panel.add(new JLabel("Becas aplicadas:"));     panel.add(lblBecas);
        panel.add(new JLabel("Descuento aplicado:"));  panel.add(lblDescuento);
        panel.add(bold("Valor final de matrícula:")); panel.add(lblValorFinal);
        return panel;
    }

    private JPanel crearSeccionPensiones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Pensiones Mensuales"),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        String[] columnas = {"Mes", "Valor Base", "Descuento", "Valor Final", "Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPensiones = new JTable(modeloTabla);
        tablaPensiones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPensiones.setRowHeight(24);
        tablaPensiones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaPensiones.setSelectionBackground(new Color(200, 225, 255));

        // Renderer de color por estado
        tablaPensiones.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String estado = (String) modeloTabla.getValueAt(row, 5);
                if (!sel) {
                    if ("PAGADA".equals(estado))   c.setBackground(new Color(220, 255, 220));
                    else if ("VENCIDA".equals(estado)) c.setBackground(new Color(255, 220, 220));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tablaPensiones);
        sp.setPreferredSize(new Dimension(0, 180));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Lógica ────────────────────────────────────────────────────────
    private void cargarCombo() {
        cmbAlumnos.removeAllItems();
        cmbAlumnos.addItem("-- Seleccione un alumno --");
        for (RegistroAlumnos reg : servicio.getRegistros()) {
            cmbAlumnos.addItem(reg.getAlumno().getNombre()
                    + "  [" + reg.getAlumno().getCedula() + "]");
        }
    }

    private void mostrarInfoAlumno() {
        int idx = cmbAlumnos.getSelectedIndex();
        if (idx <= 0) { limpiarInfo(); return; }

        RegistroAlumnos reg = servicio.getRegistros().get(idx - 1);
        Alumno alumno = reg.getAlumno();

        lblNombre.setText(alumno.getNombre());
        lblCedula.setText(alumno.getCedula());
        lblPromedio.setText(String.format("%.2f", alumno.getPromedio()));
        lblNivel.setText(alumno.getNivelAcademico() != null ? alumno.getNivelAcademico() : "—");

        if (reg.getMatriculas().isEmpty()) {
            lblBecas.setText("Sin matrícula registrada");
            lblDescuento.setText("—");
            lblValorFinal.setText("—");
            modeloTabla.setRowCount(0);
            return;
        }

        Matricula mat = reg.getMatriculas().get(reg.getMatriculas().size() - 1);

        // Becas
        if (mat.getBecas().isEmpty()) {
            lblBecas.setText("Ninguna");
            lblDescuento.setText("$0.00  (0%)");
        } else {
            StringBuilder sb = new StringBuilder();
            double pct = 0;
            for (Beca b : mat.getBecas()) {
                sb.append(b.toString()).append("  ");
                pct += b.getPorcentajeDescuento();
            }
            lblBecas.setText(sb.toString().trim());
            double desc = mat.getCostoBase() * (Math.min(pct, 100) / 100.0);
            lblDescuento.setText(String.format("$%.2f  (%.0f%%)", desc, Math.min(pct, 100)));
        }

        // Valor final (pensión 0 como referencia)
        Pension p0 = mat.getPensiones()[0];
        lblValorFinal.setText(String.format("$%.2f / mes", p0.getValorFinal()));

        // Pensiones
        modeloTabla.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "EC"));
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);

        for (Pension p : mat.getPensiones()) {
            modeloTabla.addRow(new Object[]{
                p.getMes(),
                "$" + fmt.format(p.getValorBase()),
                "$" + fmt.format(p.getValorDescuento()),
                "$" + fmt.format(p.getValorFinal()),
                sdf.format(p.getFechaVencimiento()),
                p.getEstado()
            });
        }
    }

    private void limpiarInfo() {
        lblNombre.setText("—"); lblCedula.setText("—");
        lblPromedio.setText("—"); lblNivel.setText("—");
        lblBecas.setText("—"); lblDescuento.setText("—");
        lblValorFinal.setText("—");
        modeloTabla.setRowCount(0);
    }

    private void volverMenu() {
        ventanaPadre.setVisible(true);
        dispose();
    }

    // ── Utilidades UI ─────────────────────────────────────────────────
    private Border crearBordeTitulo(String titulo) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 190, 210)),
            titulo, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(39, 120, 60));
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 38));
        return btn;
    }

    private JLabel bold(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}
