<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EurekaBank Web - Portal Financiero RESTful</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Plus Jakarta Sans', system-ui, -apple-system, sans-serif;
            background: linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #0E7490 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            color: #0F172A;
        }
        .login-card {
            background: rgba(255, 255, 255, 0.98);
            border-radius: 24px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.35);
            max-width: 440px;
            width: 100%;
            padding: 40px 36px;
            text-align: center;
            position: relative;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        .brand-header {
            margin-bottom: 24px;
        }
        .avatar-wrap {
            width: 86px;
            height: 86px;
            margin: 0 auto 12px;
            background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 8px 16px rgba(14, 116, 144, 0.2);
            border: 3px solid #FFFFFF;
        }
        .avatar-wrap img {
            width: 62px;
            height: 62px;
            object-fit: contain;
            filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1));
        }
        h1 {
            font-size: 24px;
            font-weight: 800;
            color: #0F172A;
            letter-spacing: -0.5px;
            margin-bottom: 4px;
        }
        .subtitle {
            font-size: 13px;
            color: #64748B;
            font-weight: 500;
        }
        .form-group {
            text-align: left;
            margin-bottom: 16px;
        }
        label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #334155;
            margin-bottom: 6px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        input, select {
            width: 100%;
            padding: 12px 14px;
            font-size: 14px;
            font-family: inherit;
            color: #0F172A;
            background: #F8FAFC;
            border: 1.5px solid #E2E8F0;
            border-radius: 12px;
            transition: all 0.2s ease;
        }
        input:focus, select:focus {
            background: #FFFFFF;
            border-color: #0E7490;
            outline: none;
            box-shadow: 0 0 0 3px rgba(14, 116, 144, 0.15);
        }
        .quick-accounts {
            margin: 18px 0;
            padding: 12px;
            background: #F1F5F9;
            border-radius: 14px;
            text-align: left;
        }
        .quick-accounts span {
            font-size: 11px;
            font-weight: 700;
            color: #64748B;
            display: block;
            margin-bottom: 8px;
            text-transform: uppercase;
        }
        .pill-row {
            display: flex;
            gap: 6px;
            flex-wrap: wrap;
        }
        .pill {
            background: #FFFFFF;
            border: 1px solid #CBD5E1;
            padding: 6px 10px;
            border-radius: 8px;
            font-size: 11px;
            font-weight: 600;
            color: #334155;
            cursor: pointer;
            transition: all 0.15s ease;
        }
        .pill:hover {
            border-color: #0E7490;
            color: #0E7490;
            background: #F0FDF4;
        }
        .btn-submit {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #0E7490 0%, #0369A1 100%);
            color: #FFFFFF;
            border: none;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            box-shadow: 0 8px 16px rgba(14, 116, 144, 0.3);
            transition: all 0.2s ease;
            margin-top: 10px;
        }
        .btn-submit:hover {
            background: linear-gradient(135deg, #0c6178 0%, #025381 100%);
            transform: translateY(-1px);
            box-shadow: 0 10px 20px rgba(14, 116, 144, 0.35);
        }
        .alert-box {
            padding: 10px 14px;
            border-radius: 10px;
            font-size: 13px;
            margin-bottom: 16px;
            display: none;
            text-align: left;
        }
        .alert-error {
            background: #FEE2E2;
            color: #991B1B;
            border: 1px solid #FCA5A5;
        }
        .footer-info {
            margin-top: 20px;
            font-size: 11px;
            color: #94A3B8;
            font-weight: 500;
        }
    </style>
</head>
<body>
    <div class="login-card">
        <div class="brand-header">
            <div class="avatar-wrap">
                <img src="images/sullivan.png" alt="Sullivan Monster Inc" />
            </div>
            <h1>EurekaBank Web</h1>
            <p class="subtitle">Banca Digital RESTful (NRC 36883 - Grupo 08)</p>
        </div>

        <div id="alertBox" class="alert-box alert-error"></div>

        <form id="loginForm" onsubmit="realizarLogin(event)">
            <div class="form-group">
                <label>Usuario:</label>
                <input type="text" id="txtUsuario" name="usuario" value="MONSTER" required />
            </div>

            <div class="form-group">
                <label>Contraseña:</label>
                <input type="password" id="txtClave" name="clave" value="MONSTER9" required />
            </div>

            <div class="form-group">
                <label>Mi Cuenta Personal (Asociada):</label>
                <select id="selCuenta" name="cuentaElegida">
                    <option value="00100001" selected>00100001 - Juan Carlos García López (Soles)</option>
                    <option value="00300001">00300001 - Eduardo Mauricio Campaña Ortega (Soles)</option>
                    <option value="00200002">00200002 - Daniela Lissette Tituaña Moreno (Soles)</option>
                    <option value="00200001">00200001 - Gustavo Stiven Molina Guaico (Soles)</option>
                    <option value="00100002">00100002 - María Fernanda Rodríguez Torres (Soles)</option>
                    <option value="00300002">00300002 - Eduardo Campaña (Dólares)</option>
                </select>
            </div>

            <div class="quick-accounts">
                <span>⚡ Credenciales Rápidas:</span>
                <div class="pill-row">
                    <button type="button" class="pill" onclick="setCredenciales('MONSTER', 'MONSTER9', '00300001')">👤 MONSTER (Eduardo)</button>
                    <button type="button" class="pill" onclick="setCredenciales('cromero', 'chicho', '00100001')">👤 cromero (Juan C.)</button>
                    <button type="button" class="pill" onclick="setCredenciales('MONSTER', 'MONSTER9', '00200002')">👤 Daniela Tituaña</button>
                    <button type="button" class="pill" onclick="setCredenciales('MONSTER', 'MONSTER9', '00200001')">👤 Gustavo Molina</button>
                </div>
            </div>

            <button type="submit" id="btnIngresar" class="btn-submit">Ingresar a la Banca Web</button>
        </form>

        <div class="footer-info">
            Core Bancario RESTful Java | Payara Server 7 & MySQL 8.0
        </div>
    </div>

    <script>
        function setCredenciales(u, c, cta) {
            document.getElementById("txtUsuario").value = u;
            document.getElementById("txtClave").value = c;
            document.getElementById("selCuenta").value = cta;
        }

        function realizarLogin(e) {
            e.preventDefault();
            var btn = document.getElementById("btnIngresar");
            var alertBox = document.getElementById("alertBox");
            alertBox.style.display = "none";
            btn.disabled = true;
            btn.innerText = "Verificando credenciales...";

            var u = document.getElementById("txtUsuario").value.trim();
            var c = document.getElementById("txtClave").value.trim();
            var cta = document.getElementById("selCuenta").value;

            var params = new URLSearchParams();
            params.append("accion", "login");
            params.append("usuario", u);
            params.append("clave", c);
            params.append("cuentaElegida", cta);

            fetch("EurekaController", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: params.toString()
            })
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    window.location.href = "index.jsp";
                } else {
                    btn.disabled = false;
                    btn.innerText = "Ingresar a la Banca Web";
                    alertBox.style.display = "block";
                    alertBox.innerText = data.message || "Credenciales incorrectas. Verifique usuario y clave.";
                }
            })
            .catch(err => {
                btn.disabled = false;
                btn.innerText = "Ingresar a la Banca Web";
                alertBox.style.display = "block";
                alertBox.innerText = "Error de comunicación con el servidor: " + err;
            });
        }
    </script>
</body>
</html>
