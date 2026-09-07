package proyectoo2;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class PanelEditarPerfil extends PanelInsta {

    private JTextField nombre;
    private JTextField edad;
    private JLabel foto;
    private JLabel estado;
    private BotonRedondo botonEstado;

    public PanelEditarPerfil(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        nombre = new JTextField();
        edad = new JTextField();

        Estilo.darEstiloCampo(nombre);
        Estilo.darEstiloCampo(edad);
        nombre.setMaximumSize(new Dimension(420, 36));
        edad.setMaximumSize(new Dimension(420, 36));

        foto = new JLabel();
        foto.setForeground(Estilo.TEXTO_GRIS);
        foto.setFont(Estilo.PEQUENA);

        estado = new JLabel();
        estado.setForeground(Estilo.TEXTO);
        estado.setFont(Estilo.SUBTITULO);

        BotonRedondo cambiarFoto = new BotonRedondo("Cambiar foto", Estilo.PANEL_CLARO);
        cambiarFoto.addActionListener(e -> elegirFoto());

        BotonRedondo guardar = new BotonRedondo("Guardar cambios", Estilo.VERDE);
        guardar.addActionListener(e -> guardar());

        botonEstado = new BotonRedondo("Desactivar cuenta", Estilo.ROJO);
        botonEstado.addActionListener(e -> cambiarEstado());

        add(Estilo.crearTitulo("Editar perfil"));
        add(Box.createVerticalStrut(16));
        add(Estilo.crearEtiqueta("Nombre completo"));
        add(nombre);
        add(Box.createVerticalStrut(10));
        add(Estilo.crearEtiqueta("Edad"));
        add(edad);
        add(Box.createVerticalStrut(10));
        add(cambiarFoto);
        add(foto);
        add(Box.createVerticalStrut(16));
        add(guardar);
        add(Box.createVerticalStrut(24));
        add(estado);
        add(Box.createVerticalStrut(8));
        add(botonEstado);
    }

    private void elegirFoto() {
        JFileChooser selector = new JFileChooser();

        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            foto.setText(selector.getSelectedFile().getPath());
        }
    }

    private void guardar() {
        Usuario usuario = ventana.getUsuario();

        try {
            usuario.setNombreCompleto(nombre.getText().trim());
            usuario.setEdad(Integer.parseInt(edad.getText().trim()));

            if (!foto.getText().isEmpty()) {
                usuario.setFotoPerfil(foto.getText());
            }

            ArchivoUsuarios.actualizar(usuario);
            JOptionPane.showMessageDialog(this, "Perfil actualizado");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un numero");
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void cambiarEstado() {
        Usuario usuario = ventana.getUsuario();

        try {
            if (usuario.estaActiva()) {
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Si desactivas tu cuenta dejaras de aparecer en el sistema. Continuar?",
                        "Desactivar cuenta", JOptionPane.YES_NO_OPTION);

                if (opcion != JOptionPane.YES_OPTION) {
                    return;
                }

                usuario.desactivarCuenta();
                ArchivoUsuarios.actualizar(usuario);
                JOptionPane.showMessageDialog(this, "Cuenta desactivada");
            } else {
                usuario.activarCuenta();
                ArchivoUsuarios.actualizar(usuario);
                JOptionPane.showMessageDialog(this, "Tu cuenta fue reactivada");
            }

            recargar();
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void recargar() {
        Usuario usuario = ventana.getUsuario();

        nombre.setText(usuario.getNombreCompleto());
        edad.setText("" + usuario.getEdad());
        foto.setText("");

        if (usuario.estaActiva()) {
            estado.setText("Estado de la cuenta: Activa");
            botonEstado.setText("Desactivar cuenta");
            botonEstado.setColorFondo(Estilo.ROJO);
        } else {
            estado.setText("Estado de la cuenta: Inactiva");
            botonEstado.setText("Reactivar cuenta");
            botonEstado.setColorFondo(Estilo.VERDE);
        }
    }
}
