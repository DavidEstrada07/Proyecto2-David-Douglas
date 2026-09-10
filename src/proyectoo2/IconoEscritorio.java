package proyectoo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class IconoEscritorio extends JPanel {

    private String nombre;
    private String tipo;
    private Color color;
    private Escritorio escritorio;
    private boolean encima;

    public IconoEscritorio(String nombre, String tipo, Color color, Escritorio escritorio) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.color = color;
        this.escritorio = escritorio;

        setOpaque(false);
        setPreferredSize(new Dimension(104, 112));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText("Doble clic para abrir " + nombre);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    IconoEscritorio.this.escritorio.abrirHerramienta(IconoEscritorio.this.tipo);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (encima) {
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
        }

        int x = (getWidth() - 56) / 2;
        int y = 12;

        g2.setColor(color);
        g2.fillRoundRect(x, y, 56, 56, 16, 16);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        dibujarSimbolo(g2, x, y);

        g2.setFont(Estilo.PEQUENA);
        g2.setColor(Estilo.TEXTO);

        int ancho = g2.getFontMetrics().stringWidth(nombre);
        g2.drawString(nombre, (getWidth() - ancho) / 2, y + 76);
    }

    private void dibujarSimbolo(Graphics2D g2, int x, int y) {
        if (tipo.equals("Explorador")) {
            g2.fillRoundRect(x + 12, y + 20, 32, 22, 4, 4);
            g2.fillRect(x + 12, y + 15, 14, 6);
        } else if (tipo.equals("Editor")) {
            g2.fillRoundRect(x + 16, y + 13, 24, 30, 4, 4);
            g2.setColor(color);
            g2.drawLine(x + 21, y + 21, x + 35, y + 21);
            g2.drawLine(x + 21, y + 28, x + 35, y + 28);
            g2.drawLine(x + 21, y + 35, x + 30, y + 35);
        } else if (tipo.equals("Imagenes")) {
            g2.drawRoundRect(x + 13, y + 16, 30, 24, 4, 4);
            g2.fillOval(x + 19, y + 21, 7, 7);
            g2.fillPolygon(new int[]{x + 16, x + 27, x + 38}, new int[]{y + 38, y + 26, y + 38}, 3);
        } else if (tipo.equals("Consola")) {
            g2.drawRoundRect(x + 12, y + 15, 32, 26, 4, 4);
            g2.drawLine(x + 18, y + 24, x + 24, y + 28);
            g2.drawLine(x + 24, y + 28, x + 18, y + 32);
            g2.drawLine(x + 28, y + 33, x + 37, y + 33);
        } else if (tipo.equals("Musica")) {
            g2.fillOval(x + 15, y + 33, 9, 8);
            g2.fillOval(x + 32, y + 29, 9, 8);
            g2.drawLine(x + 23, y + 36, x + 23, y + 16);
            g2.drawLine(x + 40, y + 32, x + 40, y + 13);
            g2.drawLine(x + 23, y + 16, x + 40, y + 13);
        } else if (tipo.equals("INSTA+")) {
            g2.drawRoundRect(x + 13, y + 13, 30, 30, 9, 9);
            g2.drawOval(x + 21, y + 21, 14, 14);
            g2.fillOval(x + 36, y + 17, 4, 4);
        } else if (tipo.equals("Usuarios")) {
            g2.fillOval(x + 21, y + 14, 14, 14);
            g2.fillArc(x + 13, y + 29, 30, 26, 0, 180);
        }
    }
}
