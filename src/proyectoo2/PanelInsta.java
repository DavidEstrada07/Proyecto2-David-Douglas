package proyectoo2;

import javax.swing.JPanel;

public abstract class PanelInsta extends JPanel {

    protected VentanaInstaPlus ventana;

    public PanelInsta(VentanaInstaPlus ventana) {
        this.ventana = ventana;
        setBackground(Estilo.FONDO);
    }

    public abstract void recargar();
}
