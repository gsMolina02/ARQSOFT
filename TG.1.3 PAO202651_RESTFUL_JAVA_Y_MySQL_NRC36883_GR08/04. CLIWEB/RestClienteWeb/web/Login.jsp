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
            max-width: 420px;
            width: 100%;
            padding: 40px 36px;
            text-align: center;
            position: relative;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        .brand-header {
            margin-bottom: 28px;
        }
        .avatar-wrap {
            width: 96px;
            height: 96px;
            margin: 0 auto 14px;
            background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 10px 20px rgba(14, 116, 144, 0.2);
            border: 3.5px solid #FFFFFF;
            overflow: hidden;
        }
        .avatar-wrap img {
            width: 74px;
            height: 74px;
            object-fit: contain;
            filter: drop-shadow(0 4px 6px rgba(0,0,0,0.12));
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
            margin-bottom: 18px;
        }
        label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #334155;
            margin-bottom: 7px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        input {
            width: 100%;
            padding: 13px 15px;
            font-size: 14px;
            font-family: inherit;
            color: #0F172A;
            background: #F8FAFC;
            border: 1.5px solid #E2E8F0;
            border-radius: 12px;
            transition: all 0.2s ease;
        }
        input:focus {
            background: #FFFFFF;
            border-color: #0E7490;
            outline: none;
            box-shadow: 0 0 0 3px rgba(14, 116, 144, 0.15);
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
            margin-top: 24px;
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
                <img src="${pageContext.request.contextPath}/images/sullivan.png" alt="Sullivan Monster" onerror="this.onerror=null; this.src='images/sullivan.png';" />
            </div>
            <h1>EurekaBank Web</h1>
            <p class="subtitle">Banca Digital RESTful (NRC 36883 - Grupo 08)</p>
        </div>

        <div id="alertBox" class="alert-box alert-error"></div>

        <form id="loginForm" onsubmit="realizarLogin(event)">
            <div class="form-group">
                <label>Usuario:</label>
                <input type="text" id="txtUsuario" name="usuario" placeholder="Ej. MONSTER o cromero" required autocomplete="username" />
            </div>

            <div class="form-group">
                <label>Contraseña:</label>
                <input type="password" id="txtClave" name="clave" placeholder="••••••••" required autocomplete="current-password" />
            </div>

            <button type="submit" id="btnIngresar" class="btn-submit">Ingresar a la Banca Web</button>
        </form>

        <div class="footer-info">
            Core Bancario RESTful Java | Payara Server 7 & MySQL 8.0
        </div>
    </div>

    <script>
        function realizarLogin(e) {
            e.preventDefault();
            var btn = document.getElementById("btnIngresar");
            var alertBox = document.getElementById("alertBox");
            alertBox.style.display = "none";
            btn.disabled = true;
            btn.innerText = "Verificando credenciales...";

            var u = document.getElementById("txtUsuario").value.trim();
            var c = document.getElementById("txtClave").value.trim();

            var params = new URLSearchParams();
            params.append("accion", "login");
            params.append("usuario", u);
            params.append("clave", c);

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
