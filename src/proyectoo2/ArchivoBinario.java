package proyectoo2;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ArchivoBinario {

    public static synchronized void guardar(String ruta, ListaEnlazada lista) throws ArchivoCorruptoException {
        ObjectOutputStream salida = null;

        try {
            salida = new ObjectOutputStream(new FileOutputStream(ruta));

            for (int i = 0; i < lista.getTamano(); i++) {
                salida.writeObject(lista.obtener(i));
            }
        } catch (FileNotFoundException e) {
            throw new ArchivoCorruptoException(ruta);
        } catch (Exception e) {
            throw new ArchivoCorruptoException(ruta);
        } finally {
            cerrarSalida(salida);
        }
    }

    public static synchronized ListaEnlazada leer(String ruta) throws ArchivoCorruptoException {
        ListaEnlazada lista = new ListaEnlazada();
        File archivo = new File(ruta);

        if (!archivo.exists() || archivo.length() == 0) {
            return lista;
        }

        ObjectInputStream entrada = null;

        try {
            entrada = new ObjectInputStream(new FileInputStream(archivo));

            while (true) {
                lista.agregar(entrada.readObject());
            }
        } catch (EOFException e) {
            return lista;
        } catch (ClassNotFoundException e) {
            throw new ArchivoCorruptoException(ruta);
        } catch (Exception e) {
            throw new ArchivoCorruptoException(ruta);
        } finally {
            cerrarEntrada(entrada);
        }
    }

    public static void agregar(String ruta, Object dato) throws ArchivoCorruptoException {
        ListaEnlazada lista = leer(ruta);

        lista.agregar(dato);
        guardar(ruta, lista);
    }

    private static void cerrarSalida(ObjectOutputStream salida) {
        try {
            if (salida != null) {
                salida.close();
            }
        } catch (Exception e) {
            System.out.println("No se pudo cerrar el archivo");
        }
    }

    private static void cerrarEntrada(ObjectInputStream entrada) {
        try {
            if (entrada != null) {
                entrada.close();
            }
        } catch (Exception e) {
            System.out.println("No se pudo cerrar el archivo");
        }
    }
}
