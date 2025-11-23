package com.example.mcphostclaude;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat controller demonstrating Claude (Anthropic) with MCP tools.
 *
 * This showcases MCP's model-agnostic design:
 * - Same MCP Server (port 8080) provides tools
 * - Different AI models (Claude vs OpenAI) can use the same tools
 * - No changes needed to the MCP Server
 */
@RestController
@RequestMapping("/api")
public class ClaudeChatController {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeChatController.class);

    @Autowired(required = false)
    private ChatModel chatModel;

    @Autowired(required = false)
    private List<String> mcpFunctionNames;

    /**
     * Get Claude host status.
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "mcp-host-claude");
        response.put("status", "UP");
        response.put("aiModel", "Claude 3.5 Sonnet (Anthropic)");
        response.put("springAiVersion", "1.1.0-M2");
        response.put("message", "Claude MCP Host ready for AI integration");
        response.put("mcpServer", "http://localhost:8080");
        return response;
    }

    /**
     * Chat endpoint that processes queries using Claude with MCP tools.
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
     * Demo endpoint showing Claude + MCP integration.
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
        response.put("title", "Claude + MCP Demo");
        response.put("description", "Demonstrates MCP is Model-Agnostic");
        response.put("aiProvider", "Anthropic Claude");
        response.put("model", "claude-3-5-sonnet-20241022");
        response.put("mcpServer", "http://localhost:8080/mcp/message");
        response.put("comparisonHost", Map.of(
            "openai", "http://localhost:8082",
            "claude", "http://localhost:8083 (this service)"
        ));
        response.put("keyPoint", "Same MCP tools work with both OpenAI and Claude - that's the power of MCP!");
        response.put("features", new String[]{
            "✓ Connected to shared MCP Server",
            "✓ Claude 3.5 Sonnet with automatic function calling",
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
     * Process chat query using Claude with MCP tools.
     */
    private Map<String, Object> processChat(String query) {
        Map<String, Object> response = new HashMap<>();

        if (chatModel == null) {
            response.put("error", "ChatModel not configured");
            response.put("message", "Anthropic API key may be missing. Set ANTHROPIC_API_KEY environment variable.");
            response.put("status", "unavailable");
            return response;
        }

        if (query == null || query.trim().isEmpty()) {
            response.put("error", "Query parameter is required");
            response.put("example", "/api/chat?query=What+is+25+times+4");
            return response;
        }

        try {
            logger.info("Processing chat query with Claude: {}", query);

            // Call Claude ChatModel
            Prompt prompt = new Prompt(query);
            ChatResponse chatResponse = chatModel.call(prompt);

            String aiResponse = chatResponse.getResult().getOutput().getText();

            response.put("query", query);
            response.put("response", aiResponse);
            response.put("model", "Anthropic Claude 3.5 Sonnet");
            response.put("note", "MCP tools will be auto-configured when available");
            response.put("timestamp", System.currentTimeMillis());

            logger.info("Claude response generated successfully");

        } catch (Exception e) {
            logger.error("Error processing chat query with Claude", e);
            response.put("error", "Failed to process query");
            response.put("message", e.getMessage());
            response.put("query", query);
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
        response.put("architecture", Map.of(
            "mcpServer", "Provides tools (calculator, time, files, etc.)",
            "mcpHostOpenAI", "Uses tools with OpenAI GPT models",
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
        return response;
    }
}
