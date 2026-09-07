package proyectoo2;

public class UsernameDuplicadoException extends Exception {

    public UsernameDuplicadoException(String username) {
        super("El username " + username + " ya existe en el sistema");
    }
}
