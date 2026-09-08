package proyectoo2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Publicacion implements Serializable {

    private static final int MAX_TEXTO = 140;
    private static final int MAX_DESCRIPCION = 220;

    private String autor;
    private String contenido;
    private Date fecha;
    private String rutaImagen;
    private String carpetaPersonal;
    private String rutaSticker;

    public Publicacion(String autor, String contenido) {
        verificarContenido(contenido, MAX_TEXTO);

        this.autor = autor;
        this.contenido = contenido;
        fecha = new Date();
    }

    public Publicacion(String autor, String contenido, String rutaImagen, String carpetaPersonal) {
        verificarContenido(contenido, MAX_DESCRIPCION);

        this.autor = autor;
        this.contenido = contenido;
        this.rutaImagen = rutaImagen;
        this.carpetaPersonal = carpetaPersonal;
        fecha = new Date();
    }

    private void verificarContenido(String contenido, int maximo) {
        if (contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("La publicación no puede estar vacía");
        }

        if (contenido.length() > maximo) {
            throw new IllegalArgumentException("La publicación no puede pasar de " + maximo + " caracteres");
        }
    }

    public String getAutor() {
        return autor;
    }

    public String getContenido() {
        return contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public String getCarpetaPersonal() {
        return carpetaPersonal;
    }

    public String getRutaSticker() {
        return rutaSticker;
    }

    public void setRutaSticker(String rutaSticker) {
        this.rutaSticker = rutaSticker;
    }

    public boolean tieneImagen() {
        return rutaImagen != null;
    }

    public boolean tieneSticker() {
        return rutaSticker != null;
    }

    public boolean esDelAutor(String username) {
        return autor.equalsIgnoreCase(username);
    }

    public boolean mencionaA(String username) {
        return contenido.toLowerCase().contains("@" + username.toLowerCase());
    }

    public boolean contieneHashtag(String palabra) {
        return contenido.toLowerCase().contains("#" + palabra.toLowerCase());
    }

    public boolean esMasRecienteQue(Publicacion otra) {
        return fecha.after(otra.getFecha());
    }

    public String obtenerFechaTexto() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fecha);

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }

    @Override
    public String toString() {
        return autor + " escribio:\n\"" + contenido + "\" - " + obtenerFechaTexto();
    }
}
