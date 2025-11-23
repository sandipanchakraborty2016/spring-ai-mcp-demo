#!/bin/bash
# Clean target directories that may be owned by root from Docker builds

echo "Cleaning target directories..."
sudo rm -rf mcp-server/target mcp-client/target mcp-host/target mcp-host-claude/target

if [ $? -eq 0 ]; then
    echo "✓ Target directories cleaned successfully"
    echo "You can now run: mvn clean package"
else
    echo "✗ Failed to clean target directories"
    echo "Try running manually: sudo rm -rf */target"
fi
