package com.example.mcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class McpServerApplication {

	public static void main(String[] args) {
		// Check if --stdio flag is present
		boolean stdioMode = Arrays.asList(args).contains("--stdio");

		if (stdioMode) {
			// Enable STDIO transport mode
			System.setProperty("spring.ai.mcp.server.transport", "stdio");
			System.setProperty("server.port", "0"); // Disable HTTP server in STDIO mode
		}

		SpringApplication.run(McpServerApplication.class, args);
	}

}
