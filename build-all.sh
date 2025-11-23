#!/bin/bash
# Build all Maven projects using Docker (includes JDK)

set -e

echo "Building all projects using Docker..."
echo "This ensures consistent build environment with JDK 21"
echo ""

echo "Building mcp-server..."
docker run --rm -v "$PWD":/app -w /app/mcp-server maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests

echo ""
echo "Building mcp-client..."
docker run --rm -v "$PWD":/app -w /app/mcp-client maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests

echo ""
echo "Building mcp-host..."
docker run --rm -v "$PWD":/app -w /app/mcp-host maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests

echo ""
echo "Building mcp-host-claude..."
docker run --rm -v "$PWD":/app -w /app/mcp-host-claude maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests

echo ""
echo "✓ All projects built successfully!"
echo ""
echo "JARs created:"
echo "  - mcp-server/target/mcp-server-0.0.1-SNAPSHOT.jar"
echo "  - mcp-client/target/mcp-client-0.0.1-SNAPSHOT.jar"
echo "  - mcp-host/target/mcp-host-0.0.1-SNAPSHOT.jar"
echo "  - mcp-host-claude/target/mcp-host-claude-0.0.1-SNAPSHOT.jar"
