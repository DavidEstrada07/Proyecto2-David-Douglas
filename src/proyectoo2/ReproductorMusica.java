package proyectoo2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ReproductorMusica extends JInternalFrame {

    private static final int SALTO = 10;

    private ListaEnlazada canciones;
    private DefaultListModel<String> modelo;
    private JList<String> lista;
    private JTextField busqueda;
    private JSlider barra;
    private JLabel tiempo;
    private JLabel caratula;
    private JLabel descripcion;

    private Clip clip;
    private AudioInputStream audio;
    private File cancionActual;
    private double segundoActual;
    private double duracion;
    private boolean reproduciendo;
    private boolean simulado;
    private HiloReproductor hilo;

    private String carpeta;

    public ReproductorMusica(String carpeta) {
        super("Reproductor de musica", true, true, true, true);

        this.carpeta = carpeta;
        canciones = new ListaEnlazada();
        modelo = new DefaultListModel<>();

        setSize(700, 480);
        setLocation(120, 70);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBackground(Estilo.PANEL);
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contenido.add(crearLista(), BorderLayout.WEST);
        contenido.add(crearCentro(), BorderLayout.CENTER);
        contenido.add(crearControles(), BorderLayout.SOUTH);

        setContentPane(contenido);

        buscarCanciones("");
        iniciarHilo();

        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
                detener();

                if (hilo != null) {
                    hilo.detener();
                }
            }
        });
    }

    private JPanel crearLista() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Estilo.PANEL);
        panel.setPreferredSize(new Dimension(250, 0));

        busqueda = new JTextField();
        Estilo.darEstiloCampo(busqueda);
        busqueda.addCaretListener(e -> buscarCanciones(busqueda.getText().trim()));

        lista = new JList<>(modelo);
        lista.setBackground(Estilo.PANEL_CLARO);
        lista.setForeground(Estilo.TEXTO);
        lista.setFont(Estilo.NORMAL);
        lista.addListSelectionListener(e -> seleccionar());

        panel.add(busqueda, BorderLayout.NORTH);
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearCentro() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilo.PANEL);

        caratula = new JLabel("", SwingConstants.CENTER);
        caratula.setAlignmentX(0.5f);
        caratula.setPreferredSize(new Dimension(200, 200));

        descripcion = new JLabel("Selecciona una cancion", SwingConstants.CENTER);
        descripcion.setAlignmentX(0.5f);
        descripcion.setForeground(Estilo.TEXTO_GRIS);
        descripcion.setFont(Estilo.NORMAL);

        panel.add(caratula);
        panel.add(descripcion);

        return panel;
    }

    private JPanel crearControles() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Estilo.PANEL);

        barra = new JSlider(0, 100, 0);
        barra.setBackground(Estilo.PANEL);
        barra.setForeground(Estilo.ACENTO);
        barra.addChangeListener(e -> moverBarra());

        tiempo = new JLabel("0:00 / 0:00", SwingConstants.CENTER);
        tiempo.setForeground(Estilo.TEXTO);
        tiempo.setFont(Estilo.PEQUENA);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        botones.setBackground(Estilo.PANEL);

        BotonRedondo atras = new BotonRedondo("<< 10s", Estilo.PANEL_CLARO);
        atras.addActionListener(e -> saltar(-SALTO));

        BotonRedondo play = new BotonRedondo("Play", Estilo.VERDE);
        play.addActionListener(e -> reproducir());

        BotonRedondo pausa = new BotonRedondo("Pausa", Estilo.ACENTO);
        pausa.addActionListener(e -> pausar());

        BotonRedondo stop = new BotonRedondo("Stop", Estilo.ROJO);
        stop.addActionListener(e -> detener());

        BotonRedondo adelante = new BotonRedondo("10s >>", Estilo.PANEL_CLARO);
        adelante.addActionListener(e -> saltar(SALTO));

        botones.add(atras);
        botones.add(play);
        botones.add(pausa);
        botones.add(stop);
        botones.add(adelante);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(tiempo, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void buscarCanciones(String texto) {
        canciones = new ListaEnlazada();
        modelo.clear();

        agregarDeCarpeta(new File(carpeta), texto);

        if (modelo.isEmpty()) {
            modelo.addElement("(No hay musica en tu carpeta)");
        }
    }

    private void agregarDeCarpeta(File carpetaActual, String texto) {
        File[] archivos = carpetaActual.listFiles();

        if (archivos == null) {
            return;
        }

        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].isDirectory()) {
                agregarDeCarpeta(archivos[i], texto);
            } else if (OrganizadorArchivos.esMusica(archivos[i].getName())) {
                if (texto.isEmpty() || archivos[i].getName().toLowerCase().contains(texto.toLowerCase())) {
                    canciones.agregar(archivos[i]);
                    modelo.addElement(archivos[i].getName());
                }
            }
        }
    }

    private void seleccionar() {
        int indice = lista.getSelectedIndex();

        if (indice < 0 || indice >= canciones.getTamano()) {
            return;
        }

        detener();
        cancionActual = (File) canciones.obtener(indice);
        prepararCancion();
    }

    private void prepararCancion() {
        segundoActual = 0;
        simulado = false;
        cerrarClip();

        try {
            audio = AudioSystem.getAudioInputStream(cancionActual);
            clip = AudioSystem.getClip();
            clip.open(audio);
            duracion = clip.getMicrosecondLength() / 1000000.0;
        } catch (Exception e) {
            clip = null;
            simulado = true;
            duracion = cancionActual.length() / 16000.0;
        }

        barra.setMaximum((int) duracion);
        mostrarCaratula();
        mostrarDescripcion();
        actualizarAvance();
    }

    private void mostrarCaratula() {
        String nombre = cancionActual.getName();
        int punto = nombre.lastIndexOf('.');

        if (punto > 0) {
            nombre = nombre.substring(0, punto);
        }

        File imagen = new File(cancionActual.getParentFile(), nombre + ".jpg");

        if (!imagen.exists()) {
            imagen = new File(cancionActual.getParentFile(), nombre + ".png");
        }

        if (imagen.exists()) {
            Image escalada = new ImageIcon(imagen.getPath()).getImage()
                    .getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            caratula.setIcon(new ImageIcon(escalada));
            caratula.setText("");
        } else {
            caratula.setIcon(null);
            caratula.setText("<html><div style='font-size:60px'>&#9835;</div></html>");
            caratula.setForeground(Estilo.ACENTO);
        }
    }

    private void mostrarDescripcion() {
        String aviso = "";

        if (simulado) {
            aviso = "<br>(formato sin soporte de audio, avance simulado)";
        }

        descripcion.setText("<html><div style='text-align:center'><b>" + cancionActual.getName()
                + "</b><br>" + (cancionActual.length() / 1024) + " KB   |   " + formato(duracion)
                + aviso + "</div></html>");
    }

    private void reproducir() {
        if (cancionActual == null) {
            return;
        }

        if (clip == null && !simulado) {
            prepararCancion();
        }

        reproduciendo = true;

        if (clip != null) {
            clip.setMicrosecondPosition((long) (segundoActual * 1000000));
            clip.start();
        }
    }

    private void pausar() {
        reproduciendo = false;

        if (clip != null) {
            clip.stop();
        }
    }

    private void detener() {
        reproduciendo = false;
        segundoActual = 0;
        cerrarClip();
        actualizarAvance();
    }

    private void cerrarClip() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }

        if (audio != null) {
            try {
                audio.close();
            } catch (Exception e) {
                System.out.println("No se pudo cerrar el audio");
            }

            audio = null;
        }
    }

    @Override
    public void dispose() {
        cerrarClip();
        super.dispose();
    }

    private void saltar(int segundos) {
        if (cancionActual == null) {
            return;
        }

        segundoActual = segundoActual + segundos;

        if (segundoActual < 0) {
            segundoActual = 0;
        }

        if (segundoActual > duracion) {
            segundoActual = duracion;
        }

        if (clip != null) {
            clip.setMicrosecondPosition((long) (segundoActual * 1000000));
        }

        actualizarAvance();
    }

    private void moverBarra() {
        if (!barra.getValueIsAdjusting() || cancionActual == null) {
            return;
        }

        segundoActual = barra.getValue();

        if (clip != null) {
            clip.setMicrosecondPosition((long) (segundoActual * 1000000));
        }
    }

    public void actualizarAvance() {
        if (cancionActual == null) {
            return;
        }

        if (reproduciendo) {
            if (clip != null) {
                segundoActual = clip.getMicrosecondPosition() / 1000000.0;
            } else {
                segundoActual = segundoActual + 0.25;
            }

            if (segundoActual >= duracion) {
                detener();
                return;
            }
        }

        if (!barra.getValueIsAdjusting()) {
            barra.setValue((int) segundoActual);
        }

        tiempo.setText(formato(segundoActual) + " / " + formato(duracion));
    }

    private String formato(double segundos) {
        int total = (int) segundos;
        int minutos = total / 60;
        int resto = total % 60;
        String restoTexto = "" + resto;

        if (resto < 10) {
            restoTexto = "0" + resto;
        }

        return minutos + ":" + restoTexto;
    }

    private void iniciarHilo() {
        hilo = new HiloReproductor(this);

        Thread thread = new Thread(hilo);
        thread.setDaemon(true);
        thread.start();
    }
}
