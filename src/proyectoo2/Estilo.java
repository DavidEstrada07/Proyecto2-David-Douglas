package proyectoo2;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Estilo {

    public static final Color FONDO = new Color(17, 17, 24);
    public static final Color PANEL = new Color(28, 28, 38);
    public static final Color PANEL_CLARO = new Color(40, 40, 54);
    public static final Color BORDE = new Color(58, 58, 76);
    public static final Color TEXTO = new Color(236, 236, 245);
    public static final Color TEXTO_GRIS = new Color(150, 150, 172);
    public static final Color ACENTO = new Color(124, 92, 255);
    public static final Color ACENTO2 = new Color(236, 72, 153);
    public static final Color VERDE = new Color(52, 199, 123);
    public static final Color ROJO = new Color(239, 83, 80);

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 15);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font PEQUENA = new Font("SansSerif", Font.PLAIN, 12);

    public static JLabel crearTitulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TITULO);
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    public static JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(NORMAL);
        etiqueta.setForeground(TEXTO_GRIS);
        return etiqueta;
    }

    public static void darEstiloCampo(JTextField campo) {
        campo.setBackground(PANEL_CLARO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setFont(NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    public static void darEstiloPassword(JPasswordField campo) {
        darEstiloCampo(campo);
    }
}
