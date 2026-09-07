package proyectoo2;

public class ArchivoSeguidores {

    private static String rutaFollowers(String username) {
        return SistemaArchivos.archivoInsta(username, "followers.ins");
    }

    private static String rutaFollowing(String username) {
        return SistemaArchivos.archivoInsta(username, "following.ins");
    }

    public static ListaEnlazada listarFollowers(String username) throws ArchivoCorruptoException {
        return ArchivoBinario.leer(rutaFollowers(username));
    }

    public static ListaEnlazada listarFollowing(String username) throws ArchivoCorruptoException {
        return ArchivoBinario.leer(rutaFollowing(username));
    }

    public static boolean sigue(String username, String otro) throws ArchivoCorruptoException {
        return listarFollowing(username).contiene(otro);
    }

    public static void seguir(String username, String otro) throws ArchivoCorruptoException {
        ListaEnlazada following = listarFollowing(username);

        if (!following.contiene(otro)) {
            following.agregar(otro);
            ArchivoBinario.guardar(rutaFollowing(username), following);
        }

        ListaEnlazada followers = listarFollowers(otro);

        if (!followers.contiene(username)) {
            followers.agregar(username);
            ArchivoBinario.guardar(rutaFollowers(otro), followers);
        }
    }

    public static void dejarDeSeguir(String username, String otro) throws ArchivoCorruptoException {
        ListaEnlazada following = listarFollowing(username);

        if (following.eliminar(otro)) {
            ArchivoBinario.guardar(rutaFollowing(username), following);
        }

        ListaEnlazada followers = listarFollowers(otro);

        if (followers.eliminar(username)) {
            ArchivoBinario.guardar(rutaFollowers(otro), followers);
        }
    }
}
