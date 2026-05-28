package vista;

import controlador.ServicioAlumno;
import modelo.Alumno;
import modelo.Beca;
import modelo.Matricula;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Ventana para registrar un nuevo alumno.
 * - Beca de mérito: se evalúa automáticamente al ingresar el promedio.
 * - Beca familiar: checkbox manual.
 * - Beca de convenio: eliminada completamente.
 */
public class RegistroAlumno extends JFrame {

    private JFrame ventanaPadre;
    private ServicioAlumno servicio;

    // Campos de datos del alumno
    private JTextField txtNombre;
    private JTextField txtCedula;
    private JTextField txtPromedio;
    private JComboBox<String> cmbNivel;

    // Campos de matrícula
    private JTextField txtPeriodo;
    private JTextField txtCostoBase;

    // Beca familiar
    private JCheckBox chkFamiliar;

    // Panel de resultado de beca de mérito (solo lectura)
    private JLabel lblMeritoEstado;
    private JLabel lblMeritoDescuento;
    private JLabel lblMeritoValor;

    // Panel resumen
    private JLabel lblResumenOriginal;
    private JLabel lblResumenDescuento;
    private JLabel lblResumenFinal;

    public RegistroAlumno(JFrame padre, ServicioAlumno servicio) {
        this.ventanaPadre = padre;
        this.servicio = servicio;
        initComponents();
    }

    private void initComponents() {
        setTitle("Registrar Alumno");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(560, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { volverMenu(); }
        });

        JPanel contenedor = new JPanel(new BorderLayout(0, 0));
        contenedor.setBackground(new Color(245, 247, 250));

        // ── Encabezado ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 60, 114));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblHeader = new JLabel("Registrar Nuevo Alumno");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        header.add(lblHeader, BorderLayout.WEST);

        // ── Contenido central (scroll) ───────────────────────────────
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(new Color(245, 247, 250));
        centro.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        centro.add(crearSeccionDatosAlumno());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionMatricula());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionBecas());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearSeccionResumen());

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        // ── Botones inferiores ───────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.setBackground(new Color(230, 234, 240));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 215)));

        JButton btnRegistrar = crearBoton("Registrar", new Color(39, 174, 96));
        JButton btnLimpiar   = crearBoton("Limpiar",   new Color(52, 152, 219));
        JButton btnVolver    = crearBoton("Volver al Menú", new Color(127, 140, 141));

        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnVolver.addActionListener(e -> volverMenu());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnVolver);

        contenedor.add(header, BorderLayout.NORTH);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(contenedor);
    }

    // ── Sección: Datos del alumno ────────────────────────────────────
    private JPanel crearSeccionDatosAlumno() {
        JPanel panel = crearPanelSeccion("Datos del Alumno");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Datos del Alumno"), 
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre  = new JTextField();
        txtCedula  = new JTextField();
        txtPromedio = new JTextField();
        cmbNivel   = new JComboBox<>(new String[]{
            "Bachillerato", "Técnico Superior", "Tecnología",
            "Licenciatura / Ingeniería", "Posgrado"
        });

        agregarFila(panel, gbc, 0, "Nombre completo:", txtNombre);
        agregarFila(panel, gbc, 1, "Cédula:",           txtCedula);
        agregarFila(panel, gbc, 2, "Promedio (0-10):",  txtPromedio);
        agregarFila(panel, gbc, 3, "Nivel académico:",  cmbNivel);

        // Al salir del campo promedio → evaluar beca de mérito
        txtPromedio.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { evaluarMerito(); }
        });

        return panel;
    }

    // ── Sección: Matrícula ───────────────────────────────────────────
    private JPanel crearSeccionMatricula() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Datos de Matrícula"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPeriodo   = new JTextField("2025-2026");
        txtCostoBase = new JTextField("1200.00");

        agregarFila(panel, gbc, 0, "Período académico:", txtPeriodo);
        agregarFila(panel, gbc, 1, "Costo base ($):",    txtCostoBase);

        // Recalcular resumen si cambia el costo
        txtCostoBase.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { actualizarResumen(); }
        });

        return panel;
    }

    // ── Sección: Becas ───────────────────────────────────────────────
    private JPanel crearSeccionBecas() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Becas"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        // ── Beca de Mérito (automática) ──────────────────────────────
        JPanel panelMerito = new JPanel(new GridLayout(3, 2, 8, 4));
        panelMerito.setBackground(new Color(248, 252, 248));
        panelMerito.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JLabel lblTituloMerito = new JLabel("Beca de Mérito  (automática — promedio ≥ 9.0 → 50%)");
        lblTituloMerito.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTituloMerito.setForeground(new Color(39, 120, 60));

        lblMeritoEstado   = new JLabel("—");
        lblMeritoDescuento = new JLabel("—");
        lblMeritoValor     = new JLabel("—");

        panelMerito.add(lblTituloMerito);     panelMerito.add(new JLabel());
        panelMerito.add(new JLabel("Estado:"));          panelMerito.add(lblMeritoEstado);
        panelMerito.add(new JLabel("Descuento aplicado:")); panelMerito.add(lblMeritoDescuento);

        // ── Beca Familiar (manual) ───────────────────────────────────
        JPanel panelFamiliar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        panelFamiliar.setBackground(new Color(252, 248, 240));
        panelFamiliar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 210, 180)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        chkFamiliar = new JCheckBox("¿Tiene familiar en la institución?  (Beca Familiar → 15%)");
        chkFamiliar.setBackground(new Color(252, 248, 240));
        chkFamiliar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkFamiliar.addActionListener(e -> actualizarResumen());

        JLabel lblTituloFamiliar = new JLabel("Beca Familiar  (manual)");
        lblTituloFamiliar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTituloFamiliar.setForeground(new Color(140, 90, 20));

        panelFamiliar.add(lblTituloFamiliar);
        panelFamiliar.add(Box.createHorizontalStrut(10));
        panelFamiliar.add(chkFamiliar);

        panel.add(panelMerito);
        panel.add(Box.createVerticalStrut(8));
        panel.add(panelFamiliar);

        return panel;
    }

    // ── Sección: Resumen de pago ─────────────────────────────────────
    private JPanel crearSeccionResumen() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 6));
        panel.setBackground(new Color(235, 243, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            crearBordeTitulo("Resumen de Pago"),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        lblResumenOriginal  = new JLabel("—");
        lblResumenDescuento = new JLabel("—");
        lblResumenFinal     = new JLabel("—");

        lblResumenFinal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResumenFinal.setForeground(new Color(30, 60, 114));

        panel.add(new JLabel("Valor original de matrícula:"));  panel.add(lblResumenOriginal);
        panel.add(new JLabel("Descuento total aplicado:"));      panel.add(lblResumenDescuento);
        panel.add(bold("Valor final a pagar:"));                 panel.add(lblResumenFinal);

        return panel;
    }

    // ── Lógica: evaluar beca de mérito ───────────────────────────────
    private void evaluarMerito() {
        try {
            double promedio = Double.parseDouble(txtPromedio.getText().trim().replace(',', '.'));
            if (promedio >= 9.0) {
                lblMeritoEstado.setText("✔  Aplica");
                lblMeritoEstado.setForeground(new Color(39, 120, 60));
                lblMeritoDescuento.setText("50%");
            } else {
                lblMeritoEstado.setText("✘  No aplica");
                lblMeritoEstado.setForeground(new Color(180, 40, 40));
                lblMeritoDescuento.setText("0%");
            }
        } catch (NumberFormatException ex) {
            lblMeritoEstado.setText("—");
            lblMeritoDescuento.setText("—");
        }
        actualizarResumen();
    }

    // ── Lógica: actualizar resumen ───────────────────────────────────
    private void actualizarResumen() {
        try {
            double costo    = Double.parseDouble(txtCostoBase.getText().trim().replace(',', '.'));
            double promedio = 0;
            try { promedio = Double.parseDouble(txtPromedio.getText().trim().replace(',', '.')); }
            catch (NumberFormatException ignored) {}

            double pctMerito   = (promedio >= 9.0) ? 50.0 : 0.0;
            double pctFamiliar = chkFamiliar.isSelected() ? 15.0 : 0.0;
            double pctTotal    = Math.min(pctMerito + pctFamiliar, 100.0);

            double descuento = costo * (pctTotal / 100.0);
            double final_    = costo - descuento;

            NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("es", "EC"));
            fmt.setMinimumFractionDigits(2);
            fmt.setMaximumFractionDigits(2);

            lblResumenOriginal.setText("$" + fmt.format(costo));
            lblResumenDescuento.setText("$" + fmt.format(descuento) + "  (" + (int)pctTotal + "%)");
            lblResumenFinal.setText("$" + fmt.format(final_));
        } catch (NumberFormatException ex) {
            lblResumenOriginal.setText("—");
            lblResumenDescuento.setText("—");
            lblResumenFinal.setText("—");
        }
    }

    // ── Acción: registrar ────────────────────────────────────────────
    private void registrar() {
        String nombre  = txtNombre.getText().trim();
        String cedula  = txtCedula.getText().trim();
        String sPromedio = txtPromedio.getText().trim().replace(',', '.');
        String periodo = txtPeriodo.getText().trim();
        String sCosto  = txtCostoBase.getText().trim().replace(',', '.');
        String nivel   = (String) cmbNivel.getSelectedItem();

        if (nombre.isEmpty() || cedula.isEmpty() || sPromedio.isEmpty()
                || periodo.isEmpty() || sCosto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, complete todos los campos obligatorios.",
                "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double promedio, costoBase;
        try {
            promedio = Double.parseDouble(sPromedio);
            if (promedio < 0 || promedio > 10)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El promedio debe ser un número entre 0 y 10.",
                "Dato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            costoBase = Double.parseDouble(sCosto);
            if (costoBase <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El costo base debe ser un número mayor a 0.",
                "Dato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Alumno alumno = new Alumno(cedula, nombre, nivel, promedio);
        Matricula matricula = new Matricula(periodo, costoBase);

        // Beca familiar (manual)
        if (chkFamiliar.isSelected()) {
            matricula.agregarBeca(new Beca(Beca.FAMILIAR));
        }

        boolean ok = servicio.registrarAlumno(alumno, matricula);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Alumno registrado exitosamente.\n"
                + (alumno.esElegibleBecaMerito() ? "✔ Se aplicó beca de mérito (50%)." : ""),
                "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this,
                "Ya existe un alumno registrado con la cédula: " + cedula,
                "Cédula duplicada", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Acción: limpiar ──────────────────────────────────────────────
    private void limpiar() {
        txtNombre.setText("");
        txtCedula.setText("");
        txtPromedio.setText("");
        txtPeriodo.setText("2025-2026");
        txtCostoBase.setText("1200.00");
        cmbNivel.setSelectedIndex(0);
        chkFamiliar.setSelected(false);
        lblMeritoEstado.setText("—");
        lblMeritoDescuento.setText("—");
        lblMeritoValor.setText("—");
        lblResumenOriginal.setText("—");
        lblResumenDescuento.setText("—");
        lblResumenFinal.setText("—");
    }

    private void volverMenu() {
        ventanaPadre.setVisible(true);
        dispose();
    }

    // ── Utilidades UI ────────────────────────────────────────────────
    private JPanel crearPanelSeccion(String titulo) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(crearBordeTitulo(titulo));
        return p;
    }

    private Border crearBordeTitulo(String titulo) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 190, 210)),
            titulo,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(30, 60, 114));
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc,
                              int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.35;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(campo, gbc);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }

    private JLabel bold(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}
