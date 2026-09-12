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
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ExploradorArchivos extends JInternalFrame {

    private UsuarioSistema usuario;
    private Escritorio escritorio;
    private JTree arbol;
    private JComboBox<String> orden;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel barraRuta;
    private JLabel barraEstado;
    private File carpetaActual;
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
        arbol.setOpaque(true);
        arbol.setCellRenderer(new RenderizadorArbol());

        JScrollPane scrollArbol = new JScrollPane(arbol);
        scrollArbol.setBorder(null);
        scrollArbol.getViewport().setBackground(Estilo.PANEL);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, crearLista());
        division.setDividerLocation(230);
        division.setBorder(null);
        division.setBackground(Estilo.PANEL);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Estilo.PANEL);
        centro.add(crearBarraRuta(), BorderLayout.NORTH);
        centro.add(division, BorderLayout.CENTER);

        contenido.add(crearBarra(), BorderLayout.NORTH);
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(crearBarraEstado(), BorderLayout.SOUTH);

        setContentPane(contenido);
        recargar();
        agregarDobleClic();
        agregarSeleccionArbol();
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
        fila1.add(crearBoton("Importar", Estilo.VERDE, e -> importar()));

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

    private JScrollPane crearLista() {
        modeloTabla = new DefaultTableModel(new String[]{"Nombre", "Tipo", "Tamano", "Modificado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setBackground(Estilo.PANEL);
        tabla.setForeground(Estilo.TEXTO);
        tabla.setFont(Estilo.NORMAL);
        tabla.setRowHeight(26);
        tabla.setGridColor(Estilo.PANEL_CLARO);
        tabla.setSelectionBackground(Estilo.ACENTO);
        tabla.setSelectionForeground(java.awt.Color.WHITE);
        tabla.getTableHeader().setBackground(Estilo.PANEL_CLARO);
        tabla.getTableHeader().setForeground(Estilo.TEXTO);
        tabla.getTableHeader().setFont(Estilo.NORMAL);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDeLaTabla();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.PANEL);

        return scroll;
    }

    private JPanel crearBarraRuta() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Estilo.PANEL_CLARO);

        barraRuta = new JLabel("  Z:\\");
        barraRuta.setForeground(Estilo.TEXTO);
        barraRuta.setFont(Estilo.NORMAL);
        barraRuta.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        panel.add(barraRuta, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Estilo.PANEL_CLARO);

        barraEstado = new JLabel("  ");
        barraEstado.setForeground(Estilo.TEXTO_GRIS);
        barraEstado.setFont(Estilo.PEQUENA);
        barraEstado.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        panel.add(barraEstado, BorderLayout.CENTER);

        return panel;
    }

    private void agregarSeleccionArbol() {
        arbol.addTreeSelectionListener(e -> {
            File elegido = seleccionadoDelArbol();

            if (elegido != null && elegido.isDirectory()) {
                mostrarContenido(elegido);
            }
        });
    }

    private void refrescarTabla() {
        if (carpetaActual != null && carpetaActual.exists()) {
            mostrarContenido(carpetaActual);
        }
    }

    private void mostrarContenido(File carpeta) {
        carpetaActual = carpeta;
        modeloTabla.setRowCount(0);
        barraRuta.setText("  " + carpeta.getPath().replace("Z" + File.separator, "Z:" + File.separator));

        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return;
        }

        ordenarArchivos(archivos);

        int carpetas = 0;

        for (int i = 0; i < archivos.length; i++) {
            modeloTabla.addRow(new Object[]{archivos[i].getName(), tipo(archivos[i]),
                peso(archivos[i]), fecha(archivos[i])});

            if (archivos[i].isDirectory()) {
                carpetas++;
            }
        }

        barraEstado.setText("  " + carpetas + " carpetas, " + (archivos.length - carpetas) + " archivos");
    }

    private String tipo(File archivo) {
        if (archivo.isDirectory()) {
            return "Carpeta";
        }

        if (OrganizadorArchivos.esImagen(archivo.getName())) {
            return "Imagen";
        }

        if (OrganizadorArchivos.esMusica(archivo.getName())) {
            return "Musica";
        }

        return "Documento";
    }

    private String peso(File archivo) {
        if (archivo.isDirectory()) {
            return "";
        }

        return (archivo.length() / 1024) + " KB";
    }

    private String fecha(File archivo) {
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        calendario.setTimeInMillis(archivo.lastModified());

        int dia = calendario.get(java.util.Calendar.DAY_OF_MONTH);
        int mes = calendario.get(java.util.Calendar.MONTH) + 1;
        int anio = calendario.get(java.util.Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }

    private void abrirDeLaTabla() {
        File elegido = seleccionado();

        if (elegido == null) {
            return;
        }

        if (elegido.isDirectory()) {
            mostrarContenido(elegido);
        } else {
            abrir(elegido);
        }
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

        if (carpetaActual == null) {
            carpetaActual = raiz;
        }

        refrescarTabla();
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

    private File seleccionadoDelArbol() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();

        if (nodo == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un archivo o carpeta");
            return null;
        }

        ArchivoNodo dato = (ArchivoNodo) nodo.getUserObject();
        return dato.getArchivo();
    }

    private File seleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila >= 0 && carpetaActual != null) {
            return new File(carpetaActual, (String) modeloTabla.getValueAt(fila, 0));
        }

        return seleccionadoDelArbol();
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

        copiarArchivo(copiado, new File(destino, copiado.getName()));
        recargar();
    }

    private void importar() {
        File destino = seleccionado();

        if (destino == null) {
            JOptionPane.showMessageDialog(this, "Primero selecciona la carpeta donde quieres guardarlo");
            return;
        }

        if (!destino.isDirectory()) {
            destino = destino.getParentFile();
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Elige los archivos de tu computadora");
        selector.setMultiSelectionEnabled(true);

        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] elegidos = selector.getSelectedFiles();
        int copiados = 0;

        for (int i = 0; i < elegidos.length; i++) {
            if (copiarArchivo(elegidos[i], new File(destino, elegidos[i].getName()))) {
                copiados++;
            }
        }

        JOptionPane.showMessageDialog(this, "Se importaron " + copiados + " archivos a " + destino.getName());
        recargar();
    }

    private boolean copiarArchivo(File origen, File destino) {
        try (FileInputStream entrada = new FileInputStream(origen);
                FileOutputStream salida = new FileOutputStream(destino)) {

            byte[] datos = new byte[4096];
            int leidos = entrada.read(datos);

            while (leidos > 0) {
                salida.write(datos, 0, leidos);
                leidos = entrada.read(datos);
            }

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo copiar " + origen.getName());
            return false;
        }
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
        File archivo = seleccionadoDelArbol();

        if (archivo != null) {
            abrir(archivo);
        }
    }

    private void abrir(File archivo) {
        if (archivo.isDirectory()) {
            return;
        }

        String nombre = archivo.getName();

        if (OrganizadorArchivos.esImagen(nombre)) {
            escritorio.mostrar(new VisorImagenes(archivo.getParent()));
        } else if (OrganizadorArchivos.esMusica(nombre)) {
            escritorio.mostrar(new ReproductorMusica(archivo.getParent(), escritorio.carpetaMusica()));
        } else if (nombre.toLowerCase().endsWith(".txt")) {
            EditorTexto editor = new EditorTexto(archivo.getParent());
            escritorio.mostrar(editor);
            editor.abrirArchivo(archivo);
        } else {
            JOptionPane.showMessageDialog(this, "No hay un programa para abrir " + nombre);
        }
    }
}
