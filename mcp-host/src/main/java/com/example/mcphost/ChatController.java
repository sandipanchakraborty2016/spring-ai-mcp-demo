package com.example.mcphost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat controller for MCP Host with OpenAI integration.
 *
 * Provides AI-powered chat endpoints that use MCP tools.
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired(required = false)
    private ChatModel chatModel;

    @Autowired(required = false)
    private List<String> mcpFunctionNames;

    @Autowired(required = false)
    private List<io.modelcontextprotocol.client.McpSyncClient> mcpSyncClients;

    @Autowired(required = false)
    private org.springframework.ai.mcp.SyncMcpToolCallbackProvider mcpToolCallbacks;

    /**
     * Get host status.
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "mcp-host");
        response.put("status", "UP");
        response.put("springAiVersion", "1.1.0-M2");
        response.put("message", "MCP Host ready for AI integration");
        return response;
    }

    /**
     * Chat endpoint that processes queries using OpenAI with MCP tools.
     * Supports both GET with query parameter and POST with JSON body.
     */
    @GetMapping("/chat")
    public Map<String, Object> chatGet(@RequestParam(required = false) String query) {
        return processChat(query);
    }

    @PostMapping("/chat")
    public Map<String, Object> chatPost(@RequestBody(required = false) Map<String, String> request) {
        String query = request != null ? request.get("query") : null;
        return processChat(query);
    }

    /**
     * Demo endpoint showing MCP integration capability.
     * Now supports actual query processing!
     */
    @GetMapping("/demo")
    public Map<String, Object> demo(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            // If query parameter provided, process it with AI
            return processChat(query);
        }

        // Otherwise return demo information
        Map<String, Object> response = new HashMap<>();
        response.put("message", "MCP Host Demo - OpenAI with MCP Tools");
        response.put("mcpServer", "http://localhost:8080/mcp/message");
        response.put("features", new String[]{
            "✓ Connected to MCP Server for tool discovery",
            "✓ OpenAI GPT model with automatic function calling",
            "✓ Available tools: calculator, time, file operations, storage",
            "✓ Try it: /api/demo?query=What+is+25+times+4"
        });
        response.put("exampleQueries", new String[]{
            "/api/chat?query=What+time+is+it",
            "/api/chat?query=Calculate+144+divided+by+12",
            "/api/demo?query=What+is+the+square+root+of+144"
        });
        response.put("chatModelConfigured", chatModel != null);
        response.put("mcpFunctionsAvailable", mcpFunctionNames != null ? mcpFunctionNames.size() : 0);
        return response;
    }

    /**
     * Process chat query using OpenAI with MCP tools.
     */
    private Map<String, Object> processChat(String query) {
        Map<String, Object> response = new HashMap<>();

        if (chatModel == null) {
            response.put("error", "ChatModel not configured");
            response.put("message", "OpenAI API key may be missing. Set SPRING_AI_OPENAI_API_KEY environment variable.");
            response.put("status", "unavailable");
            return response;
        }

        if (query == null || query.trim().isEmpty()) {
            response.put("error", "Query parameter is required");
            response.put("example", "/api/chat?query=What+is+25+times+4");
            return response;
        }

        try {
            logger.info("Processing chat query: {}", query);

            // Configure OpenAI options with MCP tool callbacks
            ChatResponse chatResponse;
            if (mcpToolCallbacks != null) {
                logger.info("MCP tool callbacks available, configuring for function calling");
                OpenAiChatOptions chatOptions = OpenAiChatOptions.builder().build();
                chatOptions.setToolCallbacks(java.util.Arrays.asList(mcpToolCallbacks.getToolCallbacks()));

                // Call OpenAI ChatModel with MCP tools
                Prompt prompt = new Prompt(query, chatOptions);
                chatResponse = chatModel.call(prompt);
            } else {
                logger.warn("No MCP tool callbacks available");
                // Call OpenAI ChatModel without MCP tools
                Prompt prompt = new Prompt(query);
                chatResponse = chatModel.call(prompt);
            }

            String aiResponse = chatResponse.getResult().getOutput().getText();

            response.put("query", query);
            response.put("response", aiResponse);
            response.put("model", "OpenAI GPT");
            response.put("mcpToolsConfigured", mcpToolCallbacks != null);
            response.put("mcpSyncClientsAvailable", mcpSyncClients != null && !mcpSyncClients.isEmpty());
            response.put("timestamp", System.currentTimeMillis());

            logger.info("AI response generated successfully");

        } catch (Exception e) {
            logger.error("Error processing chat query", e);
            response.put("error", "Failed to process query");
            response.put("message", e.getMessage());
            response.put("query", query);
        }

        return response;
    }

    /**
     * List available MCP tools from the connected MCP server.
     */
    @GetMapping("/mcp-tools")
    public Map<String, Object> listMcpTools() {
        Map<String, Object> response = new HashMap<>();

        try {
            if (mcpSyncClients == null || mcpSyncClients.isEmpty()) {
                response.put("error", "No MCP clients available");
                response.put("status", "unavailable");
                return response;
            }

            io.modelcontextprotocol.client.McpSyncClient client = mcpSyncClients.get(0);
            io.modelcontextprotocol.spec.McpSchema.ListToolsResult toolsResult = client.listTools();

            response.put("status", "success");
            response.put("toolCount", toolsResult.tools().size());
            response.put("tools", toolsResult.tools().stream()
                    .map(tool -> Map.of(
                            "name", tool.name(),
                            "description", tool.description() != null ? tool.description() : "No description"
                    ))
                    .toList());
            response.put("mcpServer", "http://localhost:8080");
            response.put("protocol", "MCP via SSE");

        } catch (Exception e) {
            logger.error("Error listing MCP tools", e);
            response.put("error", "Failed to list tools");
            response.put("message", e.getMessage());
        }

        return response;
    }

    /**
     * Call an MCP tool directly.
     */
    @PostMapping("/mcp-tool-call")
    public Map<String, Object> callMcpTool(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (mcpSyncClients == null || mcpSyncClients.isEmpty()) {
                response.put("error", "No MCP clients available");
                response.put("status", "unavailable");
                return response;
            }

            String toolName = (String) request.get("toolName");
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) request.getOrDefault("arguments", new HashMap<>());

            if (toolName == null) {
                response.put("error", "toolName is required");
                return response;
            }

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
            response.put("protocol", "MCP via SSE");
            response.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            logger.error("Error calling MCP tool", e);
            response.put("error", "Failed to call tool");
            response.put("message", e.getMessage());
            response.put("status", "failed");
        }

        return response;
    }

    /**
     * Information about model-agnostic MCP design.
     */
    @GetMapping("/mcp-info")
    public Map<String, Object> mcpInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "Model Context Protocol - Model Agnostic");
        response.put("concept", "One set of tools, multiple AI models");
        response.put("aiModel", "OpenAI GPT models");
        response.put("architecture", Map.of(
            "mcpServer", "Provides tools (calculator, time, files, etc.)",
            "mcpHostOpenAI", "Uses tools with OpenAI GPT models (this service)",
            "mcpHostClaude", "Uses same tools with Anthropic Claude",
            "benefit", "Write tools once, use with any AI model"
        ));
        response.put("availableTools", new String[]{
            "getCurrentTime - Get current timestamp",
            "add, subtract, multiply, divide - Math operations",
            "readFile, writeFile, listFiles - File operations",
            "store, retrieve, listKeys - Data storage"
        });
        response.put("exampleQueries", new String[]{
            "What time is it?",
            "Calculate the square root of 144",
            "Create a file called hello.txt",
            "Store 'Alice' with key 'name'"
        });
        response.put("comparisonHost", Map.of(
            "openai", "http://localhost:8082 (this service)",
            "claude", "http://localhost:8083"
        ));
        return response;
    }

}
