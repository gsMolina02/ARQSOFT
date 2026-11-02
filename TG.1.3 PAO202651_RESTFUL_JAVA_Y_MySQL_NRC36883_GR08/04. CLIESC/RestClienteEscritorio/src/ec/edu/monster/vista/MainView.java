package ec.edu.monster.vista;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.modelo.CuentaDTO;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.OperationResult;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * Vista Principal Moderna de EurekaBank con Barra Lateral, Dashboard de Movimientos
 * y Vouchers Inmediatos para Depósitos, Retiros y Transferencias.
 * @author Grupo 08 (Gustavo Molina / Daniela Tituaña)
 */
public class MainView extends JFrame {

    private static final Color COLOR_SIDEBAR = new Color(24, 32, 54);
    private static final Color COLOR_SIDEBAR_HOVER = new Color(38, 49, 78);
    private static final Color COLOR_SIDEBAR_ACTIVE = new Color(14, 116, 144);
    private static final Color COLOR_BG_CONTENT = new Color(245, 247, 250);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(14, 116, 144);
    private static final Color COLOR_SUCCESS = new Color(22, 163, 74);
    private static final Color COLOR_DANGER = new Color(220, 38, 38);
    private static final Color COLOR_TEXT_DARK = new Color(30, 41, 59);
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);

    private final EurekaController controller = new EurekaController();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    private final List<CuentaDTO> listaCuentas = new ArrayList<>();

    // Componentes principales
    private CardLayout cardLayout;
    private JPanel pnlContentCards;
    private JButton btnNavDashboard;
    private JButton btnNavDeposito;
    private JButton btnNavRetiro;
    private JButton btnNavTransferencia;
    private JButton currentActiveNavBtn;

    // Dashboard componentes
    private JComboBox<String> cbCuentasDashboard;
    private JTextField txtCuentaDashboardManual;
    private JLabel lblSaldoValor;
    private JLabel lblIngresosValor;
    private JLabel lblEgresosValor;
    private JLabel lblTotalMovValor;
    private JLabel lblTitularInfo;
    private DefaultTableModel modelMovimientos;
    private JTable tblMovimientos;

    // Depósito componentes
    private JComboBox<String> cbCuentasDeposito;
    private JTextField txtCuentaDepositoManual;
    private JTextField txtImporteDeposito;
    private JPanel pnlVoucherDeposito;
    private JLabel lblVoucherDepDetalle;

    // Retiro componentes
    private JComboBox<String> cbCuentasRetiro;
    private JTextField txtCuentaRetiroManual;
    private JTextField txtImporteRetiro;
    private JPanel pnlVoucherRetiro;
    private JLabel lblVoucherRetDetalle;

    // Transferencia componentes
    private JComboBox<String> cbCuentasOrigenTransf;
    private JTextField txtCuentaOrigenManual;
    private JComboBox<String> cbCuentasDestinoTransf;
    private JTextField txtCuentaDestinoManual;
    private JTextField txtImporteTransf;
    private JPanel pnlVoucherTransf;
    private JLabel lblVoucherTransfDetalle;

    public MainView() {
        setTitle("EurekaBank - Sistema Financiero RESTful (Grupo 08)");
        setSize(1240, 800);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initCustomUI();
        cargarCuentas();
        // Cargar por defecto la primera cuenta
        SwingUtilities.invokeLater(() -> {
            if (cbCuentasDashboard.getItemCount() > 0) {
                cbCuentasDashboard.setSelectedIndex(0);
            } else {
                consultarMovimientosDirecto("00100001");
            }
        });
    }

    private void initCustomUI() {
        JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.setBackground(COLOR_BG_CONTENT);

        // Barra Lateral (Sidebar)
        JPanel pnlSidebar = crearSidebar();
        pnlRoot.add(pnlSidebar, BorderLayout.WEST);

        // Área Central con CardLayout
        cardLayout = new CardLayout();
        pnlContentCards = new JPanel(cardLayout);
        pnlContentCards.setBackground(COLOR_BG_CONTENT);

        pnlContentCards.add(crearPanelDashboard(), "DASHBOARD");
        pnlContentCards.add(crearPanelDeposito(), "DEPOSITO");
        pnlContentCards.add(crearPanelRetiro(), "RETIRO");
        pnlContentCards.add(crearPanelTransferencia(), "TRANSFERENCIA");

        pnlRoot.add(pnlContentCards, BorderLayout.CENTER);
        setContentPane(pnlRoot);

        setActiveNav(btnNavDashboard, "DASHBOARD");
    }

    // =========================================================================
    // BARRA LATERAL (SIDEBAR)
    // =========================================================================
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setPreferredSize(new Dimension(240, 750));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(15, 12, 15, 12));

        // Header Sidebar: Logo + Título
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/source/sullivan.png"));
            Image scaled = icon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        JLabel lblTitle = new JLabel("EurekaBank");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("PORTAL RESTful JAVA");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblSub.setForeground(new Color(148, 163, 184));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblLogo);
        header.add(Box.createVerticalStrut(6));
        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSub);
        header.add(Box.createVerticalStrut(15));

        // Menú de navegación
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setOpaque(false);

        btnNavDashboard = crearBotonSidebar("  Movimientos Generales", "DASHBOARD");
        btnNavDeposito = crearBotonSidebar("  Realizar Depósito", "DEPOSITO");
        btnNavRetiro = crearBotonSidebar("  Realizar Retiro", "RETIRO");
        btnNavTransferencia = crearBotonSidebar("  Transferencias", "TRANSFERENCIA");

        menu.add(btnNavDashboard);
        menu.add(Box.createVerticalStrut(6));
        menu.add(btnNavDeposito);
        menu.add(Box.createVerticalStrut(6));
        menu.add(btnNavRetiro);
        menu.add(Box.createVerticalStrut(6));
        menu.add(btnNavTransferencia);

        // Footer Sidebar: Usuario + Botón Salir
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);

        JPanel pnlUser = new JPanel(new BorderLayout());
        pnlUser.setOpaque(false);
        JLabel lblUser = new JLabel(" Empleado Activo");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(Color.WHITE);
        JLabel lblStatus = new JLabel("● Online - MySQL 8.0");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblStatus.setForeground(new Color(74, 222, 128));
        pnlUser.add(lblUser, BorderLayout.NORTH);
        pnlUser.add(lblStatus, BorderLayout.SOUTH);

        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalir.setForeground(new Color(248, 113, 113));
        btnSalir.setBackground(new Color(40, 20, 30));
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(new CompoundBorder(
                new LineBorder(new Color(239, 68, 68), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setMaximumSize(new Dimension(215, 34));
        btnSalir.setPreferredSize(new Dimension(215, 34));
        btnSalir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la sesión actual?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                new LoginView().setVisible(true);
                this.dispose();
            }
        });

        footer.add(pnlUser);
        footer.add(Box.createVerticalStrut(10));
        footer.add(btnSalir);

        sidebar.add(header, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton crearBotonSidebar(String texto, String cardName) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(COLOR_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(215, 42));
        btn.setPreferredSize(new Dimension(215, 42));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != currentActiveNavBtn) {
                    btn.setBackground(COLOR_SIDEBAR_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentActiveNavBtn) {
                    btn.setBackground(COLOR_SIDEBAR);
                }
            }
        });

        btn.addActionListener(e -> setActiveNav(btn, cardName));
        return btn;
    }

    private void setActiveNav(JButton btn, String cardName) {
        if (currentActiveNavBtn != null) {
            currentActiveNavBtn.setBackground(COLOR_SIDEBAR);
            currentActiveNavBtn.setForeground(new Color(203, 213, 225));
        }
        currentActiveNavBtn = btn;
        currentActiveNavBtn.setBackground(COLOR_SIDEBAR_ACTIVE);
        currentActiveNavBtn.setForeground(Color.WHITE);
        cardLayout.show(pnlContentCards, cardName);
    }

    // =========================================================================
    // PANEL 1: DASHBOARD / MOVIMIENTOS GENERALES
    // =========================================================================
    private JPanel crearPanelDashboard() {
        JPanel pnl = new JPanel(new BorderLayout(15, 12));
        pnl.setBackground(COLOR_BG_CONTENT);
        pnl.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Cabecera Combinada: Vertical con 3 filas que NUNCA colisionan
        JPanel pnlHeaderGroup = new JPanel();
        pnlHeaderGroup.setLayout(new BoxLayout(pnlHeaderGroup, BoxLayout.Y_AXIS));
        pnlHeaderGroup.setOpaque(false);

        // Fila 1: Título grande y subtitular
        JPanel pnlTitleRow = new JPanel(new BorderLayout(10, 0));
        pnlTitleRow.setOpaque(false);
        pnlTitleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel pnlTitleText = new JPanel();
        pnlTitleText.setLayout(new BoxLayout(pnlTitleText, BoxLayout.Y_AXIS));
        pnlTitleText.setOpaque(false);

        JLabel lblHeader = new JLabel("Movimientos Generales");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(COLOR_TEXT_DARK);

        lblTitularInfo = new JLabel("Seleccione una cuenta para consultar su balance y registro.");
        lblTitularInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitularInfo.setForeground(COLOR_TEXT_MUTED);

        pnlTitleText.add(lblHeader);
        pnlTitleText.add(Box.createVerticalStrut(2));
        pnlTitleText.add(lblTitularInfo);

        JLabel lblBadge = new JLabel("  ● API REST Conectada  ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(COLOR_SUCCESS);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(220, 252, 231));
        lblBadge.setBorder(new LineBorder(new Color(134, 239, 172), 1, true));

        pnlTitleRow.add(pnlTitleText, BorderLayout.WEST);
        pnlTitleRow.add(lblBadge, BorderLayout.EAST);

        // Fila 2: Tarjeta / Barra dedicada de selección de cuenta
        JPanel pnlSelectorCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 7));
        pnlSelectorCard.setBackground(COLOR_CARD_BG);
        pnlSelectorCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        pnlSelectorCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel lblSel = new JLabel("Cuenta / Titular:");
        lblSel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSel.setForeground(COLOR_TEXT_DARK);

        cbCuentasDashboard = new JComboBox<>();
        cbCuentasDashboard.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCuentasDashboard.setPreferredSize(new Dimension(380, 32));
        cbCuentasDashboard.addActionListener(e -> {
            String sel = (String) cbCuentasDashboard.getSelectedItem();
            if (sel != null && !sel.isEmpty()) {
                String cuenta = extraerCodigoCuenta(sel);
                txtCuentaDashboardManual.setText(cuenta);
                consultarMovimientosDirecto(cuenta);
            }
        });

        JLabel lblManual = new JLabel("o N° de cuenta:");
        lblManual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblManual.setForeground(COLOR_TEXT_MUTED);

        txtCuentaDashboardManual = new JTextField(8);
        txtCuentaDashboardManual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCuentaDashboardManual.setPreferredSize(new Dimension(100, 32));
        txtCuentaDashboardManual.setToolTipText("Ingrese número de cuenta (8 dígitos)");

        JButton btnBuscar = crearBotonModerno("🔍 Consultar", COLOR_PRIMARY);
        btnBuscar.setPreferredSize(new Dimension(110, 32));
        btnBuscar.addActionListener(e -> {
            String cuenta = txtCuentaDashboardManual.getText().trim();
            if (cuenta.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese o seleccione una cuenta.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            consultarMovimientosDirecto(cuenta);
        });

        JButton btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRefrescar.setForeground(COLOR_PRIMARY);
        btnRefrescar.setBackground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setPreferredSize(new Dimension(105, 32));
        btnRefrescar.setBorder(new LineBorder(COLOR_PRIMARY, 1, true));
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> {
            cargarCuentas();
            String c = txtCuentaDashboardManual.getText().trim();
            if (!c.isEmpty()) consultarMovimientosDirecto(c);
        });

        pnlSelectorCard.add(lblSel);
        pnlSelectorCard.add(cbCuentasDashboard);
        pnlSelectorCard.add(lblManual);
        pnlSelectorCard.add(txtCuentaDashboardManual);
        pnlSelectorCard.add(btnBuscar);
        pnlSelectorCard.add(btnRefrescar);

        // Fila 3: Tarjetas KPI (Saldo, Ingresos, Egresos, Cantidad Movimientos)
        JPanel pnlKPIs = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlKPIs.setOpaque(false);
        pnlKPIs.setPreferredSize(new Dimension(800, 85));
        pnlKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        lblSaldoValor = new JLabel("$ 0.00", SwingConstants.CENTER);
        lblIngresosValor = new JLabel("$ 0.00", SwingConstants.CENTER);
        lblEgresosValor = new JLabel("$ 0.00", SwingConstants.CENTER);
        lblTotalMovValor = new JLabel("0", SwingConstants.CENTER);

        pnlKPIs.add(crearCardKPI("SALDO DISPONIBLE", lblSaldoValor, new Color(14, 116, 144)));
        pnlKPIs.add(crearCardKPI("TOTAL INGRESOS", lblIngresosValor, COLOR_SUCCESS));
        pnlKPIs.add(crearCardKPI("TOTAL EGRESOS", lblEgresosValor, COLOR_DANGER));
        pnlKPIs.add(crearCardKPI("MOVIMIENTOS REGISTRADOS", lblTotalMovValor, new Color(100, 116, 139)));

        // Armar cabecera vertical
        pnlHeaderGroup.add(pnlTitleRow);
        pnlHeaderGroup.add(Box.createVerticalStrut(10));
        pnlHeaderGroup.add(pnlSelectorCard);
        pnlHeaderGroup.add(Box.createVerticalStrut(12));
        pnlHeaderGroup.add(pnlKPIs);

        pnl.add(pnlHeaderGroup, BorderLayout.NORTH);

        // Tabla de Movimientos
        String[] columnas = {"N° MOV", "FECHA Y HORA", "TIPO DE OPERACIÓN", "ACCIÓN", "IMPORTE"};
        modelMovimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMovimientos = new JTable(modelMovimientos);
        tblMovimientos.setRowHeight(32);
        tblMovimientos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMovimientos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMovimientos.getTableHeader().setBackground(new Color(241, 245, 249));
        tblMovimientos.getTableHeader().setForeground(COLOR_TEXT_DARK);
        tblMovimientos.setShowGrid(true);
        tblMovimientos.setGridColor(new Color(226, 232, 240));
        tblMovimientos.setSelectionBackground(new Color(224, 242, 254));
        tblMovimientos.setSelectionForeground(COLOR_TEXT_DARK);

        // Renderizador con colores para Ingreso / Salida
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                String accion = (String) table.getValueAt(row, 3);
                if (column == 3) {
                    if ("INGRESO".equalsIgnoreCase(accion)) {
                        c.setForeground(COLOR_SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(COLOR_DANGER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                } else if (column == 4) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if ("INGRESO".equalsIgnoreCase(accion)) {
                        c.setForeground(COLOR_SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(COLOR_DANGER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                } else {
                    c.setForeground(COLOR_TEXT_DARK);
                }
                return c;
            }
        };

        for (int i = 0; i < tblMovimientos.getColumnCount(); i++) {
            tblMovimientos.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        JScrollPane scroll = new JScrollPane(tblMovimientos);
        scroll.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        pnl.add(scroll, BorderLayout.CENTER);

        return pnl;
    }

    private JPanel crearCardKPI(String titulo, JLabel lblValor, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(COLOR_TEXT_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(colorAcento);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // PANEL 2: DEPÓSITO (Con Voucher Inmediato)
    // =========================================================================
    private JPanel crearPanelDeposito() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(COLOR_BG_CONTENT);
        root.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Realizar Depósito Bancario");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(COLOR_TEXT_DARK);
        root.add(lblHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlCenter.setOpaque(false);

        // Formulario
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(COLOR_CARD_BG);
        form.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel lbl1 = new JLabel("Seleccione el Cliente o Ingrese la Cuenta:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cbCuentasDeposito = new JComboBox<>();
        cbCuentasDeposito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCuentasDeposito.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbCuentasDeposito.addActionListener(e -> {
            String sel = (String) cbCuentasDeposito.getSelectedItem();
            if (sel != null) txtCuentaDepositoManual.setText(extraerCodigoCuenta(sel));
        });

        txtCuentaDepositoManual = new JTextField();
        txtCuentaDepositoManual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCuentaDepositoManual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lbl2 = new JLabel("Importe a Depositar ($):");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));

        txtImporteDeposito = new JTextField();
        txtImporteDeposito.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtImporteDeposito.setForeground(COLOR_PRIMARY);
        txtImporteDeposito.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel pnlMontos = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlMontos.setOpaque(false);
        for (double m : new double[]{20, 50, 100, 200, 500}) {
            JButton b = new JButton("$" + (int) m);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.addActionListener(ev -> txtImporteDeposito.setText(String.valueOf(m)));
            pnlMontos.add(b);
        }

        JButton btnEjecutar = crearBotonModerno("✔ Confirmar y Registrar Depósito", COLOR_SUCCESS);
        btnEjecutar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnEjecutar.addActionListener(e -> ejecutarAccionDeposito());

        form.add(lbl1);
        form.add(Box.createVerticalStrut(5));
        form.add(cbCuentasDeposito);
        form.add(Box.createVerticalStrut(5));
        form.add(new JLabel("o escribir cuenta manualmente:"));
        form.add(txtCuentaDepositoManual);
        form.add(Box.createVerticalStrut(12));
        form.add(lbl2);
        form.add(Box.createVerticalStrut(5));
        form.add(txtImporteDeposito);
        form.add(Box.createVerticalStrut(6));
        form.add(pnlMontos);
        form.add(Box.createVerticalStrut(20));
        form.add(btnEjecutar);

        // Panel de Voucher Inmediato
        pnlVoucherDeposito = crearPanelVoucher("COMPROBANTE DE DEPÓSITO");
        lblVoucherDepDetalle = new JLabel("<html><center style='color:#64748B;'>Realice un depósito para emitir<br>el comprobante digital inmediato.</center></html>", SwingConstants.CENTER);
        lblVoucherDepDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlVoucherDeposito.add(lblVoucherDepDetalle, BorderLayout.CENTER);

        pnlCenter.add(form);
        pnlCenter.add(pnlVoucherDeposito);
        root.add(pnlCenter, BorderLayout.CENTER);

        return root;
    }

    private void ejecutarAccionDeposito() {
        String cuenta = txtCuentaDepositoManual.getText().trim();
        String impStr = txtImporteDeposito.getText().trim();
        if (cuenta.isEmpty() || impStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete la cuenta y el importe a depositar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double importe;
        try {
            importe = Double.parseDouble(impStr);
            if (importe <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un importe numérico mayor a 0.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OperationResult res = controller.ejecutarDeposito(cuenta, importe);
        if (res != null && res.isSuccess()) {
            mostrarVoucherExitoso(pnlVoucherDeposito, lblVoucherDepDetalle, res, "DEPÓSITO", cuenta);
            txtImporteDeposito.setText("");
            cargarCuentas();
            int opt = JOptionPane.showOptionDialog(this,
                    "¡Depósito de $" + moneyFormat.format(importe) + " realizado con éxito!\nNuevo saldo: $" + moneyFormat.format(res.getNuevoSaldo()) +
                    "\n¿Desea ver el movimiento en la página principal?",
                    "Operación Exitosa", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new Object[]{"Ver en Movimientos", "Continuar Aquí"}, "Ver en Movimientos");
            if (opt == JOptionPane.YES_OPTION) {
                irAMovimientosDeCuenta(cuenta);
            }
        } else {
            String msg = res != null ? res.getMessage() : "Error de comunicación con el servidor REST.";
            JOptionPane.showMessageDialog(this, "No se pudo realizar el depósito:\n" + msg, "Error en Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // PANEL 3: RETIRO (Con Voucher Inmediato)
    // =========================================================================
    private JPanel crearPanelRetiro() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(COLOR_BG_CONTENT);
        root.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Realizar Retiro Bancario");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(COLOR_TEXT_DARK);
        root.add(lblHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlCenter.setOpaque(false);

        // Formulario
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(COLOR_CARD_BG);
        form.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel lbl1 = new JLabel("Seleccione el Cliente o Ingrese la Cuenta:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cbCuentasRetiro = new JComboBox<>();
        cbCuentasRetiro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCuentasRetiro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbCuentasRetiro.addActionListener(e -> {
            String sel = (String) cbCuentasRetiro.getSelectedItem();
            if (sel != null) txtCuentaRetiroManual.setText(extraerCodigoCuenta(sel));
        });

        txtCuentaRetiroManual = new JTextField();
        txtCuentaRetiroManual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCuentaRetiroManual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lbl2 = new JLabel("Importe a Retirar ($):");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));

        txtImporteRetiro = new JTextField();
        txtImporteRetiro.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtImporteRetiro.setForeground(COLOR_DANGER);
        txtImporteRetiro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel pnlMontos = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlMontos.setOpaque(false);
        for (double m : new double[]{20, 50, 100, 200, 500}) {
            JButton b = new JButton("$" + (int) m);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.addActionListener(ev -> txtImporteRetiro.setText(String.valueOf(m)));
            pnlMontos.add(b);
        }

        JButton btnEjecutar = crearBotonModerno("✔ Confirmar y Registrar Retiro", COLOR_DANGER);
        btnEjecutar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnEjecutar.addActionListener(e -> ejecutarAccionRetiro());

        form.add(lbl1);
        form.add(Box.createVerticalStrut(5));
        form.add(cbCuentasRetiro);
        form.add(Box.createVerticalStrut(5));
        form.add(new JLabel("o escribir cuenta manualmente:"));
        form.add(txtCuentaRetiroManual);
        form.add(Box.createVerticalStrut(12));
        form.add(lbl2);
        form.add(Box.createVerticalStrut(5));
        form.add(txtImporteRetiro);
        form.add(Box.createVerticalStrut(6));
        form.add(pnlMontos);
        form.add(Box.createVerticalStrut(20));
        form.add(btnEjecutar);

        // Panel de Voucher Inmediato
        pnlVoucherRetiro = crearPanelVoucher("COMPROBANTE DE RETIRO");
        lblVoucherRetDetalle = new JLabel("<html><center style='color:#64748B;'>Realice un retiro para emitir<br>el comprobante digital inmediato.</center></html>", SwingConstants.CENTER);
        lblVoucherRetDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlVoucherRetiro.add(lblVoucherRetDetalle, BorderLayout.CENTER);

        pnlCenter.add(form);
        pnlCenter.add(pnlVoucherRetiro);
        root.add(pnlCenter, BorderLayout.CENTER);

        return root;
    }

    private void ejecutarAccionRetiro() {
        String cuenta = txtCuentaRetiroManual.getText().trim();
        String impStr = txtImporteRetiro.getText().trim();
        if (cuenta.isEmpty() || impStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete la cuenta y el importe a retirar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double importe;
        try {
            importe = Double.parseDouble(impStr);
            if (importe <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un importe numérico mayor a 0.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OperationResult res = controller.ejecutarRetiro(cuenta, importe);
        if (res != null && res.isSuccess()) {
            mostrarVoucherExitoso(pnlVoucherRetiro, lblVoucherRetDetalle, res, "RETIRO", cuenta);
            txtImporteRetiro.setText("");
            cargarCuentas();
            int opt = JOptionPane.showOptionDialog(this,
                    "¡Retiro de $" + moneyFormat.format(importe) + " realizado con éxito!\nNuevo saldo: $" + moneyFormat.format(res.getNuevoSaldo()) +
                    "\n¿Desea ver el movimiento en la página principal?",
                    "Operación Exitosa", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new Object[]{"Ver en Movimientos", "Continuar Aquí"}, "Ver en Movimientos");
            if (opt == JOptionPane.YES_OPTION) {
                irAMovimientosDeCuenta(cuenta);
            }
        } else {
            String msg = res != null ? res.getMessage() : "Error de comunicación con el servidor REST.";
            JOptionPane.showMessageDialog(this, "No se pudo realizar el retiro:\n" + msg, "Error en Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // PANEL 4: TRANSFERENCIAS (Con Selector de Usuarios + Voucher Inmediato)
    // =========================================================================
    private JPanel crearPanelTransferencia() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(COLOR_BG_CONTENT);
        root.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Transferencias Interbancarias");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(COLOR_TEXT_DARK);
        root.add(lblHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlCenter.setOpaque(false);

        // Formulario
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(COLOR_CARD_BG);
        form.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Cuenta Origen
        JLabel lblOrig = new JLabel("1. Cuenta Origen (Seleccione Usuario o Ingrese Número):");
        lblOrig.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbCuentasOrigenTransf = new JComboBox<>();
        cbCuentasOrigenTransf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCuentasOrigenTransf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbCuentasOrigenTransf.addActionListener(e -> {
            String sel = (String) cbCuentasOrigenTransf.getSelectedItem();
            if (sel != null) txtCuentaOrigenManual.setText(extraerCodigoCuenta(sel));
        });

        txtCuentaOrigenManual = new JTextField();
        txtCuentaOrigenManual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCuentaOrigenManual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Cuenta Destino
        JLabel lblDest = new JLabel("2. Cuenta Destino (Seleccione Usuario o Ingrese Número):");
        lblDest.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbCuentasDestinoTransf = new JComboBox<>();
        cbCuentasDestinoTransf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbCuentasDestinoTransf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbCuentasDestinoTransf.addActionListener(e -> {
            String sel = (String) cbCuentasDestinoTransf.getSelectedItem();
            if (sel != null) txtCuentaDestinoManual.setText(extraerCodigoCuenta(sel));
        });

        txtCuentaDestinoManual = new JTextField();
        txtCuentaDestinoManual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCuentaDestinoManual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Importe
        JLabel lblMonto = new JLabel("3. Monto a Transferir ($):");
        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtImporteTransf = new JTextField();
        txtImporteTransf.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtImporteTransf.setForeground(COLOR_PRIMARY);
        txtImporteTransf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JPanel pnlMontos = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlMontos.setOpaque(false);
        for (double m : new double[]{10, 25, 50, 100, 250}) {
            JButton b = new JButton("$" + (int) m);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.addActionListener(ev -> txtImporteTransf.setText(String.valueOf(m)));
            pnlMontos.add(b);
        }

        JButton btnEjecutar = crearBotonModerno("🔄 Ejecutar Transferencia Inmediata", COLOR_PRIMARY);
        btnEjecutar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnEjecutar.addActionListener(e -> ejecutarAccionTransferencia());

        form.add(lblOrig);
        form.add(Box.createVerticalStrut(3));
        form.add(cbCuentasOrigenTransf);
        form.add(Box.createVerticalStrut(2));
        form.add(new JLabel("o número de cuenta origen:"));
        form.add(txtCuentaOrigenManual);
        form.add(Box.createVerticalStrut(8));

        form.add(lblDest);
        form.add(Box.createVerticalStrut(3));
        form.add(cbCuentasDestinoTransf);
        form.add(Box.createVerticalStrut(2));
        form.add(new JLabel("o número de cuenta destino:"));
        form.add(txtCuentaDestinoManual);
        form.add(Box.createVerticalStrut(8));

        form.add(lblMonto);
        form.add(Box.createVerticalStrut(3));
        form.add(txtImporteTransf);
        form.add(Box.createVerticalStrut(5));
        form.add(pnlMontos);
        form.add(Box.createVerticalStrut(14));
        form.add(btnEjecutar);

        // Panel de Voucher Inmediato
        pnlVoucherTransf = crearPanelVoucher("COMPROBANTE DE TRANSFERENCIA");
        lblVoucherTransfDetalle = new JLabel("<html><center style='color:#64748B;'>Seleccione las cuentas de origen y destino<br>para emitir la transferencia inmediata.</center></html>", SwingConstants.CENTER);
        lblVoucherTransfDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlVoucherTransf.add(lblVoucherTransfDetalle, BorderLayout.CENTER);

        pnlCenter.add(form);
        pnlCenter.add(pnlVoucherTransf);
        root.add(pnlCenter, BorderLayout.CENTER);

        return root;
    }

    private void ejecutarAccionTransferencia() {
        String origen = txtCuentaOrigenManual.getText().trim();
        String destino = txtCuentaDestinoManual.getText().trim();
        String impStr = txtImporteTransf.getText().trim();

        if (origen.isEmpty() || destino.isEmpty() || impStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete las cuentas de origen, destino y el monto a transferir.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origen.equals(destino)) {
            JOptionPane.showMessageDialog(this, "La cuenta de origen y destino no pueden ser la misma.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double importe;
        try {
            importe = Double.parseDouble(impStr);
            if (importe <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un importe numérico mayor a 0.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OperationResult res = controller.ejecutarTransferencia(origen, destino, importe);
        if (res != null && res.isSuccess()) {
            mostrarVoucherTransferenciaExitoso(pnlVoucherTransf, lblVoucherTransfDetalle, res, origen, destino, importe);
            txtImporteTransf.setText("");
            cargarCuentas();

            int opt = JOptionPane.showOptionDialog(this,
                    "¡Transferencia de $" + moneyFormat.format(importe) + " realizada con éxito!\n" +
                    "Debitado de: " + origen + " | Acreditado en: " + destino + "\n" +
                    "¿Desea ver el movimiento en la página principal?",
                    "Transferencia Exitosa", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new Object[]{"Ver en Movimientos", "Continuar Aquí"}, "Ver en Movimientos");
            if (opt == JOptionPane.YES_OPTION) {
                irAMovimientosDeCuenta(origen);
            }
        } else {
            String msg = res != null ? res.getMessage() : "Error de comunicación con el servidor REST.";
            JOptionPane.showMessageDialog(this, "No se pudo realizar la transferencia:\n" + msg, "Error en Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // VOUCHERS / COMPROBANTES DIGITALES EN PANTALLA
    // =========================================================================
    private JPanel crearPanelVoucher(String titulo) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblT = new JLabel(titulo, SwingConstants.CENTER);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblT.setForeground(COLOR_TEXT_DARK);
        header.add(lblT, BorderLayout.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(226, 232, 240));
        header.add(sep, BorderLayout.SOUTH);

        card.add(header, BorderLayout.NORTH);
        return card;
    }

    private void mostrarVoucherExitoso(JPanel parent, JLabel lblContenido, OperationResult res, String tipo, String cuenta) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        String html = "<html><div style='padding:15px; font-family:Segoe UI;'>"
                + "<div style='text-align:center; color:#16A34A; font-weight:bold; font-size:16px;'>✔ TRANSACCIÓN EXITOSA</div>"
                + "<hr style='border:1px dashed #CBD5E1; margin:10px 0;'>"
                + "<table width='100%' cellpadding='4' style='font-size:13px;'>"
                + "<tr><td><b>Tipo de Operación:</b></td><td>" + tipo + "</td></tr>"
                + "<tr><td><b>N° de Movimiento:</b></td><td><span style='color:#0E7490; font-weight:bold; font-size:15px;'>#" + res.getNromov() + "</span></td></tr>"
                + "<tr><td><b>Cuenta:</b></td><td><b>" + cuenta + "</b></td></tr>"
                + "<tr><td><b>Titular:</b></td><td>" + (res.getTitular() != null ? res.getTitular() : "-") + "</td></tr>"
                + "<tr><td><b>Importe:</b></td><td><span style='color:" + (tipo.equals("DEPÓSITO") ? "#16A34A" : "#DC2626") + "; font-weight:bold; font-size:16px;'>$ " + moneyFormat.format(res.getImporte()) + "</span></td></tr>"
                + "<tr><td><b>Nuevo Saldo:</b></td><td><span style='color:#0E7490; font-weight:bold;'>$ " + moneyFormat.format(res.getNuevoSaldo()) + "</span></td></tr>"
                + "<tr><td><b>Fecha y Hora:</b></td><td>" + fecha + "</td></tr>"
                + "<tr><td><b>Canal:</b></td><td>Cliente Escritorio REST Java</td></tr>"
                + "</table>"
                + "<hr style='border:1px dashed #CBD5E1; margin:10px 0;'>"
                + "<div style='text-align:center; color:#64748B; font-size:11px;'>EurekaBank Core Bancario S.A. - Control ACID</div>"
                + "</div></html>";
        lblContenido.setText(html);
        parent.revalidate();
        parent.repaint();
    }

    private void mostrarVoucherTransferenciaExitoso(JPanel parent, JLabel lblContenido, OperationResult res, String origen, String destino, double importe) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        String html = "<html><div style='padding:15px; font-family:Segoe UI;'>"
                + "<div style='text-align:center; color:#16A34A; font-weight:bold; font-size:16px;'>✔ TRANSFERENCIA COMPLETADA</div>"
                + "<hr style='border:1px dashed #CBD5E1; margin:10px 0;'>"
                + "<table width='100%' cellpadding='4' style='font-size:13px;'>"
                + "<tr><td><b>N° Movimiento:</b></td><td><span style='color:#0E7490; font-weight:bold; font-size:15px;'>#" + res.getNromov() + "</span></td></tr>"
                + "<tr><td><b>Cuenta Origen (Débito):</b></td><td><b>" + origen + "</b> (" + (res.getTitular() != null ? res.getTitular() : "Titular") + ")</td></tr>"
                + "<tr><td><b>Cuenta Destino (Crédito):</b></td><td><b>" + destino + "</b></td></tr>"
                + "<tr><td><b>Monto Transferido:</b></td><td><span style='color:#16A34A; font-weight:bold; font-size:16px;'>$ " + moneyFormat.format(importe) + "</span></td></tr>"
                + "<tr><td><b>Nuevo Saldo Origen:</b></td><td><span style='color:#0E7490; font-weight:bold;'>$ " + moneyFormat.format(res.getNuevoSaldo()) + "</span></td></tr>"
                + "<tr><td><b>Fecha y Hora:</b></td><td>" + fecha + "</td></tr>"
                + "<tr><td><b>Canal:</b></td><td>EurekaBank REST API</td></tr>"
                + "</table>"
                + "<hr style='border:1px dashed #CBD5E1; margin:10px 0;'>"
                + "<div style='text-align:center; color:#64748B; font-size:11px;'>Comprobante electrónico verificado por la base de datos</div>"
                + "</div></html>";
        lblContenido.setText(html);
        parent.revalidate();
        parent.repaint();
    }

    // =========================================================================
    // LÓGICA DE DATOS Y NAVEGACIÓN
    // =========================================================================
    private void cargarCuentas() {
        new Thread(() -> {
            List<CuentaDTO> cuentas = controller.listarCuentas();
            SwingUtilities.invokeLater(() -> {
                listaCuentas.clear();
                listaCuentas.addAll(cuentas);

                cbCuentasDashboard.removeAllItems();
                cbCuentasDeposito.removeAllItems();
                cbCuentasRetiro.removeAllItems();
                cbCuentasOrigenTransf.removeAllItems();
                cbCuentasDestinoTransf.removeAllItems();

                for (CuentaDTO c : cuentas) {
                    String item = c.getCuenta() + " - " + c.getCliente() + " (" + c.getMoneda() + " $" + moneyFormat.format(c.getSaldo()) + ")";
                    cbCuentasDashboard.addItem(item);
                    cbCuentasDeposito.addItem(item);
                    cbCuentasRetiro.addItem(item);
                    cbCuentasOrigenTransf.addItem(item);
                    cbCuentasDestinoTransf.addItem(item);
                }

                if (cbCuentasDestinoTransf.getItemCount() > 1) {
                    cbCuentasDestinoTransf.setSelectedIndex(1);
                }
            });
        }).start();
    }

    private void consultarMovimientosDirecto(String cuenta) {
        new Thread(() -> {
            CuentaDTO cInfo = controller.obtenerCuenta(cuenta);
            List<Movimiento> lista = controller.traerMovimientos(cuenta);

            SwingUtilities.invokeLater(() -> {
                modelMovimientos.setRowCount(0);
                double totalIngresos = 0;
                double totalEgresos = 0;

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                for (Movimiento m : lista) {
                    String fechaStr = m.getFecha() != null ? df.format(m.getFecha()) : "N/D";
                    String accion = m.getAccion() != null ? m.getAccion().toUpperCase() : "N/D";
                    double imp = m.getImporte();

                    if ("INGRESO".equals(accion)) {
                        totalIngresos += imp;
                    } else {
                        totalEgresos += imp;
                    }

                    modelMovimientos.addRow(new Object[]{
                            "#" + m.getNromov(),
                            fechaStr,
                            m.getTipo(),
                            accion,
                            (accion.equals("INGRESO") ? "+ " : "- ") + "$ " + moneyFormat.format(imp)
                    });
                }

                double saldoActual = (cInfo != null) ? cInfo.getSaldo() : (totalIngresos - totalEgresos);
                String moneda = (cInfo != null) ? cInfo.getMoneda() : "USD";
                String titular = (cInfo != null) ? cInfo.getCliente() : "Cliente EurekaBank";

                lblTitularInfo.setText("Titular: " + titular + " | Cuenta: " + cuenta + " (" + moneda + ")");
                lblSaldoValor.setText("$ " + moneyFormat.format(saldoActual) + " " + moneda);
                lblIngresosValor.setText("+ $ " + moneyFormat.format(totalIngresos));
                lblEgresosValor.setText("- $ " + moneyFormat.format(totalEgresos));
                lblTotalMovValor.setText(String.valueOf(lista.size()));

                // Auto-scroll al final (último movimiento)
                if (tblMovimientos.getRowCount() > 0) {
                    int lastRow = tblMovimientos.getRowCount() - 1;
                    tblMovimientos.setRowSelectionInterval(lastRow, lastRow);
                    tblMovimientos.scrollRectToVisible(tblMovimientos.getCellRect(lastRow, 0, true));
                }
            });
        }).start();
    }

    private void irAMovimientosDeCuenta(String cuenta) {
        setActiveNav(btnNavDashboard, "DASHBOARD");
        txtCuentaDashboardManual.setText(cuenta);
        // Seleccionar en el combo si existe
        for (int i = 0; i < cbCuentasDashboard.getItemCount(); i++) {
            if (cbCuentasDashboard.getItemAt(i).startsWith(cuenta)) {
                cbCuentasDashboard.setSelectedIndex(i);
                break;
            }
        }
        consultarMovimientosDirecto(cuenta);
    }

    private String extraerCodigoCuenta(String item) {
        if (item == null) return "";
        int idx = item.indexOf(" -");
        if (idx > 0) {
            return item.substring(0, idx).trim();
        }
        return item.trim();
    }

    private JButton crearBotonModerno(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName()) || "Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> new MainView().setVisible(true));
    }
}
