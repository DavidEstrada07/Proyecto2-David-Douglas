package proyectoo2;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class OrganizadorArchivos implements Runnable {

    private File carpeta;
    private ExploradorArchivos explorador;

    public OrganizadorArchivos(File carpeta, ExploradorArchivos explorador) {
        this.carpeta = carpeta;
        this.explorador = explorador;
    }

    @Override
    public void run() {
        ListaEnlazada imagenes = new ListaEnlazada();
        ListaEnlazada documentos = new ListaEnlazada();
        ListaEnlazada musica = new ListaEnlazada();

        clasificar(imagenes, documentos, musica);

        int movidos = 0;

        movidos = movidos + mover(imagenes, "Imagenes");
        movidos = movidos + mover(documentos, "Documentos");
        movidos = movidos + mover(musica, "Musica");

        avisar(movidos);
    }

    private void clasificar(ListaEnlazada imagenes, ListaEnlazada documentos, ListaEnlazada musica) {
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return;
        }

        for (int i = 0; i < archivos.length; i++) {
            File archivo = archivos[i];

            if (archivo.isDirectory()) {
                continue;
            }

            String nombre = archivo.getName().toLowerCase();

            if (esImagen(nombre)) {
                imagenes.agregar(archivo);
            } else if (esMusica(nombre)) {
                musica.agregar(archivo);
            } else if (esDocumento(nombre)) {
                documentos.agregar(archivo);
            }
        }
    }

    public static boolean esImagen(String nombre) {
        String texto = nombre.toLowerCase();
        return texto.endsWith(".png") || texto.endsWith(".jpg") || texto.endsWith(".jpeg")
                || texto.endsWith(".gif") || texto.endsWith(".bmp");
    }

    public static boolean esMusica(String nombre) {
        String texto = nombre.toLowerCase();
        return texto.endsWith(".mp3") || texto.endsWith(".wav") || texto.endsWith(".au")
                || texto.endsWith(".aiff");
    }

    public static boolean esDocumento(String nombre) {
        String texto = nombre.toLowerCase();
        return texto.endsWith(".txt") || texto.endsWith(".pdf") || texto.endsWith(".doc")
                || texto.endsWith(".docx");
    }

    private int mover(ListaEnlazada lista, String nombreCarpeta) {
        if (lista.estaVacia()) {
            return 0;
        }

        File destino = new File(carpeta, nombreCarpeta);

        if (!destino.exists()) {
            destino.mkdir();
        }

        int movidos = 0;

        for (int i = 0; i < lista.getTamano(); i++) {
            File archivo = (File) lista.obtener(i);

            if (archivo.renameTo(new File(destino, archivo.getName()))) {
                movidos++;
            }

            dormir();
        }

        return movidos;
    }

    private void dormir() {
        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            System.out.println("Organización interrumpida");
        }
    }

    private void avisar(int movidos) {
        final int cantidad = movidos;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                explorador.recargar();
                JOptionPane.showMessageDialog(explorador, "Se organizaron " + cantidad + " archivos");
            }
        });
    }
}
