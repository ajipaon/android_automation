package org.example.api;
import org.example.api.controller.DeviceController;
import org.example.api.controller.JobController;
import org.example.api.util.JsonUtil;
import org.example.config.Settings;
import org.example.service.DeviceService;
import org.example.service.JobService;
import org.example.utils.Logger;
import static spark.Spark.*;
public class ApiServer {
    private static final Logger logger = Logger.getInstance();
    private static final int    PORT   = 4567;
    public static void start() {

        port(PORT);
        threadPool(8);  

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin",  "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        options("/*", (req, res) -> {
            res.status(200);
            return "OK";
        });

        // ─────────────────────────────────────────────
        // Swagger UI — serve openapi.yaml
        // ─────────────────────────────────────────────
        get("/openapi.yaml", (req, res) -> {
            res.type("application/yaml");
            var stream = ApiServer.class.getClassLoader()
                    .getResourceAsStream("openapi.yaml");
            if (stream == null) {
                res.status(404);
                return "openapi.yaml tidak ditemukan";
            }
            return new String(stream.readAllBytes());
        });

        // Swagger UI — redirect root ke Swagger
        get("/swagger", (req, res) -> {
            res.redirect("/swagger/index.html");
            return null;
        });

        // Swagger UI static files dari WebJar
        get("/swagger/*", (req, res) -> {
            String path = req.splat()[0];
            if (path.isEmpty() || path.equals("index.html")) {
                // Inject URL openapi.yaml ke Swagger UI
                res.type("text/html");
                return buildSwaggerHtml();
            }
            var stream = ApiServer.class.getClassLoader()
                    .getResourceAsStream("META-INF/resources/webjars/swagger-ui/5.17.14/" + path);
            if (stream == null) {
                res.status(404);
                return "File tidak ditemukan: " + path;
            }
            if (path.endsWith(".css"))  res.type("text/css");
            if (path.endsWith(".js"))   res.type("application/javascript");
            if (path.endsWith(".png"))  res.type("image/png");
            return stream.readAllBytes();
        });

        // ─────────────────────────────────────────────
        // Health check
        // ─────────────────────────────────────────────
        get("/health", (req, res) -> {
            res.type("application/json");
            return JsonUtil.success("ADB Automation API is running");
        });

        // ─────────────────────────────────────────────
        // Register controllers
        // ─────────────────────────────────────────────
        new JobController(new JobService());
        new DeviceController(new DeviceService());

        // ─────────────────────────────────────────────
        // Global error handler
        // ─────────────────────────────────────────────
        exception(Exception.class, (e, req, res) -> {
            logger.error("[ApiServer] Unhandled error: %s", e.getMessage());
            res.status(500);
            res.type("application/json");
            res.body(JsonUtil.error(500, "Internal server error: " + e.getMessage()));
        });

        awaitInitialization();
        logger.info("[ApiServer] Server berjalan di http://localhost:%d", PORT);
        logger.info("[ApiServer] Swagger UI → http://localhost:%d/swagger", PORT);
    }

    public static void stop() {
        spark.Spark.stop();
        logger.info("[ApiServer] Server dihentikan");
    }

    // ─────────────────────────────────────────────
    // SWAGGER HTML
    // ─────────────────────────────────────────────

    /**
     * Generate Swagger UI HTML yang mengarah ke openapi.yaml lokal.
     */
    private static String buildSwaggerHtml() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1"/>
                    <title>ADB Automation API</title>
                    <link rel="stylesheet" href="/swagger/swagger-ui.css"/>
                </head>
                <body>
                <div id="swagger-ui"></div>
                <script src="/swagger/swagger-ui-bundle.js"></script>
                <script src="/swagger/swagger-ui-standalone-preset.js"></script>
                <script>
                    window.onload = () => {
                        SwaggerUIBundle({
                            url: "/openapi.yaml",
                            dom_id: "#swagger-ui",
                            presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
                            layout: "StandaloneLayout",
                            deepLinking: true,
                            defaultModelsExpandDepth: 1,
                        });
                    };
                </script>
                </body>
                </html>
                """;
    }
}