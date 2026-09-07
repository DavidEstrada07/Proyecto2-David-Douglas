package proyectoo2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HiloCliente extends Thread {

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String usuario;

    public HiloCliente(Socket socket) {
        this.socket = socket;
        usuario = null;
    }

    public String getUsuario() {
        return usuario;
    }

    public void enviar(String texto) {
        if (salida != null) {
            salida.println(texto);
        }
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            String linea = entrada.readLine();

            while (linea != null) {
                atender(linea);
                linea = entrada.readLine();
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado");
        } finally {
            ServidorInsta.quitarCliente(this);
            cerrar();
        }
    }

    private void atender(String linea) {
        String[] partes = linea.split(";", 5);
        String comando = partes[0];

        if (comando.equals("LOGIN") && partes.length >= 2) {
            usuario = partes[1];
            enviar("OK;conectado");
            return;
        }

        if (comando.equals("MSG") && partes.length >= 5) {
            guardarMensaje(partes[1], partes[2], partes[3], partes[4]);
        }
    }

    private void guardarMensaje(String emisor, String receptor, String tipo, String contenido) {
        try {
            TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

            if (tipo.equals("STICKER")) {
                tipoMensaje = TipoMensaje.STICKER;
            }

            ArchivoMensajes.guardar(new Mensaje(emisor, receptor, contenido, tipoMensaje));
            ServidorInsta.avisarNuevoMensaje(emisor, receptor);
        } catch (ArchivoCorruptoException e) {
            enviar("ERROR;" + e.getMessage());
        } catch (IllegalArgumentException e) {
            enviar("ERROR;" + e.getMessage());
        }
    }

    private void cerrar() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("No se pudo cerrar la conexion");
        }
    }
}
