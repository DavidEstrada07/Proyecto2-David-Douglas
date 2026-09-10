package proyectoo2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class GestorUsuarios extends JInternalFrame {

    private DefaultListModel<String> modelo;
    private JLabel resumen;

    public GestorUsuarios() {
        super("Usuarios del sistema", true, true, true, true);

        setSize(520, 420);
        setLocation(140, 70);

        modelo = new DefaultListModel<>();

        JList<String> lista = new JList<>(modelo);
        lista.setBackground(Estilo.PANEL);
        lista.setForeground(Estilo.TEXTO);
        lista.setFont(Estilo.NORMAL);
        lista.setFixedCellHeight(30);
        lista.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        resumen = new JLabel();
        resumen.setForeground(Estilo.TEXTO_GRIS);
        resumen.setFont(Estilo.PEQUENA);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        getContentPane().setBackground(Estilo.FONDO);
        add(resumen, BorderLayout.NORTH);
        add(new JScrollPane(lista), BorderLayout.CENTER);
        add(crearBarra(), BorderLayout.SOUTH);

        cargarUsuarios();
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        barra.setBackground(Estilo.PANEL_CLARO);

        BotonRedondo nuevo = new BotonRedondo("Crear usuario nuevo", Estilo.VERDE);
        nuevo.compactar();
        nuevo.addActionListener(e -> crearUsuario());

        BotonRedondo actualizar = new BotonRedondo("Actualizar lista", Estilo.PANEL);
        actualizar.compactar();
        actualizar.addActionListener(e -> cargarUsuarios());

        barra.add(nuevo);
        barra.add(actualizar);

        return barra;
    }

    private void cargarUsuarios() {
        modelo.clear();

        try {
            ListaEnlazada usuarios = ArchivoUsuariosSistema.listar();

            for (int i = 0; i < usuarios.getTamano(); i++) {
                UsuarioSistema usuario = (UsuarioSistema) usuarios.obtener(i);
                modelo.addElement(etiqueta(usuario));
            }

            resumen.setText("  Hay " + usuarios.getTamano() + " usuarios registrados en usuarios.sop");
        } catch (ArchivoCorruptoException e) {
            resumen.setText("  " + e.getMessage());
        }
    }

    private String etiqueta(UsuarioSistema usuario) {
        String tipo = "Estandar";

        if (usuario.esAdministrador()) {
            tipo = "Administrador";
        }

        return usuario.getUsername() + "      " + tipo + "      " + usuario.obtenerCarpetaRaiz();
    }

    private void crearUsuario() {
        JTextField campoUsuario = new JTextField();
        JPasswordField campoPassword = new JPasswordField();
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Estándar", "Administrador"});

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Usuario nuevo:"));
        panel.add(campoUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoPassword);
        panel.add(new JLabel("8 caracteres, mayúscula, número y símbolo"));
        panel.add(new JLabel("Tipo de cuenta:"));
        panel.add(tipo);

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
            boolean administrador = tipo.getSelectedIndex() == 1;

            ArchivoUsuariosSistema.guardar(new UsuarioSistema(nombre, password, administrador));
            SistemaArchivos.crearEspacioSistema(nombre);
            JOptionPane.showMessageDialog(this, "Usuario " + nombre + " creado con su carpeta en Z:\\");
            cargarUsuarios();
        } catch (UsernameDuplicadoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
