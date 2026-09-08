package proyectoo2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ExploradorArchivos extends JInternalFrame {

    private UsuarioSistema usuario;
    private Escritorio escritorio;
    private JTree arbol;
    private JComboBox<String> orden;
    private File copiado;

    public ExploradorArchivos(UsuarioSistema usuario, Escritorio escritorio) {
        super("Explorador de archivos", true, true, true, true);

        this.usuario = usuario;
        this.escritorio = escritorio;

        setSize(760, 540);
        setLocation(30, 20);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Estilo.PANEL);

        arbol = new JTree();
        arbol.setBackground(Estilo.PANEL);
        arbol.setForeground(Estilo.TEXTO);
        arbol.setFont(Estilo.NORMAL);
        arbol.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(arbol);
        scroll.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        scroll.getViewport().setBackground(Estilo.PANEL);

        contenido.add(crearBarra(), BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);

        setContentPane(contenido);
        recargar();
        agregarDobleClic();
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(Estilo.PANEL_CLARO);

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fila1.setBackground(Estilo.PANEL_CLARO);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fila2.setBackground(Estilo.PANEL_CLARO);

        fila1.add(crearBoton("Organizar", Estilo.ACENTO, e -> organizar()));
        fila1.add(crearBoton("Nueva carpeta", Estilo.PANEL, e -> crearCarpeta()));
        fila1.add(crearBoton("Renombrar", Estilo.PANEL, e -> renombrar()));

        fila2.add(crearBoton("Copiar", Estilo.PANEL, e -> copiar()));
        fila2.add(crearBoton("Pegar", Estilo.PANEL, e -> pegar()));
        fila2.add(crearBoton("Eliminar", Estilo.ROJO, e -> eliminar()));

        orden = new JComboBox<>(new String[]{"Nombre", "Fecha", "Tipo", "Tamaño"});
        orden.setBackground(Estilo.PANEL);
        orden.setForeground(Estilo.TEXTO);
        orden.setFont(Estilo.NORMAL);
        orden.addActionListener(e -> recargar());

        fila2.add(Estilo.crearEtiqueta("Ordenar por:"));
        fila2.add(orden);

        barra.add(fila1);
        barra.add(fila2);

        return barra;
    }

    private BotonRedondo crearBoton(String texto, java.awt.Color color, ActionListener accion) {
        BotonRedondo boton = new BotonRedondo(texto, color);

        boton.compactar();
        boton.addActionListener(accion);

        return boton;
    }

    public final void recargar() {
        File raiz = new File(escritorio.carpetaInicial());
        DefaultMutableTreeNode nodoRaiz = new DefaultMutableTreeNode(new ArchivoNodo(raiz));

        llenar(nodoRaiz, raiz);

        arbol.setModel(new DefaultTreeModel(nodoRaiz));

        for (int i = 0; i < arbol.getRowCount(); i++) {
            arbol.expandRow(i);
        }
    }

    private void llenar(DefaultMutableTreeNode nodo, File carpeta) {
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return;
        }

        ordenarArchivos(archivos);

        for (int i = 0; i < archivos.length; i++) {
            DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(new ArchivoNodo(archivos[i]));
            nodo.add(hijo);

            if (archivos[i].isDirectory()) {
                llenar(hijo, archivos[i]);
            }
        }
    }

    private void ordenarArchivos(File[] archivos) {
        String criterio = (String) orden.getSelectedItem();

        for (int i = 0; i < archivos.length - 1; i++) {
            for (int j = 0; j < archivos.length - 1 - i; j++) {
                if (vaDespues(archivos[j], archivos[j + 1], criterio)) {
                    File temporal = archivos[j];
                    archivos[j] = archivos[j + 1];
                    archivos[j + 1] = temporal;
                }
            }
        }
    }

    private boolean vaDespues(File uno, File otro, String criterio) {
        if (criterio.equals("Fecha")) {
            return uno.lastModified() > otro.lastModified();
        }

        if (criterio.equals("Tamaño")) {
            return uno.length() > otro.length();
        }

        if (criterio.equals("Tipo")) {
            return extension(uno).compareToIgnoreCase(extension(otro)) > 0;
        }

        return uno.getName().compareToIgnoreCase(otro.getName()) > 0;
    }

    private String extension(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');

        if (punto < 0) {
            return "";
        }

        return nombre.substring(punto);
    }

    private File seleccionado() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();

        if (nodo == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un archivo o carpeta");
            return null;
        }

        ArchivoNodo dato = (ArchivoNodo) nodo.getUserObject();
        return dato.getArchivo();
    }

    private void organizar() {
        File carpeta = seleccionado();

        if (carpeta == null) {
            return;
        }

        if (!carpeta.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Selecciona una carpeta para organizar");
            return;
        }

        Thread hilo = new Thread(new OrganizadorArchivos(carpeta, this));
        hilo.start();
    }

    private void crearCarpeta() {
        File carpeta = seleccionado();

        if (carpeta == null) {
            return;
        }

        if (!carpeta.isDirectory()) {
            carpeta = carpeta.getParentFile();
        }

        String nombre = JOptionPane.showInputDialog(this, "Nombre de la carpeta:");

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        new File(carpeta, nombre.trim()).mkdir();
        recargar();
    }

    private void renombrar() {
        File archivo = seleccionado();

        if (archivo == null) {
            return;
        }

        String nombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", archivo.getName());

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        if (!archivo.renameTo(new File(archivo.getParentFile(), nombre.trim()))) {
            JOptionPane.showMessageDialog(this, "No se pudo renombrar");
        }

        recargar();
    }

    private void copiar() {
        copiado = seleccionado();

        if (copiado != null) {
            JOptionPane.showMessageDialog(this, "Copiado: " + copiado.getName());
        }
    }

    private void pegar() {
        if (copiado == null) {
            JOptionPane.showMessageDialog(this, "Primero copia un archivo");
            return;
        }

        File destino = seleccionado();

        if (destino == null) {
            return;
        }

        if (!destino.isDirectory()) {
            destino = destino.getParentFile();
        }

        if (copiado.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Solo se pueden pegar archivos");
            return;
        }

        try (FileInputStream entrada = new FileInputStream(copiado);
                FileOutputStream salida = new FileOutputStream(new File(destino, copiado.getName()))) {

            byte[] datos = new byte[4096];
            int leidos = entrada.read(datos);

            while (leidos > 0) {
                salida.write(datos, 0, leidos);
                leidos = entrada.read(datos);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo pegar: " + e.getMessage());
        }

        recargar();
    }

    private void eliminar() {
        File archivo = seleccionado();

        if (archivo == null) {
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this, "Eliminar " + archivo.getName() + "?",
                "Eliminar", JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        if (!borrarTodo(archivo)) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar " + archivo.getName()
                    + ".\nCierra la ventana del editor, el visor o el reproductor si lo tienes abierto.");
        }

        recargar();
    }

    private boolean borrarTodo(File archivo) {
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();

            if (hijos != null) {
                for (int i = 0; i < hijos.length; i++) {
                    borrarTodo(hijos[i]);
                }
            }
        }

        return archivo.delete();
    }

    private void agregarDobleClic() {
        arbol.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSeleccionado();
                }
            }
        });
    }

    private void abrirSeleccionado() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();

        if (nodo == null) {
            return;
        }

        File archivo = ((ArchivoNodo) nodo.getUserObject()).getArchivo();

        if (archivo.isDirectory()) {
            return;
        }

        String nombre = archivo.getName();

        if (OrganizadorArchivos.esImagen(nombre)) {
            escritorio.mostrar(new VisorImagenes(archivo.getParent()));
        } else if (OrganizadorArchivos.esMusica(nombre)) {
            escritorio.mostrar(new ReproductorMusica(archivo.getParent()));
        } else if (nombre.toLowerCase().endsWith(".txt")) {
            EditorTexto editor = new EditorTexto(archivo.getParent());
            escritorio.mostrar(editor);
            editor.abrirArchivo(archivo);
        }
    }
}
