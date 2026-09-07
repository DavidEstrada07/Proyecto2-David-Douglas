package proyectoo2;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MiniWindows {

    public static void main(String[] args) {
        SistemaArchivos.iniciar();
        ServidorInsta.iniciar();

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el estilo");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginSistema().setVisible(true);
            }
        });
    }
}
