# Spring AI MCP Demo

A comprehensive demonstration of the **Model Context Protocol (MCP)** using Spring AI, featuring a fully-functional MCP server, client, and **model-agnostic host implementations** with multiple tool examples.

> **📘 Architecture Guide**: See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed explanation of MCP protocol usage vs REST wrappers.

## 🎯 Project Overview

This project showcases Spring AI's MCP implementation with:
- **MCP Server** (Port 8080): Exposes multiple tools via Server-Sent Events (SSE) and `@McpTool` annotations
- **MCP Client** (Port 8081): REST API wrapper for direct tool access (convenience endpoints, not using MCP protocol)
- **MCP Host OpenAI** (Port 8082): ✅ **TRUE MCP DEMO** - AI chat using OpenAI GPT with tools via MCP protocol
- **MCP Host Claude** (Port 8083): ✅ **TRUE MCP DEMO** - AI chat using Anthropic Claude with tools via MCP protocol
- **Spring Boot 3.4.12** and **Spring AI 1.1.0-M2**
- **Docker** support for containerized deployment
- **Model-Agnostic Design**: Same MCP tools work with different AI providers

### 🔍 MCP Protocol vs REST APIs

- **MCP Protocol** (SSE + JSON-RPC): Used in `mcp-host` where ChatModel calls tools ✅
- **REST Wrapper APIs**: Used in `mcp-client` for direct tool testing (not MCP protocol) ⚠️

## 🌟 Key Feature: Model-Agnostic MCP

This demo proves MCP's **model-agnostic** nature:
- **One MCP Server** provides tools (calculator, time, files, storage)
- **Multiple AI Models** can use the same tools:
  - OpenAI GPT models (mcp-host on port 8082)
  - Anthropic Claude models (mcp-host-claude on port 8083)
- **No server changes** needed to support different models
- **Same tools, different intelligence** - compare how different models use identical tools

## 🏗️ Architecture

```
┌──────────────────┐        ┌──────────────────┐        ┌─────────────────┐
│  MCP Host        │        │  MCP Host        │        │   MCP Client    │
│  (OpenAI)        │        │  (Claude)        │        │   (Port 8081)   │
│  Port 8082       │        │  Port 8083       │        │                 │
│                  │        │                  │        │   REST API      │
│  GPT-4 + Tools   │        │ Claude + Tools   │        │   Endpoints     │
└────────┬─────────┘        └────────┬─────────┘        └────────┬────────┘
         │                           │                           │
         │                           │                           │
         │  Uses MCP Tools           │  Uses MCP Tools           │ Calls MCP Tools
         │                           │                           │
         └───────────────────────────┴───────────────────────────┘
                                     │
                                     │ SSE/HTTP
                                     ▼
                            ┌─────────────────┐
                            │   MCP Server    │
                            │   (Port 8080)   │
                            └─────────────────┘
                                     │
                                     │ Exposes Tools
                                     ▼
                            ┌──────────────────┐
                            │ • Time Service   │
                            │ • Calculator     │
                            │ • File Ops       │
                            │ • Data Storage   │
                            └──────────────────┘
```

**Model-Agnostic Design**: Both OpenAI and Claude hosts use the **same** MCP server and tools!

## 🚀 Available Tools

### Time Service
- `getCurrentTime()` - Returns current timestamp

### Calculator Service
- `add(a, b)` - Addition
- `subtract(a, b)` - Subtraction
- `multiply(a, b)` - Multiplication
- `divide(a, b)` - Division
- `power(base, exponent)` - Exponentiation
- `sqrt(number)` - Square root

### File Service
- `readFile(filename)` - Read file contents
- `writeFile(filename, content)` - Create or overwrite file
- `appendToFile(filename, content)` - Append to existing file
- `listFiles()` - List all files in workspace
- `deleteFile(filename)` - Delete a file
- `getFileInfo(filename)` - Get file size and metadata
- `getWorkspacePath()` - Get workspace directory path

### Data Storage Service
- `store(key, value)` - Store key-value pair
- `retrieve(key)` - Retrieve value by key
- `delete(key)` - Delete entry
- `listKeys()` - List all keys
- `clear()` - Clear all data
- `count()` - Count entries

## 📋 Prerequisites

- **Java 21 JDK** (not just JRE - includes compiler)
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for containerized deployment)

### Installing Java 21 JDK

```bash
# Ubuntu/Debian
sudo apt-get update && sudo apt-get install openjdk-21-jdk

# Verify installation
javac -version  # Should show: javac 21.x.x
```

## 🏃 Quick Start

### Docker Compose (Recommended)

**Start all services** (MCP Server + Client + Both AI Hosts):
```bash
docker-compose up --build
```

This starts:
- **MCP Server** on port 8080
- **MCP Client** on port 8081
- **MCP Host (OpenAI)** on port 8082
- **MCP Host (Claude)** on port 8083

### Environment Variables

Before starting, set your API keys:

```bash
# For OpenAI host
export SPRING_AI_OPENAI_API_KEY="your-openai-api-key"

# For Claude host
export ANTHROPIC_API_KEY="your-anthropic-api-key"
```

Or create a `.env` file:
```properties
SPRING_AI_OPENAI_API_KEY=your-openai-api-key
ANTHROPIC_API_KEY=your-anthropic-api-key
```

### Local Development (Individual Services)

1. **Build all projects**
   ```bash
   mvn clean package -f mcp-server/pom.xml
   mvn clean package -f mcp-client/pom.xml
   mvn clean package -f mcp-host/pom.xml
   mvn clean package -f mcp-host-claude/pom.xml
   ```

2. **Start services individually**
   ```bash
   # Terminal 1: MCP Server
   cd mcp-server && mvn spring-boot:run

   # Terminal 2: MCP Client
   cd mcp-client && mvn spring-boot:run

   # Terminal 3: MCP Host (OpenAI)
   cd mcp-host && mvn spring-boot:run

   # Terminal 4: MCP Host (Claude)
   cd mcp-host-claude && mvn spring-boot:run
   ```

## 🔌 API Examples

### Comparing OpenAI vs Claude (Model-Agnostic Demo)

Both hosts provide identical endpoints - test with the same queries to compare!

#### MCP Host - OpenAI (Port 8082)

```bash
# Get host information
curl http://localhost:8082/

# Check available MCP tools
curl http://localhost:8082/api/mcp-info

# Simple calculation
curl "http://localhost:8082/api/demo?query=What+is+25+times+4"
```

#### MCP Host - Claude (Port 8083)

```bash
# Get host information
curl http://localhost:8083/

# Check available MCP tools (same tools, different model!)
curl http://localhost:8083/api/mcp-info

# Same calculation with Claude
curl "http://localhost:8083/api/demo?query=What+is+25+times+4"
```

**Try both and compare**: Ask the same question to OpenAI and Claude hosts - they use the same MCP tools but may respond differently!

### MCP Client - Direct Tool Access

#### List Available Tools
```bash
curl http://localhost:8081/api/tools
```

#### Time Service
```bash
curl http://localhost:8081/api/time
```

### Calculator Operations
```bash
# Addition
curl "http://localhost:8081/api/calculator/add?a=10&b=5"

# Division
curl "http://localhost:8081/api/calculator/divide?a=20&b=4"

# Power
curl "http://localhost:8081/api/calculator/power?base=2&exponent=8"
```

### File Operations
```bash
# List files
curl "http://localhost:8081/api/files/list"

# Write a file
curl -X POST "http://localhost:8081/api/files/write?filename=test.txt&content=Hello%20World"

# Read a file
curl "http://localhost:8081/api/files/read?filename=test.txt"

# Append to file
curl -X POST "http://localhost:8081/api/files/append?filename=test.txt&content=%0ANew%20line"

# Get file info
curl "http://localhost:8081/api/files/info?filename=test.txt"

# Delete file
curl -X DELETE "http://localhost:8081/api/files/delete?filename=test.txt"

# Get workspace path
curl "http://localhost:8081/api/files/workspace"
```

### Data Storage
```bash
# Store data
curl -X POST "http://localhost:8081/api/storage/store?key=username&value=john_doe"

# Retrieve data
curl "http://localhost:8081/api/storage/retrieve?key=username"

# List all keys
curl "http://localhost:8081/api/storage/list"

# Delete entry
curl -X DELETE "http://localhost:8081/api/storage/delete?key=username"
```

## 🌐 Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed deployment instructions for:
- Render.com (Free tier)
- Railway.app
- Fly.io
- Other cloud platforms

## 📁 Project Structure

```
spring-ai-mcp-demo/
├── mcp-server/                 # MCP Server (provides tools)
│   ├── src/main/java/
│   │   └── com/example/mcpserver/
│   │       ├── McpServerApplication.java
│   │       ├── TimeService.java         # @McpTool
│   │       ├── CalculatorService.java   # @McpTool
│   │       ├── FileService.java         # @McpTool
│   │       ├── DataStorageService.java  # @McpTool
│   │       └── HealthController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── mcp-client/                 # MCP Client (calls tools directly)
│   ├── src/main/java/
│   │   └── com/example/mcpclient/
│   │       ├── McpClientApplication.java
│   │       ├── ClientController.java
│   │       └── HealthController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── mcp-host/                   # MCP Host with OpenAI
│   ├── src/main/java/
│   │   └── com/example/mcphost/
│   │       ├── McpHostApplication.java
│   │       ├── ChatController.java      # AI chat endpoints
│   │       └── HealthController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── mcp-host-claude/            # MCP Host with Claude (Anthropic)
│   ├── src/main/java/
│   │   └── com/example/mcphostclaude/
│   │       ├── McpHostClaudeApplication.java
│   │       ├── ClaudeChatController.java
│   │       ├── ClaudeConfig.java
│   │       └── HealthController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml          # Orchestrates all 4 services
├── render.yaml                 # Deployment configuration
└── README.md
```

## 🔧 Configuration

### MCP Server Configuration
`mcp-server/src/main/resources/application.properties`:
```properties
server.port=8080
spring.ai.mcp.server.transport=sse
spring.ai.mcp.server.sse.message-endpoint=/mcp/message
```

### MCP Client Configuration
`mcp-client/src/main/resources/application.properties`:
```properties
server.port=8081
spring.ai.mcp.client.transport=sse
spring.ai.mcp.client.sse.base-url=http://localhost:8080
spring.ai.mcp.client.sse.message-endpoint=/mcp/message
```

### MCP Host (OpenAI) Configuration
`mcp-host/src/main/resources/application.properties`:
```properties
server.port=8082
spring.ai.openai.api-key=${SPRING_AI_OPENAI_API_KEY:demo-key}
spring.ai.openai.chat.options.model=gpt-4
spring.ai.mcp.client.sse.base-url=${SPRING_AI_MCP_CLIENT_SSE_BASE_URL:http://localhost:8080}
```

### MCP Host (Claude) Configuration
`mcp-host-claude/src/main/resources/application.properties`:
```properties
server.port=8083
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY:demo-key}
spring.ai.anthropic.chat.options.model=claude-3-5-sonnet-20241022
spring.ai.mcp.client.sse.base-url=${SPRING_AI_MCP_CLIENT_SSE_BASE_URL:http://localhost:8080}
```

## 🧪 Testing

### Health Checks
```bash
# MCP Server health
curl http://localhost:8080/health

# MCP Client health
curl http://localhost:8081/health

# MCP Host (OpenAI) health
curl http://localhost:8082/health

# MCP Host (Claude) health
curl http://localhost:8083/health
```

### Testing Model-Agnostic MCP

**Same query to both AI models**:
```bash
# Ask OpenAI
curl "http://localhost:8082/api/demo?query=What+time+is+it+and+calculate+50+divided+by+5"

# Ask Claude (same tools, different reasoning!)
curl "http://localhost:8083/api/demo?query=What+time+is+it+and+calculate+50+divided+by+5"
```

### Tool Discovery
```bash
# List all available tools from MCP Client
curl http://localhost:8081/api/tools

# Check MCP info from OpenAI host
curl http://localhost:8082/api/mcp-info

# Check MCP info from Claude host
curl http://localhost:8083/api/mcp-info
```

### Comprehensive Test Suite

For a complete test guide with all endpoints and examples, see [TESTING.md](TESTING.md)

## 📚 Learn More

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Model Context Protocol Specification](https://modelcontextprotocol.io/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.

## 🙋 Support

For issues and questions, please open an issue in the repository.
