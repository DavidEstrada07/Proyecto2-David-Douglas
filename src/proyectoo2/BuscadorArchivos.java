package proyectoo2;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class BuscadorArchivos extends JInternalFrame {

    private static final String[][] PROGRAMAS = {
        {"Música", "Musica"},
        {"Archivos", "Explorador"},
        {"INSTA+", "INSTA+"},
        {"Editor de texto", "Editor"},
        {"Imágenes", "Imagenes"},
        {"Consola de comandos", "Consola"},
        {"Usuarios", "Usuarios"}
    };

    private Escritorio escritorio;
    private ListaEnlazada encontrados;
    private DefaultListModel<String> modelo;

    public BuscadorArchivos(String carpeta, String texto, Escritorio escritorio) {
        super("Resultados de: " + texto, true, true, true, true);

        this.escritorio = escritorio;
        this.encontrados = new ListaEnlazada();

        setSize(520, 400);
        setLocation(90, 60);

        modelo = new DefaultListModel<>();
        buscarProgramas(texto.toLowerCase());
        buscar(new File(carpeta), texto.toLowerCase());

        JList<String> lista = new JList<>(modelo);
        lista.setBackground(Estilo.PANEL);
        lista.setForeground(Estilo.TEXTO);
        lista.setFont(Estilo.NORMAL);
        lista.setFixedCellHeight(26);

        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrir(lista.getSelectedIndex());
                }
            }
        });

        JLabel aviso = new JLabel("  " + encontrados.getTamano()
                + " coincidencias entre programas y archivos. Doble clic para abrir.");
        aviso.setForeground(Estilo.TEXTO_GRIS);
        aviso.setFont(Estilo.PEQUENA);
        aviso.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        getContentPane().setBackground(Estilo.FONDO);
        add(aviso, BorderLayout.NORTH);
        add(new JScrollPane(lista), BorderLayout.CENTER);
    }

    private void buscarProgramas(String texto) {
        for (int i = 0; i < PROGRAMAS.length; i++) {
            String nombre = PROGRAMAS[i][0];
            String tipo = PROGRAMAS[i][1];

            if (tipo.equals("Usuarios") && !escritorio.esAdministrador()) {
                continue;
            }

            if (nombre.toLowerCase().contains(texto) || tipo.toLowerCase().contains(texto)) {
                encontrados.agregar(tipo);
                modelo.addElement("[programa]  " + nombre);
            }
        }
    }

    private void buscar(File carpeta, String texto) {
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return;
        }

        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].getName().toLowerCase().contains(texto)) {
                encontrados.agregar(archivos[i]);
                modelo.addElement(etiqueta(archivos[i]));
            }

            if (archivos[i].isDirectory()) {
                buscar(archivos[i], texto);
            }
        }
    }

    private String etiqueta(File archivo) {
        if (archivo.isDirectory()) {
            return "[carpeta]  " + archivo.getName();
        }

        return "[archivo]  " + archivo.getName() + "   (" + (archivo.length() / 1024) + " KB)";
    }

    private void abrir(int posicion) {
        if (posicion < 0 || posicion >= encontrados.getTamano()) {
            return;
        }

        Object elegido = encontrados.obtener(posicion);

        if (elegido instanceof String) {
            escritorio.abrirHerramienta((String) elegido);
        } else {
            escritorio.abrirArchivo((File) elegido);
        }
    }
}
