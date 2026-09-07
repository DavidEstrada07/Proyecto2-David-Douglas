package proyectoo2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ArchivoUsuariosSistema {

    private static final int TAMANO_REGISTRO = (UsuarioSistema.MAX_USERNAME + UsuarioSistema.MAX_PASSWORD) * 2 + 1;

    public static synchronized void guardar(UsuarioSistema usuario)
            throws UsernameDuplicadoException, ArchivoCorruptoException {

        if (existe(usuario.getUsername())) {
            throw new UsernameDuplicadoException(usuario.getUsername());
        }

        RandomAccessFile archivo = null;

        try {
            archivo = new RandomAccessFile(SistemaArchivos.ARCHIVO_SISTEMA, "rw");
            archivo.seek(archivo.length());

            escribirTexto(archivo, usuario.getUsername(), UsuarioSistema.MAX_USERNAME);
            escribirTexto(archivo, usuario.getPassword(), UsuarioSistema.MAX_PASSWORD);
            archivo.writeBoolean(usuario.esAdministrador());
        } catch (IOException e) {
            throw new ArchivoCorruptoException(SistemaArchivos.ARCHIVO_SISTEMA);
        } finally {
            cerrar(archivo);
        }
    }

    public static synchronized ListaEnlazada listar() throws ArchivoCorruptoException {
        ListaEnlazada lista = new ListaEnlazada();
        File verificar = new File(SistemaArchivos.ARCHIVO_SISTEMA);

        if (!verificar.exists()) {
            return lista;
        }

        RandomAccessFile archivo = null;

        try {
            archivo = new RandomAccessFile(SistemaArchivos.ARCHIVO_SISTEMA, "r");
            int cantidad = (int) (archivo.length() / TAMANO_REGISTRO);

            for (int i = 0; i < cantidad; i++) {
                archivo.seek(i * TAMANO_REGISTRO);

                String username = leerTexto(archivo, UsuarioSistema.MAX_USERNAME);
                String password = leerTexto(archivo, UsuarioSistema.MAX_PASSWORD);
                boolean administrador = archivo.readBoolean();

                lista.agregar(new UsuarioSistema(username, password, administrador));
            }
        } catch (IOException e) {
            throw new ArchivoCorruptoException(SistemaArchivos.ARCHIVO_SISTEMA);
        } finally {
            cerrar(archivo);
        }

        return lista;
    }

    public static UsuarioSistema buscar(String username) throws ArchivoCorruptoException {
        ListaEnlazada lista = listar();

        for (int i = 0; i < lista.getTamano(); i++) {
            UsuarioSistema usuario = (UsuarioSistema) lista.obtener(i);

            if (usuario.getUsername().equalsIgnoreCase(username)) {
                return usuario;
            }
        }

        return null;
    }

    public static boolean existe(String username) throws ArchivoCorruptoException {
        return buscar(username) != null;
    }

    public static boolean hayUsuarios() throws ArchivoCorruptoException {
        return listar().getTamano() > 0;
    }

    private static void escribirTexto(RandomAccessFile archivo, String texto, int largo) throws IOException {
        String completo = texto;

        while (completo.length() < largo) {
            completo = completo + " ";
        }

        archivo.writeChars(completo.substring(0, largo));
    }

    private static String leerTexto(RandomAccessFile archivo, int largo) throws IOException {
        String texto = "";

        for (int i = 0; i < largo; i++) {
            texto = texto + archivo.readChar();
        }

        return texto.trim();
    }

    private static void cerrar(RandomAccessFile archivo) {
        try {
            if (archivo != null) {
                archivo.close();
            }
        } catch (IOException e) {
            System.out.println("No se pudo cerrar usuarios.sop");
        }
    }
}
