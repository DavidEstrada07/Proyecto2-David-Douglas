package proyectoo2;

public class CuentaDesactivadaException extends Exception {

    public CuentaDesactivadaException(String username) {
        super("La cuenta " + username + " esta desactivada");
    }
}
