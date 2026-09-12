package proyectoo2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VisorImagenes extends JInternalFrame {

    private ListaEnlazada imagenes;
    private int posicion;
    private JLabel lienzo;
    private JLabel titulo;
    private String carpeta;

    public VisorImagenes(String carpeta) {
        super("Visor de imágenes", true, true, true, true);

        this.carpeta = carpeta;
        imagenes = new ListaEnlazada();
        posicion = 0;

        setSize(620, 520);
        setLocation(140, 40);

        lienzo = new JLabel("Cargando imágenes...", SwingConstants.CENTER);
        lienzo.setForeground(Estilo.TEXTO_GRIS);
        lienzo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titulo = new JLabel("", SwingConstants.CENTER);
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Estilo.PANEL);
        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(lienzo, BorderLayout.CENTER);
        contenido.add(crearBarra(), BorderLayout.SOUTH);

        setContentPane(contenido);
        cargarEnHilo();
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        barra.setBackground(Estilo.PANEL_CLARO);

        BotonRedondo anterior = new BotonRedondo("Anterior", Estilo.PANEL);
        anterior.addActionListener(e -> mover(-1));

        BotonRedondo siguiente = new BotonRedondo("Siguiente", Estilo.ACENTO);
        siguiente.addActionListener(e -> mover(1));

        BotonRedondo cambiar = new BotonRedondo("Abrir carpeta", Estilo.PANEL);
        cambiar.addActionListener(e -> cambiarCarpeta());

        barra.add(anterior);
        barra.add(siguiente);
        barra.add(cambiar);

        return barra;
    }

    private void cargarEnHilo() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                final ListaEnlazada encontradas = buscarImagenes();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        imagenes = encontradas;
                        posicion = 0;
                        mostrar();
                    }
                });
            }
        });

        hilo.setDaemon(true);
        hilo.start();
    }

    private ListaEnlazada buscarImagenes() {
        ListaEnlazada lista = new ListaEnlazada();
        File[] archivos = new File(carpeta).listFiles();

        if (archivos == null) {
            return lista;
        }

        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].isFile() && OrganizadorArchivos.esImagen(archivos[i].getName())) {
                lista.agregar(archivos[i].getPath());
            }
        }

        return lista;
    }

    private void mover(int paso) {
        if (imagenes.estaVacia()) {
            return;
        }

        posicion = posicion + paso;

        if (posicion < 0) {
            posicion = imagenes.getTamano() - 1;
        }

        if (posicion >= imagenes.getTamano()) {
            posicion = 0;
        }

        mostrar();
    }

    private void mostrar() {
        if (imagenes.estaVacia()) {
            lienzo.setIcon(null);
            lienzo.setText("No hay imágenes en esta carpeta");
            titulo.setText("");
            return;
        }

        String ruta = (String) imagenes.obtener(posicion);
        ImageIcon icono = leerImagen(ruta);
        int ancho = 540;
        int alto = 360;

        if (icono.getIconWidth() > 0 && icono.getIconHeight() > 0) {
            alto = icono.getIconHeight() * ancho / icono.getIconWidth();

            if (alto > 360) {
                alto = 360;
                ancho = icono.getIconWidth() * alto / icono.getIconHeight();
            }
        }

        Image escalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

        lienzo.setText("");
        lienzo.setIcon(new ImageIcon(escalada));
        titulo.setText(new File(ruta).getName() + "   (" + (posicion + 1) + " de " + imagenes.getTamano() + ")");
    }

    private ImageIcon leerImagen(String ruta) {
        try {
            return new ImageIcon(javax.imageio.ImageIO.read(new File(ruta)));
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    private void cambiarCarpeta() {
        JFileChooser selector = new JFileChooser(carpeta);
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            carpeta = selector.getSelectedFile().getPath();
            cargarEnHilo();
        }
    }
}
