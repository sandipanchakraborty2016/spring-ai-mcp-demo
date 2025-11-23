package com.example.mcpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller demonstrating MCP client capabilities.
 * Provides endpoints to interact with all MCP server tools.
 *
 * Note: This is a simplified version for Spring AI 1.1.0-M2.
 * For production use, implement full MCP client integration.
 */
@RestController
@RequestMapping("/api")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    /**
     * Get client status.
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "mcp-client");
        response.put("status", "UP");
        response.put("message", "MCP Client is running with Spring AI 1.1.0-M2");
        return response;
    }

    /**
     * Example endpoint - demonstrates client capability.
     */
    @GetMapping("/demo")
    public Map<String, Object> demo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "MCP Client Demo");
        response.put("description", "Connect to MCP Server at http://localhost:8080");
        response.put("server_endpoint", "http://localhost:8080/mcp/message");
        return response;
    }
}
