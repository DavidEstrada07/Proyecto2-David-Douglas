package proyectoo2;

import javax.swing.SwingUtilities;

public class HiloReproductor implements Runnable {

    private ReproductorMusica reproductor;
    private boolean activo;

    public HiloReproductor(ReproductorMusica reproductor) {
        this.reproductor = reproductor;
        activo = true;
    }

    public void detener() {
        activo = false;
    }

    @Override
    public void run() {
        while (activo) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reproductor.actualizarAvance();
                }
            });

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
