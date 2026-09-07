package proyectoo2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PanelRedondo extends JPanel {

    private Color colorFondo;

    public PanelRedondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        setOpaque(false);
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g2.dispose();

        super.paintComponent(g);
    }
}
