package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelTimeline extends PanelInsta {

    private JPanel lista;
    private JTextArea escribir;
    private BotonRedondo botonTodos;
    private BotonRedondo botonSeguidos;
    private boolean verTodos;

    public PanelTimeline(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Estilo.FONDO);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        verTodos = true;

        JPanel arriba = new JPanel(new BorderLayout(8, 8));
        arriba.setOpaque(false);
        arriba.add(crearCaja(), BorderLayout.CENTER);
        arriba.add(crearFiltro(), BorderLayout.SOUTH);

        add(arriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearFiltro() {
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtro.setOpaque(false);

        botonTodos = new BotonRedondo("Todos", Estilo.ACENTO);
        botonTodos.compactar();
        botonTodos.addActionListener(e -> cambiarFiltro(true));

        botonSeguidos = new BotonRedondo("Solo los que sigo", Estilo.PANEL);
        botonSeguidos.compactar();
        botonSeguidos.addActionListener(e -> cambiarFiltro(false));

        filtro.add(Estilo.crearEtiqueta("Mostrar:"));
        filtro.add(botonTodos);
        filtro.add(botonSeguidos);

        return filtro;
    }

    private void cambiarFiltro(boolean todos) {
        verTodos = todos;

        if (todos) {
            botonTodos.setColorFondo(Estilo.ACENTO);
            botonSeguidos.setColorFondo(Estilo.PANEL);
        } else {
            botonTodos.setColorFondo(Estilo.PANEL);
            botonSeguidos.setColorFondo(Estilo.ACENTO);
        }

        recargar();
    }

    private JPanel crearCaja() {
        PanelRedondo caja = new PanelRedondo(Estilo.PANEL);
        caja.setLayout(new BorderLayout(8, 8));
        caja.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        caja.setPreferredSize(new Dimension(0, 130));

        escribir = new JTextArea(3, 20);
        escribir.setBackground(Estilo.PANEL_CLARO);
        escribir.setForeground(Estilo.TEXTO);
        escribir.setCaretColor(Estilo.TEXTO);
        escribir.setFont(Estilo.NORMAL);
        escribir.setLineWrap(true);
        escribir.setWrapStyleWord(true);
        escribir.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        abajo.setOpaque(false);

        BotonRedondo publicar = new BotonRedondo("Publicar", Estilo.ACENTO2);
        publicar.addActionListener(e -> publicar());

        abajo.add(Estilo.crearEtiqueta("Máximo 140 caracteres. Usa # para hashtags y @ para mencionar."));
        abajo.add(publicar);

        caja.add(new JScrollPane(escribir), BorderLayout.CENTER);
        caja.add(abajo, BorderLayout.SOUTH);

        return caja;
    }

    private void publicar() {
        String texto = escribir.getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        try {
            ArchivoPublicaciones.publicar(new Publicacion(ventana.getUsuario().getUsername(), texto));
            escribir.setText("");
            recargar();
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void recargar() {
        lista.removeAll();

        try {
            String yo = ventana.getUsuario().getUsername();
            ListaEnlazada publicaciones;

            if (verTodos) {
                publicaciones = ArchivoPublicaciones.armarDescubrir(yo);
            } else {
                publicaciones = ArchivoPublicaciones.armarTimeline(yo);
            }

            if (publicaciones.estaVacia()) {
                lista.add(Estilo.crearEtiqueta("Todavía no hay publicaciónes. Publica algo para empezar."));
            }

            for (int i = 0; i < publicaciones.getTamano(); i++) {
                lista.add(new TarjetaPublicacion((Publicacion) publicaciones.obtener(i)));
                lista.add(Box.createVerticalStrut(10));
            }
        } catch (ArchivoCorruptoException e) {
            lista.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        lista.revalidate();
        lista.repaint();
    }
}
