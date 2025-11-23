# Deployment Guide

This guide provides step-by-step instructions for deploying the Spring AI MCP Demo to various cloud platforms.

## 📋 Table of Contents

- [Render.com Deployment (Recommended)](#rendercom-deployment)
- [Railway.app Deployment](#railwayapp-deployment)
- [Fly.io Deployment](#flyio-deployment)
- [Manual Docker Deployment](#manual-docker-deployment)
- [Environment Variables](#environment-variables)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Render.com Deployment

Render.com offers a free tier perfect for demo applications with automatic HTTPS and easy Docker deployment.

### Prerequisites
- GitHub account
- Render.com account (free tier available)

### Step 1: Push to GitHub

1. Initialize git repository (if not already done):
   ```bash
   git init
   git add .
   git commit -m "Initial commit - Spring AI MCP Demo"
   ```

2. Create a new repository on GitHub and push:
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/spring-ai-mcp-demo.git
   git branch -M main
   git push -u origin main
   ```

### Step 2: Deploy MCP Server

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository
4. Configure the service:
   - **Name**: `spring-ai-mcp-server`
   - **Region**: Choose closest to you (e.g., Oregon)
   - **Branch**: `main`
   - **Root Directory**: `mcp-server`
   - **Runtime**: `Docker`
   - **Instance Type**: `Free`

5. Add Environment Variables:
   ```
   SPRING_AI_MCP_SERVER_TRANSPORT=sse
   SPRING_AI_MCP_SERVER_SSE_MESSAGE_ENDPOINT=/mcp/message
   ```

6. Advanced Settings:
   - **Health Check Path**: `/health`
   - **Docker Command**: (leave default)

7. Click **"Create Web Service"**

8. Wait for deployment (5-10 minutes). Note the URL: `https://spring-ai-mcp-server.onrender.com`

### Step 3: Deploy MCP Client

1. Click **"New +"** → **"Web Service"** again
2. Select the same repository
3. Configure the service:
   - **Name**: `spring-ai-mcp-client`
   - **Region**: Same as server
   - **Branch**: `main`
   - **Root Directory**: `mcp-client`
   - **Runtime**: `Docker`
   - **Instance Type**: `Free`

4. Add Environment Variables:
   ```
   SPRING_AI_MCP_CLIENT_TRANSPORT=sse
   SPRING_AI_MCP_CLIENT_SSE_BASE_URL=https://spring-ai-mcp-server.onrender.com
   SPRING_AI_MCP_CLIENT_SSE_MESSAGE_ENDPOINT=/mcp/message
   ```
   
   ⚠️ **Important**: Replace `spring-ai-mcp-server.onrender.com` with your actual server URL from Step 2.

5. Advanced Settings:
   - **Health Check Path**: `/health`

6. Click **"Create Web Service"**

### Step 4: Verify Deployment

1. Check server health:
   ```bash
   curl https://spring-ai-mcp-server.onrender.com/health
   ```

2. Check client health:
   ```bash
   curl https://spring-ai-mcp-client.onrender.com/health
   ```

3. Test a tool:
   ```bash
   curl https://spring-ai-mcp-client.onrender.com/api/time
   ```

### Important Notes for Render.com Free Tier

- **Cold Starts**: Free tier services spin down after 15 minutes of inactivity. First request after inactivity may take 30-60 seconds.
- **Build Time**: Initial build takes 5-10 minutes.
- **Automatic Deploys**: Render automatically redeploys on git push to main branch.

---

## 🚂 Railway.app Deployment

Railway offers $5 free credit per month and easy deployment.

### Step 1: Install Railway CLI

```bash
npm install -g @railway/cli
railway login
```

### Step 2: Deploy Server

```bash
cd mcp-server
railway init
railway up
```

Set environment variables:
```bash
railway variables set SPRING_AI_MCP_SERVER_TRANSPORT=sse
railway variables set SPRING_AI_MCP_SERVER_SSE_MESSAGE_ENDPOINT=/mcp/message
```

Get the server URL:
```bash
railway domain
```

### Step 3: Deploy Client

```bash
cd ../mcp-client
railway init
railway up
```

Set environment variables (replace SERVER_URL):
```bash
railway variables set SPRING_AI_MCP_CLIENT_TRANSPORT=sse
railway variables set SPRING_AI_MCP_CLIENT_SSE_BASE_URL=https://SERVER_URL
railway variables set SPRING_AI_MCP_CLIENT_SSE_MESSAGE_ENDPOINT=/mcp/message
```

---

## ✈️ Fly.io Deployment

Fly.io offers free tier with 3 shared-cpu VMs.

### Step 1: Install Fly CLI

```bash
# Windows (PowerShell)
iwr https://fly.io/install.ps1 -useb | iex

# macOS/Linux
curl -L https://fly.io/install.sh | sh
```

### Step 2: Login and Create Apps

```bash
fly auth login
```

### Step 3: Deploy Server

```bash
cd mcp-server
fly launch --name spring-ai-mcp-server --region ord --no-deploy

# Edit fly.toml to add environment variables
fly deploy
```

Add to `fly.toml`:
```toml
[env]
  SPRING_AI_MCP_SERVER_TRANSPORT = "sse"
  SPRING_AI_MCP_SERVER_SSE_MESSAGE_ENDPOINT = "/mcp/message"
```

### Step 4: Deploy Client

```bash
cd ../mcp-client
fly launch --name spring-ai-mcp-client --region ord --no-deploy
```

Add to `fly.toml`:
```toml
[env]
  SPRING_AI_MCP_CLIENT_TRANSPORT = "sse"
  SPRING_AI_MCP_CLIENT_SSE_BASE_URL = "https://spring-ai-mcp-server.fly.dev"
  SPRING_AI_MCP_CLIENT_SSE_MESSAGE_ENDPOINT = "/mcp/message"
```

Deploy:
```bash
fly deploy
```

---

## 🐳 Manual Docker Deployment

For any VPS or cloud VM:

### Step 1: Install Docker

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
```

### Step 2: Clone and Deploy

```bash
git clone https://github.com/YOUR_USERNAME/spring-ai-mcp-demo.git
cd spring-ai-mcp-demo
docker-compose up -d
```

### Step 3: Configure Reverse Proxy (Nginx)

```nginx
server {
    listen 80;
    server_name mcp-server.yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

server {
    listen 80;
    server_name mcp-client.yourdomain.com;

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 🔐 Environment Variables

### MCP Server Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_AI_MCP_SERVER_TRANSPORT` | Transport protocol | `sse` | Yes |
| `SPRING_AI_MCP_SERVER_SSE_MESSAGE_ENDPOINT` | SSE endpoint path | `/mcp/message` | Yes |
| `SERVER_PORT` | Server port | `8080` | No |

### MCP Client Variables

| Variable | Description | Example | Required |
|----------|-------------|---------|----------|
| `SPRING_AI_MCP_CLIENT_TRANSPORT` | Transport protocol | `sse` | Yes |
| `SPRING_AI_MCP_CLIENT_SSE_BASE_URL` | Server base URL | `http://localhost:8080` | Yes |
| `SPRING_AI_MCP_CLIENT_SSE_MESSAGE_ENDPOINT` | SSE endpoint path | `/mcp/message` | Yes |
| `SERVER_PORT` | Client port | `8081` | No |

---

## 🔧 Troubleshooting

### Issue: Client Cannot Connect to Server

**Symptoms**: Client health check shows `mcpServerConnected: false`

**Solutions**:
1. Verify server is running and accessible
2. Check `SPRING_AI_MCP_CLIENT_SSE_BASE_URL` is correct
3. Ensure server URL includes protocol (`http://` or `https://`)
4. Check firewall/security group settings

### Issue: 502 Bad Gateway on Render.com

**Symptoms**: Service shows as "Live" but returns 502 error

**Solutions**:
1. Check build logs for errors
2. Verify Dockerfile is correct
3. Ensure health check path `/health` is accessible
4. Wait for cold start (free tier spins down after inactivity)

### Issue: Docker Build Fails

**Symptoms**: `mvn dependency:go-offline` fails

**Solutions**:
1. Check internet connectivity
2. Verify Maven Central is accessible
3. Clear Maven cache: `docker-compose build --no-cache`

### Issue: Tools Not Appearing

**Symptoms**: `/api/tools` returns empty list

**Solutions**:
1. Check server logs for errors
2. Verify `McpConfig.java` has all `@Bean` methods
3. Ensure services are annotated with `@Service`
4. Restart both server and client

### Issue: High Memory Usage

**Symptoms**: Application crashes with OOM error

**Solutions**:
1. Add JVM memory limits to Dockerfile:
   ```dockerfile
   ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
   ```
2. Upgrade to paid tier with more resources

---

## 📊 Monitoring

### Health Check Endpoints

- Server: `https://your-server-url/health`
- Client: `https://your-client-url/health`

### Logs

**Render.com**: View logs in dashboard under "Logs" tab

**Railway**: `railway logs`

**Fly.io**: `fly logs`

**Docker**: `docker-compose logs -f`

---

## 🎉 Success Checklist

- [ ] Server health endpoint returns `status: UP`
- [ ] Client health endpoint shows `mcpServerConnected: true`
- [ ] `/api/tools` lists all 16 tools
- [ ] `/api/time` returns current timestamp
- [ ] Calculator endpoints work correctly
- [ ] Weather endpoints return mock data
- [ ] Storage endpoints can store/retrieve data

---

## 📞 Support

If you encounter issues not covered here:
1. Check application logs
2. Review environment variables
3. Verify network connectivity between services
4. Open an issue on GitHub with logs and error messages
