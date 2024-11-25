package GUI;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class PrintConsola {
    public static void print(JTextPane consola, Color c, String cad) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = consola.getStyledDocument();
                SimpleAttributeSet attributes = new SimpleAttributeSet();

                StyleConstants.setForeground(attributes, c);
                doc.insertString(doc.getLength(), cad, attributes);
                consola.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) { // Llave corregida
                e.printStackTrace();
            }
        });
    }
}

