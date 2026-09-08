package proyectoo2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class ClienteInsta extends Thread {

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private EscuchaMensajes escucha;
    private boolean conectado;

    public ClienteInsta(String username, EscuchaMensajes escucha) {
        this.escucha = escucha;
        conectado = false;

        try {
            socket = new Socket("127.0.0.1", ServidorInsta.PUERTO);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            salida.println("LOGIN;" + username);
            conectado = true;

            setDaemon(true);
            start();
        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    public boolean estaConectado() {
        return conectado;
    }

    public void enviarMensaje(String emisor, String receptor, TipoMensaje tipo, String contenido) {
        if (!conectado) {
            return;
        }

        salida.println("MSG;" + emisor + ";" + receptor + ";" + tipo.toString() + ";" + contenido);
    }

    @Override
    public void run() {
        try {
            String linea = entrada.readLine();

            while (linea != null) {
                procesar(linea);
                linea = entrada.readLine();
            }
        } catch (Exception e) {
            System.out.println("Se perdio la conexion con el servidor");
        } finally {
            conectado = false;
        }
    }

    private void procesar(String linea) {
        String[] partes = linea.split(";", 3);

        if (!partes[0].equals("NUEVO") || partes.length < 3) {
            return;
        }

        final String emisor = partes[1];
        final String receptor = partes[2];

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                escucha.mensajeRecibido(emisor, receptor);
            }
        });
    }

    public void desconectar() {
        try {
            conectado = false;
            socket.close();
        } catch (Exception e) {
            System.out.println("No se pudo cerrar el cliente");
        }
    }
}
