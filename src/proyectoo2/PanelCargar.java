package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PanelCargar extends PanelInsta {

    private JLabel vista;
    private JTextArea descripcion;
    private JTextField carpetaPersonal;
    private File imagenElegida;

    public PanelCargar(VentanaInstaPlus ventana) {
        super(ventana);

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        vista = new JLabel("Sin imagen seleccionada", SwingConstants.CENTER);
        vista.setForeground(Estilo.TEXTO_GRIS);
        vista.setPreferredSize(new Dimension(360, 300));

        descripcion = new JTextArea(4, 20);
        descripcion.setBackground(Estilo.PANEL_CLARO);
        descripcion.setForeground(Estilo.TEXTO);
        descripcion.setCaretColor(Estilo.TEXTO);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        carpetaPersonal = new JTextField();
        Estilo.darEstiloCampo(carpetaPersonal);
        carpetaPersonal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        BotonRedondo elegir = new BotonRedondo("Elegir imagen", Estilo.PANEL_CLARO);
        elegir.addActionListener(e -> elegir());

        BotonRedondo publicar = new BotonRedondo("Publicar", Estilo.ACENTO2);
        publicar.addActionListener(e -> publicar());

        javax.swing.JPanel derecha = new javax.swing.JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(Estilo.FONDO);

        derecha.add(Estilo.crearTitulo("Cargar imagen"));
        derecha.add(Box.createVerticalStrut(10));
        derecha.add(elegir);
        derecha.add(Box.createVerticalStrut(14));
        derecha.add(Estilo.crearEtiqueta("Descripción (máximo 220, usa # y @)"));
        derecha.add(new JScrollPane(descripcion));
        derecha.add(Box.createVerticalStrut(12));
        derecha.add(Estilo.crearEtiqueta("Carpeta personal (Viajes, Familia, Memes...)"));
        derecha.add(carpetaPersonal);
        derecha.add(Box.createVerticalStrut(16));
        derecha.add(publicar);

        add(vista, BorderLayout.WEST);
        add(derecha, BorderLayout.CENTER);
    }

    private void elegir() {
        JFileChooser selector = new JFileChooser();

        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        if (!OrganizadorArchivos.esImagen(archivo.getName())) {
            JOptionPane.showMessageDialog(this, "Debes elegir una imagen");
            return;
        }

        imagenElegida = archivo;

        Image escalada = new ImageIcon(archivo.getPath()).getImage()
                .getScaledInstance(340, 280, Image.SCALE_SMOOTH);
        vista.setIcon(new ImageIcon(escalada));
        vista.setText("");
    }

    private void publicar() {
        if (imagenElegida == null) {
            JOptionPane.showMessageDialog(this, "Primero elige una imagen");
            return;
        }

        String texto = descripcion.getText().trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe una descripción");
            return;
        }

        try {
            String usuario = ventana.getUsuario().getUsername();
            String carpeta = carpetaPersonal.getText().trim();
            String destino = SistemaArchivos.carpetaInsta(usuario) + File.separator + "imágenes";

            if (!carpeta.isEmpty()) {
                destino = SistemaArchivos.carpetaInsta(usuario) + File.separator + "folders_personales"
                        + File.separator + carpeta;
                SistemaArchivos.crearCarpeta(destino);
            }

            File copia = new File(destino, System.currentTimeMillis() + "_" + imagenElegida.getName());
            copiar(imagenElegida, copia);

            ArchivoPublicaciones.publicar(new Publicacion(usuario, texto, copia.getPath(), carpeta));

            JOptionPane.showMessageDialog(this, "Publicación creada");
            limpiar();
            ventana.mostrarSeccion("Timeline");
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (IllegalArgumentException e) {
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
            JOptionPane.showMessageDialog(this, "No se pudo copiar la imagen");
        }
    }

    private void limpiar() {
        imagenElegida = null;
        descripcion.setText("");
        carpetaPersonal.setText("");
        vista.setIcon(null);
        vista.setText("Sin imagen seleccionada");
    }

    @Override
    public void recargar() {
        limpiar();
    }
}
