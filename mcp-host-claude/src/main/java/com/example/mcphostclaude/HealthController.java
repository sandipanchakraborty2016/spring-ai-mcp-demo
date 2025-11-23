package com.example.mcphostclaude;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for MCP Host Claude.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "mcp-host-claude");
        health.put("timestamp", System.currentTimeMillis());
        health.put("aiProvider", "Anthropic Claude");
        health.put("springAiVersion", "1.1.0-M2");
        return health;
    }

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Spring AI MCP Host - Claude Edition");
        info.put("version", "1.0.0");
        info.put("springAiVersion", "1.1.0-M2");
        info.put("aiProvider", "Anthropic Claude 3.5 Sonnet");
        info.put("description", "AI application using MCP tools with Claude - demonstrates model-agnostic MCP");
        info.put("comparison", "Compare with mcp-host (OpenAI) on port 8082");
        info.put("endpoints", Map.of(
            "health", "/health",
            "status", "/api/status",
            "demo", "/api/demo",
            "mcpInfo", "/api/mcp-info"
        ));
        info.put("keyFeature", "Same MCP tools, different AI model!");
        return info;
    }
}
