package proyectoo2;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PanelInstaLogin extends PanelInsta {

    private CardLayout tarjetas;
    private JPanel contenedor;

    private JTextField usuarioLogin;
    private JPasswordField passwordLogin;

    private JTextField nombreRegistro;
    private JTextField usuarioRegistro;
    private JPasswordField passwordRegistro;
    private JTextField edadRegistro;
    private JComboBox<String> generoRegistro;
    private JComboBox<String> privacidadRegistro;
    private JLabel reglas;
    private JLabel rutaFoto;

    public PanelInstaLogin(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new java.awt.BorderLayout());

        tarjetas = new CardLayout();
        contenedor = new JPanel(tarjetas);
        contenedor.setBackground(Estilo.FONDO);

        contenedor.add(crearLogin(), "login");
        contenedor.add(crearRegistro(), "registro");

        add(contenedor, java.awt.BorderLayout.CENTER);
    }

    private JPanel crearLogin() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilo.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 120, 50, 120));

        usuarioLogin = new JTextField();
        passwordLogin = new JPasswordField();

        Estilo.darEstiloCampo(usuarioLogin);
        Estilo.darEstiloPassword(passwordLogin);
        limitar(usuarioLogin);
        limitar(passwordLogin);

        BotonRedondo entrar = new BotonRedondo("Log In", Estilo.ACENTO2);
        entrar.addActionListener(e -> iniciarSesion());

        BotonRedondo crear = new BotonRedondo("Crear cuenta", Estilo.PANEL_CLARO);
        crear.addActionListener(e -> mostrarRegistro());

        JLabel titulo = Estilo.crearTitulo("INSTA+");
        titulo.setForeground(Estilo.ACENTO2);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(Estilo.crearEtiqueta("La red social de Mini-Windows"));
        panel.add(Box.createVerticalStrut(24));
        panel.add(Estilo.crearEtiqueta("Username"));
        panel.add(usuarioLogin);
        panel.add(Box.createVerticalStrut(12));
        panel.add(Estilo.crearEtiqueta("Password"));
        panel.add(passwordLogin);
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearCheck(passwordLogin));
        panel.add(Box.createVerticalStrut(20));
        panel.add(entrar);
        panel.add(Box.createVerticalStrut(8));
        panel.add(crear);

        return panel;
    }

    private JPanel crearRegistro() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilo.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 120, 24, 120));

        nombreRegistro = new JTextField();
        usuarioRegistro = new JTextField();
        passwordRegistro = new JPasswordField();
        edadRegistro = new JTextField();
        generoRegistro = new JComboBox<>(new String[]{"M", "F"});
        privacidadRegistro = new JComboBox<>(new String[]{"Publica", "Privada"});

        Estilo.darEstiloCampo(nombreRegistro);
        Estilo.darEstiloCampo(usuarioRegistro);
        Estilo.darEstiloPassword(passwordRegistro);
        Estilo.darEstiloCampo(edadRegistro);

        limitar(nombreRegistro);
        limitar(usuarioRegistro);
        limitar(passwordRegistro);
        limitar(edadRegistro);
        generoRegistro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        privacidadRegistro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        reglas = new JLabel("Mínimo 8 caracteres, una mayúscula, un número y un símbolo");
        reglas.setFont(Estilo.PEQUENA);
        reglas.setForeground(Estilo.TEXTO_GRIS);

        passwordRegistro.addCaretListener(e -> revisarReglas());

        rutaFoto = new JLabel("Sin foto seleccionada");
        rutaFoto.setFont(Estilo.PEQUENA);
        rutaFoto.setForeground(Estilo.TEXTO_GRIS);

        BotonRedondo foto = new BotonRedondo("Elegir foto de perfil", Estilo.PANEL_CLARO);
        foto.addActionListener(e -> elegirFoto());

        BotonRedondo registrar = new BotonRedondo("Registrarme", Estilo.VERDE);
        registrar.addActionListener(e -> registrar());

        BotonRedondo volver = new BotonRedondo("Ya tengo cuenta", Estilo.PANEL_CLARO);
        volver.addActionListener(e -> tarjetas.show(contenedor, "login"));

        JLabel titulo = Estilo.crearTitulo("Crear cuenta");
        titulo.setForeground(Estilo.ACENTO2);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(12));
        panel.add(Estilo.crearEtiqueta("Nombre completo"));
        panel.add(nombreRegistro);
        panel.add(Box.createVerticalStrut(8));
        panel.add(Estilo.crearEtiqueta("Username"));
        panel.add(usuarioRegistro);
        panel.add(Box.createVerticalStrut(8));
        panel.add(Estilo.crearEtiqueta("Password"));
        panel.add(passwordRegistro);
        panel.add(reglas);
        panel.add(crearCheck(passwordRegistro));
        panel.add(Box.createVerticalStrut(8));
        panel.add(Estilo.crearEtiqueta("Edad"));
        panel.add(edadRegistro);
        panel.add(Box.createVerticalStrut(8));
        panel.add(Estilo.crearEtiqueta("Genero"));
        panel.add(generoRegistro);
        panel.add(Box.createVerticalStrut(10));
        panel.add(Estilo.crearEtiqueta("Tipo de cuenta (privada = solo tus seguidores ven tus posts)"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(privacidadRegistro);
        panel.add(Box.createVerticalStrut(10));
        panel.add(foto);
        panel.add(rutaFoto);
        panel.add(Box.createVerticalStrut(14));
        panel.add(registrar);
        panel.add(Box.createVerticalStrut(6));
        panel.add(volver);

        return panel;
    }

    private JCheckBox crearCheck(JPasswordField campo) {
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

    private void limitar(JTextField campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private void revisarReglas() {
        String password = new String(passwordRegistro.getPassword());

        if (password.isEmpty()) {
            reglas.setForeground(Estilo.TEXTO_GRIS);
            reglas.setText("Mínimo 8 caracteres, una mayúscula, un número y un símbolo");
            return;
        }

        reglas.setText(ValidadorPassword.obtenerMensaje(password));

        if (ValidadorPassword.esValida(password)) {
            reglas.setForeground(Estilo.VERDE);
        } else {
            reglas.setForeground(Estilo.ROJO);
        }
    }

    private void elegirFoto() {
        JFileChooser selector = new JFileChooser();

        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            rutaFoto.setText(selector.getSelectedFile().getPath());
        }
    }

    private void iniciarSesion() {
        String username = usuarioLogin.getText().trim();
        String password = new String(passwordLogin.getPassword());

        try {
            Usuario usuario = ArchivoUsuarios.iniciarSesion(username, password);

            if (usuario == null) {
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Datos incorrectos. ¿Deseas crear una cuenta nueva?", "INSTA+",
                        JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    mostrarRegistro();
                }

                return;
            }

            passwordLogin.setText("");
            ventana.entrar(usuario);
        } catch (CuentaDesactivadaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void mostrarRegistro() {
        limpiarRegistro();
        tarjetas.show(contenedor, "registro");
    }

    private void limpiarRegistro() {
        nombreRegistro.setText("");
        usuarioRegistro.setText("");
        passwordRegistro.setText("");
        edadRegistro.setText("");
        generoRegistro.setSelectedIndex(0);
        privacidadRegistro.setSelectedIndex(0);
        rutaFoto.setText("Sin foto seleccionada");
        revisarReglas();
    }

    private void registrar() {
        String password = new String(passwordRegistro.getPassword());

        if (!ValidadorPassword.esValida(password)) {
            JOptionPane.showMessageDialog(this, ValidadorPassword.obtenerMensaje(password));
            return;
        }

        try {
            int edad = Integer.parseInt(edadRegistro.getText().trim());
            char genero = ((String) generoRegistro.getSelectedItem()).charAt(0);
            String foto = rutaFoto.getText();

            if (foto.equals("Sin foto seleccionada")) {
                foto = "";
            }

            boolean privada = privacidadRegistro.getSelectedIndex() == 1;
            Usuario usuario = new Usuario(nombreRegistro.getText().trim(), genero,
                    usuarioRegistro.getText().trim(), password, edad, foto);

            usuario.setPrivada(privada);
            ArchivoUsuarios.guardar(usuario);

            JOptionPane.showMessageDialog(this, "Cuenta creada, ya puedes iniciar sesión");
            usuarioLogin.setText(usuario.getUsername());
            tarjetas.show(contenedor, "login");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (UsernameDuplicadoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void recargar() {
        tarjetas.show(contenedor, "login");
    }
}
