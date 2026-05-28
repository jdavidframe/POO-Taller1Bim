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
 * Ventana Gestor de Pagos.
 * Selección por ComboBox → muestra saldos → permite registrar pagos de pensiones.
 */
public class GestionPagos extends JFrame {

    private JFrame ventanaPadre;
    private ServicioAlumno servicio;

    private JComboBox<String> cmbAlumnos;

    // Info del alumno
    private JLabel lblNombre, lblCedula;
    private JLabel lblMatBase, lblDescuento, lblValorFinal, lblSaldoPendiente, lblAlertaMorosidad;

    // Tabla de pensiones
    private DefaultTableModel modeloTabla;
    private JTable tablaPensiones;

    public GestionPagos(JFrame padre, ServicioAlumno servicio) {
        this.ventanaPadre = padre;
        this.servicio = servicio;
        initComponents();
        cargarCombo();
    }

    private void initComponents() {
        setTitle("Gestor de Pagos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(720, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { volverMenu(); }
        });

        JPanel contenedor = new JPanel(new BorderLayout(0, 0));
        contenedor.setBackground(new Color(245, 247, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(142, 68, 173));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel lblH = new JLabel("Gestor de Pagos");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblH.setForeground(Color.WHITE);
        header.add(lblH, BorderLayout.WEST);

        // Centro
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(new Color(245, 247, 250));
        centro.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        centro.add(crearSeccionSelector());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionInfoAlumno());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionPensiones());

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.setBackground(new Color(230, 234, 240));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 215)));

        JButton btnPagar      = crearBoton("Registrar Pago",    new Color(142, 68, 173));
        JButton btnActualizar = crearBoton("Actualizar Saldo",  new Color(52, 152, 219));
        JButton btnHistorial  = crearBoton("Ver Historial",     new Color(39, 174, 96));
        JButton btnVolver     = crearBoton("Volver al Menú",    new Color(127, 140, 141));

        btnPagar.addActionListener(e -> registrarPago());
        btnActualizar.addActionListener(e -> { cargarCombo(); limpiarInfo(); });
        btnHistorial.addActionListener(e -> abrirHistorial());
        btnVolver.addActionListener(e -> volverMenu());

        panelBotones.add(btnPagar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnHistorial);
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
        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 5));
        panel.setBackground(new Color(248, 245, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Información del Alumno"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        lblNombre        = new JLabel("—");
        lblCedula        = new JLabel("—");
        lblMatBase       = new JLabel("—");
        lblDescuento     = new JLabel("—");
        lblValorFinal    = new JLabel("—");
        lblSaldoPendiente = new JLabel("—");
        lblAlertaMorosidad = new JLabel("");

        lblValorFinal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSaldoPendiente.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSaldoPendiente.setForeground(new Color(180, 40, 40));

        panel.add(new JLabel("Nombre:"));                panel.add(lblNombre);
        panel.add(new JLabel("Cédula:"));                panel.add(lblCedula);
        panel.add(new JLabel("Matrícula base:"));        panel.add(lblMatBase);
        panel.add(new JLabel("Descuento aplicado:"));    panel.add(lblDescuento);
        panel.add(bold("Valor final / mes:"));           panel.add(lblValorFinal);
        panel.add(bold("Saldo pendiente total:"));       panel.add(lblSaldoPendiente);

        return panel;
    }

    private JPanel crearSeccionPensiones() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Pensiones — seleccione una fila para pagar"),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        String[] cols = {"#", "Mes", "Valor Base", "Descuento", "Valor Final", "Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPensiones = new JTable(modeloTabla);
        tablaPensiones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPensiones.setRowHeight(24);
        tablaPensiones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaPensiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPensiones.setSelectionBackground(new Color(180, 160, 230));

        tablaPensiones.getColumnModel().getColumn(0).setPreferredWidth(30);

        tablaPensiones.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String estado = (String) modeloTabla.getValueAt(row, 6);
                if (!sel) {
                    if ("PAGADA".equals(estado))   c.setBackground(new Color(220, 255, 220));
                    else if ("VENCIDA".equals(estado)) c.setBackground(new Color(255, 215, 215));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tablaPensiones);
        sp.setPreferredSize(new Dimension(0, 220));

        panel.add(lblAlertaMorosidad, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Lógica ─────────────────────────────────────────────────────────
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

        if (reg.getMatriculas().isEmpty()) {
            lblMatBase.setText("Sin matrícula"); lblDescuento.setText("—");
            lblValorFinal.setText("—"); lblSaldoPendiente.setText("—");
            modeloTabla.setRowCount(0);
            return;
        }

        Matricula mat = reg.getMatriculas().get(reg.getMatriculas().size() - 1);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "EC"));
        fmt.setMinimumFractionDigits(2); fmt.setMaximumFractionDigits(2);

        lblMatBase.setText("$" + fmt.format(mat.getCostoBase()));

        // Descuento total
        double pct = 0;
        for (Beca b : mat.getBecas()) pct += b.getPorcentajeDescuento();
        pct = Math.min(pct, 100);
        double desc = mat.getCostoBase() * (pct / 100.0);
        lblDescuento.setText("$" + fmt.format(desc) + "  (" + (int)pct + "%)");

        // Valor final (pensión representativa)
        Pension p0 = mat.getPensiones()[0];
        lblValorFinal.setText("$" + fmt.format(p0.getValorFinal()) + " / mes");

        // Saldo pendiente: suma de pensiones no pagadas
        double saldo = 0;
        for (Pension p : mat.getPensiones()) {
            if (!"PAGADA".equalsIgnoreCase(p.getEstado())) {
                saldo += p.getValorFinal();
            }
        }
        lblSaldoPendiente.setText("$" + fmt.format(saldo));

        // Alerta morosidad
        boolean alerta = servicio.tieneAlertaMorosidad(alumno.getCedula());
        if (alerta) {
            lblAlertaMorosidad.setText("⚠  ALERTA: más de 2 pensiones vencidas");
            lblAlertaMorosidad.setForeground(new Color(180, 40, 40));
            lblAlertaMorosidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            lblAlertaMorosidad.setText("");
        }

        // Tabla pensiones
        modeloTabla.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Pension[] pensiones = mat.getPensiones();
        for (int i = 0; i < pensiones.length; i++) {
            Pension p = pensiones[i];
            modeloTabla.addRow(new Object[]{
                i + 1,
                p.getMes(),
                "$" + fmt.format(p.getValorBase()),
                "$" + fmt.format(p.getValorDescuento()),
                "$" + fmt.format(p.getValorFinal()),
                sdf.format(p.getFechaVencimiento()),
                p.getEstado()
            });
        }
    }

    private void registrarPago() {
        int idx = cmbAlumnos.getSelectedIndex();
        if (idx <= 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un alumno primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaTabla = tablaPensiones.getSelectedRow();
        if (filaTabla < 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una pensión de la tabla para registrar el pago.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estado = (String) modeloTabla.getValueAt(filaTabla, 6);
        if ("PAGADA".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                "Esta pensión ya está marcada como PAGADA.",
                "Pago ya registrado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        RegistroAlumnos reg = servicio.getRegistros().get(idx - 1);
        String cedula = reg.getAlumno().getCedula();
        String mes    = (String) modeloTabla.getValueAt(filaTabla, 1);
        String monto  = (String) modeloTabla.getValueAt(filaTabla, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Registrar pago de pensión " + mes + " por " + monto + "?",
            "Confirmar pago", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = servicio.pagarPension(cedula, -1, filaTabla);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Pago registrado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            mostrarInfoAlumno(); // refrescar tabla y saldo
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo registrar el pago. Verifique los datos.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirHistorial() {
        new Historial(ventanaPadre, servicio).setVisible(true);
        dispose();
    }

    private void limpiarInfo() {
        lblNombre.setText("—"); lblCedula.setText("—");
        lblMatBase.setText("—"); lblDescuento.setText("—");
        lblValorFinal.setText("—"); lblSaldoPendiente.setText("—");
        lblAlertaMorosidad.setText("");
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
            new Font("Segoe UI", Font.BOLD, 12), new Color(100, 50, 160));
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(155, 38));
        return btn;
    }

    private JLabel bold(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}
