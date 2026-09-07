package proyectoo2;

import javax.swing.SwingUtilities;

public class HiloNotificaciones implements Runnable {

    private VentanaInstaPlus ventana;
    private boolean activo;
    private int ultimoTotal;

    public HiloNotificaciones(VentanaInstaPlus ventana) {
        this.ventana = ventana;
        activo = true;
        ultimoTotal = -1;
    }

    public void detener() {
        activo = false;
    }

    @Override
    public void run() {
        while (activo) {
            revisar();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void revisar() {
        if (ventana.getUsuario() == null) {
            ultimoTotal = -1;
            return;
        }

        try {
            String yo = ventana.getUsuario().getUsername();
            ListaEnlazada mensajes = ArchivoMensajes.listar(yo);
            final int total = mensajes.getTamano();
            final int noLeidos = contarNoLeidos(mensajes, yo);
            final boolean llegaronNuevos = ultimoTotal >= 0 && total != ultimoTotal;

            ultimoTotal = total;

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ventana.mostrarNotificacion(noLeidos);

                    if (llegaronNuevos) {
                        ventana.refrescarInbox();
                    }
                }
            });
        } catch (ArchivoCorruptoException e) {
            System.out.println(e.getMessage());
        }
    }

    private int contarNoLeidos(ListaEnlazada mensajes, String yo) {
        int cantidad = 0;

        for (int i = 0; i < mensajes.getTamano(); i++) {
            Mensaje mensaje = (Mensaje) mensajes.obtener(i);

            if (mensaje.esParaUsuario(yo) && !mensaje.estaLeido()) {
                cantidad++;
            }
        }

        return cantidad;
    }
}
