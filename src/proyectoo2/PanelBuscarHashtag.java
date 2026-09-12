package proyectoo2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class PanelBuscarHashtag extends PanelInsta {

    private JTextField campo;
    private JPanel lista;

    public PanelBuscarHashtag(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        campo = new JTextField();
        Estilo.darEstiloCampo(campo);
        campo.addActionListener(e -> buscar());

        BotonRedondo boton = new BotonRedondo("Buscar", Estilo.ACENTO);
        boton.addActionListener(e -> buscar());

        JPanel arriba = new JPanel(new BorderLayout(8, 8));
        arriba.setOpaque(false);
        arriba.add(Estilo.crearTitulo("Buscar hashtag"), BorderLayout.NORTH);
        arriba.add(campo, BorderLayout.CENTER);
        arriba.add(boton, BorderLayout.EAST);

        lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Estilo.FONDO);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);

        add(arriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void buscar() {
        String palabra = campo.getText().trim().replace("#", "");
        lista.removeAll();

        if (palabra.isEmpty()) {
            lista.revalidate();
            lista.repaint();
            return;
        }

        try {
            ListaEnlazada resultado = ArchivoPublicaciones.buscarPorHashtag(palabra, ventana.getUsuario().getUsername());

            if (resultado.estaVacia()) {
                lista.add(Estilo.crearEtiqueta("No hay publicaciónes con #" + palabra));
            }

            for (int i = 0; i < resultado.getTamano(); i++) {
                lista.add(new TarjetaPublicacion((Publicacion) resultado.obtener(i)));
                lista.add(Box.createVerticalStrut(10));
            }
        } catch (ArchivoCorruptoException e) {
            lista.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        lista.revalidate();
        lista.repaint();
    }

    @Override
    public void recargar() {
        campo.setText("");
        lista.removeAll();
        lista.revalidate();
        lista.repaint();
    }
}
