package com.example.mcphost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo controller for MCP Host.
 *
 * Note: This is a simplified version for Spring AI 1.1.0-M2.
 * To enable AI chat with MCP tools:
 * 1. Add spring-ai-openai-spring-boot-starter dependency
 * 2. Configure OpenAI API key
 * 3. Implement ChatClient with MCP function callbacks
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

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
     * Demo endpoint showing MCP integration capability.
     */
    @GetMapping("/demo")
    public Map<String, Object> demo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "MCP Host Demo");
        response.put("mcpServer", "http://localhost:8080/mcp/message");
        response.put("mcpClient", "http://localhost:8081");
        response.put("features", new String[]{
            "Connect to MCP Server for tool discovery",
            "Integrate tools with AI chat models",
            "Stream responses using Server-Sent Events",
            "Automatic function calling with AI"
        });
        response.put("nextSteps", "Add OpenAI dependency and API key to enable AI chat");
        return response;
    }

}
