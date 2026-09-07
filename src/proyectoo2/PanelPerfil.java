package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class PanelPerfil extends PanelInsta {

    private JLabel foto;
    private JLabel datos;
    private JPanel grid;

    public PanelPerfil(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        foto = new JLabel("", SwingConstants.CENTER);
        foto.setPreferredSize(new Dimension(150, 150));

        datos = new JLabel();
        datos.setFont(Estilo.NORMAL);
        datos.setForeground(Estilo.TEXTO);

        PanelRedondo encabezado = new PanelRedondo(Estilo.PANEL);
        encabezado.setLayout(new BorderLayout(16, 16));
        encabezado.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        encabezado.setPreferredSize(new Dimension(0, 190));
        encabezado.add(foto, BorderLayout.WEST);
        encabezado.add(datos, BorderLayout.CENTER);

        grid = new JPanel(new GridLayout(0, 3, 8, 8));
        grid.setBackground(Estilo.FONDO);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);

        add(encabezado, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void recargar() {
        Usuario usuario = ventana.getUsuario();

        mostrarFoto(usuario.getFotoPerfil());
        mostrarDatos(usuario);
        mostrarPublicaciones(usuario.getUsername());
    }

    private void mostrarFoto(String ruta) {
        String camino = ruta;

        if (camino == null) {
            camino = "";
        }

        File archivo = new File(camino);

        if (archivo.exists() && archivo.isFile()) {
            Image escalada = new ImageIcon(archivo.getPath()).getImage()
                    .getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            foto.setIcon(new ImageIcon(escalada));
            foto.setText("");
        } else {
            foto.setIcon(null);
            foto.setText("<html><div style='font-size:44px'>&#9787;</div></html>");
            foto.setForeground(Estilo.ACENTO);
        }
    }

    private void mostrarDatos(Usuario usuario) {
        int followers = 0;
        int following = 0;
        int publicaciones = 0;

        try {
            followers = ArchivoSeguidores.listarFollowers(usuario.getUsername()).getTamano();
            following = ArchivoSeguidores.listarFollowing(usuario.getUsername()).getTamano();
            publicaciones = ArchivoPublicaciones.listar(usuario.getUsername()).getTamano();
        } catch (ArchivoCorruptoException e) {
            System.out.println(e.getMessage());
        }

        String estado = "Activa";

        if (!usuario.estaActiva()) {
            estado = "Inactiva";
        }

        datos.setText("<html><span style='font-size:16pt'><b>" + usuario.getNombreCompleto() + "</b></span><br>"
                + "@" + usuario.getUsername() + "<br><br>"
                + "Edad: " + usuario.getEdad() + " &nbsp; Genero: " + usuario.getGenero() + "<br>"
                + "Registro: " + usuario.obtenerFechaRegistroTexto() + "<br>"
                + "Cuenta: " + estado + "<br><br>"
                + "<b>" + publicaciones + "</b> publicaciones &nbsp; "
                + "<b>" + followers + "</b> followers &nbsp; "
                + "<b>" + following + "</b> following</html>");
    }

    private void mostrarPublicaciones(String username) {
        grid.removeAll();

        try {
            ListaEnlazada publicaciones = ArchivoPublicaciones.listarOrdenadas(username);

            for (int i = 0; i < publicaciones.getTamano(); i++) {
                grid.add(crearCelda((Publicacion) publicaciones.obtener(i)));
            }
        } catch (ArchivoCorruptoException e) {
            grid.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        grid.revalidate();
        grid.repaint();
    }

    private JPanel crearCelda(Publicacion publicacion) {
        PanelRedondo celda = new PanelRedondo(Estilo.PANEL);
        celda.setLayout(new BorderLayout());
        celda.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        celda.setPreferredSize(new Dimension(180, 180));

        JLabel contenido = new JLabel("<html><div style='width:150px'>" + publicacion.getContenido()
                + "</div></html>");
        contenido.setForeground(Estilo.TEXTO);
        contenido.setFont(Estilo.PEQUENA);

        if (publicacion.tieneImagen() && new File(publicacion.getRutaImagen()).exists()) {
            Image escalada = new ImageIcon(publicacion.getRutaImagen()).getImage()
                    .getScaledInstance(160, 120, Image.SCALE_SMOOTH);
            JLabel imagen = new JLabel(new ImageIcon(escalada));
            celda.add(imagen, BorderLayout.CENTER);
            celda.add(contenido, BorderLayout.SOUTH);
        } else {
            celda.add(contenido, BorderLayout.CENTER);
        }

        return celda;
    }
}
