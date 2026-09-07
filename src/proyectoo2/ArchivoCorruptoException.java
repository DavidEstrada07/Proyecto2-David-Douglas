package proyectoo2;

public class ArchivoCorruptoException extends Exception {

    public ArchivoCorruptoException(String nombreArchivo) {
        super("No se pudo leer el archivo " + nombreArchivo + " porque esta danado");
    }
}
