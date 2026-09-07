package proyectoo2;

import java.io.Serializable;

public class Sticker implements Serializable {

    private String nombre;
    private String ruta;
    private boolean personal;

    public Sticker(String nombre, String ruta, boolean personal) {
        verificarFormato(ruta);

        this.nombre = nombre;
        this.ruta = ruta;
        this.personal = personal;
    }

    private void verificarFormato(String ruta) {
        String rutaMinuscula = ruta.toLowerCase();

        if (!rutaMinuscula.endsWith(".png") && !rutaMinuscula.endsWith(".jpg")) {
            throw new IllegalArgumentException("El sticker debe ser un archivo .png o .jpg");
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public boolean esPersonal() {
        return personal;
    }

    public boolean seLlama(String nombre) {
        return this.nombre.equalsIgnoreCase(nombre);
    }

    @Override
    public String toString() {
        String origen = "Global";

        if (personal) {
            origen = "Personal";
        }

        return nombre + " (" + origen + ")";
    }
}
