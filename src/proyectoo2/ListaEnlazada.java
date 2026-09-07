package proyectoo2;

public class ListaEnlazada {

    private Nodo inicio;
    private int tamano;

    public ListaEnlazada() {
        inicio = null;
        tamano = 0;
    }

    public int getTamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return inicio == null;
    }

    public void agregar(Object dato) {
        Nodo nuevo = new Nodo(dato);

        if (inicio == null) {
            inicio = nuevo;
        } else {
            Nodo actual = inicio;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        tamano++;
    }

    public void agregarAlInicio(Object dato) {
        Nodo nuevo = new Nodo(dato);

        nuevo.setSiguiente(inicio);
        inicio = nuevo;
        tamano++;
    }

    public Object obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException("Posicion invalida");
        }

        Nodo actual = inicio;

        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }

        return actual.getDato();
    }

    public boolean contiene(Object dato) {
        Nodo actual = inicio;

        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    public boolean eliminar(Object dato) {
        if (inicio == null) {
            return false;
        }

        if (inicio.getDato().equals(dato)) {
            inicio = inicio.getSiguiente();
            tamano--;
            return true;
        }

        Nodo actual = inicio;

        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(dato)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamano--;
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    public void vaciar() {
        inicio = null;
        tamano = 0;
    }

    @Override
    public String toString() {
        String texto = "";
        Nodo actual = inicio;

        while (actual != null) {
            texto = texto + actual.getDato() + "\n";
            actual = actual.getSiguiente();
        }

        return texto;
    }
}
