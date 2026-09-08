package proyectoo2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Usuario implements Serializable {

    private String nombreCompleto;
    private char genero;
    private String username;
    private String password;
    private Date fechaRegistro;
    private int edad;
    private boolean cuentaActiva;
    private String fotoPerfil;

    public Usuario(String nombreCompleto, char genero, String username, String password, int edad, String fotoPerfil) {
        this.username = username;
        this.fotoPerfil = fotoPerfil;

        setNombreCompleto(nombreCompleto);
        setGenero(genero);
        setPassword(password);
        setEdad(edad);

        fechaRegistro = new Date();
        cuentaActiva = true;
    }

    public String getUsername() {
        return username;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (!soloLetras(nombreCompleto)) {
            throw new IllegalArgumentException("El nombre solo puede tener letras y espacios");
        }

        this.nombreCompleto = nombreCompleto;
    }

    private boolean soloLetras(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            char letra = texto.charAt(i);

            if (!Character.isLetter(letra) && letra != ' ') {
                return false;
            }
        }

        return true;
    }

    public char getGenero() {
        return genero;
    }

    public void setGenero(char genero) {
        char letra = Character.toUpperCase(genero);

        if (letra != 'M' && letra != 'F') {
            throw new IllegalArgumentException("El genero debe ser M o F");
        }

        this.genero = letra;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        this.password = password;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("Edad inválida");
        }

        this.edad = edad;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public boolean estaActiva() {
        return cuentaActiva;
    }

    public void activarCuenta() {
        cuentaActiva = true;
    }

    public void desactivarCuenta() {
        cuentaActiva = false;
    }

    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }

    public boolean coincideConBusqueda(String texto) {
        return username.toLowerCase().contains(texto.toLowerCase());
    }

    public String obtenerFechaRegistroTexto() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fechaRegistro);

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        return dia + "/" + mes + "/" + anio;
    }

    @Override
    public String toString() {
        String estado = "Activa";

        if (!cuentaActiva) {
            estado = "Inactiva";
        }

        return "Nombre: " + nombreCompleto + ", Username: " + username + ", Edad: " + edad
                + ", Genero: " + genero + ", Registro: " + obtenerFechaRegistroTexto()
                + ", Cuenta: " + estado;
    }
}
