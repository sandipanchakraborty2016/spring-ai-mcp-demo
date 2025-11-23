#!/bin/bash
# Startup script for MCP Host with OpenAI

# Load .env file if it exists
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    export $(cat .env | grep -v '^#' | xargs)
fi

# Check if API key is set
if [ -z "$SPRING_AI_OPENAI_API_KEY" ]; then
    echo "⚠️  Warning: SPRING_AI_OPENAI_API_KEY is not set"
    echo "Please create a .env file with:"
    echo "SPRING_AI_OPENAI_API_KEY=your-key-here"
fi

cd mcp-host
echo "Starting MCP Host (OpenAI) on port 8082..."
java -jar target/mcp-host-0.0.1-SNAPSHOT.jar
