package proyectoo2;

public class UsuarioSistema {

    public static final int MAX_USERNAME = 15;
    public static final int MAX_PASSWORD = 15;

    private static final String UNIDAD_RAIZ = "Z:\\";

    private String username;
    private String password;
    private boolean administrador;

    public UsuarioSistema(String username, String password, boolean administrador) {
        verificarDatos(username, password);

        this.username = username;
        this.password = password;
        this.administrador = administrador;
    }

    private void verificarDatos(String username, String password) {
        if (username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario no puede estar vacio");
        }

        if (username.length() > MAX_USERNAME) {
            throw new IllegalArgumentException("El usuario no puede pasar de " + MAX_USERNAME + " caracteres");
        }

        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contrasena no puede estar vacia");
        }

        if (password.length() > MAX_PASSWORD) {
            throw new IllegalArgumentException("La contrasena no puede pasar de " + MAX_PASSWORD + " caracteres");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        verificarDatos(username, password);

        this.password = password;
    }

    public boolean esAdministrador() {
        return administrador;
    }

    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }

    public String obtenerCarpetaRaiz() {
        return UNIDAD_RAIZ + username;
    }

    public boolean puedeAccederA(String username) {
        if (administrador) {
            return true;
        }

        return this.username.equalsIgnoreCase(username);
    }

    @Override
    public String toString() {
        String tipo = "Estandar";

        if (administrador) {
            tipo = "Administrador";
        }

        return username + " (" + tipo + ") - " + obtenerCarpetaRaiz();
    }
}
