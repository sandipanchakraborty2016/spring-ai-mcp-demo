package com.example.mcphostclaude;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for MCP Host with Claude.
 *
 * Note: This demonstrates MCP's model-agnostic nature.
 * The same MCP tools work with Claude, OpenAI, or any other model.
 *
 * To enable full integration:
 * 1. Set ANTHROPIC_API_KEY environment variable
 * 2. Configure ChatClient with MCP function callbacks
 * 3. Claude will automatically discover and use MCP tools
 */
@Configuration
public class ClaudeConfig {

    // Configuration beans for Claude + MCP integration will be added here
    // when full ChatClient integration is implemented

}
