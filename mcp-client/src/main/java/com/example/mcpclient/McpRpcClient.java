package com.example.mcpclient;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP RPC Client
 * Uses Spring AI's auto-configured McpSyncClient from the mcpSyncClients list
 */
@Component
public class McpRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(McpRpcClient.class);

    @Autowired(required = false)
    private List<McpSyncClient> mcpSyncClients;

    /**
     * Call an MCP tool using Spring AI's auto-configured McpSyncClient
     */
    public String callTool(String toolName, Map<String, Object> arguments) {
        try {
            if (mcpSyncClients == null || mcpSyncClients.isEmpty()) {
                throw new RuntimeException("No MCP clients configured. Check application.properties configuration.");
            }

            // Use the first client in the list
            McpSyncClient client = mcpSyncClients.get(0);

            logger.info("Calling MCP tool '{}' with arguments: {}", toolName, arguments);

            // Call tool using McpSyncClient
            McpSchema.CallToolResult result = client.callTool(
                    new McpSchema.CallToolRequest(toolName, arguments)
            );

            logger.info("Tool '{}' executed successfully", toolName);

            // Extract text content from result
            if (result.content() != null && !result.content().isEmpty()) {
                var content = result.content().get(0);
                if (content instanceof McpSchema.TextContent textContent) {
                    return textContent.text();
                }
            }

            return result.toString();

        } catch (Exception e) {
            logger.error("Error calling MCP tool '{}': {}", toolName, e.getMessage(), e);
            throw new RuntimeException("Failed to call MCP tool: " + toolName, e);
        }
    }
}
