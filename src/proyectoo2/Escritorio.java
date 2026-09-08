package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Escritorio extends JFrame {

    private UsuarioSistema usuario;
    private JDesktopPane escritorio;
    private JLabel etiquetaReloj;

    public Escritorio(UsuarioSistema usuario) {
        this.usuario = usuario;

        setTitle("Mini-Windows - " + usuario.getUsername());
        setSize(1180, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        escritorio = new FondoEscritorio();
        escritorio.setBackground(Estilo.FONDO);

        add(crearBarra(), BorderLayout.NORTH);
        add(escritorio, BorderLayout.CENTER);

        iniciarReloj();
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.PANEL);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        botones.setOpaque(false);

        botones.add(crearBoton("Explorador", Estilo.ACENTO));
        botones.add(crearBoton("Editor", Estilo.PANEL_CLARO));
        botones.add(crearBoton("Imágenes", Estilo.PANEL_CLARO));
        botones.add(crearBoton("Consola", Estilo.PANEL_CLARO));
        botones.add(crearBoton("Música", Estilo.PANEL_CLARO));
        botones.add(crearBoton("INSTA+", Estilo.ACENTO2));

        if (usuario.esAdministrador()) {
            botones.add(crearBoton("Usuarios", Estilo.VERDE));
        }

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        derecha.setOpaque(false);

        etiquetaReloj = new JLabel();
        etiquetaReloj.setForeground(Estilo.TEXTO_GRIS);
        etiquetaReloj.setFont(Estilo.PEQUENA);

        BotonRedondo salir = new BotonRedondo("Cerrar sesión", Estilo.ROJO);
        salir.addActionListener(e -> cerrarSesion());

        derecha.add(etiquetaReloj);
        derecha.add(salir);

        barra.add(botones, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    private BotonRedondo crearBoton(String texto, Color color) {
        BotonRedondo boton = new BotonRedondo(texto, color);
        boton.addActionListener(e -> abrirHerramienta(texto));
        return boton;
    }

    private void abrirHerramienta(String nombre) {
        if (nombre.equals("Explorador")) {
            mostrar(new ExploradorArchivos(usuario, this));
        } else if (nombre.equals("Editor")) {
            mostrar(new EditorTexto(carpetaInicial()));
        } else if (nombre.equals("Imágenes")) {
            mostrar(new VisorImagenes(carpetaInicial()));
        } else if (nombre.equals("Consola")) {
            mostrar(new ConsolaComandos(carpetaInicial()));
        } else if (nombre.equals("Música")) {
            mostrar(new ReproductorMusica(carpetaInicial()));
        } else if (nombre.equals("INSTA+")) {
            mostrar(new VentanaInstaPlus());
        } else if (nombre.equals("Usuarios")) {
            crearUsuarioNuevo();
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

    public String carpetaInicial() {
        if (usuario.esAdministrador()) {
            return SistemaArchivos.UNIDAD;
        }

        return SistemaArchivos.carpetaSistema(usuario.getUsername());
    }

    private void crearUsuarioNuevo() {
        JTextField campoUsuario = new JTextField();
        JPasswordField campoPassword = new JPasswordField();

        JPanel panel = new JPanel(new java.awt.GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Usuario nuevo:"));
        panel.add(campoUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoPassword);
        panel.add(new JLabel("8 caracteres, mayúscula, número y símbolo"));

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Crear usuario del sistema",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nombre = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (!ValidadorPassword.esValida(password)) {
            JOptionPane.showMessageDialog(this, ValidadorPassword.obtenerMensaje(password));
            return;
        }

        try {
            ArchivoUsuariosSistema.guardar(new UsuarioSistema(nombre, password, false));
            SistemaArchivos.crearEspacioSistema(nombre);
            JOptionPane.showMessageDialog(this, "Usuario " + nombre + " creado con su carpeta en Z:\\");
        } catch (UsernameDuplicadoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void iniciarReloj() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    java.util.Calendar calendario = java.util.Calendar.getInstance();

                    int hora = calendario.get(java.util.Calendar.HOUR_OF_DAY);
                    int minuto = calendario.get(java.util.Calendar.MINUTE);
                    String minutoTexto = "" + minuto;

                    if (minuto < 10) {
                        minutoTexto = "0" + minuto;
                    }

                    final String texto = usuario.getUsername() + "   |   " + hora + ":" + minutoTexto + "   ";

                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            etiquetaReloj.setText(texto);
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
