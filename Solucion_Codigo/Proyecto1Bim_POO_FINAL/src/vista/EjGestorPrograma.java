package vista;

import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación.
 */
public class EjGestorPrograma {

    public static void main(String[] args) {
        // Aplicar Look & Feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
