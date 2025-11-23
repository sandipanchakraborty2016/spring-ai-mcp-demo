# Spring AI MCP Demo

A comprehensive demonstration of the **Model Context Protocol (MCP)** using Spring AI, featuring a fully-functional MCP server and client with multiple tool examples.

## 🎯 Project Overview

This project showcases Spring AI's MCP implementation with:
- **MCP Server**: Exposes multiple tools via Server-Sent Events (SSE)
- **MCP Client**: Consumes and invokes server tools through REST endpoints
- **Spring Boot 3.4.12** and **Spring AI BOM 1.0.3**
- **Docker** support for containerized deployment
- **Render.com** deployment configuration

## 🏗️ Architecture

```
┌─────────────────┐                           ┌─────────────────┐
│   MCP Host      │                           │   MCP Client    │
│  (Port 8082)    │                           │   (Port 8081)   │
│                 │                           │                 │
│  AI ChatClient  │                           │   REST API      │
│  + MCP Tools    │                           │   Endpoints     │
└────────┬────────┘                           └────────┬────────┘
         │                                             │
         │ Uses MCP Tools                              │ Calls MCP Tools
         │                                             │
         └──────────────────┬──────────────────────────┘
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

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for containerized deployment)

## 🏃 Quick Start

### Local Development

1. **Clone the repository**
   ```bash
   cd spring-ai-mcp-demo
   ```

2. **Build both projects**
   ```bash
   # Build MCP Server
   cd mcp-server
   mvn clean package
   cd ..

   # Build MCP Client
   cd mcp-client
   mvn clean package
   cd ..
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

2. **Start MCP Client** (in a new terminal)
   ```bash
   cd mcp-client
   mvn spring-boot:run
   ```

## 🔌 API Examples

### MCP Host - AI Chat with Streaming 🆕

The MCP Host now supports **real-time streaming responses** using Server-Sent Events (SSE):

#### Streaming Chat (GET)
```bash
# Stream AI response token by token - use -N to disable buffering
curl -N "http://localhost:8082/api/chat/stream?message=What+is+15+plus+27"
```

#### Streaming Chat (POST)
```bash
# For longer messages or complex queries
curl -N -X POST http://localhost:8082/api/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"message":"Calculate the square root of 144 and tell me what time it is"}'
```

#### Non-Streaming Chat
```bash
# Traditional request-response
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What time is it?"}'
```

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
├── mcp-server/                 # MCP Server application
│   ├── src/main/java/
│   │   └── com/example/mcpserver/
│   │       ├── McpServerApplication.java
│   │       ├── McpConfig.java
│   │       ├── TimeService.java
│   │       ├── CalculatorService.java
│   │       ├── FileService.java
│   │       ├── DataStorageService.java
│   │       └── HealthController.java
│   ├── Dockerfile
│   └── pom.xml
├── mcp-client/                 # MCP Client application
│   ├── src/main/java/
│   │   └── com/example/mcpclient/
│   │       ├── McpClientApplication.java
│   │       ├── ClientController.java
│   │       └── HealthController.java
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── render.yaml
└── README.md
```

## 🔧 Configuration

### MCP Server Configuration
Key properties in `mcp-server/src/main/resources/application.properties`:
```properties
spring.ai.mcp.server.transport=sse
spring.ai.mcp.server.sse.message-endpoint=/mcp/message
```

### MCP Client Configuration
Key properties in `mcp-client/src/main/resources/application.properties`:
```properties
spring.ai.mcp.client.transport=sse
spring.ai.mcp.client.sse.base-url=http://localhost:8080
spring.ai.mcp.client.sse.message-endpoint=/mcp/message
```

## 🧪 Testing

### Health Checks
```bash
# Server health
curl http://localhost:8080/health

# Client health
curl http://localhost:8081/health
```

### Tool Discovery
```bash
# List all available tools
curl http://localhost:8081/api/tools
```

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
