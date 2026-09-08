package proyectoo2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class SistemaArchivos {

    public static final String UNIDAD = "Z";
    public static final String INSTA_RAIZ = "INSTA_RAIZ";
    public static final String ARCHIVO_SISTEMA = "usuarios.sop";
    public static final String STICKERS_GLOBALES = INSTA_RAIZ + File.separator + "stickers_globales";

    private static final String[] NOMBRES_STICKERS = {"Feliz", "Triste", "Corazon", "Risa", "Aplauso"};

    public static void iniciar() {
        crearCarpeta(UNIDAD);
        crearCarpeta(INSTA_RAIZ);
        crearCarpeta(STICKERS_GLOBALES);
        crearStickersPorDefecto();
    }

    public static void crearCarpeta(String ruta) {
        File carpeta = new File(ruta);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    public static String carpetaSistema(String username) {
        return UNIDAD + File.separator + username;
    }

    public static void crearEspacioSistema(String username) {
        String raiz = carpetaSistema(username);

        crearCarpeta(raiz);
        crearCarpeta(raiz + File.separator + "Mis Documentos");
        crearCarpeta(raiz + File.separator + "Musica");
        crearCarpeta(raiz + File.separator + "Mis Imagenes");
    }

    public static String carpetaInsta(String username) {
        return INSTA_RAIZ + File.separator + username;
    }

    public static String archivoInsta(String username, String nombre) {
        return carpetaInsta(username) + File.separator + nombre;
    }

    public static String archivoUsuarios() {
        return INSTA_RAIZ + File.separator + "users.ins";
    }

    public static void crearEspacioInsta(String username) {
        String raiz = carpetaInsta(username);

        crearCarpeta(raiz);
        crearCarpeta(raiz + File.separator + "imágenes");
        crearCarpeta(raiz + File.separator + "folders_personales");
        crearCarpeta(raiz + File.separator + "stickers_personales");
    }

    public static String[] nombresStickers() {
        return NOMBRES_STICKERS;
    }

    public static String rutaStickerGlobal(String nombre) {
        return STICKERS_GLOBALES + File.separator + nombre.toLowerCase() + ".png";
    }

    private static void crearStickersPorDefecto() {
        for (int i = 0; i < NOMBRES_STICKERS.length; i++) {
            File archivo = new File(rutaStickerGlobal(NOMBRES_STICKERS[i]));

            if (!archivo.exists()) {
                copiarSticker(NOMBRES_STICKERS[i], archivo);
            }
        }
    }

    private static void copiarSticker(String nombre, File destino) {
        String recurso = "stickers/" + nombre.toLowerCase() + ".png";
        InputStream entrada = SistemaArchivos.class.getResourceAsStream(recurso);

        if (entrada == null) {
            dibujarSticker(destino, nombre);
            return;
        }

        try (FileOutputStream salida = new FileOutputStream(destino)) {
            byte[] bloque = new byte[4096];
            int leidos = entrada.read(bloque);

            while (leidos > 0) {
                salida.write(bloque, 0, leidos);
                leidos = entrada.read(bloque);
            }
        } catch (IOException e) {
            System.out.println("No se pudo copiar el sticker: " + e.getMessage());
        } finally {
            try {
                entrada.close();
            } catch (IOException e) {
                System.out.println("No se pudo cerrar el sticker");
            }
        }
    }

    private static void dibujarSticker(File archivo, String nombre) {
        BufferedImage imagen = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 196, 61));
        g2.fillOval(5, 5, 110, 110);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(nombre, 15, 65);
        g2.dispose();

        try {
            ImageIO.write(imagen, "png", archivo);
        } catch (IOException e) {
            System.out.println("No se pudo crear el sticker: " + e.getMessage());
        }
    }
}
