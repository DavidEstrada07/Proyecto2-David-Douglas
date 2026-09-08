package proyectoo2;

public class ValidadorPassword {

    public static final int MINIMO = 8;

    public static boolean tieneMayuscula(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static boolean tieneNumero(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static boolean tieneSimbolo(String password) {
        for (int i = 0; i < password.length(); i++) {
            char letra = password.charAt(i);

            if (!Character.isLetterOrDigit(letra) && !Character.isWhitespace(letra)) {
                return true;
            }
        }

        return false;
    }

    public static boolean esValida(String password) {
        return password.length() >= MINIMO
                && tieneMayuscula(password)
                && tieneNumero(password)
                && tieneSimbolo(password);
    }

    public static String obtenerMensaje(String password) {
        if (password.length() < MINIMO) {
            return "La contraseña debe tener al menos " + MINIMO + " caracteres";
        }

        if (!tieneMayuscula(password)) {
            return "La contraseña debe tener al menos una letra mayúscula";
        }

        if (!tieneNumero(password)) {
            return "La contraseña debe tener al menos un número";
        }

        if (!tieneSimbolo(password)) {
            return "La contraseña debe tener al menos un símbolo";
        }

        return "Contraseña válida";
    }
}
