# Quick Start Guide

## 🚀 Get Started in 3 Steps

### 1. Run Locally with Docker

```bash
# Clone and navigate to project
cd spring-ai-mcp-demo

# Start both services
docker-compose up --build

# Wait for services to start (about 2-3 minutes)
```

### 2. Test the Services

```bash
# Check health
curl http://localhost:8080/health  # Server
curl http://localhost:8081/health  # Client

# List available tools
curl http://localhost:8081/api/tools

# Try a calculator operation
curl "http://localhost:8081/api/calculator/add?a=10&b=5"
```

### 3. Deploy to Render.com (Free)

1. Push to GitHub
2. Go to [render.com](https://render.com) and sign up
3. Create two web services:
   - **Server**: Point to `mcp-server` directory
   - **Client**: Point to `mcp-client` directory
4. Set environment variables (see DEPLOYMENT.md)
5. Deploy!

## 📚 Full Documentation

- [README.md](README.md) - Complete project documentation
- [DEPLOYMENT.md](DEPLOYMENT.md) - Detailed deployment guide

## 🎯 Quick API Reference

### List Tools
```bash
GET http://localhost:8081/api/tools
```

### Time
```bash
GET http://localhost:8081/api/time
```

### Calculator
```bash
GET http://localhost:8081/api/calculator/add?a=X&b=Y
GET http://localhost:8081/api/calculator/subtract?a=X&b=Y
GET http://localhost:8081/api/calculator/multiply?a=X&b=Y
GET http://localhost:8081/api/calculator/divide?a=X&b=Y
GET http://localhost:8081/api/calculator/power?base=X&exponent=Y
GET http://localhost:8081/api/calculator/sqrt?number=X
```

### Files
```bash
GET  http://localhost:8081/api/files/list
POST http://localhost:8081/api/files/write?filename=FILE&content=CONTENT
GET  http://localhost:8081/api/files/read?filename=FILE
POST http://localhost:8081/api/files/append?filename=FILE&content=CONTENT
GET  http://localhost:8081/api/files/info?filename=FILE
DELETE http://localhost:8081/api/files/delete?filename=FILE
GET  http://localhost:8081/api/files/workspace
```

### Storage
```bash
POST http://localhost:8081/api/storage/store?key=KEY&value=VALUE
GET  http://localhost:8081/api/storage/retrieve?key=KEY
DELETE http://localhost:8081/api/storage/delete?key=KEY
GET  http://localhost:8081/api/storage/list
DELETE http://localhost:8081/api/storage/clear
GET  http://localhost:8081/api/storage/count
```

## 🔧 Troubleshooting

**Services won't start?**
- Check Docker is running
- Try `docker-compose down` then `docker-compose up --build`

**Client can't connect to server?**
- Verify server is running: `curl http://localhost:8080/health`
- Check docker-compose logs: `docker-compose logs mcp-server`

**Need help?**
- See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed troubleshooting
