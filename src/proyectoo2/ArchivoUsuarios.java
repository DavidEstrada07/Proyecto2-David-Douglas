package proyectoo2;

public class ArchivoUsuarios {

    public static ListaEnlazada listar() throws ArchivoCorruptoException {
        return ArchivoBinario.leer(SistemaArchivos.archivoUsuarios());
    }

    public static void guardar(Usuario usuario) throws UsernameDuplicadoException, ArchivoCorruptoException {
        if (buscar(usuario.getUsername()) != null) {
            throw new UsernameDuplicadoException(usuario.getUsername());
        }

        ArchivoBinario.agregar(SistemaArchivos.archivoUsuarios(), usuario);
        SistemaArchivos.crearEspacioInsta(usuario.getUsername());
        ArchivoStickers.crearPorDefecto(usuario.getUsername());
    }

    public static Usuario buscar(String username) throws ArchivoCorruptoException {
        ListaEnlazada lista = listar();

        for (int i = 0; i < lista.getTamano(); i++) {
            Usuario usuario = (Usuario) lista.obtener(i);

            if (usuario.getUsername().equalsIgnoreCase(username)) {
                return usuario;
            }
        }

        return null;
    }

    public static Usuario iniciarSesion(String username, String password)
            throws CuentaDesactivadaException, ArchivoCorruptoException {

        Usuario usuario = buscar(username);

        if (usuario == null || !usuario.verificarPassword(password)) {
            return null;
        }

        if (!usuario.estaActiva()) {
            throw new CuentaDesactivadaException(username);
        }

        return usuario;
    }

    public static void actualizar(Usuario modificado) throws ArchivoCorruptoException {
        ListaEnlazada lista = listar();
        ListaEnlazada nueva = new ListaEnlazada();

        for (int i = 0; i < lista.getTamano(); i++) {
            Usuario usuario = (Usuario) lista.obtener(i);

            if (usuario.getUsername().equalsIgnoreCase(modificado.getUsername())) {
                nueva.agregar(modificado);
            } else {
                nueva.agregar(usuario);
            }
        }

        ArchivoBinario.guardar(SistemaArchivos.archivoUsuarios(), nueva);
    }

    public static ListaEnlazada buscarPorTexto(String texto) throws ArchivoCorruptoException {
        ListaEnlazada lista = listar();
        ListaEnlazada resultado = new ListaEnlazada();

        for (int i = 0; i < lista.getTamano(); i++) {
            Usuario usuario = (Usuario) lista.obtener(i);

            if (usuario.estaActiva() && usuario.coincideConBusqueda(texto)) {
                resultado.agregar(usuario);
            }
        }

        return resultado;
    }
}
