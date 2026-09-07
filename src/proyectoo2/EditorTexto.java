package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;

public class EditorTexto extends JInternalFrame {

    private JTextPane area;
    private JComboBox<String> fuentes;
    private JComboBox<String> tamanos;
    private String carpeta;
    private File archivoActual;

    public EditorTexto(String carpeta) {
        super("Editor de texto", true, true, true, true);

        this.carpeta = carpeta;

        setSize(680, 500);
        setLocation(80, 50);

        area = new JTextPane();
        area.setBackground(Estilo.PANEL_CLARO);
        area.setForeground(Estilo.TEXTO);
        area.setCaretColor(Estilo.TEXTO);
        area.setFont(Estilo.NORMAL);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Estilo.PANEL);
        contenido.add(crearBarra(), BorderLayout.NORTH);
        contenido.add(new JScrollPane(area), BorderLayout.CENTER);

        setContentPane(contenido);
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        barra.setBackground(Estilo.PANEL_CLARO);

        fuentes = new JComboBox<>(new String[]{"SansSerif", "Serif", "Monospaced", "Dialog"});
        fuentes.addActionListener(e -> cambiarFuente());

        tamanos = new JComboBox<>(new String[]{"12", "14", "16", "20", "24", "32"});
        tamanos.setSelectedItem("14");
        tamanos.addActionListener(e -> cambiarTamano());

        BotonRedondo color = new BotonRedondo("Color", Estilo.ACENTO2);
        color.addActionListener(e -> cambiarColor());

        BotonRedondo abrir = new BotonRedondo("Abrir", Estilo.PANEL);
        abrir.addActionListener(e -> abrir());

        BotonRedondo guardar = new BotonRedondo("Guardar", Estilo.VERDE);
        guardar.addActionListener(e -> guardar());

        BotonRedondo nuevo = new BotonRedondo("Nuevo", Estilo.PANEL);
        nuevo.addActionListener(e -> {
            area.setText("");
            archivoActual = null;
        });

        barra.add(Estilo.crearEtiqueta("Fuente:"));
        barra.add(fuentes);
        barra.add(Estilo.crearEtiqueta("Tamano:"));
        barra.add(tamanos);
        barra.add(color);
        barra.add(nuevo);
        barra.add(abrir);
        barra.add(guardar);

        return barra;
    }

    private void cambiarFuente() {
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontFamily(atributos, (String) fuentes.getSelectedItem());
        aplicar(atributos);
    }

    private void cambiarTamano() {
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontSize(atributos, Integer.parseInt((String) tamanos.getSelectedItem()));
        aplicar(atributos);
    }

    private void cambiarColor() {
        Color color = JColorChooser.showDialog(this, "Color del texto", Color.WHITE);

        if (color == null) {
            return;
        }

        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setForeground(atributos, color);
        aplicar(atributos);
    }

    private void aplicar(SimpleAttributeSet atributos) {
        int inicio = area.getSelectionStart();
        int fin = area.getSelectionEnd();

        if (inicio == fin) {
            area.setCharacterAttributes(atributos, false);
        } else {
            area.getStyledDocument().setCharacterAttributes(inicio, fin - inicio, atributos, false);
        }

        area.requestFocus();
    }

    private void abrir() {
        JFileChooser selector = new JFileChooser(carpeta);

        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            abrirArchivo(selector.getSelectedFile());
        }
    }

    public final void abrirArchivo(File archivo) {
        try (FileInputStream entrada = new FileInputStream(archivo)) {
            area.setDocument(area.getEditorKit().createDefaultDocument());
            new RTFEditorKit().read(entrada, area.getDocument(), 0);
            archivoActual = archivo;
            setTitle("Editor de texto - " + archivo.getName());
        } catch (Exception e) {
            leerComoTextoSimple(archivo);
        }
    }

    private void leerComoTextoSimple(File archivo) {
        try (java.util.Scanner lector = new java.util.Scanner(archivo)) {
            String texto = "";

            while (lector.hasNextLine()) {
                texto = texto + lector.nextLine() + "\n";
            }

            area.setText(texto);
            archivoActual = archivo;
            setTitle("Editor de texto - " + archivo.getName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo");
        }
    }

    private void guardar() {
        File destino = archivoActual;

        if (destino == null) {
            JFileChooser selector = new JFileChooser(carpeta);

            if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            destino = selector.getSelectedFile();

            if (!destino.getName().toLowerCase().endsWith(".txt")) {
                destino = new File(destino.getParentFile(), destino.getName() + ".txt");
            }
        }

        try (FileOutputStream salida = new FileOutputStream(destino)) {
            new RTFEditorKit().write(salida, area.getDocument(), 0, area.getDocument().getLength());
            archivoActual = destino;
            setTitle("Editor de texto - " + destino.getName());
            JOptionPane.showMessageDialog(this, "Archivo guardado con su formato");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + e.getMessage());
        }
    }
}
