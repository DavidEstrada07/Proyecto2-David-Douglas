package proyectoo2;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class RenderizadorArbol extends DefaultTreeCellRenderer {

    private static final Color TRANSPARENTE = new Color(0, 0, 0, 0);

    public RenderizadorArbol() {
        setOpaque(false);
        setBackgroundNonSelectionColor(TRANSPARENTE);
        setBackgroundSelectionColor(Estilo.ACENTO);
        setTextNonSelectionColor(Color.WHITE);
        setTextSelectionColor(Color.WHITE);
        setBorderSelectionColor(TRANSPARENTE);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree arbol, Object valor, boolean seleccionado,
            boolean abierto, boolean hoja, int fila, boolean tieneFoco) {

        super.getTreeCellRendererComponent(arbol, valor, seleccionado, abierto, hoja, fila, tieneFoco);

        setOpaque(seleccionado);
        setFont(Estilo.NORMAL);
        setIcon(iconoDe(valor, abierto));

        return this;
    }

    private javax.swing.Icon iconoDe(Object valor, boolean abierto) {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) valor;

        if (!(nodo.getUserObject() instanceof ArchivoNodo)) {
            return getDefaultClosedIcon();
        }

        ArchivoNodo dato = (ArchivoNodo) nodo.getUserObject();

        if (!dato.getArchivo().isDirectory()) {
            return getDefaultLeafIcon();
        }

        if (abierto) {
            return getDefaultOpenIcon();
        }

        return getDefaultClosedIcon();
    }
}
