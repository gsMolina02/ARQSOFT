package ec.edu.monster.vista;

import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.controlador.CuentaControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.util.FormatoUtil;
import ec.edu.monster.util.ValidacionUtil;
import ec.edu.monster.vista.controles.BotonRedondeado;
import ec.edu.monster.vista.controles.CampoTexto;
import ec.edu.monster.vista.controles.Estilo;
import ec.edu.monster.vista.controles.FiltroTexto;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Pantalla principal: consulta de movimientos (CU-04), depósito (CU-05), retiro (CU-06) y
 * transferencia (CU-07). A la derecha se muestran los datos y movimientos de la última cuenta usada.
 * Los campos de cuenta tienen una flecha que despliega todas las cuentas del servidor.
 */
public class FormOperaciones extends JFrame {

    /** Cómo se cerró la pantalla. */
    public enum Salida {
        SALIR_DE_LA_APLICACION,
        VOLVER_AL_INICIO_DE_SESION,
        CONFIGURAR_SERVIDOR
    }

    private enum Operacion {
        MOVIMIENTOS,
        DEPOSITO,
        RETIRO,
        TRANSFERENCIA
    }

    private static final DateTimeFormatter FORMATO_FECHA_TABLA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA_TABLA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LoginControlador loginControlador;
    private final CuentaControlador cuentaControlador;
    private final Consumer<Salida> alCerrar;
    private final Map<BotonRedondeado, Operacion> pestanias = new LinkedHashMap<>();
    private final String textoEstado;

    private final JLabel lblSubtitulo = Componentes.etiqueta("", Estilo.fuente(10f), Estilo.TEXTO_ENCABEZADO);
    private final BotonRedondeado btnUsuario = Componentes.botonEncabezado("");
    private final JPopupMenu mnuUsuario = new JPopupMenu();

    private final JPanel pnlFormulario = Componentes.panel(Color.WHITE);
    private final JLabel lblCuenta = etiquetaCampo("Número de cuenta");
    private final CampoTexto txtCuenta = campoNumerico("Ejemplo: 00100001", FiltroTexto.cuenta());
    private final JLabel lblDestino = etiquetaCampo("Cuenta de destino");
    private final CampoTexto txtDestino = campoNumerico("Ejemplo: 00200001", FiltroTexto.cuenta());
    private final JLabel lblImporte = etiquetaCampo("Importe en USD");
    private final CampoTexto txtImporte = campoNumerico("Ejemplo: 100 o 25.50", FiltroTexto.importe());
    private final BotonRedondeado btnEjecutar = Componentes.botonPrincipal("Consultar", 12.5f);

    private final JLabel lblResultadoTitulo = Componentes.etiqueta("Comprobante", Estilo.fuente(10.5f), Estilo.TEXTO_ETIQUETA);
    private final JLabel lblResultado = Componentes.etiqueta("—", Estilo.fuente(22f, Font.BOLD), Estilo.AZUL);

    private final JLabel lblCuentaTitulo = Componentes.etiqueta("Movimientos de la cuenta", Estilo.fuente(13f, Font.BOLD),
            Estilo.TEXTO);
    private final JLabel lblCuentaDatos = Componentes.parrafo("Consulte una cuenta para ver su saldo y sus movimientos.",
            Estilo.fuente(9.5f), Estilo.TEXTO_SECUNDARIO);
    private final BotonRedondeado btnActualizar = new BotonRedondeado("Actualizar");
    private final DefaultTableModel modeloMovimientos = new DefaultTableModel(new Object[] {"N°", "Fecha", "Tipo", "Importe"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tblMovimientos = new JTable(modeloMovimientos);
    private final List<MovimientoDTO> movimientosMostrados = new ArrayList<>();
    private String simboloMostrado = "$";

    private final JLabel lblEstado = Componentes.etiqueta("Listo", Estilo.fuente(9f), Color.GRAY);

    private Operacion operacion;
    private String cuentaMostrada;
    // Se piden al servidor la primera vez que se abre la lista y se renuevan con "Actualizar"
    private List<CuentaDTO> cuentasServidor;
    private boolean enPeticion;
    private boolean cerrada;

    public FormOperaciones(LoginControlador loginControlador, Consumer<Salida> alCerrar) {
        this.loginControlador = loginControlador;
        this.cuentaControlador = loginControlador.crearControladorCuenta();
        this.alCerrar = alCerrar;
        this.textoEstado = "Conectado a " + loginControlador.getUrlServidor();

        setTitle("EurekaBank SOAP GR08 - Operaciones");
        setResizable(false);
        // Cerrar la ventana con la X equivale a salir de la aplicación
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (!enPeticion) {
                    cerrar(Salida.SALIR_DE_LA_APLICACION);
                }
            }

            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtCuenta.requestFocusInWindow();
            }
        });

        JPanel contenido = Componentes.panel(Estilo.FONDO);
        contenido.setPreferredSize(new Dimension(1060, 640));
        setContentPane(contenido);

        construirEncabezado(contenido);
        construirPestanias(contenido);
        construirFormulario(contenido);
        construirResultado(contenido);
        construirCuenta(contenido);
        construirBarraEstado(contenido);

        txtCuenta.setConLista(true, () -> mostrarListaCuentas(txtCuenta));
        txtDestino.setConLista(true, () -> mostrarListaCuentas(txtDestino));

        seleccionarOperacion(Operacion.MOVIMIENTOS);
        pack();
    }

    private void construirEncabezado(JPanel contenido) {
        JPanel encabezado = Componentes.ubicar(contenido, Componentes.panel(Estilo.AZUL), 0, 0, 1060, 92);
        Componentes.ubicar(encabezado, Componentes.etiqueta("EurekaBank · Operaciones", Estilo.fuente(19f, Font.BOLD),
                Color.WHITE), 30, 10, 500, 45);
        Componentes.ubicar(encabezado, lblSubtitulo, 34, 56, 600, 23);

        btnUsuario.setText(loginControlador.getEmpleado() + "  ▼");
        btnUsuario.addActionListener(e -> mnuUsuario.show(btnUsuario,
                btnUsuario.getWidth() - mnuUsuario.getPreferredSize().width, btnUsuario.getHeight() + 4));
        Componentes.ubicar(encabezado, btnUsuario, 808, 26, 220, 40);

        // Las opciones de la sesión se agrupan en un menú para no ocupar espacio en la ventana
        JMenuItem mnuCambiarContrasenia = new JMenuItem("Cambiar contraseña");
        mnuCambiarContrasenia.setFont(Estilo.fuente(10.5f));
        mnuCambiarContrasenia.setPreferredSize(new Dimension(224, 34));
        mnuCambiarContrasenia.addActionListener(e -> cambiarContrasenia());
        JMenuItem mnuCerrarSesion = new JMenuItem("Cerrar sesión");
        mnuCerrarSesion.setFont(Estilo.fuente(10.5f));
        mnuCerrarSesion.setForeground(Estilo.ERROR);
        mnuCerrarSesion.setPreferredSize(new Dimension(224, 34));
        mnuCerrarSesion.addActionListener(e -> cerrarSesion());
        mnuUsuario.add(mnuCambiarContrasenia);
        mnuUsuario.addSeparator();
        mnuUsuario.add(mnuCerrarSesion);
    }

    private void construirPestanias(JPanel contenido) {
        pestanias.put(pestania(contenido, "Movimientos", 32, 131), Operacion.MOVIMIENTOS);
        pestanias.put(pestania(contenido, "Depósito", 174, 131), Operacion.DEPOSITO);
        pestanias.put(pestania(contenido, "Retiro", 316, 131), Operacion.RETIRO);
        pestanias.put(pestania(contenido, "Transferencia", 458, 134), Operacion.TRANSFERENCIA);
    }

    private BotonRedondeado pestania(JPanel contenido, String texto, int x, int ancho) {
        BotonRedondeado boton = new BotonRedondeado(texto);
        boton.setFont(Estilo.fuente(10.5f, Font.BOLD));
        boton.setRadioBorde(22);
        // La pestaña activa ya se distingue por el color: no necesita el contorno de foco
        boton.setFocusable(false);
        boton.addActionListener(e -> {
            if (!enPeticion) {
                seleccionarOperacion(pestanias.get(boton));
            }
        });
        return Componentes.ubicar(contenido, boton, x, 114, ancho, 48);
    }

    private void construirFormulario(JPanel contenido) {
        Componentes.ubicar(contenido, pnlFormulario, 32, 178, 560, 270);
        Componentes.ubicar(pnlFormulario, lblCuenta, 24, 18, 260, 25);
        Componentes.ubicar(pnlFormulario, txtCuenta, 24, 44, 512, 48);
        Componentes.ubicar(pnlFormulario, lblDestino, 292, 18, 244, 25);
        Componentes.ubicar(pnlFormulario, txtDestino, 292, 44, 244, 48);
        Componentes.ubicar(pnlFormulario, lblImporte, 24, 106, 260, 25);
        Componentes.ubicar(pnlFormulario, txtImporte, 24, 132, 512, 48);
        btnEjecutar.setRadioBorde(24);
        btnEjecutar.addActionListener(e -> ejecutar());
        Componentes.ubicar(pnlFormulario, btnEjecutar, 24, 194, 512, 54);

        txtCuenta.addActionListener(e -> ejecutar());
        txtDestino.addActionListener(e -> ejecutar());
        txtImporte.addActionListener(e -> ejecutar());
    }

    private void construirResultado(JPanel contenido) {
        JPanel resultado = Componentes.ubicar(contenido, Componentes.panel(Estilo.SUAVE), 32, 464, 560, 120);
        Componentes.ubicar(resultado, lblResultadoTitulo, 22, 14, 516, 36);
        Componentes.ubicar(resultado, lblResultado, 22, 50, 516, 56);
    }

    private void construirCuenta(JPanel contenido) {
        JPanel cuenta = Componentes.ubicar(contenido, Componentes.panel(Color.WHITE), 612, 114, 416, 470);
        Componentes.ubicar(cuenta, lblCuentaTitulo, 18, 14, 270, 30);
        Componentes.ubicar(cuenta, lblCuentaDatos, 20, 48, 378, 66);

        btnActualizar.setFont(Estilo.fuente(9.5f, Font.BOLD));
        btnActualizar.setColores(Estilo.SUAVE, Estilo.AZUL, Estilo.SUAVE_HOVER, Estilo.SUAVE_PRESIONADO);
        btnActualizar.setRadioBorde(16);
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizarCuenta());
        Componentes.ubicar(cuenta, btnActualizar, 294, 14, 104, 32);

        tblMovimientos.setFont(Estilo.fuente(9.5f));
        tblMovimientos.setRowHeight(46);
        tblMovimientos.setShowVerticalLines(false);
        tblMovimientos.setGridColor(new Color(233, 236, 239));
        tblMovimientos.setSelectionBackground(Estilo.SUAVE);
        tblMovimientos.setSelectionForeground(Estilo.TEXTO);
        tblMovimientos.setFillsViewportHeight(true);
        tblMovimientos.getTableHeader().setReorderingAllowed(false);
        tblMovimientos.getTableHeader().setResizingAllowed(false);

        JTableHeader encabezado = tblMovimientos.getTableHeader();
        encabezado.setPreferredSize(new Dimension(0, 34));
        DefaultTableCellRenderer estiloEncabezado = new DefaultTableCellRenderer();
        estiloEncabezado.setBackground(Estilo.FONDO);
        estiloEncabezado.setForeground(Estilo.TEXTO_ETIQUETA);
        estiloEncabezado.setFont(Estilo.fuente(9.5f, Font.BOLD));
        estiloEncabezado.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        encabezado.setDefaultRenderer((tabla, valor, seleccionada, foco, fila, columna) -> {
            JLabel etiqueta = (JLabel) estiloEncabezado.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
            etiqueta.setFont(Estilo.fuente(9.5f, Font.BOLD));
            etiqueta.setHorizontalAlignment(columna == 3 ? SwingConstants.RIGHT : SwingConstants.LEFT);
            return etiqueta;
        });

        RenderizadorMovimiento renderizador = new RenderizadorMovimiento();
        int[] anchos = {38, 92, 150, 98};
        for (int columna = 0; columna < anchos.length; columna++) {
            tblMovimientos.getColumnModel().getColumn(columna).setPreferredWidth(anchos[columna]);
            tblMovimientos.getColumnModel().getColumn(columna).setCellRenderer(renderizador);
        }

        JScrollPane desplazamiento = new JScrollPane(tblMovimientos);
        desplazamiento.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        desplazamiento.getViewport().setBackground(Color.WHITE);
        Componentes.ubicar(cuenta, desplazamiento, 18, 120, 380, 332);
    }

    private void construirBarraEstado(JPanel contenido) {
        JPanel barra = Componentes.ubicar(contenido, Componentes.panel(Color.WHITE), 0, 614, 1060, 26);
        lblEstado.setText(textoEstado);
        lblEstado.setVerticalAlignment(SwingConstants.CENTER);
        Componentes.ubicar(barra, lblEstado, 12, 0, 1030, 26);
    }

    // Cada operación muestra solo los campos que necesita
    private void seleccionarOperacion(Operacion elegida) {
        operacion = elegida;
        pintarPestanias();
        txtCuenta.quitarError();
        txtDestino.quitarError();
        txtImporte.quitarError();

        boolean esTransferencia = elegida == Operacion.TRANSFERENCIA;
        boolean llevaImporte = elegida != Operacion.MOVIMIENTOS;

        lblCuenta.setText(esTransferencia ? "Cuenta de origen" : "Número de cuenta");
        txtCuenta.setSize(esTransferencia ? 244 : 512, 48);
        lblDestino.setVisible(esTransferencia);
        txtDestino.setVisible(esTransferencia);
        lblImporte.setVisible(llevaImporte);
        txtImporte.setVisible(llevaImporte);
        btnEjecutar.setLocation(24, llevaImporte ? 194 : 106);

        switch (elegida) {
            case DEPOSITO -> configurarTextos("Depositar", "Registre un depósito en una cuenta activa");
            case RETIRO -> configurarTextos("Retirar", "Registre un retiro; la cuenta debe tener saldo suficiente");
            case TRANSFERENCIA -> configurarTextos("Transferir", "Transfiera entre dos cuentas activas distintas");
            default -> configurarTextos("Consultar", "Consulte los datos, el saldo y los movimientos de una cuenta");
        }

        if (!llevaImporte) {
            txtImporte.setText("");
        }
        limpiarResultado();
        pnlFormulario.revalidate();
        pnlFormulario.repaint();
        txtCuenta.requestFocusInWindow();
    }

    private void configurarTextos(String boton, String subtitulo) {
        btnEjecutar.setText(boton);
        lblSubtitulo.setText(subtitulo);
    }

    // La pestaña activa se pinta en azul; las demás en tono suave
    private void pintarPestanias() {
        pestanias.forEach((boton, opcion) -> {
            boolean activa = opcion == operacion;
            boton.setColores(activa ? Estilo.AZUL : Estilo.SUAVE, activa ? Color.WHITE : Estilo.AZUL,
                    activa ? Estilo.AZUL_HOVER : Estilo.SUAVE_HOVER, activa ? Estilo.AZUL : Estilo.SUAVE_PRESIONADO);
        });
    }

    private void ejecutar() {
        if (enPeticion) {
            return;
        }
        switch (operacion) {
            case MOVIMIENTOS -> consultar();
            case DEPOSITO -> registrarEnCuenta(true);
            case RETIRO -> registrarEnCuenta(false);
            case TRANSFERENCIA -> transferir();
        }
    }

    private void consultar() {
        String cuenta = leerCuenta(txtCuenta);
        if (cuenta == null) {
            return;
        }
        ejecutarEnFondo("Consultando la cuenta " + cuenta + "...", () -> cuentaControlador.consultar(cuenta),
                respuesta -> mostrarCuenta(respuesta, true));
    }

    private void registrarEnCuenta(boolean esDeposito) {
        String cuenta = leerCuenta(txtCuenta);
        BigDecimal importe = cuenta == null ? null : leerImporte();
        if (importe == null) {
            return;
        }

        String accion = esDeposito ? "depositar" : "retirar";
        if (!DialogoConfirmacion.confirmar(this, esDeposito ? "¿Registrar el depósito?" : "¿Registrar el retiro?",
                "Se va a " + accion + " " + FormatoUtil.formatearMonto(importe) + " en la cuenta " + cuenta + ".",
                esDeposito ? "Sí, depositar" : "Sí, retirar")) {
            return;
        }

        ejecutarEnFondo(esDeposito ? "Registrando el depósito..." : "Registrando el retiro...", () -> {
            RespuestaTransaccion respuesta = esDeposito
                    ? cuentaControlador.depositar(cuenta, importe)
                    : cuentaControlador.retirar(cuenta, importe);
            return new Resultado(respuesta, cuentaControlador.consultar(cuenta));
        }, resultado -> mostrarTransaccion(resultado, cuenta));
    }

    private void transferir() {
        String origen = leerCuenta(txtCuenta);
        String destino = origen == null ? null : leerCuenta(txtDestino);
        if (destino == null) {
            return;
        }
        if (origen.equals(destino)) {
            mostrarErrorDeCampo(txtDestino, ValidacionUtil.MENSAJE_CUENTAS_IGUALES);
            return;
        }
        BigDecimal importe = leerImporte();
        if (importe == null) {
            return;
        }

        if (!DialogoConfirmacion.confirmar(this, "¿Registrar la transferencia?",
                "Se van a transferir " + FormatoUtil.formatearMonto(importe) + " de la cuenta " + origen
                + " a la cuenta " + destino + ". Ambas cuentas se actualizan en una sola operación.", "Sí, transferir")) {
            return;
        }

        ejecutarEnFondo("Registrando la transferencia...", () -> new Resultado(
                cuentaControlador.transferir(origen, destino, importe), cuentaControlador.consultar(origen)),
                resultado -> mostrarTransaccion(resultado, origen));
    }

    // Despliega todas las cuentas debajo del campo; las que no están activas se muestran en rojo
    // (se pueden consultar, pero el servidor rechaza depósitos, retiros y transferencias en ellas)
    private void mostrarListaCuentas(CampoTexto campo) {
        if (enPeticion) {
            return;
        }
        if (cuentasServidor == null) {
            ejecutarEnFondo("Cargando la lista de cuentas...", cuentaControlador::listarCuentas, respuesta -> {
                cuentasServidor = respuesta.cuentas();
                desplegarCuentas(campo);
            });
            return;
        }
        desplegarCuentas(campo);
    }

    private void desplegarCuentas(CampoTexto campo) {
        JPopupMenu lista = new JPopupMenu();
        if (cuentasServidor.isEmpty()) {
            JMenuItem vacia = new JMenuItem("No hay cuentas registradas");
            vacia.setEnabled(false);
            lista.add(vacia);
        }
        for (CuentaDTO cuenta : cuentasServidor) {
            JMenuItem opcion = new JMenuItem(FormatoUtil.formatearOpcionCuenta(cuenta));
            opcion.setFont(Estilo.fuente(11f, cuenta.numero().equals(campo.getText()) ? Font.BOLD : Font.PLAIN));
            opcion.setForeground(cuenta.estaActiva() ? Estilo.TEXTO : Estilo.ERROR);
            opcion.setPreferredSize(new Dimension(Math.max(campo.getWidth(), opcion.getPreferredSize().width + 16), 36));
            opcion.addActionListener(e -> {
                campo.setText(cuenta.numero());
                campo.requestFocusInWindow();
            });
            lista.add(opcion);
        }
        lista.show(campo, 0, campo.getHeight() + 2);
    }

    private void actualizarCuenta() {
        if (!enPeticion && cuentaMostrada != null) {
            cuentasServidor = null;
            String cuenta = cuentaMostrada;
            ejecutarEnFondo("Actualizando la cuenta " + cuenta + "...", () -> cuentaControlador.consultar(cuenta),
                    respuesta -> mostrarCuenta(respuesta, false));
        }
    }

    // Comprobante de la operación y movimientos actualizados de la cuenta
    private record Resultado(RespuestaTransaccion transaccion, RespuestaMovimientos movimientos) {
    }

    private void mostrarTransaccion(Resultado resultado, String cuenta) {
        RespuestaTransaccion respuesta = resultado.transaccion();
        txtImporte.setText("");
        mostrarCuenta(resultado.movimientos(), false);
        lblResultadoTitulo.setText("Movimiento Nro " + respuesta.numeroMovimiento() + " · nuevo saldo de la cuenta "
                + cuenta + ":");
        lblResultado.setText(FormatoUtil.formatearMonto(respuesta.saldo()));
        lblResultado.setToolTipText(respuesta.mensaje());
        DialogoConfirmacion.informar(this, "¡Operación registrada!",
                respuesta.mensaje() + ". Nuevo saldo: " + FormatoUtil.formatearMonto(respuesta.saldo()) + ".");
    }

    // Los movimientos vienen del servidor: incluyen lo registrado desde la consola, la web y el móvil
    private void mostrarCuenta(RespuestaMovimientos respuesta, boolean mostrarSaldo) {
        CuentaDTO cuenta = respuesta.cuenta();
        String saldo = FormatoUtil.formatearMonto(cuenta.saldo(), cuenta.simboloMoneda());

        lblCuentaTitulo.setText("Cuenta " + cuenta.numero());
        lblCuentaDatos.setText(Componentes.html(cuenta.cliente() + " · cédula " + cuenta.cedula() + "\n"
                + cuenta.sucursal() + " · " + cuenta.moneda() + " · estado " + cuenta.estado() + "\n"
                + "Saldo " + saldo + " · " + respuesta.mensaje()));
        lblCuentaDatos.setForeground("ACTIVO".equals(cuenta.estado()) ? Estilo.TEXTO_SECUNDARIO : Estilo.ERROR);

        // El más reciente arriba
        movimientosMostrados.clear();
        movimientosMostrados.addAll(respuesta.movimientos());
        movimientosMostrados.sort(Comparator.comparingInt(MovimientoDTO::numero).reversed());
        simboloMostrado = cuenta.simboloMoneda();
        modeloMovimientos.setRowCount(0);
        for (MovimientoDTO movimiento : movimientosMostrados) {
            String tipo = movimiento.cuentaReferencia() == null
                    ? movimiento.tipo()
                    : movimiento.tipo() + "\n" + (movimiento.esIngreso() ? "desde " : "hacia ") + movimiento.cuentaReferencia();
            String fecha = movimiento.fechaHora() == null ? "-"
                    : movimiento.fechaHora().format(FORMATO_FECHA_TABLA) + "\n" + movimiento.fechaHora().format(FORMATO_HORA_TABLA);
            modeloMovimientos.addRow(new Object[] {movimiento.numero(), fecha, tipo,
                FormatoUtil.formatearMovimiento(movimiento.importe(), movimiento.esIngreso(), simboloMostrado)});
        }
        tblMovimientos.clearSelection();

        cuentaMostrada = cuenta.numero();
        btnActualizar.setEnabled(true);

        if (mostrarSaldo) {
            lblResultadoTitulo.setText("Saldo disponible de la cuenta " + cuenta.numero() + " (" + cuenta.estado() + ")");
            lblResultado.setText(saldo);
            lblResultado.setToolTipText(cuenta.cliente() + " · " + cuenta.sucursal());
        }
    }

    private void limpiarResultado() {
        lblResultadoTitulo.setText(operacion == Operacion.MOVIMIENTOS ? "Saldo disponible" : "Comprobante");
        lblResultado.setText("—");
        lblResultado.setToolTipText(null);
    }

    private String leerCuenta(CampoTexto campo) {
        try {
            return ValidacionUtil.validarCuenta(campo.getText());
        } catch (IllegalArgumentException ex) {
            mostrarErrorDeCampo(campo, ex.getMessage());
            return null;
        }
    }

    private BigDecimal leerImporte() {
        try {
            return ValidacionUtil.interpretarImporte(txtImporte.getText());
        } catch (IllegalArgumentException ex) {
            mostrarErrorDeCampo(txtImporte, ex.getMessage());
            return null;
        }
    }

    private void mostrarErrorDeCampo(CampoTexto campo, String mensaje) {
        campo.marcarError();
        DialogoConfirmacion.informar(this, "Datos inválidos", mensaje + ".", "!");
        campo.requestFocusInWindow();
    }

    // Cambiar contraseña se abre como otra pantalla: mismo tamaño y misma posición que esta,
    // así la cubre por completo en lugar de verse como una ventana flotante
    private void cambiarContrasenia() {
        FormCambiarContrasenia pantalla = new FormCambiarContrasenia(this, loginControlador);
        pantalla.setLocation(getLocation());
        pantalla.setVisible(true);

        // Si el usuario movió la pantalla, esta reaparece en ese mismo lugar
        setLocation(pantalla.getLocation());
        if (pantalla.isSesionExpirada()) {
            cerrar(Salida.VOLVER_AL_INICIO_DE_SESION);
        }
    }

    private void cerrarSesion() {
        if (!DialogoConfirmacion.confirmar(this, "¿Cerrar sesión?",
                "Tendrá que volver a ingresar su usuario y contraseña para seguir operando.", "Sí, cerrar sesión")) {
            return;
        }

        cambiarEstadoOcupado(true, "Cerrando la sesión...");
        Tarea.ejecutar(loginControlador::cerrarSesion, mensaje -> {
            cambiarEstadoOcupado(false, null);
            cerrar(Salida.VOLVER_AL_INICIO_DE_SESION);
        }, ex -> {
            cambiarEstadoOcupado(false, null);
            if (ex instanceof ExcepcionClienteWS servicio && servicio.esErrorConexion()) {
                DialogoConfirmacion.informar(this, "Sesión cerrada en este equipo",
                        "No se pudo avisar al servidor; la sesión vencerá sola tras 30 minutos sin uso.", "!");
            }
            // Si la sesión ya había vencido en el servidor no hay nada más que cerrar
            cerrar(Salida.VOLVER_AL_INICIO_DE_SESION);
        });
    }

    private <T> void ejecutarEnFondo(String mensaje, Supplier<T> trabajo, Consumer<T> alTerminar) {
        cambiarEstadoOcupado(true, mensaje);
        Tarea.ejecutar(trabajo, resultado -> {
            cambiarEstadoOcupado(false, null);
            alTerminar.accept(resultado);
        }, ex -> {
            cambiarEstadoOcupado(false, null);
            if (ex instanceof ExcepcionClienteWS servicio) {
                manejarErrorServicio(servicio);
            } else {
                DialogoConfirmacion.informar(this, "Datos inválidos", ex.getMessage() + ".", "!");
            }
        });
    }

    // 401: token inválido o sesión vencida (CU-10) → inicio de sesión.
    // Sin conexión: se ofrece cambiar la dirección del servidor.
    private void manejarErrorServicio(ExcepcionClienteWS ex) {
        if (ex.esErrorAutenticacion()) {
            DialogoConfirmacion.informar(this, "Sesión no válida", ex.getMessage() + " Debe iniciar sesión nuevamente.", "!");
            cerrar(Salida.VOLVER_AL_INICIO_DE_SESION);
            return;
        }
        if (ex.esErrorConexion()) {
            if (DialogoConfirmacion.confirmar(this, "Sin conexión con el servidor",
                    ex.getMessage() + " ¿Desea cambiar la dirección del servidor?", "Configurar servidor")) {
                cerrar(Salida.CONFIGURAR_SERVIDOR);
            }
            return;
        }
        DialogoConfirmacion.informar(this, "No se pudo completar la operación", ex.getMessage(), "!");
    }

    private void cerrar(Salida salida) {
        if (cerrada) {
            return;
        }
        cerrada = true;
        dispose();
        alCerrar.accept(salida);
    }

    private void cambiarEstadoOcupado(boolean ocupado, String mensaje) {
        // Las pestañas no se deshabilitan (se verían grises); se ignoran sus clics mientras hay una petición
        enPeticion = ocupado;
        for (Component componente : pnlFormulario.getComponents()) {
            componente.setEnabled(!ocupado);
        }
        btnUsuario.setEnabled(!ocupado);
        btnActualizar.setEnabled(!ocupado && cuentaMostrada != null);
        setCursor(ocupado ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        lblEstado.setText(ocupado && mensaje != null ? mensaje : textoEstado);
    }

    private static JLabel etiquetaCampo(String texto) {
        return Componentes.etiqueta(texto, Estilo.fuente(10.5f), Estilo.TEXTO_ETIQUETA);
    }

    // Bloquea letras y símbolos mientras se escribe
    private static CampoTexto campoNumerico(String ayuda, FiltroTexto filtro) {
        CampoTexto campo = new CampoTexto();
        campo.setFont(Estilo.fuente(13f));
        campo.setTextoAyuda(ayuda);
        campo.setFiltro(filtro);
        return campo;
    }

    /**
     * Celdas de dos líneas (fecha y hora; tipo y cuenta de la transferencia) e importe en verde o rojo según la acción.
     */
    private final class RenderizadorMovimiento extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada, boolean foco,
                int fila, int columna) {
            JLabel celda = (JLabel) super.getTableCellRendererComponent(tabla, valor, seleccionada, false, fila, columna);
            celda.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            celda.setVerticalAlignment(SwingConstants.CENTER);
            celda.setToolTipText(null);
            String texto = valor == null ? "" : valor.toString();
            celda.setText(texto.contains("\n") ? Componentes.html(texto) : texto);

            MovimientoDTO movimiento = fila < movimientosMostrados.size() ? movimientosMostrados.get(fila) : null;
            if (columna == 3 && movimiento != null) {
                celda.setFont(Estilo.fuente(9.5f, Font.BOLD));
                celda.setForeground(movimiento.esIngreso() ? Estilo.EXITO : Estilo.ERROR);
                celda.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                celda.setFont(Estilo.fuente(9.5f));
                celda.setForeground(Estilo.TEXTO);
                celda.setHorizontalAlignment(columna == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            }
            if (columna == 2 && movimiento != null) {
                celda.setToolTipText(movimiento.accion() + " · registrado por el empleado " + movimiento.empleado()
                        + " el " + FormatoUtil.formatearFechaHora(movimiento.fechaHora()));
            }
            return celda;
        }
    }
}
