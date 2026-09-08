package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class PanelInbox extends PanelInsta {

    private JPanel contactos;
    private JPanel chat;
    private JScrollPane scrollChat;
    private JTextField campo;
    private JLabel titulo;
    private JPanel galeria;
    private JPanel personas;
    private JScrollPane scrollPersonas;
    private JScrollPane scrollGaleria;
    private String contactoActual;

    public PanelInbox(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        contactos = new JPanel();
        contactos.setLayout(new BoxLayout(contactos, BoxLayout.Y_AXIS));
        contactos.setBackground(Estilo.FONDO);

        JScrollPane scrollContactos = new JScrollPane(contactos);
        scrollContactos.setBorder(null);
        scrollContactos.setPreferredSize(new Dimension(220, 0));
        scrollContactos.getViewport().setBackground(Estilo.FONDO);

        chat = new JPanel();
        chat.setLayout(new BoxLayout(chat, BoxLayout.Y_AXIS));
        chat.setBackground(Estilo.FONDO);

        scrollChat = new JScrollPane(chat);
        scrollChat.setBorder(null);
        scrollChat.getViewport().setBackground(Estilo.FONDO);
        scrollChat.getVerticalScrollBar().setUnitIncrement(16);

        titulo = new JLabel("Selecciona una conversación");
        titulo.setFont(Estilo.SUBTITULO);
        titulo.setForeground(Estilo.TEXTO);

        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        derecha.setBackground(Estilo.FONDO);
        derecha.add(crearEncabezado(), BorderLayout.NORTH);
        derecha.add(scrollChat, BorderLayout.CENTER);
        derecha.add(crearEnvio(), BorderLayout.SOUTH);

        add(crearIzquierda(scrollContactos), BorderLayout.WEST);
        add(derecha, BorderLayout.CENTER);
    }

    private JPanel crearIzquierda(JScrollPane scroll) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Estilo.FONDO);

        BotonRedondo nueva = new BotonRedondo("Nueva conversación", Estilo.ACENTO);
        nueva.addActionListener(e -> nuevaConversacion());

        personas = new JPanel();
        personas.setLayout(new BoxLayout(personas, BoxLayout.Y_AXIS));
        personas.setBackground(Estilo.PANEL);
        personas.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        scrollPersonas = new JScrollPane(personas);
        scrollPersonas.setBorder(null);
        scrollPersonas.setPreferredSize(new Dimension(220, 170));
        scrollPersonas.getViewport().setBackground(Estilo.PANEL);
        scrollPersonas.setVisible(false);

        JPanel arriba = new JPanel(new BorderLayout(6, 6));
        arriba.setBackground(Estilo.FONDO);
        arriba.add(nueva, BorderLayout.NORTH);
        arriba.add(scrollPersonas, BorderLayout.CENTER);

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilo.FONDO);

        BotonRedondo borrar = new BotonRedondo("Eliminar conversación", Estilo.ROJO);
        borrar.addActionListener(e -> eliminarConversacion());

        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(borrar, BorderLayout.EAST);

        return encabezado;
    }

    private JPanel crearEnvio() {
        JPanel envio = new JPanel(new BorderLayout(6, 6));
        envio.setBackground(Estilo.FONDO);

        galeria = new JPanel(new GridLayout(0, 6, 6, 6));
        galeria.setBackground(Estilo.PANEL);
        galeria.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        scrollGaleria = new JScrollPane(galeria);
        scrollGaleria.setBorder(null);
        scrollGaleria.setPreferredSize(new Dimension(0, 130));
        scrollGaleria.getViewport().setBackground(Estilo.PANEL);
        scrollGaleria.setVisible(false);

        campo = new JTextField();
        Estilo.darEstiloCampo(campo);
        campo.addActionListener(e -> enviarTexto());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setBackground(Estilo.FONDO);

        BotonRedondo sticker = new BotonRedondo("Sticker", Estilo.PANEL_CLARO);
        sticker.addActionListener(e -> abrirGaleria());

        BotonRedondo enviar = new BotonRedondo("Enviar", Estilo.ACENTO2);
        enviar.addActionListener(e -> enviarTexto());

        botones.add(sticker);
        botones.add(enviar);

        envio.add(campo, BorderLayout.CENTER);
        envio.add(botones, BorderLayout.EAST);

        JPanel abajo = new JPanel(new BorderLayout(6, 6));
        abajo.setBackground(Estilo.FONDO);
        abajo.add(scrollGaleria, BorderLayout.NORTH);
        abajo.add(envio, BorderLayout.CENTER);

        return abajo;
    }

    private void nuevaConversacion() {
        if (scrollPersonas.isVisible()) {
            cerrarPersonas();
            return;
        }

        personas.removeAll();

        try {
            String yo = ventana.getUsuario().getUsername();
            ListaEnlazada usuarios = ArchivoUsuarios.listar();
            int encontrados = 0;

            for (int i = 0; i < usuarios.getTamano(); i++) {
                Usuario usuario = (Usuario) usuarios.obtener(i);

                if (usuario.estaActiva() && !usuario.getUsername().equalsIgnoreCase(yo)) {
                    personas.add(crearBotonPersona(usuario.getUsername()));
                    personas.add(Box.createVerticalStrut(4));
                    encontrados++;
                }
            }

            if (encontrados == 0) {
                personas.add(Estilo.crearEtiqueta("No hay otras cuentas todavía"));
            }
        } catch (ArchivoCorruptoException e) {
            personas.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        scrollPersonas.setVisible(true);
        personas.revalidate();
        personas.repaint();
        revalidate();
        repaint();
    }

    private void cerrarPersonas() {
        scrollPersonas.setVisible(false);
        revalidate();
        repaint();
    }

    private BotonRedondo crearBotonPersona(String username) {
        BotonRedondo boton = new BotonRedondo("@" + username, Estilo.PANEL_CLARO);

        boton.compactar();
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        boton.addActionListener(e -> {
            cerrarPersonas();
            abrirChat(username);
        });

        return boton;
    }

    public void abrirChat(String username) {
        contactoActual = username;
        titulo.setText("Conversación con @" + username);

        try {
            ArchivoMensajes.marcarLeidos(ventana.getUsuario().getUsername(), username);
        } catch (ArchivoCorruptoException e) {
            System.out.println(e.getMessage());
        }

        mostrarChat();
        cargarContactos();
    }

    private void mostrarChat() {
        chat.removeAll();

        if (contactoActual == null) {
            chat.revalidate();
            chat.repaint();
            return;
        }

        try {
            String yo = ventana.getUsuario().getUsername();
            ListaEnlazada mensajes = ArchivoMensajes.conversacion(yo, contactoActual);

            for (int i = 0; i < mensajes.getTamano(); i++) {
                chat.add(crearBurbuja((Mensaje) mensajes.obtener(i), yo));
                chat.add(Box.createVerticalStrut(6));
            }
        } catch (ArchivoCorruptoException e) {
            chat.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        chat.revalidate();
        chat.repaint();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollChat.getVerticalScrollBar().setValue(scrollChat.getVerticalScrollBar().getMaximum());
            }
        });
    }

    private JPanel crearBurbuja(Mensaje mensaje, String yo) {
        boolean mio = mensaje.getEmisor().equalsIgnoreCase(yo);

        PanelRedondo burbuja = new PanelRedondo(Estilo.PANEL);

        if (mio) {
            burbuja.setColorFondo(Estilo.ACENTO);
        }

        burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
        burbuja.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        burbuja.setMaximumSize(new Dimension(420, 260));

        if (mensaje.esSticker()) {
            JLabel imagen = new JLabel();
            File archivo = new File(mensaje.getContenido());

            if (archivo.exists()) {
                Image escalada = new ImageIcon(archivo.getPath()).getImage()
                        .getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                imagen.setIcon(new ImageIcon(escalada));
            } else {
                imagen.setText("[Sticker]");
                imagen.setForeground(Estilo.TEXTO);
            }

            burbuja.add(imagen);
        } else {
            JLabel texto = new JLabel("<html><div style='width:300px'>" + mensaje.getContenido() + "</div></html>");
            texto.setForeground(Estilo.TEXTO);
            texto.setFont(Estilo.NORMAL);
            burbuja.add(texto);
        }

        JLabel pie = new JLabel(mensaje.getEmisor() + " - " + mensaje.obtenerFechaTexto());
        pie.setFont(Estilo.PEQUENA);
        pie.setForeground(new java.awt.Color(210, 210, 230));
        burbuja.add(pie);

        int alineacion = FlowLayout.LEFT;

        if (mio) {
            alineacion = FlowLayout.RIGHT;
        }

        JPanel fila = new JPanel(new FlowLayout(alineacion, 4, 2));
        fila.setBackground(Estilo.FONDO);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(burbuja);

        return fila;
    }

    private void enviarTexto() {
        String texto = campo.getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        enviar(texto, TipoMensaje.TEXTO);
        campo.setText("");
    }

    private void enviar(String contenido, TipoMensaje tipo) {
        if (contactoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero abre una conversación");
            return;
        }

        String yo = ventana.getUsuario().getUsername();

        if (contenido.length() > 300) {
            JOptionPane.showMessageDialog(this, "El mensaje no puede pasar de 300 caracteres");
            return;
        }

        ClienteInsta cliente = ventana.getCliente();

        if (cliente != null && cliente.estaConectado()) {
            cliente.enviarMensaje(yo, contactoActual, tipo, contenido);
            refrescarDespuesDeEnviar();
            return;
        }

        try {
            ArchivoMensajes.guardar(new Mensaje(yo, contactoActual, contenido, tipo));
            mostrarChat();
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void refrescarDespuesDeEnviar() {
        javax.swing.Timer temporizador = new javax.swing.Timer(400, e -> {
            mostrarChat();
            cargarContactos();
        });

        temporizador.setRepeats(false);
        temporizador.start();
    }

    private void abrirGaleria() {
        if (contactoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero abre una conversación");
            return;
        }

        if (scrollGaleria.isVisible()) {
            cerrarGaleria();
            return;
        }

        galeria.removeAll();

        try {
            ListaEnlazada stickers = ArchivoStickers.listar(ventana.getUsuario().getUsername());

            for (int i = 0; i < stickers.getTamano(); i++) {
                Sticker sticker = (Sticker) stickers.obtener(i);
                galeria.add(crearBotonSticker(sticker));
            }
        } catch (ArchivoCorruptoException e) {
            galeria.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        BotonRedondo importar = new BotonRedondo("Importar", Estilo.VERDE);
        importar.addActionListener(e -> importarSticker());
        galeria.add(importar);

        scrollGaleria.setVisible(true);
        galeria.revalidate();
        galeria.repaint();
        revalidate();
        repaint();
    }

    private void cerrarGaleria() {
        scrollGaleria.setVisible(false);
        revalidate();
        repaint();
    }

    private javax.swing.JButton crearBotonSticker(Sticker sticker) {
        javax.swing.JButton boton = new javax.swing.JButton();
        File archivo = new File(sticker.getRuta());

        if (archivo.exists()) {
            Image escalada = new ImageIcon(sticker.getRuta()).getImage()
                    .getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(escalada));
        } else {
            boton.setText(sticker.getNombre());
        }

        boton.setBackground(Estilo.PANEL_CLARO);
        boton.setToolTipText(sticker.getNombre());
        boton.addActionListener(e -> {
            cerrarGaleria();
            enviar(sticker.getRuta(), TipoMensaje.STICKER);
        });

        return boton;
    }

    private void importarSticker() {
        JFileChooser selector = new JFileChooser();

        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File elegido = selector.getSelectedFile();

        try {
            String usuario = ventana.getUsuario().getUsername();
            String destino = SistemaArchivos.carpetaInsta(usuario) + File.separator + "stickers_personales";
            File copia = new File(destino, elegido.getName());

            Sticker sticker = new Sticker(elegido.getName(), copia.getPath(), true);

            copiar(elegido, copia);
            ArchivoStickers.agregar(usuario, sticker);

            JOptionPane.showMessageDialog(this, "Sticker agregado a tu galeria");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void copiar(File origen, File destino) {
        try (FileInputStream entrada = new FileInputStream(origen);
                FileOutputStream salida = new FileOutputStream(destino)) {

            byte[] datos = new byte[4096];
            int leidos = entrada.read(datos);

            while (leidos > 0) {
                salida.write(datos, 0, leidos);
                leidos = entrada.read(datos);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo copiar el sticker");
        }
    }

    private void eliminarConversacion() {
        if (contactoActual == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this, "¿Eliminar toda la conversación?", "Eliminar",
                JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ArchivoMensajes.eliminarConversacion(ventana.getUsuario().getUsername(), contactoActual);
            contactoActual = null;
            titulo.setText("Selecciona una conversación");
            mostrarChat();
            cargarContactos();
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void cargarContactos() {
        contactos.removeAll();

        try {
            String yo = ventana.getUsuario().getUsername();
            ListaEnlazada lista = ArchivoMensajes.contactos(yo);
            ListaEnlazada siguiendo = ArchivoSeguidores.listarFollowing(yo);

            for (int i = 0; i < siguiendo.getTamano(); i++) {
                String otro = (String) siguiendo.obtener(i);

                if (!lista.contiene(otro)) {
                    lista.agregar(otro);
                }
            }

            if (lista.estaVacia()) {
                contactos.add(Estilo.crearEtiqueta("Sin conversaciónes"));
            }

            for (int i = 0; i < lista.getTamano(); i++) {
                String otro = (String) lista.obtener(i);
                contactos.add(crearBotonContacto(otro));
                contactos.add(Box.createVerticalStrut(6));
            }
        } catch (ArchivoCorruptoException e) {
            contactos.add(Estilo.crearEtiqueta(e.getMessage()));
        }

        contactos.revalidate();
        contactos.repaint();
    }

    private BotonRedondo crearBotonContacto(String otro) throws ArchivoCorruptoException {
        String texto = "@" + otro;
        int noLeidos = contarNoLeidosDe(otro);

        if (noLeidos > 0) {
            texto = texto + "  (" + noLeidos + ")";
        }

        BotonRedondo boton = new BotonRedondo(texto, Estilo.PANEL);

        if (otro.equalsIgnoreCase(contactoActual)) {
            boton.setColorFondo(Estilo.ACENTO);
        }

        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boton.addActionListener(e -> abrirChat(otro));

        return boton;
    }

    private int contarNoLeidosDe(String otro) throws ArchivoCorruptoException {
        String yo = ventana.getUsuario().getUsername();
        ListaEnlazada mensajes = ArchivoMensajes.conversacion(yo, otro);
        int cantidad = 0;

        for (int i = 0; i < mensajes.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) mensajes.obtener(i);

            if (mensaje.esParaUsuario(yo) && !mensaje.estaLeido()) {
                cantidad++;
            }
        }

        return cantidad;
    }

    public void llegoMensaje(String emisor, String receptor) {
        String yo = ventana.getUsuario().getUsername();

        if (contactoActual != null
                && (emisor.equalsIgnoreCase(contactoActual) || receptor.equalsIgnoreCase(contactoActual))) {

            if (emisor.equalsIgnoreCase(contactoActual)) {
                try {
                    ArchivoMensajes.marcarLeidos(yo, contactoActual);
                } catch (ArchivoCorruptoException e) {
                    System.out.println(e.getMessage());
                }
            }

            mostrarChat();
        }

        cargarContactos();
    }

    @Override
    public void recargar() {
        cargarContactos();
        mostrarChat();
    }
}
