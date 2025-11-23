package com.example.mcpserver;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Time service providing current timestamp.
 * Methods annotated with @McpTool are automatically exposed as MCP tools.
 */
@Service
public class TimeService {

    @McpTool(name = "getCurrentTime", description = "Get the current date and time in ISO format")
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
