package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TarjetaPublicacion extends PanelRedondo {

    public TarjetaPublicacion(Publicacion publicacion) {
        super(Estilo.PANEL);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        add(crearEncabezado(publicacion), BorderLayout.NORTH);
        add(crearCuerpo(publicacion), BorderLayout.CENTER);
    }

    private JPanel crearEncabezado(Publicacion publicacion) {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel autor = new JLabel(publicacion.getAutor() + " escribio:");
        autor.setFont(Estilo.SUBTITULO);
        autor.setForeground(Estilo.ACENTO2);

        JLabel fecha = new JLabel(publicacion.obtenerFechaTexto());
        fecha.setFont(Estilo.PEQUENA);
        fecha.setForeground(Estilo.TEXTO_GRIS);

        encabezado.add(autor, BorderLayout.WEST);
        encabezado.add(fecha, BorderLayout.EAST);

        return encabezado;
    }

    private JPanel crearCuerpo(Publicacion publicacion) {
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);

        JLabel texto = new JLabel("<html><div style='width:420px'>\"" + publicacion.getContenido()
                + "\"</div></html>");
        texto.setFont(Estilo.NORMAL);
        texto.setForeground(Estilo.TEXTO);
        texto.setAlignmentX(0f);

        cuerpo.add(texto);

        if (publicacion.tieneImagen()) {
            cuerpo.add(crearImagen(publicacion.getRutaImagen(), 320));
        }

        if (publicacion.tieneSticker()) {
            cuerpo.add(crearImagen(publicacion.getRutaSticker(), 90));
        }

        if (publicacion.getCarpetaPersonal() != null && !publicacion.getCarpetaPersonal().isEmpty()) {
            JLabel carpeta = new JLabel("Carpeta: " + publicacion.getCarpetaPersonal());
            carpeta.setFont(Estilo.PEQUENA);
            carpeta.setForeground(Estilo.TEXTO_GRIS);
            carpeta.setAlignmentX(0f);
            cuerpo.add(carpeta);
        }

        return cuerpo;
    }

    private JLabel crearImagen(String ruta, int ancho) {
        JLabel etiqueta = new JLabel();
        etiqueta.setAlignmentX(0f);

        File archivo = new File(ruta);

        if (!archivo.exists()) {
            etiqueta.setText("(imagen no encontrada)");
            etiqueta.setForeground(Estilo.TEXTO_GRIS);
            return etiqueta;
        }

        ImageIcon icono = new ImageIcon(ruta);
        int alto = ancho;

        if (icono.getIconWidth() > 0) {
            alto = icono.getIconHeight() * ancho / icono.getIconWidth();
        }

        Image escalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        etiqueta.setIcon(new ImageIcon(escalada));

        return etiqueta;
    }
}
