package proyectoo2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class BotonRedondo extends JButton {

    private Color colorFondo;
    private boolean encima;

    public BotonRedondo(String texto, Color colorFondo) {
        super(texto);

        this.colorFondo = colorFondo;
        encima = false;

        setForeground(Color.WHITE);
        setFont(Estilo.SUBTITULO);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        agregarEfecto();
    }

    private void agregarEfecto() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    public void compactar() {
        setFont(Estilo.NORMAL);
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = colorFondo;

        if (encima) {
            color = color.brighter();
        }

        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.dispose();

        super.paintComponent(g);
    }
}
