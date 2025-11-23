# Spring AI MCP Demo - Architecture

This project demonstrates the **Model Context Protocol (MCP)** using Spring AI 1.1.0-M2.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    MCP Protocol Demonstration                     │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┐                              ┌─────────────────┐
│  MCP Host        │                              │   MCP Client    │
│  (OpenAI)        │                              │   (Port 8081)   │
│  Port 8082       │                              │                 │
│                  │                              │   REST Wrapper  │
│  ✅ TRUE MCP     │                              │   (Convenience) │
│  ChatModel uses  │                              │                 │
│  tools via MCP   │                              │   Direct tool   │
│  protocol (SSE)  │                              │   execution     │
└────────┬─────────┘                              └────────┬────────┘
         │                                                 │
         │ Uses MCP Protocol (SSE + JSON-RPC)             │ (No MCP protocol)
         │                                                 │
         └─────────────────────┬───────────────────────────┘
                               │
                               ▼
                      ┌─────────────────┐
                      │   MCP Server    │
                      │   (Port 8080)   │
                      └─────────────────┘
                               │
                               │ @McpTool annotations
                               ▼
                      ┌──────────────────┐
                      │ • TimeService    │
                      │ • Calculator     │
                      │ • FileOps        │
                      │ • DataStorage    │
                      └──────────────────┘
```

## Components

### 1. MCP Server (port 8080) ✅ MCP Protocol
**Purpose**: Exposes tools via MCP protocol
**Technology**: Spring Boot + `@McpTool` annotations
**Protocol**: Server-Sent Events (SSE) with JSON-RPC at `/mcp/message`

**How it works**:
- Services annotated with `@McpTool` are automatically exposed via MCP
- Example: `CalculatorService.add(a, b)` becomes an MCP tool
- Accessed by AI models through MCP client integration

### 2. MCP Host (port 8082) ✅ TRUE MCP DEMONSTRATION
**Purpose**: AI application that uses OpenAI ChatModel with MCP tools
**Technology**: Spring Boot + Spring AI + OpenAI
**MCP Integration**: ✅ **Uses actual MCP protocol**

**How it works**:
1. Spring AI auto-configures MCP client based on `application.properties`
2. MCP tools are registered as `FunctionCallback` instances
3. ChatModel can call these tools during AI conversations
4. Tools are invoked via MCP protocol (SSE + JSON-RPC)

**Example**:
```bash
curl "http://localhost:8082/api/chat?query=What+is+25+times+4"
# ChatModel may call calculator.multiply(25, 4) via MCP if needed
```

### 3. MCP Client (port 8081) ⚠️ REST Wrapper (NOT MCP Protocol)
**Purpose**: Convenience REST API for direct tool access
**Technology**: Spring Boot with REST controllers
**MCP Integration**: ❌ **Does NOT use MCP protocol** - provides direct REST access

**How it works**:
- Provides REST endpoints matching README documentation
- Executes tool logic locally (same algorithms as server)
- Useful for testing tools without AI involvement
- **NOT demonstrating MCP protocol** - just REST wrappers

**Example**:
```bash
curl "http://localhost:8081/api/calculator/add?a=10&b=5"
# Returns: {"result": 15.0, "status": "success"}
```

## MCP Protocol Usage

### Where MCP Protocol IS Used ✅
- **MCP Server** → **MCP Host**:
  - ChatModel calls tools via Spring AI's MCP client
  - Uses SSE transport with JSON-RPC messages
  - Demonstrates true MCP architecture

### Where MCP Protocol is NOT Used ❌
- **MCP Client REST endpoints**:
  - Direct HTTP GET/POST to execute tools
  - No SSE, no JSON-RPC
  - Just convenience wrappers

## Key Technologies

- **Spring Boot 3.4.12**: Application framework
- **Spring AI 1.1.0-M2**: AI integration with MCP support
- **Model Context Protocol 0.13.1**: Tool protocol specification
- **OpenAI GPT-4o-mini**: AI model for chat
- **Server-Sent Events (SSE)**: MCP transport mechanism
- **JSON-RPC**: MCP message format

## Testing MCP Protocol

### Test TRUE MCP Usage (mcp-host):
```bash
# Start all services
cd mcp-server && mvn spring-boot:run  # Terminal 1
cd mcp-host && mvn spring-boot:run     # Terminal 2

# Ask AI to use calculator (it will call MCP tools)
curl "http://localhost:8082/api/chat?query=Calculate+the+square+root+of+144"
```

### Test REST Wrapper (mcp-client):
```bash
# Direct tool access (no MCP protocol involved)
curl "http://localhost:8081/api/calculator/sqrt?number=144"
```

## Summary

✅ **For MCP Protocol demonstration**: Use `mcp-host` (port 8082)
⚠️  **For direct tool testing**: Use `mcp-client` (port 8081)
🔧 **Tool definitions**: Defined in `mcp-server` (port 8080)

The repository correctly demonstrates MCP in the `mcp-host` module, where Spring AI integrates MCP tools with ChatModel using the actual MCP protocol (SSE + JSON-RPC).
