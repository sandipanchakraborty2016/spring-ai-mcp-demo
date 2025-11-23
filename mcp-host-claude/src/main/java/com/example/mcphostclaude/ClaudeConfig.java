package com.example.mcphostclaude;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for MCP Host with Claude (Anthropic).
 *
 * This demonstrates MCP's model-agnostic nature:
 * - Same MCP tools configuration
 * - Different AI model (Claude vs OpenAI)
 * - No changes needed to the MCP Server
 *
 * Spring AI 1.1.0-M2 auto-configures AnthropicChatModel and McpClient.
 */
@Configuration
public class ClaudeConfig {

}
