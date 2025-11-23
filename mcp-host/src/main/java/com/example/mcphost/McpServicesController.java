package com.example.mcphost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller that demonstrates direct MCP Server tool calls.
 *
 * This controller provides REST endpoints that call MCP Server tools directly
 * via the McpSyncClient. These endpoints demonstrate:
 * - File operations (read, write, list, delete, etc.)
 * - Data storage operations (store, retrieve, delete, etc.)
 * - Calculator operations
 * - Time operations
 */
@RestController
@RequestMapping("/api/mcp")
public class McpServicesController {

    private static final Logger logger = LoggerFactory.getLogger(McpServicesController.class);

    @Autowired(required = false)
    private java.util.List<io.modelcontextprotocol.client.McpSyncClient> mcpSyncClients;

    /**
     * Get the MCP client, or return error if not available
     */
    private Map<String, Object> getMcpClient() {
        Map<String, Object> response = new HashMap<>();
        if (mcpSyncClients == null || mcpSyncClients.isEmpty()) {
            response.put("error", "No MCP clients available");
            response.put("message", "MCP clients are not initialized. Spring AI 1.1.0-M2 creates MCP clients on-demand for ChatModel function calling, not as standalone beans.");
            response.put("status", "unavailable");
        }
        return response;
    }

    /**
     * Call an MCP tool with the given name and arguments
     */
    private Map<String, Object> callMcpTool(String toolName, Map<String, Object> arguments) {
        Map<String, Object> response = getMcpClient();
        if (response.containsKey("error")) {
            return response;
        }

        try {
            logger.info("Calling MCP tool: {} with arguments: {}", toolName, arguments);

            io.modelcontextprotocol.client.McpSyncClient client = mcpSyncClients.get(0);
            io.modelcontextprotocol.spec.McpSchema.CallToolResult result = client.callTool(
                    new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(toolName, arguments)
            );

            response.put("status", "success");
            response.put("tool", toolName);
            response.put("result", result.content().stream()
                    .map(content -> {
                        if (content instanceof io.modelcontextprotocol.spec.McpSchema.TextContent textContent) {
                            return textContent.text();
                        }
                        return content.toString();
                    })
                    .toList());
            response.put("timestamp", System.currentTimeMillis());

            logger.info("Tool '{}' executed successfully", toolName);
            return response;

        } catch (Exception e) {
            logger.error("Error calling MCP tool '{}': {}", toolName, e.getMessage(), e);
            response.put("error", "Failed to call tool");
            response.put("message", e.getMessage());
            response.put("status", "failed");
            return response;
        }
    }

    // ========== File Service Endpoints ==========

    @GetMapping("/files")
    public Map<String, Object> listFiles() {
        return callMcpTool("listFiles", new HashMap<>());
    }

    @GetMapping("/files/{filename}")
    public Map<String, Object> readFile(@PathVariable String filename) {
        Map<String, Object> args = new HashMap<>();
        args.put("filename", filename);
        return callMcpTool("readFile", args);
    }

    @PostMapping("/files/{filename}")
    public Map<String, Object> writeFile(@PathVariable String filename, @RequestBody Map<String, String> body) {
        Map<String, Object> args = new HashMap<>();
        args.put("filename", filename);
        args.put("content", body.get("content"));
        return callMcpTool("writeFile", args);
    }

    @PutMapping("/files/{filename}/append")
    public Map<String, Object> appendToFile(@PathVariable String filename, @RequestBody Map<String, String> body) {
        Map<String, Object> args = new HashMap<>();
        args.put("filename", filename);
        args.put("content", body.get("content"));
        return callMcpTool("appendToFile", args);
    }

    @DeleteMapping("/files/{filename}")
    public Map<String, Object> deleteFile(@PathVariable String filename) {
        Map<String, Object> args = new HashMap<>();
        args.put("filename", filename);
        return callMcpTool("deleteFile", args);
    }

    @GetMapping("/files/{filename}/info")
    public Map<String, Object> getFileInfo(@PathVariable String filename) {
        Map<String, Object> args = new HashMap<>();
        args.put("filename", filename);
        return callMcpTool("getFileInfo", args);
    }

    @GetMapping("/workspace")
    public Map<String, Object> getWorkspacePath() {
        return callMcpTool("getWorkspacePath", new HashMap<>());
    }

    // ========== Data Storage Service Endpoints ==========

    @PostMapping("/storage/{key}")
    public Map<String, Object> store(@PathVariable String key, @RequestBody Map<String, String> body) {
        Map<String, Object> args = new HashMap<>();
        args.put("key", key);
        args.put("value", body.get("value"));
        return callMcpTool("store", args);
    }

    @GetMapping("/storage/{key}")
    public Map<String, Object> retrieve(@PathVariable String key) {
        Map<String, Object> args = new HashMap<>();
        args.put("key", key);
        return callMcpTool("retrieve", args);
    }

    @DeleteMapping("/storage/{key}")
    public Map<String, Object> deleteFromStorage(@PathVariable String key) {
        Map<String, Object> args = new HashMap<>();
        args.put("key", key);
        return callMcpTool("delete", args);
    }

    @GetMapping("/storage")
    public Map<String, Object> listKeys() {
        return callMcpTool("listKeys", new HashMap<>());
    }

    @DeleteMapping("/storage")
    public Map<String, Object> clearStorage() {
        return callMcpTool("clear", new HashMap<>());
    }

    @GetMapping("/storage/count")
    public Map<String, Object> countEntries() {
        return callMcpTool("count", new HashMap<>());
    }

    // ========== Calculator Service Endpoints ==========

    @GetMapping("/calc/add")
    public Map<String, Object> add(@RequestParam double a, @RequestParam double b) {
        Map<String, Object> args = new HashMap<>();
        args.put("a", a);
        args.put("b", b);
        return callMcpTool("add", args);
    }

    @GetMapping("/calc/subtract")
    public Map<String, Object> subtract(@RequestParam double a, @RequestParam double b) {
        Map<String, Object> args = new HashMap<>();
        args.put("a", a);
        args.put("b", b);
        return callMcpTool("subtract", args);
    }

    @GetMapping("/calc/multiply")
    public Map<String, Object> multiply(@RequestParam double a, @RequestParam double b) {
        Map<String, Object> args = new HashMap<>();
        args.put("a", a);
        args.put("b", b);
        return callMcpTool("multiply", args);
    }

    @GetMapping("/calc/divide")
    public Map<String, Object> divide(@RequestParam double a, @RequestParam double b) {
        Map<String, Object> args = new HashMap<>();
        args.put("a", a);
        args.put("b", b);
        return callMcpTool("divide", args);
    }

    // ========== Time Service Endpoints ==========

    @GetMapping("/time")
    public Map<String, Object> getCurrentTime() {
        return callMcpTool("getCurrentTime", new HashMap<>());
    }

    // ========== Information Endpoint ==========

    @GetMapping("/info")
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "MCP Services Controller");
        response.put("description", "REST endpoints for MCP Server tools");
        response.put("mcpClientsAvailable", mcpSyncClients != null && !mcpSyncClients.isEmpty());
        response.put("note", "In Spring AI 1.1.0-M2, MCP clients are created on-demand for ChatModel function calling");

        Map<String, String[]> endpoints = new HashMap<>();
        endpoints.put("files", new String[]{
                "GET /api/mcp/files - List all files",
                "GET /api/mcp/files/{filename} - Read a file",
                "POST /api/mcp/files/{filename} - Write to a file",
                "PUT /api/mcp/files/{filename}/append - Append to a file",
                "DELETE /api/mcp/files/{filename} - Delete a file",
                "GET /api/mcp/files/{filename}/info - Get file info",
                "GET /api/mcp/workspace - Get workspace path"
        });
        endpoints.put("storage", new String[]{
                "POST /api/mcp/storage/{key} - Store a value",
                "GET /api/mcp/storage/{key} - Retrieve a value",
                "DELETE /api/mcp/storage/{key} - Delete a value",
                "GET /api/mcp/storage - List all keys",
                "DELETE /api/mcp/storage - Clear all storage",
                "GET /api/mcp/storage/count - Count entries"
        });
        endpoints.put("calculator", new String[]{
                "GET /api/mcp/calc/add?a=5&b=3",
                "GET /api/mcp/calc/subtract?a=5&b=3",
                "GET /api/mcp/calc/multiply?a=5&b=3",
                "GET /api/mcp/calc/divide?a=6&b=2"
        });
        endpoints.put("time", new String[]{
                "GET /api/mcp/time - Get current time"
        });

        response.put("endpoints", endpoints);
        return response;
    }
}
