package proyectoo2;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ConsolaComandos extends JInternalFrame {

    private JTextArea salida;
    private JTextField entrada;
    private File carpetaActual;

    public ConsolaComandos(String carpeta) {
        super("Consola de comandos", true, true, true, true);

        carpetaActual = new File(carpeta);

        setSize(620, 420);
        setLocation(200, 90);

        salida = new JTextArea();
        salida.setEditable(false);
        salida.setBackground(new java.awt.Color(12, 12, 18));
        salida.setForeground(new java.awt.Color(120, 255, 170));
        salida.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        salida.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        entrada = new JTextField();
        entrada.setBackground(new java.awt.Color(20, 20, 28));
        entrada.setForeground(new java.awt.Color(120, 255, 170));
        entrada.setCaretColor(new java.awt.Color(120, 255, 170));
        entrada.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        entrada.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        entrada.addActionListener(e -> ejecutar());

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(new JScrollPane(salida), BorderLayout.CENTER);
        contenido.add(entrada, BorderLayout.SOUTH);

        setContentPane(contenido);

        escribir("Mini-Windows [Consola de comandos]");
        escribir("Comandos: mkdir, rm, cd, cd.., dir, date, time");
        mostrarRuta();
    }

    private void mostrarRuta() {
        escribir("");
        escribir(carpetaActual.getPath() + ">");
    }

    private void escribir(String texto) {
        salida.append(texto + "\n");
        salida.setCaretPosition(salida.getDocument().getLength());
    }

    private void ejecutar() {
        String linea = entrada.getText().trim();
        entrada.setText("");

        if (linea.isEmpty()) {
            return;
        }

        escribir(carpetaActual.getPath() + "> " + linea);

        String comando = linea;
        String argumento = "";
        int espacio = linea.indexOf(' ');

        if (espacio > 0) {
            comando = linea.substring(0, espacio);
            argumento = linea.substring(espacio + 1).trim();
        }

        correr(comando.toLowerCase(), argumento);
        mostrarRuta();
    }

    private void correr(String comando, String argumento) {
        if (comando.equals("dir")) {
            listar();
        } else if (comando.equals("mkdir")) {
            crearCarpeta(argumento);
        } else if (comando.equals("rm")) {
            eliminar(argumento);
        } else if (comando.equals("cd..")) {
            subir();
        } else if (comando.equals("cd")) {
            entrar(argumento);
        } else if (comando.equals("date")) {
            mostrarFecha();
        } else if (comando.equals("time")) {
            mostrarHora();
        } else {
            escribir("'" + comando + "' no se reconoce como un comando");
        }
    }

    private void listar() {
        File[] archivos = carpetaActual.listFiles();

        if (archivos == null || archivos.length == 0) {
            escribir("La carpeta está vacía");
            return;
        }

        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].isDirectory()) {
                escribir("<DIR>    " + archivos[i].getName());
            } else {
                escribir("         " + archivos[i].getName() + "   " + archivos[i].length() + " bytes");
            }
        }
    }

    private void crearCarpeta(String nombre) {
        if (nombre.isEmpty()) {
            escribir("Escribe el nombre de la carpeta");
            return;
        }

        if (new File(carpetaActual, nombre).mkdir()) {
            escribir("Carpeta creada: " + nombre);
        } else {
            escribir("No se pudo crear la carpeta");
        }
    }

    private void eliminar(String nombre) {
        File carpeta = new File(carpetaActual, nombre);

        if (!carpeta.exists()) {
            escribir("No existe: " + nombre);
            return;
        }

        if (carpeta.delete()) {
            escribir("Eliminado: " + nombre);
        } else {
            escribir("No se pudo eliminar (revisa que este vacía)");
        }
    }

    private void entrar(String nombre) {
        File destino = new File(carpetaActual, nombre);

        if (destino.isDirectory()) {
            carpetaActual = destino;
        } else {
            escribir("No existe la carpeta: " + nombre);
        }
    }

    private void subir() {
        File padre = carpetaActual.getParentFile();

        if (padre != null && padre.exists()) {
            carpetaActual = padre;
        } else {
            escribir("Ya estas en la carpeta raiz");
        }
    }

    private void mostrarFecha() {
        Calendar calendario = Calendar.getInstance();

        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);

        escribir("Fecha actual: " + dia + "/" + mes + "/" + anio);
    }

    private void mostrarHora() {
        Calendar calendario = Calendar.getInstance();

        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);
        int segundo = calendario.get(Calendar.SECOND);

        escribir("Hora actual: " + hora + ":" + minuto + ":" + segundo);
    }
}
