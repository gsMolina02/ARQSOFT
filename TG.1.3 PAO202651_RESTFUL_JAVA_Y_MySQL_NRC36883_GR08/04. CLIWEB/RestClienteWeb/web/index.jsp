<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String miCuenta = (String) session.getAttribute("MI_CUENTA");
    if (miCuenta == null || miCuenta.isEmpty()) {
        miCuenta = "00100001";
        session.setAttribute("MI_CUENTA", miCuenta);
    }
    String titular = (String) session.getAttribute("TITULAR");
    if (titular == null || titular.isEmpty()) {
        titular = "Juan Carlos García López";
        session.setAttribute("TITULAR", titular);
    }
    String userLogin = (String) session.getAttribute("USER_LOGIN");
    if (userLogin == null || userLogin.isEmpty()) {
        userLogin = "MONSTER";
        session.setAttribute("USER_LOGIN", userLogin);
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EurekaBank Web - Portal Financiero Digital (GR08)</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-body: #F8FAFC;
            --color-primary: #0E7490;
            --color-primary-dark: #094F62;
            --color-sidebar: #0F172A;
            --color-sidebar-hover: #1E293B;
            --color-success: #10B981;
            --color-danger: #EF4444;
            --color-card-bg: #FFFFFF;
            --text-dark: #0F172A;
            --text-muted: #64748B;
            --border-light: #E2E8F0;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.06);
            --shadow-md: 0 4px 6px -1px rgba(0,0,0,0.08), 0 2px 4px -2px rgba(0,0,0,0.05);
            --shadow-lg: 0 10px 15px -3px rgba(0,0,0,0.08), 0 4px 6px -4px rgba(0,0,0,0.04);
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Plus Jakarta Sans', system-ui, -apple-system, sans-serif;
            background: var(--bg-body);
            color: var(--text-dark);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* BARRA SUPERIOR / NAVBAR */
        .navbar {
            background: var(--color-sidebar);
            color: #FFFFFF;
            padding: 14px 28px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            position: sticky;
            top: 0;
            z-index: 50;
        }
        .navbar-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            text-decoration: none;
            color: #FFFFFF;
        }
        .navbar-brand img {
            width: 44px;
            height: 44px;
            object-fit: contain;
            filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
        }
        .brand-text h2 {
            font-size: 18px;
            font-weight: 800;
            letter-spacing: -0.5px;
            line-height: 1.2;
        }
        .brand-text span {
            font-size: 10px;
            color: #38BDF8;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .nav-tabs {
            display: flex;
            gap: 6px;
            list-style: none;
        }
        .nav-item {
            padding: 9px 16px;
            border-radius: 10px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            color: #94A3B8;
            transition: all 0.2s ease;
            display: flex;
            align-items: center;
            gap: 8px;
            user-select: none;
        }
        .nav-item:hover {
            background: var(--color-sidebar-hover);
            color: #FFFFFF;
        }
        .nav-item.active {
            background: var(--color-primary);
            color: #FFFFFF;
            box-shadow: 0 4px 12px rgba(14, 116, 144, 0.4);
        }

        .user-chip {
            display: flex;
            align-items: center;
            gap: 12px;
            background: rgba(255, 255, 255, 0.08);
            padding: 6px 14px;
            border-radius: 30px;
            font-size: 12px;
            border: 1px solid rgba(255, 255, 255, 0.12);
        }
        .user-chip .dot {
            width: 8px;
            height: 8px;
            background: #4ADE80;
            border-radius: 50%;
            display: inline-block;
        }
        .btn-logout {
            color: #F87171;
            text-decoration: none;
            font-weight: 700;
            margin-left: 6px;
            transition: color 0.15s;
        }
        .btn-logout:hover {
            color: #EF4444;
            text-decoration: underline;
        }

        /* CONTENIDO PRINCIPAL */
        .main-container {
            flex: 1;
            max-width: 1240px;
            width: 100%;
            margin: 24px auto;
            padding: 0 20px;
        }

        .tab-pane {
            display: none;
            animation: fadeIn 0.25s ease-in-out;
        }
        .tab-pane.active {
            display: block;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(4px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* HERO CARD (MIS MOVIMIENTOS) */
        .hero-account {
            background: linear-gradient(135deg, #0F172A 0%, #1E293B 70%, #0E7490 100%);
            color: #FFFFFF;
            border-radius: 20px;
            padding: 28px 32px;
            margin-bottom: 24px;
            box-shadow: var(--shadow-lg);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
            position: relative;
            overflow: hidden;
        }
        .hero-account::after {
            content: '';
            position: absolute;
            right: -20px;
            bottom: -30px;
            width: 220px;
            height: 220px;
            background: radial-gradient(circle, rgba(14, 116, 144, 0.4) 0%, transparent 70%);
            pointer-events: none;
        }
        .hero-info h3 {
            font-size: 13px;
            font-weight: 700;
            color: #38BDF8;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 4px;
        }
        .hero-info h1 {
            font-size: 26px;
            font-weight: 800;
            letter-spacing: -0.5px;
            margin-bottom: 4px;
        }
        .hero-info p {
            font-size: 13px;
            color: #CBD5E1;
        }
        .hero-balance {
            text-align: right;
        }
        .hero-balance span {
            font-size: 12px;
            font-weight: 700;
            color: #94A3B8;
            text-transform: uppercase;
        }
        .hero-balance .amount {
            font-size: 36px;
            font-weight: 800;
            color: #FFFFFF;
            letter-spacing: -1px;
            margin-top: 2px;
        }

        /* KPI STATS ROW */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }
        .stat-card {
            background: var(--color-card-bg);
            border-radius: 16px;
            padding: 18px 20px;
            border: 1px solid var(--border-light);
            box-shadow: var(--shadow-sm);
        }
        .stat-card .label {
            font-size: 11px;
            font-weight: 700;
            color: var(--text-muted);
            text-transform: uppercase;
            margin-bottom: 6px;
        }
        .stat-card .val {
            font-size: 22px;
            font-weight: 800;
            letter-spacing: -0.5px;
        }
        .val.income { color: var(--color-success); }
        .val.expense { color: var(--color-danger); }
        .val.neutral { color: var(--color-primary); }

        /* CARDS & CONTAINERS */
        .card {
            background: var(--color-card-bg);
            border-radius: 18px;
            border: 1px solid var(--border-light);
            box-shadow: var(--shadow-md);
            padding: 24px;
            margin-bottom: 24px;
        }
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
            padding-bottom: 14px;
            border-bottom: 1px solid var(--border-light);
        }
        .card-title {
            font-size: 18px;
            font-weight: 800;
            color: var(--text-dark);
        }
        .card-subtitle {
            font-size: 13px;
            color: var(--text-muted);
        }

        /* TABLAS MODERNAS */
        .table-responsive {
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 13px;
        }
        th {
            background: #F1F5F9;
            color: #475569;
            font-weight: 700;
            padding: 12px 16px;
            text-align: left;
            border-bottom: 1px solid var(--border-light);
            text-transform: uppercase;
            font-size: 11px;
            letter-spacing: 0.5px;
        }
        td {
            padding: 14px 16px;
            border-bottom: 1px solid #F1F5F9;
            color: var(--text-dark);
            vertical-align: middle;
        }
        tr:hover td {
            background: #F8FAFC;
        }
        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
        }
        .badge-ingreso {
            background: #DCFCE7;
            color: #15803D;
        }
        .badge-salida {
            background: #FEE2E2;
            color: #B91C1C;
        }
        .amount-green {
            color: var(--color-success);
            font-weight: 800;
        }
        .amount-red {
            color: var(--color-danger);
            font-weight: 800;
        }

        /* FORMULARIOS Y VOUCHERS */
        .split-grid {
            display: grid;
            grid-template-columns: 1.2fr 1fr;
            gap: 24px;
        }
        @media (max-width: 860px) {
            .split-grid { grid-template-columns: 1fr; }
            .nav-tabs { display: none; } /* En pantallas muy chicas */
        }
        .form-group {
            margin-bottom: 16px;
            text-align: left;
        }
        label.form-label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #334155;
            margin-bottom: 6px;
            text-transform: uppercase;
        }
        .form-control {
            width: 100%;
            padding: 11px 14px;
            font-size: 14px;
            font-family: inherit;
            border: 1.5px solid var(--border-light);
            border-radius: 10px;
            background: #FFFFFF;
            transition: all 0.2s;
        }
        .form-control:focus {
            border-color: var(--color-primary);
            outline: none;
            box-shadow: 0 0 0 3px rgba(14, 116, 144, 0.15);
        }
        .chips-row {
            display: flex;
            gap: 6px;
            margin-top: 6px;
            flex-wrap: wrap;
        }
        .chip-btn {
            background: #F1F5F9;
            border: 1px solid var(--border-light);
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 11px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.15s;
        }
        .chip-btn:hover {
            background: #E2E8F0;
            border-color: #CBD5E1;
        }
        .btn-action {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            padding: 13px;
            font-size: 14px;
            font-weight: 700;
            color: #FFFFFF;
            background: var(--color-primary);
            border: none;
            border-radius: 10px;
            cursor: pointer;
            box-shadow: var(--shadow-sm);
            transition: all 0.2s;
            margin-top: 10px;
        }
        .btn-action:hover {
            background: var(--color-primary-dark);
            transform: translateY(-1px);
            box-shadow: var(--shadow-md);
        }
        .btn-action.success { background: var(--color-success); }
        .btn-action.success:hover { background: #059669; }
        .btn-action.danger { background: var(--color-danger); }
        .btn-action.danger:hover { background: #DC2626; }

        /* VOUCHER DIGITAL BOX */
        .voucher-box {
            background: #FFFFFF;
            border: 1.5px dashed #CBD5E1;
            border-radius: 16px;
            padding: 24px;
            text-align: center;
            position: relative;
        }
        .voucher-header {
            font-size: 14px;
            font-weight: 800;
            color: var(--text-dark);
            margin-bottom: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .voucher-table {
            width: 100%;
            text-align: left;
            margin: 14px 0;
            font-size: 13px;
        }
        .voucher-table td {
            padding: 7px 4px;
            border: none;
        }
        .voucher-table td:last-child {
            text-align: right;
            font-weight: 700;
        }
        .voucher-success-badge {
            background: #DCFCE7;
            color: #15803D;
            padding: 6px 14px;
            border-radius: 20px;
            font-weight: 800;
            font-size: 12px;
            display: inline-block;
            margin-bottom: 12px;
        }
    </style>
</head>
<body>

    <!-- NAVBAR SUPERIOR MODERNA -->
    <header class="navbar">
        <a href="index.jsp" class="navbar-brand">
            <img src="images/sullivan.png" alt="EurekaBank" />
            <div class="brand-text">
                <h2>EurekaBank Web</h2>
                <span>Portal RESTful Java (GR08)</span>
            </div>
        </a>

        <!-- PESTAÑAS DE NAVEGACIÓN -->
        <ul class="nav-tabs">
            <li class="nav-item active" onclick="mostrarPestana('mis-movimientos')">
                💳 Mis Movimientos
            </li>
            <li class="nav-item" onclick="mostrarPestana('movimientos-generales')">
                📊 Movimientos Generales
            </li>
            <li class="nav-item" onclick="mostrarPestana('depositos')">
                📥 Depósito
            </li>
            <li class="nav-item" onclick="mostrarPestana('retiros')">
                📤 Retiro
            </li>
            <li class="nav-item" onclick="mostrarPestana('transferencias')">
                🔄 Transferencia
            </li>
        </ul>

        <!-- USUARIO ACTIVO Y SALIR -->
        <div class="user-chip">
            <span class="dot"></span>
            <span>👤 <b id="lblUsuarioChip"><%= titular %></b> (<%= miCuenta %>)</span>
            <a href="Login.jsp" class="btn-logout" title="Cerrar Sesión">Salir</a>
        </div>
    </header>

    <div class="main-container">

        <!-- =============================================================== -->
        <!-- PESTAÑA 1: MIS MOVIMIENTOS (EXCLUSIVO DE MI CUENTA PERSONAL)   -->
        <!-- =============================================================== -->
        <div id="pane-mis-movimientos" class="tab-pane active">
            <!-- BANNER HERO DE MI CUENTA -->
            <div class="hero-account">
                <div class="hero-info">
                    <h3>Mi Cuenta Personal EurekaBank</h3>
                    <h1 id="heroTitular"><%= titular %></h1>
                    <p>Número de Cuenta: <b id="heroCuenta"><%= miCuenta %></b> &nbsp;|&nbsp; Estado: <span style="color:#4ADE80; font-weight:700;">● ACTIVA</span></p>
                </div>
                <div class="hero-balance">
                    <span>Saldo Disponible</span>
                    <div class="amount" id="heroSaldo">$ 0.00</div>
                </div>
            </div>

            <!-- TARJETAS DE RESUMEN KPI -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="label">Total Ingresos Recibidos</div>
                    <div class="val income" id="kpiMisIngresos">+ $ 0.00</div>
                </div>
                <div class="stat-card">
                    <div class="label">Total Salidas / Egresos</div>
                    <div class="val expense" id="kpiMisEgresos">- $ 0.00</div>
                </div>
                <div class="stat-card">
                    <div class="label">Operaciones Registradas</div>
                    <div class="val neutral" id="kpiMisMovimientos">0</div>
                </div>
                <div class="stat-card" style="display:flex; flex-direction:column; justify-content:center;">
                    <div class="label">Cambiar Mi Cuenta Personal</div>
                    <select id="selCambiarMiCuenta" class="form-control" style="padding:6px; font-size:12px;" onchange="cambiarMiCuentaActiva(this.value)">
                        <option value="">Cargando cuentas...</option>
                    </select>
                </div>
            </div>

            <!-- TABLA DE MOVIMIENTOS DE MI CUENTA -->
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2 class="card-title">Historial de Mis Movimientos</h2>
                        <span class="card-subtitle">Registro cronológico de transacciones realizadas en mi cuenta personal</span>
                    </div>
                    <div>
                        <button type="button" class="chip-btn" style="padding:8px 14px; background:#0E7490; color:#fff;" onclick="cargarMisMovimientos()">
                            🔄 Actualizar Lista
                        </button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table id="tblMisMovimientos">
                        <thead>
                            <tr>
                                <th>N° Mov</th>
                                <th>Fecha</th>
                                <th>Tipo de Operación</th>
                                <th>Acción</th>
                                <th>Importe</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyMisMovimientos">
                            <tr><td colspan="5" style="text-align:center; padding:30px; color:#94A3B8;">Cargando tus movimientos...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- =============================================================== -->
        <!-- PESTAÑA 2: MOVIMIENTOS GENERALES (BUSCADOR DE CUALQUIER CUENTA) -->
        <!-- =============================================================== -->
        <div id="pane-movimientos-generales" class="tab-pane">
            <div class="card" style="background:#F1F5F9; border-color:#CBD5E1;">
                <div class="card-header" style="border:none; margin:0; padding:0 0 12px 0;">
                    <div>
                        <h2 class="card-title">Auditoría y Consulta de Cuentas Generales</h2>
                        <span class="card-subtitle">Busque y consulte los movimientos de cualquier cliente o cuenta de la institución</span>
                    </div>
                </div>

                <div style="display:flex; gap:12px; align-items:center; flex-wrap:wrap;">
                    <div style="flex:1; min-width:280px;">
                        <label class="form-label">Seleccionar Cliente / Cuenta:</label>
                        <select id="selCuentasGenerales" class="form-control" onchange="seleccionarCuentaGeneral(this.value)">
                            <option value="">Cargando lista de clientes...</option>
                        </select>
                    </div>
                    <div style="width:160px;">
                        <label class="form-label">o N° de Cuenta:</label>
                        <input type="text" id="txtCuentaGeneralManual" class="form-control" placeholder="00100001" maxlength="8" />
                    </div>
                    <div style="padding-top:22px;">
                        <button type="button" class="btn-action" style="padding:10px 20px; width:auto; margin:0;" onclick="consultarCuentaGeneralManual()">
                            🔍 Consultar
                        </button>
                    </div>
                </div>
            </div>

            <!-- TARJETA DE RESUMEN DE LA CUENTA CONSULTADA -->
            <div id="boxInfoCuentaGeneral" class="stats-grid" style="display:none;">
                <div class="stat-card">
                    <div class="label">Titular de la Cuenta</div>
                    <div class="val" id="lblGenTitular" style="font-size:16px;">-</div>
                </div>
                <div class="stat-card">
                    <div class="label">Saldo Actual</div>
                    <div class="val neutral" id="lblGenSaldo">$ 0.00</div>
                </div>
                <div class="stat-card">
                    <div class="label">Total Ingresos</div>
                    <div class="val income" id="lblGenIngresos">+ $ 0.00</div>
                </div>
                <div class="stat-card">
                    <div class="label">Total Egresos</div>
                    <div class="val expense" id="lblGenEgresos">- $ 0.00</div>
                </div>
            </div>

            <!-- TABLA DE MOVIMIENTOS GENERALES -->
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2 class="card-title">Libro de Movimientos de la Cuenta Consultada</h2>
                        <span class="card-subtitle" id="lblSubtituloGeneral">Seleccione una cuenta arriba para ver su historial</span>
                    </div>
                </div>

                <div class="table-responsive">
                    <table id="tblMovimientosGenerales">
                        <thead>
                            <tr>
                                <th>Cuenta</th>
                                <th>N° Mov</th>
                                <th>Fecha</th>
                                <th>Tipo de Operación</th>
                                <th>Acción</th>
                                <th>Importe</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyMovimientosGenerales">
                            <tr><td colspan="6" style="text-align:center; padding:30px; color:#94A3B8;">Seleccione una cuenta para consultar su registro.</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- =============================================================== -->
        <!-- PESTAÑA 3: REALIZAR DEPÓSITO                                    -->
        <!-- =============================================================== -->
        <div id="pane-depositos" class="tab-pane">
            <div class="split-grid">
                <!-- FORMULARIO -->
                <div class="card">
                    <div class="card-header">
                        <div>
                            <h2 class="card-title">Realizar Depósito Bancario</h2>
                            <span class="card-subtitle">Acredite fondos a cualquier cuenta activa de forma inmediata</span>
                        </div>
                    </div>

                    <form id="formDeposito" onsubmit="ejecutarDeposito(event)">
                        <div class="form-group">
                            <label class="form-label">Seleccionar Cuenta Destinataria:</label>
                            <select id="selDepositoCuenta" class="form-control" onchange="document.getElementById('txtDepositoManual').value=this.value">
                                <option value="">Cargando cuentas...</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">o Ingresar Cuenta Manualmente:</label>
                            <input type="text" id="txtDepositoManual" class="form-control" placeholder="Ej: 00100001" maxlength="8" required />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Importe a Depositar ($):</label>
                            <input type="number" id="txtDepositoImporte" class="form-control" step="0.01" min="0.01" placeholder="0.00" style="font-size:18px; font-weight:800; color:#10B981;" required />
                            <div class="chips-row">
                                <span style="font-size:11px; color:#64748B; margin-right:4px;">Montos rápidos:</span>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtDepositoImporte').value='20'">$20</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtDepositoImporte').value='50'">$50</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtDepositoImporte').value='100'">$100</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtDepositoImporte').value='200'">$200</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtDepositoImporte').value='500'">$500</button>
                            </div>
                        </div>

                        <button type="submit" id="btnEjecutarDep" class="btn-action success">✔ Confirmar y Registrar Depósito</button>
                    </form>
                </div>

                <!-- COMPROBANTE / VOUCHER INMEDIATO -->
                <div class="voucher-box" id="boxVoucherDeposito">
                    <div class="voucher-header">Comprobante de Depósito Digital</div>
                    <div id="voucherDepositoContenido" style="color:#64748B; padding:30px 10px;">
                        Complete el formulario para emitir el voucher oficial con número de movimiento y nuevo saldo.
                    </div>
                </div>
            </div>
        </div>

        <!-- =============================================================== -->
        <!-- PESTAÑA 4: REALIZAR RETIRO                                      -->
        <!-- =============================================================== -->
        <div id="pane-retiros" class="tab-pane">
            <div class="split-grid">
                <div class="card">
                    <div class="card-header">
                        <div>
                            <h2 class="card-title">Realizar Retiro Bancario</h2>
                            <span class="card-subtitle">Debite fondos de una cuenta activa con validación de saldo disponible</span>
                        </div>
                    </div>

                    <form id="formRetiro" onsubmit="ejecutarRetiro(event)">
                        <div class="form-group">
                            <label class="form-label">Seleccionar Cuenta:</label>
                            <select id="selRetiroCuenta" class="form-control" onchange="document.getElementById('txtRetiroManual').value=this.value">
                                <option value="">Cargando cuentas...</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">o Ingresar Cuenta Manualmente:</label>
                            <input type="text" id="txtRetiroManual" class="form-control" placeholder="Ej: 00100001" maxlength="8" required />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Importe a Retirar ($):</label>
                            <input type="number" id="txtRetiroImporte" class="form-control" step="0.01" min="0.01" placeholder="0.00" style="font-size:18px; font-weight:800; color:#EF4444;" required />
                            <div class="chips-row">
                                <span style="font-size:11px; color:#64748B; margin-right:4px;">Montos rápidos:</span>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtRetiroImporte').value='20'">$20</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtRetiroImporte').value='50'">$50</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtRetiroImporte').value='100'">$100</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtRetiroImporte').value='200'">$200</button>
                            </div>
                        </div>

                        <button type="submit" id="btnEjecutarRet" class="btn-action danger">✔ Confirmar y Registrar Retiro</button>
                    </form>
                </div>

                <div class="voucher-box" id="boxVoucherRetiro">
                    <div class="voucher-header">Comprobante de Retiro Digital</div>
                    <div id="voucherRetiroContenido" style="color:#64748B; padding:30px 10px;">
                        Complete el formulario para emitir el voucher oficial con número de movimiento y nuevo saldo.
                    </div>
                </div>
            </div>
        </div>

        <!-- =============================================================== -->
        <!-- PESTAÑA 5: TRANSFERENCIAS INTERBANCARIAS                       -->
        <!-- =============================================================== -->
        <div id="pane-transferencias" class="tab-pane">
            <div class="split-grid">
                <div class="card">
                    <div class="card-header">
                        <div>
                            <h2 class="card-title">Transferencias Interbancarias</h2>
                            <span class="card-subtitle">Transfiera fondos entre cuentas con acreditación en tiempo real</span>
                        </div>
                    </div>

                    <form id="formTransferencia" onsubmit="ejecutarTransferencia(event)">
                        <!-- ORIGEN -->
                        <div class="form-group">
                            <label class="form-label">1. Cuenta Origen (Débito):</label>
                            <select id="selTransfOrigen" class="form-control" onchange="document.getElementById('txtTransfOrigenManual').value=this.value">
                                <option value="">Cargando cuentas...</option>
                            </select>
                        </div>
                        <div class="form-group" style="margin-top:-6px;">
                            <input type="text" id="txtTransfOrigenManual" class="form-control" placeholder="Cuenta origen manual" maxlength="8" required />
                        </div>

                        <!-- DESTINO -->
                        <div class="form-group">
                            <label class="form-label">2. Cuenta Destino (Crédito / Beneficiario):</label>
                            <select id="selTransfDestino" class="form-control" onchange="document.getElementById('txtTransfDestinoManual').value=this.value">
                                <option value="">Cargando cuentas...</option>
                            </select>
                        </div>
                        <div class="form-group" style="margin-top:-6px;">
                            <input type="text" id="txtTransfDestinoManual" class="form-control" placeholder="Cuenta destino manual" maxlength="8" required />
                        </div>

                        <!-- IMPORTE -->
                        <div class="form-group">
                            <label class="form-label">3. Monto a Transferir ($):</label>
                            <input type="number" id="txtTransfImporte" class="form-control" step="0.01" min="0.01" placeholder="0.00" style="font-size:18px; font-weight:800; color:#0E7490;" required />
                            <div class="chips-row">
                                <span style="font-size:11px; color:#64748B; margin-right:4px;">Montos rápidos:</span>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtTransfImporte').value='10'">$10</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtTransfImporte').value='25'">$25</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtTransfImporte').value='50'">$50</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtTransfImporte').value='100'">$100</button>
                                <button type="button" class="chip-btn" onclick="document.getElementById('txtTransfImporte').value='250'">$250</button>
                            </div>
                        </div>

                        <button type="submit" id="btnEjecutarTransf" class="btn-action">🔄 Realizar Transferencia Inmediata</button>
                    </form>
                </div>

                <div class="voucher-box" id="boxVoucherTransf">
                    <div class="voucher-header">Comprobante de Transferencia Electrónica</div>
                    <div id="voucherTransfContenido" style="color:#64748B; padding:30px 10px;">
                        Seleccione las cuentas de origen y destino para ejecutar la operación y emitir el voucher oficial.
                    </div>
                </div>
            </div>
        </div>

    </div>

    <!-- SCRIPT PRINCIPAL DE LA BANCA WEB -->
    <script>
        var MI_CUENTA_ACTIVA = "<%= miCuenta %>";
        var TITULAR_ACTIVO = "<%= titular %>";
        var TODAS_LAS_CUENTAS = [];

        // Navegación entre pestañas
        function mostrarPestana(idTab) {
            document.querySelectorAll('.tab-pane').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));

            var pane = document.getElementById('pane-' + idTab);
            if (pane) pane.classList.add('active');

            // Actualizar pestaña activa en navbar
            var indexMap = {
                'mis-movimientos': 0,
                'movimientos-generales': 1,
                'depositos': 2,
                'retiros': 3,
                'transferencias': 4
            };
            var items = document.querySelectorAll('.nav-item');
            if (items[indexMap[idTab]]) {
                items[indexMap[idTab]].classList.add('active');
            }

            if (idTab === 'mis-movimientos') {
                cargarMisMovimientos();
            }
        }

        // Cargar todas las cuentas para poblar selects
        function cargarCuentasGlobales() {
            fetch("EurekaController?accion=cuentas")
                .then(r => r.json())
                .then(cuentas => {
                    TODAS_LAS_CUENTAS = cuentas;
                    poblarSelects(cuentas);
                })
                .catch(err => console.error("Error al cargar cuentas:", err));
        }

        function poblarSelects(cuentas) {
            var selects = [
                document.getElementById("selCambiarMiCuenta"),
                document.getElementById("selCuentasGenerales"),
                document.getElementById("selDepositoCuenta"),
                document.getElementById("selRetiroCuenta"),
                document.getElementById("selTransfOrigen"),
                document.getElementById("selTransfDestino")
            ];

            selects.forEach(sel => {
                if (!sel) return;
                sel.innerHTML = "";
            });

            cuentas.forEach((c, idx) => {
                var optText = c.cuenta + " - " + c.cliente + " (" + c.moneda + " $" + parseFloat(c.saldo).toFixed(2) + ")";

                selects.forEach(sel => {
                    if (!sel) return;
                    var opt = document.createElement("option");
                    opt.value = c.cuenta;
                    opt.text = optText;
                    sel.appendChild(opt);
                });
            });

            // Establecer valores por defecto
            if (document.getElementById("selCambiarMiCuenta")) {
                document.getElementById("selCambiarMiCuenta").value = MI_CUENTA_ACTIVA;
            }
            if (document.getElementById("selDepositoCuenta")) {
                document.getElementById("selDepositoCuenta").value = MI_CUENTA_ACTIVA;
                document.getElementById("txtDepositoManual").value = MI_CUENTA_ACTIVA;
            }
            if (document.getElementById("selRetiroCuenta")) {
                document.getElementById("selRetiroCuenta").value = MI_CUENTA_ACTIVA;
                document.getElementById("txtRetiroManual").value = MI_CUENTA_ACTIVA;
            }
            if (document.getElementById("selTransfOrigen")) {
                document.getElementById("selTransfOrigen").value = MI_CUENTA_ACTIVA;
                document.getElementById("txtTransfOrigenManual").value = MI_CUENTA_ACTIVA;
            }
            if (document.getElementById("selTransfDestino") && cuentas.length > 1) {
                var destIdx = (cuentas[0].cuenta === MI_CUENTA_ACTIVA && cuentas.length > 1) ? 1 : 0;
                document.getElementById("selTransfDestino").selectedIndex = destIdx;
                document.getElementById("txtTransfDestinoManual").value = cuentas[destIdx].cuenta;
            }
        }

        // Cargar movimientos de MI cuenta personal
        function cargarMisMovimientos() {
            var tbody = document.getElementById("tbodyMisMovimientos");
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding:25px; color:#94A3B8;">Consultando movimientos de tu cuenta ' + MI_CUENTA_ACTIVA + '...</td></tr>';

            // 1. Consultar saldo y titular
            fetch("EurekaController?accion=cuenta&nCuenta=" + MI_CUENTA_ACTIVA)
                .then(r => r.json())
                .then(c => {
                    if (c && c.cuenta) {
                        document.getElementById("heroTitular").innerText = c.cliente;
                        document.getElementById("heroCuenta").innerText = c.cuenta;
                        document.getElementById("heroSaldo").innerText = "$ " + parseFloat(c.saldo).toFixed(2) + " " + c.moneda;
                        TITULAR_ACTIVO = c.cliente;
                    }
                }).catch(e => {});

            // 2. Consultar lista de movimientos
            fetch("EurekaController?nCuenta=" + MI_CUENTA_ACTIVA)
                .then(r => r.json())
                .then(data => {
                    if (!data || data.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding:30px; color:#64748B;">No hay movimientos registrados en tu cuenta.</td></tr>';
                        document.getElementById("kpiMisIngresos").innerText = "+ $ 0.00";
                        document.getElementById("kpiMisEgresos").innerText = "- $ 0.00";
                        document.getElementById("kpiMisMovimientos").innerText = "0";
                        return;
                    }

                    var ingresos = 0;
                    var egresos = 0;
                    var html = "";

                    data.forEach(m => {
                        var esIngreso = (m.accion === "INGRESO");
                        var imp = parseFloat(m.importe);
                        if (esIngreso) ingresos += imp; else egresos += imp;

                        var badgeCls = esIngreso ? "badge-ingreso" : "badge-salida";
                        var amountCls = esIngreso ? "amount-green" : "amount-red";
                        var signo = esIngreso ? "+ " : "- ";

                        html += "<tr>" +
                            "<td><b>#" + m.nromov + "</b></td>" +
                            "<td>" + (m.fecha || "Reciente") + "</td>" +
                            "<td>" + m.tipo + "</td>" +
                            "<td><span class='badge " + badgeCls + "'>" + m.accion + "</span></td>" +
                            "<td class='" + amountCls + "'>" + signo + "$" + imp.toFixed(2) + "</td>" +
                            "</tr>";
                    });

                    tbody.innerHTML = html;
                    document.getElementById("kpiMisIngresos").innerText = "+ $ " + ingresos.toFixed(2);
                    document.getElementById("kpiMisEgresos").innerText = "- $ " + egresos.toFixed(2);
                    document.getElementById("kpiMisMovimientos").innerText = data.length;
                })
                .catch(err => {
                    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:#EF4444; padding:20px;">Error al conectar con la API REST: ' + err + '</td></tr>';
                });
        }

        function cambiarMiCuentaActiva(nuevaCuenta) {
            if (!nuevaCuenta) return;
            MI_CUENTA_ACTIVA = nuevaCuenta;
            // Notificar al servlet
            var params = new URLSearchParams();
            params.append("accion", "setMiCuenta");
            params.append("cuenta", nuevaCuenta);
            fetch("EurekaController", { method: "POST", body: params }).then(() => {
                cargarMisMovimientos();
            });
        }

        // Consultas generales
        function seleccionarCuentaGeneral(cta) {
            document.getElementById("txtCuentaGeneralManual").value = cta;
            consultarCuentaGeneral(cta);
        }

        function consultarCuentaGeneralManual() {
            var cta = document.getElementById("txtCuentaGeneralManual").value.trim();
            if (!cta) {
                alert("Ingrese un número de cuenta válido.");
                return;
            }
            consultarCuentaGeneral(cta);
        }

        function consultarCuentaGeneral(cta) {
            var tbody = document.getElementById("tbodyMovimientosGenerales");
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:25px; color:#94A3B8;">Cargando movimientos de la cuenta ' + cta + '...</td></tr>';

            // Datos de la cuenta
            fetch("EurekaController?accion=cuenta&nCuenta=" + cta)
                .then(r => r.json())
                .then(c => {
                    if (c && c.cuenta) {
                        document.getElementById("boxInfoCuentaGeneral").style.display = "grid";
                        document.getElementById("lblGenTitular").innerText = c.cliente + " (" + c.cuenta + ")";
                        document.getElementById("lblGenSaldo").innerText = "$ " + parseFloat(c.saldo).toFixed(2) + " " + c.moneda;
                        document.getElementById("lblSubtituloGeneral").innerText = "Titular: " + c.cliente + " | Cuenta: " + c.cuenta + " (" + c.moneda + ")";
                    }
                }).catch(e => {});

            // Movimientos
            fetch("EurekaController?nCuenta=" + cta)
                .then(r => r.json())
                .then(data => {
                    if (!data || data.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:30px; color:#64748B;">No se encontraron movimientos registrados para esta cuenta.</td></tr>';
                        return;
                    }
                    var ing = 0, egr = 0;
                    var html = "";
                    data.forEach(m => {
                        var esIng = (m.accion === "INGRESO");
                        var imp = parseFloat(m.importe);
                        if (esIng) ing += imp; else egr += imp;

                        html += "<tr>" +
                            "<td><b>" + m.cuenta + "</b></td>" +
                            "<td>#" + m.nromov + "</td>" +
                            "<td>" + (m.fecha || "Reciente") + "</td>" +
                            "<td>" + m.tipo + "</td>" +
                            "<td><span class='badge " + (esIng ? "badge-ingreso" : "badge-salida") + "'>" + m.accion + "</span></td>" +
                            "<td class='" + (esIng ? "amount-green" : "amount-red") + "'>" + (esIng ? "+" : "-") + "$" + imp.toFixed(2) + "</td>" +
                            "</tr>";
                    });
                    tbody.innerHTML = html;
                    document.getElementById("lblGenIngresos").innerText = "+ $ " + ing.toFixed(2);
                    document.getElementById("lblGenEgresos").innerText = "- $ " + egr.toFixed(2);
                })
                .catch(err => {
                    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:#EF4444; padding:20px;">Error: ' + err + '</td></tr>';
                });
        }

        // Operaciones transaccionales
        function ejecutarDeposito(e) {
            e.preventDefault();
            var cta = document.getElementById("txtDepositoManual").value.trim();
            var imp = document.getElementById("txtDepositoImporte").value.trim();
            var btn = document.getElementById("btnEjecutarDep");

            btn.disabled = true;
            btn.innerText = "Procesando depósito...";

            var params = new URLSearchParams();
            params.append("accion", "deposito");
            params.append("cuenta", cta);
            params.append("importe", imp);

            fetch("EurekaController", { method: "POST", body: params })
                .then(r => r.json())
                .then(res => {
                    btn.disabled = false;
                    btn.innerText = "✔ Confirmar y Registrar Depósito";

                    if (res.success) {
                        renderVoucher("voucherDepositoContenido", "DEPÓSITO", res, cta, imp);
                        document.getElementById("txtDepositoImporte").value = "";
                        cargarCuentasGlobales();
                        if (cta === MI_CUENTA_ACTIVA) cargarMisMovimientos();
                    } else {
                        alert("Error al realizar depósito: " + (res.message || "Operación rechazada"));
                    }
                })
                .catch(err => {
                    btn.disabled = false;
                    btn.innerText = "✔ Confirmar y Registrar Depósito";
                    alert("Error de conexión: " + err);
                });
        }

        function ejecutarRetiro(e) {
            e.preventDefault();
            var cta = document.getElementById("txtRetiroManual").value.trim();
            var imp = document.getElementById("txtRetiroImporte").value.trim();
            var btn = document.getElementById("btnEjecutarRet");

            btn.disabled = true;
            btn.innerText = "Procesando retiro...";

            var params = new URLSearchParams();
            params.append("accion", "retiro");
            params.append("cuenta", cta);
            params.append("importe", imp);

            fetch("EurekaController", { method: "POST", body: params })
                .then(r => r.json())
                .then(res => {
                    btn.disabled = false;
                    btn.innerText = "✔ Confirmar y Registrar Retiro";

                    if (res.success) {
                        renderVoucher("voucherRetiroContenido", "RETIRO", res, cta, imp);
                        document.getElementById("txtRetiroImporte").value = "";
                        cargarCuentasGlobales();
                        if (cta === MI_CUENTA_ACTIVA) cargarMisMovimientos();
                    } else {
                        alert("Error al realizar retiro: " + (res.message || "Saldo insuficiente o cuenta inactiva."));
                    }
                })
                .catch(err => {
                    btn.disabled = false;
                    btn.innerText = "✔ Confirmar y Registrar Retiro";
                    alert("Error de conexión: " + err);
                });
        }

        function ejecutarTransferencia(e) {
            e.preventDefault();
            var orig = document.getElementById("txtTransfOrigenManual").value.trim();
            var dest = document.getElementById("txtTransfDestinoManual").value.trim();
            var imp = document.getElementById("txtTransfImporte").value.trim();
            var btn = document.getElementById("btnEjecutarTransf");

            if (orig === dest) {
                alert("La cuenta de origen y destino no pueden ser la misma.");
                return;
            }

            btn.disabled = true;
            btn.innerText = "Procesando transferencia...";

            var params = new URLSearchParams();
            params.append("accion", "transferencia");
            params.append("cuentaOrigen", orig);
            params.append("cuentaDestino", dest);
            params.append("importe", imp);

            fetch("EurekaController", { method: "POST", body: params })
                .then(r => r.json())
                .then(res => {
                    btn.disabled = false;
                    btn.innerText = "🔄 Realizar Transferencia Inmediata";

                    if (res.success) {
                        renderVoucherTransferencia("voucherTransfContenido", res, orig, dest, imp);
                        document.getElementById("txtTransfImporte").value = "";
                        cargarCuentasGlobales();
                        if (orig === MI_CUENTA_ACTIVA || dest === MI_CUENTA_ACTIVA) cargarMisMovimientos();
                    } else {
                        alert("Error en la transferencia: " + (res.message || "Fondos insuficientes"));
                    }
                })
                .catch(err => {
                    btn.disabled = false;
                    btn.innerText = "🔄 Realizar Transferencia Inmediata";
                    alert("Error de conexión: " + err);
                });
        }

        function renderVoucher(elementId, tipo, res, cta, imp) {
            var dateStr = new Date().toLocaleString('es-EC');
            var html = '<div class="voucher-success-badge">✔ TRANSACCIÓN EXITOSA</div>' +
                '<table class="voucher-table">' +
                '<tr><td>Tipo:</td><td>' + tipo + '</td></tr>' +
                '<tr><td>N° Movimiento:</td><td><b style="color:#0E7490;">#' + (res.nromov || '-') + '</b></td></tr>' +
                '<tr><td>Cuenta:</td><td>' + cta + '</td></tr>' +
                '<tr><td>Titular:</td><td>' + (res.titular || '-') + '</td></tr>' +
                '<tr><td>Monto:</td><td style="color:' + (tipo === "DEPÓSITO" ? "#10B981" : "#EF4444") + '; font-size:16px;">$ ' + parseFloat(imp).toFixed(2) + '</td></tr>' +
                '<tr><td>Nuevo Saldo:</td><td><b style="color:#0E7490;">$ ' + (res.nuevoSaldo ? parseFloat(res.nuevoSaldo).toFixed(2) : '-') + '</b></td></tr>' +
                '<tr><td>Fecha / Hora:</td><td>' + dateStr + '</td></tr>' +
                '</table>' +
                '<button type="button" class="btn-action" style="padding:8px; font-size:12px; background:#0F172A;" onclick="mostrarPestana(\'mis-movimientos\')">👉 Ver en Mis Movimientos</button>';
            document.getElementById(elementId).innerHTML = html;
        }

        function renderVoucherTransferencia(elementId, res, orig, dest, imp) {
            var dateStr = new Date().toLocaleString('es-EC');
            var html = '<div class="voucher-success-badge">✔ TRANSFERENCIA EXITOSA</div>' +
                '<table class="voucher-table">' +
                '<tr><td>Operación:</td><td>TRANSFERENCIA</td></tr>' +
                '<tr><td>N° Movimiento:</td><td><b style="color:#0E7490;">#' + (res.nromov || '-') + '</b></td></tr>' +
                '<tr><td>Cuenta Origen:</td><td>' + orig + ' (' + (res.titular || 'Titular') + ')</td></tr>' +
                '<tr><td>Cuenta Destino:</td><td>' + dest + '</td></tr>' +
                '<tr><td>Monto Transferido:</td><td style="color:#10B981; font-size:16px;">$ ' + parseFloat(imp).toFixed(2) + '</td></tr>' +
                '<tr><td>Nuevo Saldo Origen:</td><td><b style="color:#0E7490;">$ ' + (res.nuevoSaldo ? parseFloat(res.nuevoSaldo).toFixed(2) : '-') + '</b></td></tr>' +
                '<tr><td>Fecha / Hora:</td><td>' + dateStr + '</td></tr>' +
                '</table>' +
                '<button type="button" class="btn-action" style="padding:8px; font-size:12px; background:#0F172A;" onclick="mostrarPestana(\'mis-movimientos\')">👉 Ver en Mis Movimientos</button>';
            document.getElementById(elementId).innerHTML = html;
        }

        // Al cargar página
        window.onload = function() {
            cargarCuentasGlobales();
            cargarMisMovimientos();

            // Soportar parámetro URL ?tab=...
            var params = new URLSearchParams(window.location.search);
            var tabParam = params.get("tab");
            if (tabParam) {
                mostrarPestana(tabParam);
            }
        };
    </script>
</body>
</html>
