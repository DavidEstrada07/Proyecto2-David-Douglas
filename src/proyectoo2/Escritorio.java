package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Escritorio extends JFrame {

    private UsuarioSistema usuario;
    private JDesktopPane escritorio;
    private JLabel etiquetaReloj;
    private JTextField campoBusqueda;

    public Escritorio(UsuarioSistema usuario) {
        this.usuario = usuario;

        setTitle("Mini-Windows - " + usuario.getUsername());
        setSize(1180, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        escritorio = new FondoEscritorio();
        escritorio.setBackground(Estilo.FONDO);

        crearIconos();

        add(escritorio, BorderLayout.CENTER);
        add(crearBarra(), BorderLayout.SOUTH);

        iniciarReloj();
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.PANEL);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        izquierda.setOpaque(false);

        BotonRedondo salir = new BotonRedondo("Cerrar sesión", Estilo.ROJO);
        salir.compactar();
        salir.addActionListener(e -> cerrarSesion());
        izquierda.add(salir);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        centro.setOpaque(false);

        campoBusqueda = new JTextField(26);
        campoBusqueda.setBackground(Estilo.PANEL_CLARO);
        campoBusqueda.setForeground(Estilo.TEXTO);
        campoBusqueda.setCaretColor(Estilo.TEXTO);
        campoBusqueda.setFont(Estilo.NORMAL);
        campoBusqueda.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        campoBusqueda.addActionListener(e -> buscarArchivos());

        BotonRedondo lupa = new BotonRedondo("Buscar", Estilo.ACENTO2);
        lupa.compactar();
        lupa.addActionListener(e -> buscarArchivos());

        centro.add(campoBusqueda);
        centro.add(lupa);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        derecha.setOpaque(false);

        etiquetaReloj = new JLabel(textoReloj());
        etiquetaReloj.setForeground(Estilo.TEXTO_GRIS);
        etiquetaReloj.setFont(Estilo.NORMAL);
        etiquetaReloj.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        derecha.add(etiquetaReloj);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(centro, BorderLayout.CENTER);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    private void crearIconos() {
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 16, 16));
        panel.setOpaque(false);

        panel.add(new IconoEscritorio("Música", "Musica", Estilo.ACENTO, this));
        panel.add(new IconoEscritorio("Archivos", "Explorador", new Color(232, 168, 56), this));
        panel.add(new IconoEscritorio("INSTA+", "INSTA+", Estilo.ACENTO2, this));
        panel.add(new IconoEscritorio("Editor", "Editor", new Color(70, 130, 200), this));
        panel.add(new IconoEscritorio("Imágenes", "Imagenes", Estilo.VERDE, this));
        panel.add(new IconoEscritorio("Consola", "Consola", new Color(80, 84, 100), this));

        if (usuario.esAdministrador()) {
            panel.add(new IconoEscritorio("Usuarios", "Usuarios", new Color(190, 90, 140), this));
        }

        java.awt.Dimension medida = panel.getPreferredSize();
        panel.setBounds(20, 20, medida.width, medida.height);

        escritorio.add(panel, Integer.valueOf(-1));
    }

    private void buscarArchivos() {
        String texto = campoBusqueda.getText().trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe el nombre de un archivo o carpeta");
            return;
        }

        mostrar(new BuscadorArchivos(carpetaInicial(), texto, this));
    }

    public void abrirArchivo(File archivo) {
        if (archivo.isDirectory()) {
            mostrar(new ExploradorArchivos(usuario, this));
            return;
        }

        String nombre = archivo.getName();

        if (OrganizadorArchivos.esImagen(nombre)) {
            mostrar(new VisorImagenes(archivo.getParent()));
        } else if (OrganizadorArchivos.esMusica(nombre)) {
            mostrar(new ReproductorMusica(archivo.getParent(), carpetaMusica()));
        } else {
            mostrar(new EditorTexto(archivo.getParent()));
        }
    }

    public void abrirHerramienta(String nombre) {
        if (nombre.equals("Explorador")) {
            mostrar(new ExploradorArchivos(usuario, this));
        } else if (nombre.equals("Editor")) {
            mostrar(new EditorTexto(carpetaInicial()));
        } else if (nombre.equals("Imagenes")) {
            mostrar(new VisorImagenes(carpetaInicial()));
        } else if (nombre.equals("Consola")) {
            mostrar(new ConsolaComandos(carpetaInicial()));
        } else if (nombre.equals("Musica")) {
            mostrar(new ReproductorMusica(carpetaMusica(), carpetaMusica()));
        } else if (nombre.equals("INSTA+")) {
            mostrar(new VentanaInstaPlus());
        } else if (nombre.equals("Usuarios")) {
            mostrar(new GestorUsuarios());
        }
    }

    public void mostrar(JInternalFrame ventana) {
        escritorio.add(ventana);
        ventana.setVisible(true);

        try {
            ventana.setSelected(true);
        } catch (Exception e) {
            System.out.println("No se pudo enfocar la ventana");
        }
    }

    public boolean esAdministrador() {
        return usuario.esAdministrador();
    }

    public String carpetaInicial() {
        if (usuario.esAdministrador()) {
            return SistemaArchivos.UNIDAD;
        }

        return SistemaArchivos.carpetaSistema(usuario.getUsername());
    }

    public String carpetaMusica() {
        return usuario.obtenerCarpetaRaiz().replace("Z:\\", "Z" + java.io.File.separator)
                + java.io.File.separator + "Musica";
    }

    private String textoReloj() {
        java.util.Calendar calendario = java.util.Calendar.getInstance();

        int hora = calendario.get(java.util.Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(java.util.Calendar.MINUTE);
        String minutoTexto = "" + minuto;

        if (minuto < 10) {
            minutoTexto = "0" + minuto;
        }

        return usuario.getUsername() + "   |   " + hora + ":" + minutoTexto;
    }

    private void iniciarReloj() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final String texto = textoReloj();

                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            etiquetaReloj.setText(texto);
                            etiquetaReloj.getParent().revalidate();
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        hilo.setDaemon(true);
        hilo.start();
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar la sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            new LoginSistema().setVisible(true);
            dispose();
        }
    }

    private class FondoEscritorio extends JDesktopPane {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            GradientPaint degradado = new GradientPaint(0, 0, new Color(24, 22, 42), getWidth(), getHeight(),
                    new Color(12, 12, 20));

            g2.setPaint(degradado);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
