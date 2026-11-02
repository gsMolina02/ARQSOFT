package ec.edu.monster.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ec.edu.monster.modelo.CuentaDTO;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.OperationResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Cliente HTTP RESTful para el servidor EurekaBank WS_EurekaBank
 * Compatible con Java 8+ sin dependencias externas pesadas.
 * @author Grupo 08 (Gustavo Molina / Daniela TituaÃƒÂ±a)
 */
public class CoreBancario {

    public static class Response {
        private final int status;
        private final String body;

        public Response(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public int getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }
    }

    private static String resolveBaseUri() {
        String target = null;
        try {
            java.io.File[] files = new java.io.File[] {
                new java.io.File("servidor_ip.txt"),
                new java.io.File("dist", "servidor_ip.txt"),
                new java.io.File("..", "servidor_ip.txt"),
                new java.io.File("../..", "servidor_ip.txt")
            };
            for (java.io.File f : files) {
                if (f.exists()) {
                    String ip = new String(java.nio.file.Files.readAllBytes(f.toPath()), java.nio.charset.StandardCharsets.UTF_8).trim();
                    if (ip.startsWith("\uFEFF")) {
                        ip = ip.substring(1).trim();
                    }
                    if (!ip.isEmpty()) {
                        target = ip;
                        break;
                    }
                }
            }
            if (target == null) {
                String env = System.getenv("EUREKABANK_REST_URL");
                if (env != null && !env.trim().isEmpty()) {
                    target = env.trim();
                }
            }
        } catch (Exception ignored) {}

        if (target == null || target.isEmpty()) {
            return "https://exchange-por-bottom-exam.trycloudflare.com/Rest_EurekaBank/webresources/coreBancario";
        }

        target = target.trim();
        if (target.startsWith("http://") || target.startsWith("https://")) {
            String base = target.endsWith("/") ? target.substring(0, target.length() - 1) : target;
            if (!base.contains("coreBancario")) {
                base += "/Rest_EurekaBank/webresources/coreBancario";
            }
            return base;
        }
        String cleanHost = target.contains(":") ? target : target + ":8080";
        return "http://" + cleanHost + "/Rest_EurekaBank/webresources/coreBancario";
    }

    private final String baseUri;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                String s = json.getAsString();
                String[] formats = new String[] {
                    "MMM d, yyyy", "MMM dd, yyyy", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy"
                };
                for (Locale loc : new Locale[] { Locale.getDefault(), Locale.forLanguageTag("es"), Locale.US }) {
                    for (String fmt : formats) {
                        try {
                            return new java.text.SimpleDateFormat(fmt, loc).parse(s);
                        } catch (Exception ignored) {}
                    }
                }
                return null;
            })
            .create();

    public CoreBancario() {
        this.baseUri = resolveBaseUri();
    }

    public boolean validarIngreso(String usuario, String clave) {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("usuario", usuario);
            req.addProperty("clave", clave);
            Response resp = sendPost("/login", req);
            if (resp.getStatus() == 200) {
                JsonObject obj = gson.fromJson(resp.getBody(), JsonObject.class);
                return obj.has("success") && obj.get("success").getAsBoolean();
            }
        } catch (Exception e) {
            System.err.println("Error en login REST: " + e.getMessage());
        }
        return false;
    }

    public List<CuentaDTO> getCuentas() {
        try {
            URL url = new URL(baseUri + "/cuentas");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    return gson.fromJson(reader, new TypeToken<List<CuentaDTO>>(){}.getType());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar cuentas: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public CuentaDTO getCuenta(String cuenta) {
        try {
            URL url = new URL(baseUri + "/cuenta/" + cuenta);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    return gson.fromJson(reader, CuentaDTO.class);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar cuenta: " + e.getMessage());
        }
        return null;
    }

    public List<Movimiento> getMovimientos(String cuenta) {
        try {
            URL url = new URL(baseUri + "/movimientos/" + cuenta);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    return gson.fromJson(reader, new TypeToken<List<Movimiento>>(){}.getType());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar movimientos: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public OperationResult ejecutarDeposito(String cuenta, double importe) {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("cuenta", cuenta);
            req.addProperty("importe", importe);
            Response resp = sendPost("/deposito", req);
            if (resp.getBody() != null) {
                return gson.fromJson(resp.getBody(), OperationResult.class);
            }
        } catch (Exception e) {
            OperationResult err = new OperationResult();
            err.setSuccess(false);
            err.setMessage("Error de conexiÃƒÂ³n: " + e.getMessage());
            return err;
        }
        OperationResult err = new OperationResult();
        err.setSuccess(false);
        err.setMessage("Respuesta nula del servidor");
        return err;
    }

    public OperationResult ejecutarRetiro(String cuenta, double importe) {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("cuenta", cuenta);
            req.addProperty("importe", importe);
            Response resp = sendPost("/retiro", req);
            if (resp.getBody() != null) {
                return gson.fromJson(resp.getBody(), OperationResult.class);
            }
        } catch (Exception e) {
            OperationResult err = new OperationResult();
            err.setSuccess(false);
            err.setMessage("Error de conexiÃƒÂ³n: " + e.getMessage());
            return err;
        }
        OperationResult err = new OperationResult();
        err.setSuccess(false);
        err.setMessage("Respuesta nula del servidor");
        return err;
    }

    public OperationResult ejecutarTransferencia(String cuentaOrigen, String cuentaDestino, double importe) {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("cuentaOrigen", cuentaOrigen);
            req.addProperty("cuentaDestino", cuentaDestino);
            req.addProperty("importe", importe);
            Response resp = sendPost("/transferencia", req);
            if (resp.getBody() != null) {
                return gson.fromJson(resp.getBody(), OperationResult.class);
            }
        } catch (Exception e) {
            OperationResult err = new OperationResult();
            err.setSuccess(false);
            err.setMessage("Error de conexiÃƒÂ³n: " + e.getMessage());
            return err;
        }
        OperationResult err = new OperationResult();
        err.setSuccess(false);
        err.setMessage("Respuesta nula del servidor");
        return err;
    }

    public Response registrarDeposito(Object requestEntity) {
        return sendPost("/deposito", requestEntity);
    }

    public Response registrarRetiro(Object requestEntity) {
        return sendPost("/retiro", requestEntity);
    }

    public Response registrarTransferencia(Object requestEntity) {
        return sendPost("/transferencia", requestEntity);
    }

    private Response sendPost(String endpoint, Object requestEntity) {
        try {
            URL url = new URL(baseUri + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);

            String json = (requestEntity instanceof String) ? (String) requestEntity : gson.toJson(requestEntity);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    code >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            return new Response(code, sb.toString());
        } catch (Exception e) {
            System.err.println("Error de comunicaciÃƒÂ³n HTTP POST: " + e.getMessage());
            return new Response(500, e.getMessage());
        }
    }

    public void close() {
    }
}


