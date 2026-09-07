package proyectoo2;

public class ArchivoMensajes {

    private static String ruta(String username) {
        return SistemaArchivos.archivoInsta(username, "inbox.ins");
    }

    public static ListaEnlazada listar(String username) throws ArchivoCorruptoException {
        return ArchivoBinario.leer(ruta(username));
    }

    public static synchronized void guardar(Mensaje mensaje) throws ArchivoCorruptoException {
        ArchivoBinario.agregar(ruta(mensaje.getEmisor()), mensaje);

        if (!mensaje.getEmisor().equalsIgnoreCase(mensaje.getReceptor())) {
            ArchivoBinario.agregar(ruta(mensaje.getReceptor()), mensaje);
        }
    }

    public static ListaEnlazada conversacion(String usuario, String otro) throws ArchivoCorruptoException {
        ListaEnlazada todos = listar(usuario);
        ListaEnlazada resultado = new ListaEnlazada();

        for (int i = 0; i < todos.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) todos.obtener(i);

            if (mensaje.esConversacionEntre(usuario, otro)) {
                resultado.agregar(mensaje);
            }
        }

        return resultado;
    }

    public static ListaEnlazada contactos(String usuario) throws ArchivoCorruptoException {
        ListaEnlazada todos = listar(usuario);
        ListaEnlazada contactos = new ListaEnlazada();

        for (int i = 0; i < todos.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) todos.obtener(i);
            String otro = mensaje.getEmisor();

            if (otro.equalsIgnoreCase(usuario)) {
                otro = mensaje.getReceptor();
            }

            if (!contactos.contiene(otro)) {
                contactos.agregar(otro);
            }
        }

        return contactos;
    }

    public static int contarNoLeidos(String usuario) throws ArchivoCorruptoException {
        ListaEnlazada todos = listar(usuario);
        int cantidad = 0;

        for (int i = 0; i < todos.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) todos.obtener(i);

            if (mensaje.esParaUsuario(usuario) && !mensaje.estaLeido()) {
                cantidad++;
            }
        }

        return cantidad;
    }

    public static synchronized void marcarLeidos(String usuario, String otro) throws ArchivoCorruptoException {
        ListaEnlazada todos = listar(usuario);
        boolean cambio = false;

        for (int i = 0; i < todos.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) todos.obtener(i);

            if (mensaje.esParaUsuario(usuario) && mensaje.getEmisor().equalsIgnoreCase(otro)
                    && !mensaje.estaLeido()) {
                mensaje.marcarComoLeido();
                cambio = true;
            }
        }

        if (cambio) {
            ArchivoBinario.guardar(ruta(usuario), todos);
        }
    }

    public static synchronized void eliminarConversacion(String usuario, String otro) throws ArchivoCorruptoException {
        ListaEnlazada todos = listar(usuario);
        ListaEnlazada nueva = new ListaEnlazada();

        for (int i = 0; i < todos.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) todos.obtener(i);

            if (!mensaje.esConversacionEntre(usuario, otro)) {
                nueva.agregar(mensaje);
            }
        }

        ArchivoBinario.guardar(ruta(usuario), nueva);
    }
}
