package proyectoo2;

import java.net.ServerSocket;
import java.net.Socket;

public class ServidorInsta extends Thread {

    public static final int PUERTO = 5000;

    private static ListaEnlazada conectados = new ListaEnlazada();

    private ServerSocket servidor;

    public ServidorInsta(ServerSocket servidor) {
        this.servidor = servidor;
    }

    public static ServidorInsta iniciar() {
        try {
            ServerSocket socket = new ServerSocket(PUERTO);
            ServidorInsta servidor = new ServidorInsta(socket);

            servidor.setDaemon(true);
            servidor.start();

            System.out.println("Servidor INSTA+ escuchando en el puerto " + PUERTO);
            return servidor;
        } catch (Exception e) {
            System.out.println("El servidor ya estaba corriendo, esta instancia sera solo cliente");
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = servidor.accept();
                HiloCliente hilo = new HiloCliente(socket);

                agregarCliente(hilo);
                hilo.setDaemon(true);
                hilo.start();
            } catch (Exception e) {
                System.out.println("Error aceptando conexion: " + e.getMessage());
                return;
            }
        }
    }

    public static synchronized void agregarCliente(HiloCliente hilo) {
        conectados.agregar(hilo);
    }

    public static synchronized void quitarCliente(HiloCliente hilo) {
        conectados.eliminar(hilo);
    }

    public static synchronized void avisarNuevoMensaje(String emisor, String receptor) {
        for (int i = 0; i < conectados.getTamano(); i++) {
            HiloCliente hilo = (HiloCliente) conectados.obtener(i);
            String usuario = hilo.getUsuario();

            if (usuario == null) {
                continue;
            }

            if (usuario.equalsIgnoreCase(emisor) || usuario.equalsIgnoreCase(receptor)) {
                hilo.enviar("NUEVO;" + emisor + ";" + receptor);
            }
        }
    }
}
