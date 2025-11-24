# Deploying Spring AI MCP Demo to Render.com

This guide walks you through deploying the Spring AI MCP Demo to Render.com using the `render.yaml` blueprint.

## Prerequisites

1. **GitHub Repository**: Your code must be in a GitHub repository
2. **Render.com Account**: Sign up at [https://render.com](https://render.com)
3. **OpenAI API Key**: Get one from [https://platform.openai.com](https://platform.openai.com)

## Architecture on Render

The deployment consists of 2 services:

```
┌─────────────────────┐         ┌──────────────────────┐
│  MCP Server         │◄────────│  MCP Host (OpenAI)   │
│  Port: 8080         │   SSE   │  Port: 8082          │
│  Free Tier          │         │  Free Tier           │
└─────────────────────┘         └──────────────────────┘
```

## Step-by-Step Deployment

### Step 1: Push Your Code to GitHub

```bash
# Make sure all changes are committed
git status

# Add new files
git add render.yaml mcp-server/Dockerfile mcp-host/Dockerfile DEPLOY-RENDER.md

# Commit
git commit -m "Add Render deployment configuration

- Created render.yaml for automatic deployment
- Added Dockerfiles for mcp-server and mcp-host
- Added deployment documentation

🤖 Generated with Claude Code

Co-Authored-By: Claude <noreply@anthropic.com>"

# Push to GitHub
git push origin development  # or main
```

### Step 2: Connect to Render

1. Go to [https://dashboard.render.com](https://dashboard.render.com)
2. Click **"New"** → **"Blueprint"**
3. Connect your GitHub repository
4. Select your repository: `sandipanchakraborty2016/spring-ai-mcp-demo`
5. Choose branch: `development` (or `main`)
6. Render will automatically detect the `render.yaml` file

### Step 3: Configure Environment Variables

Before deploying, you need to set the OpenAI API key:

1. In the Render dashboard, find the **mcp-host** service
2. Go to **"Environment"** tab
3. Add the environment variable:
   - **Key**: `SPRING_AI_OPENAI_API_KEY`
   - **Value**: Your OpenAI API key (starts with `sk-proj-...`)
4. Click **"Save Changes"**

### Step 4: Deploy

1. Click **"Apply"** or **"Create Resources"**
2. Render will:
   - Build Docker images for both services
   - Deploy MCP Server first (provides tools)
   - Deploy MCP Host (connects to Server via SSE)
3. Wait 5-10 minutes for deployment to complete

## Accessing Your Deployed Application

Once deployed, you'll get URLs like:

- **MCP Server**: `https://spring-ai-mcp-server.onrender.com`
- **MCP Host**: `https://spring-ai-mcp-host.onrender.com`

### Test Your Deployment

```bash
# Replace with your actual Render URLs
MCP_SERVER_URL="https://spring-ai-mcp-server.onrender.com"
MCP_HOST_URL="https://spring-ai-mcp-host.onrender.com"

# Test MCP Server health
curl "$MCP_SERVER_URL/health"

# Test MCP Host status
curl "$MCP_HOST_URL/api/status"

# Test ChatModel with MCP tools
curl "$MCP_HOST_URL/api/chat?query=What+is+25+times+4"

# Test current time
curl "$MCP_HOST_URL/api/chat?query=What+time+is+it"

# List available MCP tools
curl "$MCP_HOST_URL/api/mcp-tools"
```

## Configuration Details

### render.yaml Explained

```yaml
services:
  # MCP Server - Provides tools via MCP protocol
  - type: web
    name: spring-ai-mcp-server
    runtime: docker
    plan: free                    # Free tier
    region: oregon                # Choose closest region
    healthCheckPath: /health      # Health endpoint
    autoDeploy: true             # Auto-deploy on push
    
  # MCP Host - AI chat with OpenAI + MCP tools
  - type: web
    name: spring-ai-mcp-host
    runtime: docker
    plan: free
    healthCheckPath: /api/status
    envVars:
      # Automatically connects to MCP Server
      - key: SPRING_AI_MCP_CLIENT_SSE_BASE_URL
        fromService:
          name: spring-ai-mcp-server
          type: web
          property: host          # Gets Server's URL
```

## Free Tier Limitations

Render's free tier includes:

- ✅ **750 hours/month** per service (enough for 24/7 operation)
- ✅ **Automatic HTTPS**
- ✅ **Custom domains** supported
- ⚠️ **Services spin down after 15 minutes** of inactivity
- ⚠️ **First request after spin-down** may take 30-60 seconds

### Handling Cold Starts

To keep services warm, you can:

1. Use a cron job to ping the health endpoint every 10 minutes:
   ```bash
   */10 * * * * curl https://your-app.onrender.com/health
   ```

2. Or upgrade to a paid plan ($7/month per service) for always-on instances

## Monitoring

### View Logs

1. Go to Render Dashboard
2. Select your service
3. Click on **"Logs"** tab
4. Monitor real-time logs for errors or MCP tool calls

### Check Service Metrics

- **Events**: See deployment history
- **Metrics**: CPU, memory usage
- **Shell**: SSH into running container

## Troubleshooting

### Issue: MCP Host can't connect to Server

**Solution**: Check environment variables
```bash
# In mcp-host service, verify this is set:
SPRING_AI_MCP_CLIENT_SSE_BASE_URL=<mcp-server-url>
```

### Issue: OpenAI API key not working

**Solution**: Verify the key
1. Check the environment variable is set correctly
2. Ensure no extra spaces or quotes
3. Verify key is active on OpenAI platform

### Issue: Service won't start

**Solution**: Check logs
1. Look for Java errors in deployment logs
2. Common issues:
   - Port already in use (shouldn't happen on Render)
   - Missing dependencies (check Dockerfile)
   - Invalid configuration (check application.yaml)

### Issue: ChatModel not calling MCP tools

**Solution**: Verify MCP connection
```bash
# Test if MCP Server is reachable from Host
curl "$MCP_HOST_URL/api/mcp-tools"
# Should return list of 20 tools
```

## Updating Your Deployment

### Option 1: Automatic (Recommended)

With `autoDeploy: true`, every push to your branch triggers a deployment:

```bash
# Make changes
git add .
git commit -m "Update feature"
git push origin development

# Render automatically deploys
```

### Option 2: Manual

1. Go to Render Dashboard
2. Select service
3. Click **"Manual Deploy"** → **"Deploy latest commit"**

## Cost Estimation

### Free Tier (Current Configuration)

- **MCP Server**: $0/month (free tier)
- **MCP Host**: $0/month (free tier)
- **Total**: $0/month

### Paid Tier (Always-On)

- **MCP Server**: $7/month (Starter plan)
- **MCP Host**: $7/month (Starter plan)
- **Total**: $14/month

## Advanced Configuration

### Custom Domain

1. Go to **Settings** → **Custom Domain**
2. Add your domain (e.g., `mcp.yourdomain.com`)
3. Update DNS records as instructed
4. Wait for SSL certificate provisioning

### Environment-Specific Configuration

Create different branches for different environments:

- `main` → Production
- `development` → Staging
- `feature/*` → Feature testing

Each can have its own Render blueprint.

### Database Integration

If you need persistent storage:

1. Add PostgreSQL service to `render.yaml`:
   ```yaml
   databases:
     - name: mcp-postgres
       plan: free
   ```

2. Connect to database in Spring:
   ```properties
   spring.datasource.url=${DATABASE_URL}
   ```

## Security Best Practices

1. **Never commit API keys** to Git
2. **Use environment variables** for secrets
3. **Rotate OpenAI API keys** regularly
4. **Monitor usage** on OpenAI dashboard
5. **Enable rate limiting** in production

## Support

- **Render Docs**: https://render.com/docs
- **Render Community**: https://community.render.com
- **Spring AI Docs**: https://docs.spring.io/spring-ai/reference/
- **Project Issues**: https://github.com/sandipanchakraborty2016/spring-ai-mcp-demo/issues

## Next Steps

After successful deployment:

1. ✅ Test all endpoints (see [TESTING.md](TESTING.md))
2. ✅ Monitor logs for errors
3. ✅ Set up monitoring/alerting
4. ✅ Configure custom domain (optional)
5. ✅ Share your demo URL!

---

**Deployed Successfully?** 🎉

Test it live:
```bash
curl "https://spring-ai-mcp-host.onrender.com/api/chat?query=Hello+MCP"
```
