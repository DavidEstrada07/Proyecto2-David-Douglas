package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class PanelBuscarPerfil extends PanelInsta {

    private JTextField campo;
    private JPanel resultados;
    private JPanel detalle;
    private String perfilAbierto;

    public PanelBuscarPerfil(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        campo = new JTextField();
        Estilo.darEstiloCampo(campo);
        campo.addActionListener(e -> buscar());

        BotonRedondo boton = new BotonRedondo("Buscar", Estilo.ACENTO);
        boton.addActionListener(e -> buscar());

        JPanel arriba = new JPanel(new BorderLayout(8, 8));
        arriba.setOpaque(false);
        arriba.add(Estilo.crearTitulo("Buscar profile"), BorderLayout.NORTH);
        arriba.add(campo, BorderLayout.CENTER);
        arriba.add(boton, BorderLayout.EAST);

        resultados = new JPanel();
        resultados.setLayout(new BoxLayout(resultados, BoxLayout.Y_AXIS));
        resultados.setBackground(Estilo.FONDO);

        JScrollPane scrollResultados = new JScrollPane(resultados);
        scrollResultados.setBorder(null);
        scrollResultados.setPreferredSize(new Dimension(300, 0));
        scrollResultados.getViewport().setBackground(Estilo.FONDO);

        detalle = new JPanel();
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));
        detalle.setBackground(Estilo.FONDO);

        JScrollPane scrollDetalle = new JScrollPane(detalle);
        scrollDetalle.setBorder(null);
        scrollDetalle.getViewport().setBackground(Estilo.FONDO);

        add(arriba, BorderLayout.NORTH);
        add(scrollResultados, BorderLayout.WEST);
        add(scrollDetalle, BorderLayout.CENTER);
    }

    private void buscar() {
        resultados.removeAll();
        String texto = campo.getText().trim();

        try {
            ListaEnlazada encontrados = ArchivoUsuarios.buscarPorTexto(texto);
            String yo = ventana.getUsuario().getUsername();

            if (encontrados.estaVacia()) {
                resultados.add(Estilo.crearEtiqueta("Sin resultados"));
            }

            for (int i = 0; i < encontrados.getTamano(); i++) {
                Usuario usuario = (Usuario) encontrados.obtener(i);

                if (usuario.getUsername().equalsIgnoreCase(yo)) {
                    continue;
                }

                resultados.add(crearFila(usuario, yo));
                resultados.add(Box.createVerticalStrut(6));
            }
        } catch (ArchivoCorruptoException e) {
            resultados.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        resultados.revalidate();
        resultados.repaint();
    }

    private JPanel crearFila(Usuario usuario, String yo) throws ArchivoCorruptoException {
        boolean siguiendo = ArchivoSeguidores.sigue(yo, usuario.getUsername());
        String estado = "No lo sigues";

        if (siguiendo) {
            estado = "Lo sigo";
        }

        PanelRedondo fila = new PanelRedondo(Estilo.PANEL);
        fila.setLayout(new BorderLayout(8, 8));
        fila.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel texto = new JLabel(usuario.getUsername().toUpperCase() + " - " + estado);
        texto.setForeground(Estilo.TEXTO);
        texto.setFont(Estilo.NORMAL);

        BotonRedondo entrar = new BotonRedondo("Entrar", Estilo.ACENTO);
        entrar.addActionListener(e -> abrirPerfil(usuario.getUsername()));

        fila.add(texto, BorderLayout.CENTER);
        fila.add(entrar, BorderLayout.EAST);

        return fila;
    }

    private void abrirPerfil(String username) {
        perfilAbierto = username;
        detalle.removeAll();

        try {
            Usuario usuario = ArchivoUsuarios.buscar(username);

            if (usuario == null || !usuario.estaActiva()) {
                detalle.add(Estilo.crearEtiqueta("Esta cuenta no esta disponible"));
                detalle.revalidate();
                detalle.repaint();
                return;
            }

            String yo = ventana.getUsuario().getUsername();
            boolean siguiendo = ArchivoSeguidores.sigue(yo, username);

            int followers = ArchivoSeguidores.listarFollowers(username).getTamano();
            int following = ArchivoSeguidores.listarFollowing(username).getTamano();

            JLabel datos = new JLabel("<html><span style='font-size:15pt'><b>" + usuario.getNombreCompleto()
                    + "</b></span><br>@" + usuario.getUsername() + "<br><br>"
                    + "Genero: " + usuario.getGenero() + " &nbsp; Edad: " + usuario.getEdad() + "<br>"
                    + "Ingreso: " + usuario.obtenerFechaRegistroTexto() + "<br><br>"
                    + "<b>" + followers + "</b> followers &nbsp; <b>" + following + "</b> following</html>");
            datos.setForeground(Estilo.TEXTO);
            datos.setAlignmentX(0f);

            BotonRedondo seguir = new BotonRedondo("Seguir", Estilo.VERDE);

            if (siguiendo) {
                seguir = new BotonRedondo("Dejar de seguir", Estilo.ROJO);
            }

            final boolean sigueAhora = siguiendo;
            seguir.setAlignmentX(0f);
            seguir.addActionListener(e -> cambiarSeguimiento(username, sigueAhora));

            BotonRedondo mensaje = new BotonRedondo("Enviar mensaje", Estilo.ACENTO);
            mensaje.setAlignmentX(0f);
            mensaje.addActionListener(e -> ventana.abrirChatCon(username));

            detalle.add(datos);
            detalle.add(Box.createVerticalStrut(10));
            detalle.add(seguir);
            detalle.add(Box.createVerticalStrut(6));
            detalle.add(mensaje);
            detalle.add(Box.createVerticalStrut(14));

            JLabel titulo = Estilo.crearEtiqueta("Sus publicaciónes");
            titulo.setAlignmentX(0f);
            detalle.add(titulo);
            detalle.add(Box.createVerticalStrut(6));

            ListaEnlazada publicaciones = ArchivoPublicaciones.listarOrdenadas(username);

            for (int i = 0; i < publicaciones.getTamano(); i++) {
                TarjetaPublicacion tarjeta = new TarjetaPublicacion((Publicacion) publicaciones.obtener(i));
                tarjeta.setAlignmentX(0f);
                detalle.add(tarjeta);
                detalle.add(Box.createVerticalStrut(8));
            }
        } catch (ArchivoCorruptoException e) {
            detalle.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        detalle.revalidate();
        detalle.repaint();
    }

    private void cambiarSeguimiento(String username, boolean siguiendo) {
        try {
            String yo = ventana.getUsuario().getUsername();

            if (siguiendo) {
                int opcion = JOptionPane.showConfirmDialog(this, "Dejar de seguir a " + username + "?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (opcion != JOptionPane.YES_OPTION) {
                    return;
                }

                ArchivoSeguidores.dejarDeSeguir(yo, username);
            } else {
                ArchivoSeguidores.seguir(yo, username);
            }

            buscar();
            abrirPerfil(username);
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void recargar() {
        if (perfilAbierto != null) {
            abrirPerfil(perfilAbierto);
        }

        buscar();
    }
}
