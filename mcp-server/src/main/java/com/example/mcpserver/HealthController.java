package com.example.mcpserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring server status.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "mcp-server");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Spring AI MCP Server");
        info.put("version", "1.0.0");
        info.put("description", "Model Context Protocol Server with Spring AI 1.1.0-RC1");
        info.put("endpoints", Map.of(
            "health", "/health",
            "mcp", "/mcp/message"
        ));
        return info;
    }
}
