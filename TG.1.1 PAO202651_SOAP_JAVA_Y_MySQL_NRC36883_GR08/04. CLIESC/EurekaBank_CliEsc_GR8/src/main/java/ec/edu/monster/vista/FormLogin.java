package ec.edu.monster.vista;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.vista.controles.BotonRedondeado;
import ec.edu.monster.vista.controles.CampoTexto;
import ec.edu.monster.vista.controles.Estilo;
import ec.edu.monster.vista.controles.ImagenRecortada;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Inicio de sesión (CU-01). A la izquierda la imagen y la marca; a la derecha las credenciales y el
 * enlace "Configurar servidor" para usar el servidor Payara de otra computadora.
 */
public class FormLogin extends JFrame {

    private final ServidorControlador servidorControlador;
    private final CampoTexto txtUsuario = new CampoTexto();
    private final CampoTexto txtContrasenia = new CampoTexto(true);
    private final JLabel lblError = Componentes.parrafo("", Estilo.fuente(9.5f), Estilo.ERROR);
    private final BotonRedondeado btnIngresar = Componentes.botonPrincipal("Iniciar sesión", 12f);
    private final JLabel lblServidor = Componentes.parrafo("", Estilo.fuente(8.5f), new Color(150, 156, 162));
    private LoginControlador loginControlador;

    public FormLogin(ServidorControlador servidorControlador) {
        this.servidorControlador = servidorControlador;
        setTitle("EurekaBank SOAP GR08 - Iniciar sesión");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel contenido = Componentes.panel(Color.WHITE);
        contenido.setPreferredSize(new Dimension(900, 560));
        setContentPane(contenido);

        construirLateral(contenido);
        construirFormulario(contenido);
        conectar();

        pack();
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtUsuario.requestFocusInWindow();
            }
        });
    }

    private void construirLateral(JPanel contenido) {
        JPanel lateral = Componentes.ubicar(contenido, Componentes.panel(Estilo.AZUL), 0, 0, 420, 560);
        ImagenRecortada imagen = new ImagenRecortada(cargarImagen(), 0.5f, 0.5f);
        imagen.setBackground(Estilo.AZUL);
        Componentes.ubicar(lateral, imagen, 0, 0, 420, 420);
        Componentes.ubicar(lateral, Componentes.etiqueta("EurekaBank SOAP GR08", Estilo.fuente(24f, Font.BOLD), Color.WHITE),
                32, 434, 380, 54);
        Componentes.ubicar(lateral, Componentes.parrafo("Sistema de Cuentas de Ahorro\n"
                + "Movimientos · Depósitos · Retiros · Transferencias", Estilo.fuente(10.5f), Estilo.TEXTO_ENCABEZADO),
                38, 492, 360, 54);
    }

    private void construirFormulario(JPanel contenido) {
        Componentes.ubicar(contenido, Componentes.etiqueta("Iniciar sesión", Estilo.fuente(20f, Font.BOLD), Estilo.TEXTO),
                466, 66, 390, 46);
        Componentes.ubicar(contenido, Componentes.etiqueta("Ingrese sus credenciales para continuar",
                Estilo.fuente(10f), Estilo.TEXTO_SECUNDARIO), 470, 116, 390, 23);

        Componentes.ubicar(contenido, Componentes.etiqueta("Usuario", Estilo.fuente(10f), Estilo.TEXTO_ETIQUETA),
                470, 170, 200, 23);
        txtUsuario.setTextoAyuda("Ej.: Monster");
        txtUsuario.addActionListener(e -> txtContrasenia.requestFocusInWindow());
        Componentes.ubicar(contenido, txtUsuario, 470, 196, 390, 48);

        Componentes.ubicar(contenido, Componentes.etiqueta("Contraseña", Estilo.fuente(10f), Estilo.TEXTO_ETIQUETA),
                470, 260, 200, 23);
        txtContrasenia.setTextoAyuda("Escriba su contraseña");
        txtContrasenia.addActionListener(e -> ingresar());
        Componentes.ubicar(contenido, txtContrasenia, 470, 286, 390, 48);

        Componentes.ubicar(contenido, lblError, 470, 348, 390, 42);
        btnIngresar.addActionListener(e -> ingresar());
        Componentes.ubicar(contenido, btnIngresar, 470, 394, 390, 52);

        JLabel lnkServidor = Componentes.etiqueta("Configurar servidor", Estilo.fuente(9f), Estilo.AZUL);
        lnkServidor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lnkServidor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirServidor();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lnkServidor.setText("<html><u>Configurar servidor</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lnkServidor.setText("Configurar servidor");
            }
        });
        Componentes.ubicar(contenido, lnkServidor, 470, 462, 160, 22);
        Componentes.ubicar(contenido, lblServidor, 470, 490, 390, 44);
    }

    // Crea el cliente del servicio con la dirección configurada (se repite al cambiar de servidor)
    private void conectar() {
        ClienteWSEurekaBank clienteWs = new ClienteWSEurekaBank(servidorControlador.getUrlActual());
        loginControlador = new LoginControlador(clienteWs);
        lblServidor.setText(Componentes.html("Servidor SOAP: " + loginControlador.getUrlServidor()));
    }

    private void abrirServidor() {
        if (new FormServidor(this, servidorControlador).mostrar()) {
            conectar();
            lblError.setText("");
        }
    }

    private void ingresar() {
        if (!btnIngresar.isEnabled()) {
            return;
        }
        lblError.setText("");

        if (txtUsuario.getText().isBlank() || txtContrasenia.getText().isBlank()) {
            lblError.setText("Ingrese el usuario y la contraseña.");
            (txtUsuario.getText().isBlank() ? txtUsuario : txtContrasenia).requestFocusInWindow();
            return;
        }

        LoginModelo modelo = new LoginModelo(txtUsuario.getText().trim(), txtContrasenia.getText());
        cambiarEstadoOcupado(true);
        Tarea.ejecutar(() -> loginControlador.iniciarSesion(modelo), sesion -> {
            cambiarEstadoOcupado(false);
            abrirOperaciones();
        }, ex -> {
            cambiarEstadoOcupado(false);
            lblError.setText(Componentes.html(ex.getMessage()));
            if (ex instanceof ExcepcionClienteWS) {
                txtContrasenia.selectAll();
                txtContrasenia.requestFocusInWindow();
            }
        });
    }

    private void abrirOperaciones() {
        setVisible(false);
        FormOperaciones operaciones = new FormOperaciones(loginControlador, this::alVolverDeOperaciones);
        operaciones.setLocationRelativeTo(null);
        operaciones.setVisible(true);
    }

    private void alVolverDeOperaciones(FormOperaciones.Salida salida) {
        // Se cerró la ventana sin "Cerrar sesión": se avisa igual al servidor para que la sesión no quede abierta
        if (loginControlador.haySesionActiva()) {
            Tarea.ejecutar(() -> {
                try {
                    return loginControlador.cerrarSesion();
                } catch (ExcepcionClienteWS ex) {
                    // Sin conexión o sesión ya vencida: el token local ya se borró
                    return "";
                }
            }, mensaje -> continuarTrasOperaciones(salida), ex -> continuarTrasOperaciones(salida));
        } else {
            continuarTrasOperaciones(salida);
        }
    }

    private void continuarTrasOperaciones(FormOperaciones.Salida salida) {
        if (salida == FormOperaciones.Salida.SALIR_DE_LA_APLICACION) {
            dispose();
            System.exit(0);
            return;
        }

        // Cerró sesión o la sesión expiró: vuelve al inicio de sesión
        txtContrasenia.setText("");
        setVisible(true);
        txtContrasenia.requestFocusInWindow();

        // Se perdió la conexión con el servidor: se abre directamente "Configurar servidor"
        if (salida == FormOperaciones.Salida.CONFIGURAR_SERVIDOR) {
            abrirServidor();
        }
    }

    private void cambiarEstadoOcupado(boolean ocupado) {
        btnIngresar.setEnabled(!ocupado);
        txtUsuario.setEnabled(!ocupado);
        txtContrasenia.setEnabled(!ocupado);
        btnIngresar.setText(ocupado ? "Conectando..." : "Iniciar sesión");
        setCursor(ocupado ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private static Image cargarImagen() {
        try (InputStream entrada = FormLogin.class.getResourceAsStream("/imagenes/ImagenLogin.jpg")) {
            return entrada == null ? null : ImageIO.read(entrada);
        } catch (IOException ex) {
            // Sin imagen se ve solo el fondo azul
            return null;
        }
    }
}
