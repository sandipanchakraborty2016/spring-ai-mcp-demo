package com.example.mcphostclaude;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP Host using Claude (Anthropic).
 * Demonstrates that MCP is model-agnostic - same MCP server tools work with different AI models.
 */
@SpringBootApplication
public class McpHostClaudeApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpHostClaudeApplication.class, args);
	}

}
