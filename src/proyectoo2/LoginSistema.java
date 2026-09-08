package proyectoo2;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginSistema extends JFrame {

    private CardLayout tarjetas;
    private JPanel contenedor;

    private JTextField campoUsuarioLogin;
    private JPasswordField campoPasswordLogin;

    private JTextField campoUsuarioRegistro;
    private JPasswordField campoPasswordRegistro;
    private JLabel etiquetaReglas;

    public LoginSistema() {
        setTitle("Mini-Windows");
        setSize(920, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tarjetas = new CardLayout();
        contenedor = new JPanel(tarjetas);
        contenedor.setBackground(Estilo.FONDO);

        contenedor.add(crearPanelLogin(), "login");
        contenedor.add(crearPanelRegistro(), "registro");

        getContentPane().setBackground(Estilo.FONDO);
        add(crearLateral(), BorderLayout.WEST);
        add(contenedor, BorderLayout.CENTER);

        mostrarPantallaInicial();
    }

    private void mostrarPantallaInicial() {
        try {
            if (!ArchivoUsuariosSistema.hayUsuarios()) {
                tarjetas.show(contenedor, "registro");
                JOptionPane.showMessageDialog(this,
                        "Es la primera vez que se inicia el sistema.\nCrea tu cuenta de usuario para continuar.");
            }
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private JPanel crearLateral() {
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(Estilo.ACENTO);
        lateral.setPreferredSize(new Dimension(320, 0));
        lateral.setBorder(BorderFactory.createEmptyBorder(90, 34, 30, 34));

        JLabel titulo = new JLabel("Mini-Windows");
        titulo.setFont(Estilo.TITULO.deriveFont(30f));
        titulo.setForeground(java.awt.Color.WHITE);

        JLabel texto = new JLabel("Bienvenido!");
        texto.setFont(Estilo.NORMAL);
        texto.setForeground(new java.awt.Color(235, 230, 255));

        lateral.add(titulo);
        lateral.add(Box.createVerticalStrut(24));
        lateral.add(texto);

        return lateral;
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilo.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(90, 60, 40, 60));

        campoUsuarioLogin = new JTextField();
        campoPasswordLogin = new JPasswordField();

        Estilo.darEstiloCampo(campoUsuarioLogin);
        Estilo.darEstiloPassword(campoPasswordLogin);

        JCheckBox mostrar = crearCheckMostrar(campoPasswordLogin);

        BotonRedondo entrar = new BotonRedondo("Iniciar sesión", Estilo.ACENTO);
        entrar.addActionListener(e -> iniciarSesion());

        BotonRedondo irRegistro = new BotonRedondo("Crear una cuenta", Estilo.PANEL_CLARO);
        irRegistro.addActionListener(e -> tarjetas.show(contenedor, "registro"));

        panel.add(Estilo.crearTitulo("Iniciar sesión"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(Estilo.crearEtiqueta("Ingresa a tu propio sistema de archivos"));
        panel.add(Box.createVerticalStrut(26));
        panel.add(Estilo.crearEtiqueta("Usuario"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(campoUsuarioLogin);
        panel.add(Box.createVerticalStrut(14));
        panel.add(Estilo.crearEtiqueta("Contraseña"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(campoPasswordLogin);
        panel.add(Box.createVerticalStrut(8));
        panel.add(mostrar);
        panel.add(Box.createVerticalStrut(22));
        panel.add(entrar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(irRegistro);

        limitarAltura(campoUsuarioLogin);
        limitarAltura(campoPasswordLogin);

        return panel;
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilo.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(70, 60, 40, 60));

        campoUsuarioRegistro = new JTextField();
        campoPasswordRegistro = new JPasswordField();

        Estilo.darEstiloCampo(campoUsuarioRegistro);
        Estilo.darEstiloPassword(campoPasswordRegistro);

        etiquetaReglas = new JLabel("Mínimo 8 caracteres, una mayúscula, un número y un símbolo");
        etiquetaReglas.setFont(Estilo.PEQUENA);
        etiquetaReglas.setForeground(Estilo.TEXTO_GRIS);

        campoPasswordRegistro.addCaretListener(e -> revisarReglas());

        JCheckBox mostrar = crearCheckMostrar(campoPasswordRegistro);

        BotonRedondo crear = new BotonRedondo("Crear cuenta", Estilo.VERDE);
        crear.addActionListener(e -> registrar());

        BotonRedondo volver = new BotonRedondo("Ya tengo cuenta", Estilo.PANEL_CLARO);
        volver.addActionListener(e -> tarjetas.show(contenedor, "login"));

        panel.add(Estilo.crearTitulo("Crear cuenta"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(Estilo.crearEtiqueta("Inicie sesión"));
        panel.add(Box.createVerticalStrut(24));
        panel.add(Estilo.crearEtiqueta("Usuario"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(campoUsuarioRegistro);
        panel.add(Box.createVerticalStrut(14));
        panel.add(Estilo.crearEtiqueta("Contraseña"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(campoPasswordRegistro);
        panel.add(Box.createVerticalStrut(6));
        panel.add(etiquetaReglas);
        panel.add(Box.createVerticalStrut(6));
        panel.add(mostrar);
        panel.add(Box.createVerticalStrut(22));
        panel.add(crear);
        panel.add(Box.createVerticalStrut(10));
        panel.add(volver);

        limitarAltura(campoUsuarioRegistro);
        limitarAltura(campoPasswordRegistro);

        return panel;
    }

    private JCheckBox crearCheckMostrar(JPasswordField campo) {
        JCheckBox mostrar = new JCheckBox("Mostrar contraseña");
        mostrar.setBackground(Estilo.FONDO);
        mostrar.setForeground(Estilo.TEXTO_GRIS);
        mostrar.setFont(Estilo.PEQUENA);
        mostrar.setFocusPainted(false);

        mostrar.addActionListener(e -> {
            if (mostrar.isSelected()) {
                campo.setEchoChar((char) 0);
            } else {
                campo.setEchoChar('\u2022');
            }
        });

        return mostrar;
    }

    private void limitarAltura(JTextField campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private void revisarReglas() {
        String password = new String(campoPasswordRegistro.getPassword());

        if (password.isEmpty()) {
            etiquetaReglas.setForeground(Estilo.TEXTO_GRIS);
            etiquetaReglas.setText("Mínimo 8 caracteres, una mayúscula, un número y un símbolo");
            return;
        }

        etiquetaReglas.setText(ValidadorPassword.obtenerMensaje(password));

        if (ValidadorPassword.esValida(password)) {
            etiquetaReglas.setForeground(Estilo.VERDE);
        } else {
            etiquetaReglas.setForeground(Estilo.ROJO);
        }
    }

    private void iniciarSesion() {
        String username = campoUsuarioLogin.getText().trim();
        String password = new String(campoPasswordLogin.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe tu usuario y tu contraseña");
            return;
        }

        try {
            UsuarioSistema usuario = ArchivoUsuariosSistema.buscar(username);

            if (usuario == null || !usuario.verificarPassword(password)) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
                return;
            }

            SistemaArchivos.crearEspacioSistema(usuario.getUsername());

            Escritorio escritorio = new Escritorio(usuario);
            escritorio.setVisible(true);
            dispose();
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void registrar() {
        String username = campoUsuarioRegistro.getText().trim();
        String password = new String(campoPasswordRegistro.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe un nombre de usuario");
            return;
        }

        if (username.length() > UsuarioSistema.MAX_USERNAME) {
            JOptionPane.showMessageDialog(this,
                    "El usuario no puede pasar de " + UsuarioSistema.MAX_USERNAME + " caracteres");
            return;
        }

        if (!ValidadorPassword.esValida(password)) {
            JOptionPane.showMessageDialog(this, ValidadorPassword.obtenerMensaje(password));
            return;
        }

        try {
            boolean administrador = !ArchivoUsuariosSistema.hayUsuarios();
            UsuarioSistema usuario = new UsuarioSistema(username, password, administrador);

            ArchivoUsuariosSistema.guardar(usuario);
            SistemaArchivos.crearEspacioSistema(username);

            String mensaje = "Cuenta creada. Ya puedes iniciar sesión.";

            if (administrador) {
                mensaje = "Cuenta creada como administrador del sistema.\nYa puedes iniciar sesión.";
            }

            JOptionPane.showMessageDialog(this, mensaje);
            campoUsuarioLogin.setText(username);
            tarjetas.show(contenedor, "login");
        } catch (UsernameDuplicadoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
