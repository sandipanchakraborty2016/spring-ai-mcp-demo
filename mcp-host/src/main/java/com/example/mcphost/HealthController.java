package com.example.mcphost;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for MCP Host.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "mcp-host");
        health.put("timestamp", System.currentTimeMillis());
        health.put("springAiVersion", "1.1.0-M2");
        return health;
    }

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Spring AI MCP Host");
        info.put("version", "1.0.0");
        info.put("springAiVersion", "1.1.0-M2");
        info.put("description", "AI application that integrates MCP tools with chat models");
        info.put("endpoints", Map.of(
            "health", "/health",
            "status", "/api/status",
            "demo", "/api/demo"
        ));
        info.put("note", "Ready for MCP and AI integration");
        return info;
    }
}
