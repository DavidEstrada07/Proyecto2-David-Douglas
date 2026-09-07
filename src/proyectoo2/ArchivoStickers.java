package proyectoo2;

public class ArchivoStickers {

    private static String ruta(String username) {
        return SistemaArchivos.archivoInsta(username, "stickers.ins");
    }

    public static void crearPorDefecto(String username) throws ArchivoCorruptoException {
        ListaEnlazada lista = new ListaEnlazada();
        String[] nombres = SistemaArchivos.nombresStickers();

        for (int i = 0; i < nombres.length; i++) {
            lista.agregar(new Sticker(nombres[i], SistemaArchivos.rutaStickerGlobal(nombres[i]), false));
        }

        ArchivoBinario.guardar(ruta(username), lista);
    }

    public static ListaEnlazada listar(String username) throws ArchivoCorruptoException {
        return ArchivoBinario.leer(ruta(username));
    }

    public static void agregar(String username, Sticker sticker) throws ArchivoCorruptoException {
        ArchivoBinario.agregar(ruta(username), sticker);
    }

    public static Sticker buscarPorRuta(String username, String rutaSticker) throws ArchivoCorruptoException {
        ListaEnlazada lista = listar(username);

        for (int i = 0; i < lista.getTamano(); i++) {
            Sticker sticker = (Sticker) lista.obtener(i);

            if (sticker.getRuta().equals(rutaSticker)) {
                return sticker;
            }
        }

        return null;
    }
}
