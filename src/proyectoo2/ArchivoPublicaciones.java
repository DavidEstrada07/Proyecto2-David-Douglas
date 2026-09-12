package proyectoo2;

public class ArchivoPublicaciones {

    private static String ruta(String username) {
        return SistemaArchivos.archivoInsta(username, "insta.ins");
    }

    public static ListaEnlazada listar(String username) throws ArchivoCorruptoException {
        return ArchivoBinario.leer(ruta(username));
    }

    public static void publicar(Publicacion publicacion) throws ArchivoCorruptoException {
        ArchivoBinario.agregar(ruta(publicacion.getAutor()), publicacion);
    }

    public static ListaEnlazada listarOrdenadas(String username) throws ArchivoCorruptoException {
        return ordenar(listar(username));
    }

    public static ListaEnlazada armarDescubrir(String username) throws ArchivoCorruptoException {
        ListaEnlazada timeline = new ListaEnlazada();
        ListaEnlazada usuarios = ArchivoUsuarios.listar();

        for (int i = 0; i < usuarios.getTamano(); i++) {
            Usuario usuario = (Usuario) usuarios.obtener(i);

            if (usuario.estaActiva() && puedeVer(usuario, username)) {
                agregarTodas(timeline, listar(usuario.getUsername()));
            }
        }

        return ordenar(timeline);
    }

    public static ListaEnlazada armarTimeline(String username) throws ArchivoCorruptoException {
        ListaEnlazada timeline = new ListaEnlazada();

        agregarTodas(timeline, listar(username));

        ListaEnlazada siguiendo = ArchivoSeguidores.listarFollowing(username);

        for (int i = 0; i < siguiendo.getTamano(); i++) {
            String seguido = (String) siguiendo.obtener(i);

            if (cuentaVisible(seguido)) {
                agregarTodas(timeline, listar(seguido));
            }
        }

        return ordenar(timeline);
    }

    public static boolean puedeVer(Usuario duenio, String quienMira) throws ArchivoCorruptoException {
        boolean loSigue = ArchivoSeguidores.sigue(quienMira, duenio.getUsername());

        return duenio.puedeVerSusPublicaciones(quienMira, loSigue);
    }

    public static ListaEnlazada buscarPorHashtag(String palabra, String quienMira) throws ArchivoCorruptoException {
        ListaEnlazada resultado = new ListaEnlazada();
        ListaEnlazada usuarios = ArchivoUsuarios.listar();

        for (int i = 0; i < usuarios.getTamano(); i++) {
            Usuario usuario = (Usuario) usuarios.obtener(i);

            if (!usuario.estaActiva() || !puedeVer(usuario, quienMira)) {
                continue;
            }

            ListaEnlazada publicaciones = listar(usuario.getUsername());

            for (int j = 0; j < publicaciones.getTamano(); j++) {
                Publicacion publicacion = (Publicacion) publicaciones.obtener(j);

                if (publicacion.contieneHashtag(palabra) && !resultado.contiene(publicacion)) {
                    resultado.agregar(publicacion);
                }
            }
        }

        return resultado;
    }

    public static ListaEnlazada buscarMenciones(String username) throws ArchivoCorruptoException {
        ListaEnlazada resultado = new ListaEnlazada();
        ListaEnlazada usuarios = ArchivoUsuarios.listar();

        for (int i = 0; i < usuarios.getTamano(); i++) {
            Usuario usuario = (Usuario) usuarios.obtener(i);

            if (!usuario.estaActiva() || usuario.getUsername().equalsIgnoreCase(username)) {
                continue;
            }

            if (!puedeVer(usuario, username)) {
                continue;
            }

            ListaEnlazada publicaciones = listar(usuario.getUsername());

            for (int j = 0; j < publicaciones.getTamano(); j++) {
                Publicacion publicacion = (Publicacion) publicaciones.obtener(j);

                if (publicacion.mencionaA(username) && !resultado.contiene(publicacion)) {
                    resultado.agregar(publicacion);
                }
            }
        }

        return ordenar(resultado);
    }

    private static void agregarTodas(ListaEnlazada destino, ListaEnlazada origen) {
        for (int i = 0; i < origen.getTamano(); i++) {
            destino.agregar(origen.obtener(i));
        }
    }

    private static boolean cuentaVisible(String username) throws ArchivoCorruptoException {
        Usuario usuario = ArchivoUsuarios.buscar(username);
        return usuario != null && usuario.estaActiva();
    }

    private static ListaEnlazada ordenar(ListaEnlazada lista) {
        ListaEnlazada ordenada = new ListaEnlazada();

        while (!lista.estaVacia()) {
            Publicacion mayor = (Publicacion) lista.obtener(0);

            for (int i = 1; i < lista.getTamano(); i++) {
                Publicacion actual = (Publicacion) lista.obtener(i);

                if (actual.esMasRecienteQue(mayor)) {
                    mayor = actual;
                }
            }

            ordenada.agregar(mayor);
            lista.eliminar(mayor);
        }

        return ordenada;
    }
}
