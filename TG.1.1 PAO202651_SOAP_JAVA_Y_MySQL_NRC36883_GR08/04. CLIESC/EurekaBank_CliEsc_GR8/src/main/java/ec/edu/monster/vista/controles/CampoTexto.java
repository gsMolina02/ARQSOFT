package ec.edu.monster.vista.controles;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * Cuadro de texto con bordes redondeados, margen interno, texto de ayuda y borde resaltado al recibir el foco.
 * En modo contraseña muestra un botón con un ojo para ver u ocultar lo escrito.
 * Con setConLista muestra una flecha que pide abrir la lista de opciones (setAlSolicitarLista).
 */
public class CampoTexto extends JPanel {

    // Glifos de la fuente "Segoe MDL2 Assets" (incluida en Windows 10 y 11)
    private static final String GLIFO_MOSTRAR = "";
    private static final String GLIFO_OCULTAR = "";
    private static final String GLIFO_LISTA = "\uE70D";
    private static final int MARGEN_HORIZONTAL = 12;
    private static final int ANCHO_BOTON_OJO = 36;
    private static final char CARACTER_OCULTO = '●';

    private final JTextField campo;
    private final JLabel botonOjo = new JLabel(GLIFO_MOSTRAR, SwingConstants.CENTER);
    private final JLabel botonLista = new JLabel(GLIFO_LISTA, SwingConstants.CENTER);
    private final boolean esContrasena;
    private boolean conLista;
    private Runnable alSolicitarLista;
    private String textoAyuda = "";
    private boolean contrasenaVisible;
    private boolean mouseEncima;
    private boolean conError;
    private int radioBorde = 10;

    public CampoTexto() {
        this(false);
    }

    public CampoTexto(boolean esContrasena) {
        super(null);
        this.esContrasena = esContrasena;
        setOpaque(false);
        setBackground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        campo = esContrasena ? new CampoClave() : new CampoNormal();
        campo.setBorder(BorderFactory.createEmptyBorder());
        campo.setOpaque(false);
        campo.setForeground(Estilo.TEXTO);
        campo.setCaretColor(Estilo.TEXTO);
        campo.setFont(Estilo.fuente(12f));
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                quitarError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                quitarError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                quitarError();
            }
        });

        MouseAdapter seguimiento = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cambiarMouseEncima(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cambiarMouseEncima(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                campo.requestFocusInWindow();
            }
        };
        addMouseListener(seguimiento);
        campo.addMouseListener(seguimiento);

        botonOjo.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 15));
        botonOjo.setForeground(Estilo.TEXTO_SECUNDARIO);
        botonOjo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonOjo.setVisible(esContrasena);
        botonOjo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alternarContrasenaVisible();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                botonOjo.setForeground(Estilo.AZUL);
                cambiarMouseEncima(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botonOjo.setForeground(Estilo.TEXTO_SECUNDARIO);
                cambiarMouseEncima(false);
            }
        });

        botonLista.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 13));
        botonLista.setForeground(Estilo.TEXTO_SECUNDARIO);
        botonLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonLista.setToolTipText("Ver la lista (Alt+↓)");
        botonLista.setVisible(false);
        botonLista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                solicitarLista();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                botonLista.setForeground(Estilo.AZUL);
                cambiarMouseEncima(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botonLista.setForeground(Estilo.TEXTO_SECUNDARIO);
                cambiarMouseEncima(false);
            }
        });

        // Alt+Abajo y F4 abren la lista, como en un ComboBox de Windows
        AbstractAction abrirLista = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                solicitarLista();
            }
        };
        campo.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK), "abrirLista");
        campo.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "abrirLista");
        campo.getActionMap().put("abrirLista", abrirLista);

        add(campo);
        add(botonOjo);
        add(botonLista);
    }

    /**
     * Muestra una flecha a la derecha para elegir el valor de una lista; al pulsarla se ejecuta la acción indicada.
     */
    public void setConLista(boolean conLista, Runnable alSolicitarLista) {
        this.conLista = conLista && !esContrasena;
        this.alSolicitarLista = alSolicitarLista;
        botonLista.setVisible(this.conLista);
        revalidate();
        repaint();
    }

    public String getText() {
        return esContrasena ? new String(((JPasswordField) campo).getPassword()) : campo.getText();
    }

    public void setText(String texto) {
        campo.setText(texto == null ? "" : texto);
    }

    /**
     * Texto de ayuda que se muestra mientras el campo está vacío.
     */
    public void setTextoAyuda(String textoAyuda) {
        this.textoAyuda = textoAyuda == null ? "" : textoAyuda;
        campo.repaint();
    }

    /**
     * Limita lo que se puede escribir (por ejemplo, solo dígitos en el número de cuenta).
     */
    public void setFiltro(DocumentFilter filtro) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(filtro);
    }

    public void setRadioBorde(int radioBorde) {
        this.radioBorde = radioBorde;
        repaint();
    }

    /**
     * Resalta el borde en rojo hasta que el usuario vuelva a escribir (equivale al ErrorProvider de Windows Forms).
     */
    public void marcarError() {
        conError = true;
        repaint();
    }

    public void quitarError() {
        if (conError) {
            conError = false;
            repaint();
        }
    }

    public void addActionListener(ActionListener accion) {
        campo.addActionListener(accion);
    }

    public void selectAll() {
        campo.selectAll();
    }

    public JTextComponent getCampo() {
        return campo;
    }

    @Override
    public void setFont(Font fuente) {
        super.setFont(fuente);
        if (campo != null) {
            campo.setFont(fuente);
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        return campo.requestFocusInWindow();
    }

    @Override
    public void setEnabled(boolean habilitado) {
        super.setEnabled(habilitado);
        campo.setEnabled(habilitado);
        botonOjo.setEnabled(habilitado);
        botonLista.setEnabled(habilitado);
        repaint();
    }

    // Texto centrado verticalmente con margen a los lados; el ojo o la flecha ocupan el extremo derecho
    @Override
    public void doLayout() {
        int anchoOjo = esContrasena || conLista ? ANCHO_BOTON_OJO : 0;
        int altoTexto = campo.getPreferredSize().height;
        campo.setBounds(MARGEN_HORIZONTAL, (getHeight() - altoTexto) / 2,
                Math.max(0, getWidth() - MARGEN_HORIZONTAL * 2 - anchoOjo), altoTexto);
        botonOjo.setBounds(getWidth() - ANCHO_BOTON_OJO - 6, 4, ANCHO_BOTON_OJO, Math.max(0, getHeight() - 8));
        botonLista.setBounds(botonOjo.getBounds());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean conFoco = campo.isFocusOwner();
        Color borde = !isEnabled() ? new Color(222, 226, 230)
                : conError ? Estilo.ERROR
                : conFoco ? Estilo.AZUL
                : mouseEncima ? new Color(134, 142, 150)
                : Estilo.BORDE;
        float grosor = conFoco || conError ? 2f : 1.2f;

        RoundRectangle2D forma = new RoundRectangle2D.Float(grosor / 2, grosor / 2, getWidth() - grosor - 1,
                getHeight() - grosor - 1, radioBorde * 2f, radioBorde * 2f);
        g2.setColor(isEnabled() ? getBackground() : new Color(248, 249, 250));
        g2.fill(forma);
        g2.setColor(borde);
        g2.setStroke(new java.awt.BasicStroke(grosor));
        g2.draw(forma);
        g2.dispose();
    }

    private void solicitarLista() {
        if (conLista && isEnabled() && alSolicitarLista != null) {
            campo.requestFocusInWindow();
            alSolicitarLista.run();
        }
    }

    private void alternarContrasenaVisible() {
        contrasenaVisible = !contrasenaVisible;
        ((JPasswordField) campo).setEchoChar(contrasenaVisible ? (char) 0 : CARACTER_OCULTO);
        botonOjo.setText(contrasenaVisible ? GLIFO_OCULTAR : GLIFO_MOSTRAR);
        campo.requestFocusInWindow();
    }

    private void cambiarMouseEncima(boolean encima) {
        mouseEncima = encima;
        repaint();
    }

    // El texto de ayuda se dibuja en gris mientras el campo está vacío
    private void pintarAyuda(JTextComponent componente, Graphics g) {
        if (componente.getDocument().getLength() > 0 || textoAyuda.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(new Color(150, 156, 162));
        g2.setFont(componente.getFont());
        Insets margen = componente.getInsets();
        int y = (componente.getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(textoAyuda, margen.left + 1, y);
        g2.dispose();
    }

    private final class CampoNormal extends JTextField {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            pintarAyuda(this, g);
        }
    }

    private final class CampoClave extends JPasswordField {

        private CampoClave() {
            setEchoChar(CARACTER_OCULTO);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            pintarAyuda(this, g);
        }
    }
}
