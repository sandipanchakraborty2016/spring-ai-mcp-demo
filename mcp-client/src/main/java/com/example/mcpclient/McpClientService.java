package com.example.mcpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP Client Service - Calls MCP server tools via JSON-RPC protocol over HTTP
 *
 * This service uses the MCP protocol (JSON-RPC 2.0) to communicate with the MCP server.
 * Tools are executed on the server, not locally.
 */
@Service
public class McpClientService {

    private static final Logger logger = LoggerFactory.getLogger(McpClientService.class);

    @Value("${spring.ai.mcp.client.annotation-scanner.servers.mcp-server.sse.base-url:http://localhost:8080}")
    private String mcpServerBaseUrl;

    @Autowired
    private McpRpcClient mcpRpcClient;

    public Map<String, Object> listTools() {
        Map<String, Object> response = new HashMap<>();
        String[] toolNames = {
            "getCurrentTime",
            "add", "subtract", "multiply", "divide", "power", "sqrt",
            "listFiles", "writeFile", "readFile", "appendToFile", "getFileInfo", "deleteFile", "getWorkspacePath",
            "store", "retrieve", "listKeys", "delete", "clear", "count"
        };

        response.put("status", "success");
        response.put("server", mcpServerBaseUrl);
        response.put("tools", toolNames);
        response.put("count", toolNames.length);
        response.put("protocol", "MCP JSON-RPC 2.0");
        return response;
    }

    // ==================== TIME SERVICE ====================

    public Map<String, Object> getCurrentTime() {
        return callMcpTool("getCurrentTime", Map.of());
    }

    // ==================== CALCULATOR SERVICE ====================

    public Map<String, Object> add(double a, double b) {
        return callMcpTool("add", Map.of("a", a, "b", b));
    }

    public Map<String, Object> subtract(double a, double b) {
        return callMcpTool("subtract", Map.of("a", a, "b", b));
    }

    public Map<String, Object> multiply(double a, double b) {
        return callMcpTool("multiply", Map.of("a", a, "b", b));
    }

    public Map<String, Object> divide(double a, double b) {
        return callMcpTool("divide", Map.of("a", a, "b", b));
    }

    public Map<String, Object> power(double base, double exponent) {
        return callMcpTool("power", Map.of("base", base, "exponent", exponent));
    }

    public Map<String, Object> sqrt(double number) {
        return callMcpTool("sqrt", Map.of("number", number));
    }

    // ==================== FILE SERVICE ====================

    public Map<String, Object> listFiles() {
        return callMcpTool("listFiles", Map.of());
    }

    public Map<String, Object> writeFile(String filename, String content) {
        return callMcpTool("writeFile", Map.of("filename", filename, "content", content));
    }

    public Map<String, Object> readFile(String filename) {
        return callMcpTool("readFile", Map.of("filename", filename));
    }

    public Map<String, Object> appendToFile(String filename, String content) {
        return callMcpTool("appendToFile", Map.of("filename", filename, "content", content));
    }

    public Map<String, Object> getFileInfo(String filename) {
        return callMcpTool("getFileInfo", Map.of("filename", filename));
    }

    public Map<String, Object> deleteFile(String filename) {
        return callMcpTool("deleteFile", Map.of("filename", filename));
    }

    public Map<String, Object> getWorkspacePath() {
        return callMcpTool("getWorkspacePath", Map.of());
    }

    // ==================== DATA STORAGE SERVICE ====================

    public Map<String, Object> storeData(String key, String value) {
        return callMcpTool("store", Map.of("key", key, "value", value));
    }

    public Map<String, Object> retrieveData(String key) {
        return callMcpTool("retrieve", Map.of("key", key));
    }

    public Map<String, Object> listKeys() {
        return callMcpTool("listKeys", Map.of());
    }

    public Map<String, Object> deleteData(String key) {
        return callMcpTool("delete", Map.of("key", key));
    }

    public Map<String, Object> clearStorage() {
        return callMcpTool("clear", Map.of());
    }

    public Map<String, Object> countEntries() {
        return callMcpTool("count", Map.of());
    }

    // ==================== HELPER METHOD ====================

    /**
     * Call MCP server tool via JSON-RPC protocol
     */
    private Map<String, Object> callMcpTool(String toolName, Map<String, Object> arguments) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Calling MCP tool '{}' via JSON-RPC with arguments: {}", toolName, arguments);

            // Call tool via MCP JSON-RPC protocol
            String result = mcpRpcClient.callTool(toolName, arguments);

            response.put("result", result);
            response.put("tool", toolName);
            response.put("status", "success");
            response.put("protocol", "MCP JSON-RPC 2.0");
            response.put("source", "mcp-server");
            response.put("timestamp", System.currentTimeMillis());

            logger.info("Tool '{}' executed successfully via MCP protocol", toolName);

        } catch (Exception e) {
            logger.error("Error calling MCP tool '{}': {}", toolName, e.getMessage());
            response.put("error", e.getMessage());
            response.put("tool", toolName);
            response.put("status", "failed");
        }
        return response;
    }
}
