package proyectoo2;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PanelInteracciones extends PanelInsta {

    private JPanel lista;

    public PanelInteracciones(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Estilo.FONDO);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);

        add(Estilo.crearTitulo("Interacciones"), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void recargar() {
        lista.removeAll();

        try {
            ListaEnlazada menciones = ArchivoPublicaciones.buscarMenciones(ventana.getUsuario().getUsername());

            if (menciones.estaVacia()) {
                lista.add(Estilo.crearEtiqueta("Nadie te ha mencionado todavia"));
            }

            for (int i = 0; i < menciones.getTamano(); i++) {
                lista.add(new TarjetaPublicacion((Publicacion) menciones.obtener(i)));
                lista.add(Box.createVerticalStrut(10));
            }
        } catch (ArchivoCorruptoException e) {
            lista.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        lista.revalidate();
        lista.repaint();
    }
}
