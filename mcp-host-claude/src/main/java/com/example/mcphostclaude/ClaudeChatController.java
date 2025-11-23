package com.example.mcphostclaude;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
     * Demo endpoint showing Claude + MCP integration.
     */
    @GetMapping("/demo")
    public Map<String, Object> demo() {
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
            "Connect to shared MCP Server",
            "Use tools: calculator, time, file operations, storage",
            "Stream responses using Server-Sent Events",
            "Automatic function calling with Claude",
            "Model-agnostic tool integration"
        });
        response.put("setup", Map.of(
            "step1", "Set ANTHROPIC_API_KEY environment variable",
            "step2", "Claude automatically discovers MCP tools",
            "step3", "Ask Claude to use tools: 'What time is it?' or 'Calculate 25 + 17'"
        ));
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
