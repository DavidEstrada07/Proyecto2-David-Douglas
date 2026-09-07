package proyectoo2;

import java.io.File;

public class ArchivoNodo {

    private File archivo;

    public ArchivoNodo(File archivo) {
        this.archivo = archivo;
    }

    public File getArchivo() {
        return archivo;
    }

    @Override
    public String toString() {
        if (archivo.getName().isEmpty()) {
            return archivo.getPath();
        }

        if (archivo.isDirectory()) {
            return archivo.getName();
        }

        return archivo.getName() + "   (" + archivo.length() / 1024 + " KB)";
    }
}
