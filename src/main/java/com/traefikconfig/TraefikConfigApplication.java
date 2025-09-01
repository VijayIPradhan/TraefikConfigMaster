package com.traefikconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class TraefikConfigApplication {

    private static final Logger logger = LoggerFactory.getLogger(TraefikConfigApplication.class);

    public static void main(String[] args) {
        logger.info("🚀 Starting Traefik Config Manager Application...");
        SpringApplication.run(TraefikConfigApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("✅ Traefik Config Manager Application is ready!");
        logger.info("🌐 API endpoints available at:");
        logger.info("   📋 GET  /api/traefik/config - Get current configuration");
        logger.info("   ➕ POST /api/traefik/add-host - Add new host");
        logger.info("   🗑️ DELETE /api/traefik/delete-host - Delete host");
        logger.info("   🔧 POST /api/traefik/add-host-custom - Add host with custom config");
        logger.info("   🗑️ DELETE /api/traefik/delete-host-custom - Delete host with custom config");
        logger.info("   📋 POST /api/traefik/config-custom - Get config with custom settings");
        logger.info("   🔗 GET  /api/traefik/services - Get service URLs from configuration");
        logger.info("   🔗 POST /api/traefik/services-custom - Get service URLs with custom config");
        logger.info("   🏥 GET  /api/health - Health check");
        logger.info("   ⚙️ GET  /api/config/current - Get configuration properties");
        logger.info("   🔧 PUT  /api/config/update - Update configuration properties");
        logger.info("   🔄 POST /api/config/reset - Reset configuration to defaults");
        logger.info("📊 Application is running on port 8080");
        logger.info("📚 Swagger UI available at: http://localhost:8080/swagger-ui.html");
        logger.info("📄 OpenAPI docs available at: http://localhost:8080/api-docs");
        logger.info("📝 Logs are being written to: logs/traefik-config-manager.log");
    }
}