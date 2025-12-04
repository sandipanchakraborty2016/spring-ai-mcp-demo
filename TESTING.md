# Testing Guide for Spring AI MCP Demo

This guide provides comprehensive testing examples for all MCP services.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Service Endpoints](#service-endpoints)
- [1. MCP Host (OpenAI) Tests](#1-mcp-host-openai-tests)
- [2. MCP Host Direct Tool Tests](#2-mcp-host-direct-tool-tests)
- [3. MCP Server SSE Tests](#3-mcp-server-sse-tests)
- [4. Monitoring and Debugging](#4-monitoring-and-debugging)
- [5. Complete Test Script](#5-complete-test-script)

---

## Prerequisites

Ensure all services are running:

```bash
# Check service status
curl -s http://localhost:8080/health  # MCP Server
curl -s http://localhost:8082/api/status  # MCP Host

# Verify OpenAI API key is configured
curl -s http://localhost:8082/api/status | jq
```

---

## Service Endpoints

| Service | Port | Protocol | Purpose |
|---------|------|----------|---------|
| **MCP Server** | 8080 | SSE + JSON-RPC | Provides tools via MCP protocol |
| **MCP Host (OpenAI)** | 8082 | REST | AI chat with MCP tools |

---

## 1. MCP Host (OpenAI) Tests

### 1.1 ChatModel with MCP Tools

These tests demonstrate OpenAI ChatModel automatically discovering and calling MCP tools:

#### Basic Calculations
```bash
# Test multiplication (calls MCP multiply tool)
curl -s "http://localhost:8082/api/chat?query=What+is+25+times+4" | jq

# Expected: "25 times 4 is 100."
# Tool called: multiply(25, 4)
```

```bash
# Test square root (calls MCP sqrt tool)
curl -s "http://localhost:8082/api/chat?query=Calculate+the+square+root+of+144" | jq

# Expected: "The square root of 144 is 12."
# Tool called: sqrt(144)
```

```bash
# Test division
curl -s "http://localhost:8082/api/chat?query=What+is+100+divided+by+5" | jq

# Expected: "100 divided by 5 is 20."
# Tool called: divide(100, 5)
```

#### Multi-Step Calculations
```bash
# Test chained operations
curl -s "http://localhost:8082/api/chat?query=Calculate+the+square+root+of+144+and+then+multiply+it+by+5" | jq

# Expected: "The square root of 144 is 12, and when multiplied by 5, it equals 60."
# Tools called: sqrt(144) → multiply(12, 5)
```

```bash
# Test addition
curl -s "http://localhost:8082/api/chat?query=Add+456+and+789" | jq

# Expected: "456 plus 789 equals 1245."
# Tool called: add(456, 789)
```

#### Time Service
```bash
# Test current time (calls MCP getCurrentTime tool)
curl -s "http://localhost:8082/api/chat?query=What+is+the+current+time" | jq

# Expected: "The current time is 2025-11-23T20:24:12."
# Tool called: getCurrentTime()
```

#### POST Method
```bash
# Test using POST with JSON body
curl -s -X POST "http://localhost:8082/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"query":"What is 15 times 8?"}' | jq
```

### 1.2 List Available MCP Tools

```bash
# List all 20 MCP tools discovered by the host
curl -s "http://localhost:8082/api/mcp-tools" | jq

# Expected: List of all tools with names and descriptions
```

### 1.3 MCP Info and Status

```bash
# Get MCP configuration info
curl -s "http://localhost:8082/api/mcp-info" | jq

# Get host status
curl -s "http://localhost:8082/api/status" | jq
```

---

## 2. MCP Host Direct Tool Tests

These endpoints call MCP tools directly without using ChatModel (for testing purposes):

### 2.1 Calculator Tools

```bash
# Multiply
curl -s "http://localhost:8082/api/mcp/calc/multiply?a=25&b=4" | jq
# Expected: {"result": ["100.0"], "status": "success"}

# Add
curl -s "http://localhost:8082/api/mcp/calc/add?a=100&b=200" | jq
# Expected: {"result": ["300.0"], "status": "success"}

# Square root
curl -s "http://localhost:8082/api/mcp/calc/sqrt?number=144" | jq
# Expected: {"result": ["12.0"], "status": "success"}

# Divide
curl -s "http://localhost:8082/api/mcp/calc/divide?a=100&b=5" | jq
# Expected: {"result": ["20.0"], "status": "success"}

# Power
curl -s "http://localhost:8082/api/mcp/calc/power?base=2&exponent=10" | jq
# Expected: {"result": ["1024.0"], "status": "success"}

# Subtract
curl -s "http://localhost:8082/api/mcp/calc/subtract?a=100&b=25" | jq
# Expected: {"result": ["75.0"], "status": "success"}
```

### 2.2 Time Tool

```bash
# Get current time
curl -s "http://localhost:8082/api/mcp/time" | jq
# Expected: {"result": ["2025-11-23T20:15:44.004397"], "status": "success"}
```

### 2.3 Storage Operations

```bash
# Store a value
curl -s -X POST "http://localhost:8082/api/mcp/storage/name" \
  -H "Content-Type: application/json" \
  -d '{"value":"Alice"}' | jq

# Retrieve the value
curl -s "http://localhost:8082/api/mcp/storage/name" | jq
# Expected: {"result": ["Alice"], "status": "success"}

# Store more values
curl -s -X POST "http://localhost:8082/api/mcp/storage/age" \
  -H "Content-Type: application/json" \
  -d '{"value":"30"}' | jq

curl -s -X POST "http://localhost:8082/api/mcp/storage/city" \
  -H "Content-Type: application/json" \
  -d '{"value":"New York"}' | jq

# List all keys
curl -s "http://localhost:8082/api/mcp/storage" | jq
# Expected: {"result": ["name", "age", "city"], "status": "success"}

# Count entries
curl -s "http://localhost:8082/api/mcp/storage/count" | jq
# Expected: {"result": ["3"], "status": "success"}

# Delete a key
curl -s -X DELETE "http://localhost:8082/api/mcp/storage/age" | jq

# Clear all storage
curl -s -X DELETE "http://localhost:8082/api/mcp/storage" | jq
```

### 2.4 File Operations

```bash
# Write a file
curl -s -X POST "http://localhost:8082/api/mcp/files/hello.txt" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello from MCP!"}' | jq

# Read the file
curl -s "http://localhost:8082/api/mcp/files/hello.txt" | jq
# Expected: {"result": ["Hello from MCP!"], "status": "success"}

# Append to file
curl -s -X PUT "http://localhost:8082/api/mcp/files/hello.txt/append" \
  -H "Content-Type: application/json" \
  -d '{"content":"\nAppended line!"}' | jq

# Get file info
curl -s "http://localhost:8082/api/mcp/files/hello.txt/info" | jq

# List all files
curl -s "http://localhost:8082/api/mcp/files" | jq

# Get workspace path
curl -s "http://localhost:8082/api/mcp/workspace" | jq

# Delete file
curl -s -X DELETE "http://localhost:8082/api/mcp/files/hello.txt" | jq
```

---

## 3. MCP Server SSE Tests

### 3.1 SSE Endpoint

```bash
# Test SSE endpoint (should return event stream)
curl -v http://localhost:8080/sse 2>&1 | head -20
# Expected: HTTP/1.1 200, Content-Type: text/event-stream
```

### 3.2 Health Check

```bash
# Check MCP Server health
curl -s http://localhost:8080/health | jq
```

---

## 4. Monitoring and Debugging

### 4.1 Watch MCP Tool Calls in Real-Time

```bash
# Monitor MCP Host logs for tool execution
tail -f /tmp/host-mcp-tools.log | grep -E "(Executing tool call|MCP)"

# You should see lines like:
# 2025-11-23T20:23:52.636Z DEBUG ... Executing tool call: multiply
# 2025-11-23T20:24:12.656Z DEBUG ... Executing tool call: getCurrentTime
```

### 4.2 Watch MCP Server Logs

```bash
# Monitor MCP Server activity
tail -f /tmp/mcp-server.log
```

### 4.3 Check Tool Call History

```bash
# See all MCP tool calls that have been executed
grep "Executing tool call" /tmp/host-mcp-tools.log
```

---

## 5. Complete Test Script

Run all tests at once:

```bash
#!/bin/bash

echo "=== Spring AI MCP Demo - Complete Test Suite ==="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== 1. Health Checks ===${NC}"
curl -s http://localhost:8080/health | jq -r '.status' && echo "✓ MCP Server"
curl -s http://localhost:8082/api/status | jq -r '.status' && echo "✓ MCP Host"
echo ""

echo -e "${BLUE}=== 2. ChatModel Tests (OpenAI + MCP Tools) ===${NC}"
echo -e "${GREEN}Test: Multiplication${NC}"
curl -s "http://localhost:8082/api/chat?query=What+is+25+times+4" | jq -r '.response'

echo -e "${GREEN}Test: Current Time${NC}"
curl -s "http://localhost:8082/api/chat?query=What+is+the+current+time" | jq -r '.response'

echo -e "${GREEN}Test: Square Root${NC}"
curl -s "http://localhost:8082/api/chat?query=Calculate+the+square+root+of+144" | jq -r '.response'

echo -e "${GREEN}Test: Multi-step Calculation${NC}"
curl -s "http://localhost:8082/api/chat?query=Calculate+the+square+root+of+144+and+then+multiply+it+by+5" | jq -r '.response'
echo ""

echo -e "${BLUE}=== 3. Direct Tool Tests ===${NC}"
echo -e "${GREEN}Calculator: 100 + 200${NC}"
curl -s "http://localhost:8082/api/mcp/calc/add?a=100&b=200" | jq

echo -e "${GREEN}Calculator: sqrt(144)${NC}"
curl -s "http://localhost:8082/api/mcp/calc/sqrt?number=144" | jq

echo -e "${GREEN}Time Tool${NC}"
curl -s "http://localhost:8082/api/mcp/time" | jq
echo ""

echo -e "${BLUE}=== 4. Storage Tests ===${NC}"
curl -s -X POST "http://localhost:8082/api/mcp/storage/test_name" \
  -H "Content-Type: application/json" \
  -d '{"value":"Test User"}' | jq
echo "✓ Stored value"

curl -s "http://localhost:8082/api/mcp/storage/test_name" | jq -r '.result[0]'
echo "✓ Retrieved value"

curl -s "http://localhost:8082/api/mcp/storage" | jq
echo "✓ Listed keys"
echo ""

echo -e "${BLUE}=== 5. File Tests ===${NC}"
curl -s -X POST "http://localhost:8082/api/mcp/files/test.txt" \
  -H "Content-Type: application/json" \
  -d '{"content":"Test content"}' | jq
echo "✓ Created file"

curl -s "http://localhost:8082/api/mcp/files/test.txt" | jq -r '.result[0]'
echo "✓ Read file"

curl -s "http://localhost:8082/api/mcp/files" | jq
echo "✓ Listed files"
echo ""

echo -e "${BLUE}=== 6. Tool Discovery ===${NC}"
curl -s "http://localhost:8082/api/mcp-tools" | jq '.toolCount'
echo "tools available"
echo ""

echo -e "${GREEN}=== All Tests Complete ===${NC}"
```

Save this as `test-suite.sh`, make it executable, and run:

```bash
chmod +x test-suite.sh
./test-suite.sh
```

---

## Expected Results Summary

### ✅ Successful Test Indicators

1. **ChatModel Responses**: Natural language answers with correct calculations
2. **MCP Tool Calls**: Logs show "Executing tool call: [toolname]"
3. **Direct Tool Calls**: JSON responses with `"status": "success"`
4. **Tool Discovery**: 20 tools listed (calculator, time, files, storage)
5. **Response Fields**:
   - `mcpToolsConfigured: true`
   - `mcpSyncClientsAvailable: true`

### ❌ Common Issues

1. **"ChatModel not configured"**: OpenAI API key not set
2. **"No MCP clients available"**: MCP Server not running or SSE connection failed
3. **400 Bad Request**: Incorrect JSON format or missing parameters
4. **Connection refused**: Service not started or wrong port

---

## Advanced Testing

### Test MCP Tool Execution Flow

```bash
# Terminal 1: Watch logs
tail -f /tmp/host-mcp-tools.log | grep "tool"

# Terminal 2: Make a request
curl "http://localhost:8082/api/chat?query=What+is+50+times+2"

# You should see in Terminal 1:
# - "MCP tool callbacks available, configuring for function calling"
# - "Executing tool call: multiply"
```

### Test Tool Callback Configuration

```bash
# Verify MCP tool callbacks are configured
curl -s "http://localhost:8082/api/chat?query=test" | jq '.mcpToolsConfigured'
# Expected: true
```

---

## Notes

- All MCP Host tests use **OpenAI GPT-4o-mini** with automatic function calling
- Tool calls happen **automatically** based on ChatModel's understanding
- Direct tool endpoints bypass ChatModel for testing tool functionality
- Storage and file operations persist in memory (cleared on restart)
- Workspace for files: Check with `/api/mcp/workspace` endpoint

For issues or questions, check the [main README](README.md) or open an issue.
