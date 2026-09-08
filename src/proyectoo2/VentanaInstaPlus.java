package proyectoo2;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class VentanaInstaPlus extends JInternalFrame implements EscuchaMensajes {

    private Usuario usuario;
    private ClienteInsta cliente;
    private HiloNotificaciones notificaciones;

    private CardLayout principal;
    private JPanel contenedorPrincipal;

    private CardLayout secciones;
    private JPanel contenedorSecciones;

    private PanelInstaLogin panelLogin;
    private PanelPerfil panelPerfil;
    private PanelCargar panelCargar;
    private PanelTimeline panelTimeline;
    private PanelInteracciones panelInteracciones;
    private PanelBuscarPerfil panelBuscarPerfil;
    private PanelBuscarHashtag panelBuscarHashtag;
    private PanelInbox panelInbox;
    private PanelEditarPerfil panelEditar;

    private JLabel etiquetaUsuario;
    private BotonRedondo botonInbox;

    public VentanaInstaPlus() {
        super("INSTA+", true, true, true, true);

        setSize(1000, 640);
        setLocation(20, 10);

        principal = new CardLayout();
        contenedorPrincipal = new JPanel(principal);
        contenedorPrincipal.setBackground(Estilo.FONDO);

        panelLogin = new PanelInstaLogin(this);

        contenedorPrincipal.add(panelLogin, "login");
        contenedorPrincipal.add(crearApp(), "app");

        setContentPane(contenedorPrincipal);
        principal.show(contenedorPrincipal, "login");

        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                salir();
            }
        });
    }

    private JPanel crearApp() {
        JPanel app = new JPanel(new BorderLayout());
        app.setBackground(Estilo.FONDO);

        panelPerfil = new PanelPerfil(this);
        panelCargar = new PanelCargar(this);
        panelTimeline = new PanelTimeline(this);
        panelInteracciones = new PanelInteracciones(this);
        panelBuscarPerfil = new PanelBuscarPerfil(this);
        panelBuscarHashtag = new PanelBuscarHashtag(this);
        panelInbox = new PanelInbox(this);
        panelEditar = new PanelEditarPerfil(this);

        secciones = new CardLayout();
        contenedorSecciones = new JPanel(secciones);
        contenedorSecciones.setBackground(Estilo.FONDO);

        contenedorSecciones.add(panelPerfil, "Perfil");
        contenedorSecciones.add(panelCargar, "Cargar");
        contenedorSecciones.add(panelTimeline, "Timeline");
        contenedorSecciones.add(panelInteracciones, "Interacciones");
        contenedorSecciones.add(panelBuscarPerfil, "BuscarPerfil");
        contenedorSecciones.add(panelBuscarHashtag, "BuscarHashtag");
        contenedorSecciones.add(panelInbox, "Inbox");
        contenedorSecciones.add(panelEditar, "Editar");

        app.add(crearMenu(), BorderLayout.WEST);
        app.add(contenedorSecciones, BorderLayout.CENTER);

        return app;
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Estilo.PANEL);
        menu.setPreferredSize(new Dimension(210, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        JLabel titulo = Estilo.crearTitulo("INSTA+");
        titulo.setForeground(Estilo.ACENTO2);
        titulo.setAlignmentX(0f);

        etiquetaUsuario = new JLabel();
        etiquetaUsuario.setFont(Estilo.PEQUENA);
        etiquetaUsuario.setForeground(Estilo.TEXTO_GRIS);
        etiquetaUsuario.setAlignmentX(0f);

        menu.add(titulo);
        menu.add(etiquetaUsuario);
        menu.add(Box.createVerticalStrut(18));

        menu.add(crearOpcion("Perfil", "Perfil"));
        menu.add(crearOpcion("Cargar imágenes", "Cargar"));
        menu.add(crearOpcion("Comentarios", "Timeline"));
        menu.add(crearOpcion("Interacciones", "Interacciones"));
        menu.add(crearOpcion("Buscar profile", "BuscarPerfil"));
        menu.add(crearOpcion("Buscar hashtag", "BuscarHashtag"));

        botonInbox = new BotonRedondo("Inbox", Estilo.PANEL_CLARO);
        botonInbox.setAlignmentX(0f);
        botonInbox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        botonInbox.addActionListener(e -> mostrarSeccion("Inbox"));

        menu.add(botonInbox);
        menu.add(Box.createVerticalStrut(6));
        menu.add(crearOpcion("Editar perfil", "Editar"));
        menu.add(Box.createVerticalGlue());

        BotonRedondo salir = new BotonRedondo("Cerrar sesión", Estilo.ROJO);
        salir.setAlignmentX(0f);
        salir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        salir.addActionListener(e -> cerrarSesion());

        menu.add(salir);

        return menu;
    }

    private BotonRedondo crearOpcion(String texto, String seccion) {
        BotonRedondo boton = new BotonRedondo(texto, Estilo.PANEL_CLARO);

        boton.setAlignmentX(0f);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boton.addActionListener(e -> mostrarSeccion(seccion));

        return boton;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public ClienteInsta getCliente() {
        return cliente;
    }

    public void entrar(Usuario usuario) {
        this.usuario = usuario;

        etiquetaUsuario.setText("@" + usuario.getUsername());
        cliente = new ClienteInsta(usuario.getUsername(), this);

        notificaciones = new HiloNotificaciones(this);
        Thread hilo = new Thread(notificaciones);
        hilo.setDaemon(true);
        hilo.start();

        principal.show(contenedorPrincipal, "app");
        mostrarSeccion("Timeline");
    }

    public final void mostrarSeccion(String seccion) {
        secciones.show(contenedorSecciones, seccion);

        PanelInsta panel = buscarPanel(seccion);

        if (panel != null) {
            panel.recargar();
        }
    }

    private PanelInsta buscarPanel(String seccion) {
        if (seccion.equals("Perfil")) {
            return panelPerfil;
        }

        if (seccion.equals("Cargar")) {
            return panelCargar;
        }

        if (seccion.equals("Timeline")) {
            return panelTimeline;
        }

        if (seccion.equals("Interacciones")) {
            return panelInteracciones;
        }

        if (seccion.equals("BuscarPerfil")) {
            return panelBuscarPerfil;
        }

        if (seccion.equals("BuscarHashtag")) {
            return panelBuscarHashtag;
        }

        if (seccion.equals("Inbox")) {
            return panelInbox;
        }

        if (seccion.equals("Editar")) {
            return panelEditar;
        }

        return null;
    }

    public void abrirChatCon(String username) {
        mostrarSeccion("Inbox");
        panelInbox.abrirChat(username);
    }

    public void mostrarNotificacion(int noLeidos) {
        if (noLeidos > 0) {
            botonInbox.setText("Inbox  (" + noLeidos + ")");
            botonInbox.setColorFondo(Estilo.ACENTO2);
        } else {
            botonInbox.setText("Inbox");
            botonInbox.setColorFondo(Estilo.PANEL_CLARO);
        }
    }

    public void refrescarInbox() {
        if (usuario == null) {
            return;
        }

        panelInbox.recargar();
    }

    @Override
    public void mensajeRecibido(String emisor, String receptor) {
        if (usuario == null) {
            return;
        }

        panelInbox.llegoMensaje(emisor, receptor);
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión en INSTA+?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        salir();
        usuario = null;
        panelLogin.recargar();
        principal.show(contenedorPrincipal, "login");
    }

    private void salir() {
        if (cliente != null) {
            cliente.desconectar();
            cliente = null;
        }

        if (notificaciones != null) {
            notificaciones.detener();
            notificaciones = null;
        }
    }
}
