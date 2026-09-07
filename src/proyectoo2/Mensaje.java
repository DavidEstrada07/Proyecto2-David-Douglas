package proyectoo2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Mensaje implements Serializable {

    private static final int MAX_MENSAJE = 300;

    private String emisor;
    private String receptor;
    private Date fechaHora;
    private String contenido;
    private TipoMensaje tipo;
    private boolean leido;

    public Mensaje(String emisor, String receptor, String contenido, TipoMensaje tipo) {
        if (tipo == TipoMensaje.TEXTO) {
            verificarTexto(contenido);
        }

        this.emisor = emisor;
        this.receptor = receptor;
        this.contenido = contenido;
        this.tipo = tipo;

        fechaHora = new Date();
        leido = false;
    }

    private void verificarTexto(String contenido) {
        if (contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacio");
        }

        if (contenido.length() > MAX_MENSAJE) {
            throw new IllegalArgumentException("El mensaje no puede pasar de " + MAX_MENSAJE + " caracteres");
        }
    }

    public String getEmisor() {
        return emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getContenido() {
        return contenido;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public boolean esSticker() {
        return tipo == TipoMensaje.STICKER;
    }

    public boolean estaLeido() {
        return leido;
    }

    public void marcarComoLeido() {
        leido = true;
    }

    public boolean esParaUsuario(String username) {
        return receptor.equalsIgnoreCase(username);
    }

    public boolean esConversacionEntre(String uno, String otro) {
        boolean enviado = emisor.equalsIgnoreCase(uno) && receptor.equalsIgnoreCase(otro);
        boolean recibido = emisor.equalsIgnoreCase(otro) && receptor.equalsIgnoreCase(uno);

        return enviado || recibido;
    }

    public String obtenerFechaTexto() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fechaHora);

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        String minutoTexto = "" + minuto;

        if (minuto < 10) {
            minutoTexto = "0" + minuto;
        }

        return dia + "/" + mes + "/" + anio + " " + hora + ":" + minutoTexto;
    }

    @Override
    public String toString() {
        String texto = contenido;

        if (esSticker()) {
            texto = "[Sticker]";
        }

        return emisor + " para " + receptor + ": " + texto + " - " + obtenerFechaTexto();
    }
}
