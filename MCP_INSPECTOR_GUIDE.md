# Using MCP Inspector with Your Server

## Option 1: Official MCP Inspector (STDIO)

The official MCP Inspector requires STDIO transport. Run your MCP server in STDIO mode:

```bash
cd mcp-server
java -jar target/mcp-server-0.0.1-SNAPSHOT.jar --stdio
```

Then in another terminal:

```bash
npx @modelcontextprotocol/inspector java -jar /home/sandipan/spring-ai-mcp-demo/mcp-server/target/mcp-server-0.0.1-SNAPSHOT.jar --stdio
```

## Option 2: Fix the McpSyncClient Bean (Recommended for Production)

The root issue is that Spring AI 1.1.0-M2's autoconfiguration isn't creating the `McpSyncClient` bean.

### Quick Test Without Inspector

Since the MCP server is running with 20 tools, but you can't call them without a working client, here's what you need to do:

1. **The MCP server IS working** - it has registered all 20 tools
2. **The MCP client/host are NOT working** - the `McpSyncClient` bean isn't being created
3. **You cannot test via HTTP/curl** - MCP protocol requires proper client initialization

### What the Tools Do (Reference)

Your MCP server has these working tools ready to be called once the client is fixed:

**Calculator**: add, subtract, multiply, divide, power, sqrt  
**Time**: getCurrentTime  
**Files**: readFile, writeFile, appendToFile, listFiles, deleteFile, getFileInfo, getWorkspacePath  
**Storage**: store, retrieve, delete, listKeys, clear, count  

## Next Steps

To actually USE these tools, you need to fix the `McpSyncClient` bean creation issue in Spring AI 1.1.0-M2.

Possible solutions:
1. Downgrade to a stable Spring AI version
2. Use STDIO transport instead of SSE
3. Wait for Spring AI 1.1.0 final release
4. Manually configure the MCP client (we tried this, didn't work)
