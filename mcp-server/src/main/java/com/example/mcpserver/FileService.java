package com.example.mcpserver;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File management service providing read and write operations.
 * Methods annotated with @McpTool are automatically exposed as MCP tools.
 * 
 * Files are stored in a workspace directory for safety.
 */
@Service
public class FileService {

    private final Path workspaceDir;

    public FileService() {
        // Create a workspace directory for file operations
        this.workspaceDir = Paths.get(System.getProperty("user.home"), ".mcp-demo-workspace");
        try {
            Files.createDirectories(workspaceDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create workspace directory", e);
        }
    }

    @McpTool(name = "readFile", description = "Read the contents of a file from the workspace")
    public String readFile(String filename) {
        try {
            Path filePath = workspaceDir.resolve(filename);
            if (!Files.exists(filePath)) {
                return String.format("File '%s' does not exist", filename);
            }
            String content = Files.readString(filePath);
            return String.format("Content of '%s':\n%s", filename, content);
        } catch (IOException e) {
            return String.format("Error reading file '%s': %s", filename, e.getMessage());
        }
    }

    @McpTool(name = "writeFile", description = "Write content to a file in the workspace (creates or overwrites)")
    public String writeFile(String filename, String content) {
        try {
            Path filePath = workspaceDir.resolve(filename);
            Files.writeString(filePath, content);
            return String.format("Successfully wrote %d characters to '%s'", content.length(), filename);
        } catch (IOException e) {
            return String.format("Error writing file '%s': %s", filename, e.getMessage());
        }
    }

    @McpTool(name = "appendToFile", description = "Append content to an existing file in the workspace")
    public String appendToFile(String filename, String content) {
        try {
            Path filePath = workspaceDir.resolve(filename);
            if (!Files.exists(filePath)) {
                return String.format("File '%s' does not exist. Use writeFile to create it first.", filename);
            }
            Files.writeString(filePath, content, StandardOpenOption.APPEND);
            return String.format("Successfully appended %d characters to '%s'", content.length(), filename);
        } catch (IOException e) {
            return String.format("Error appending to file '%s': %s", filename, e.getMessage());
        }
    }

    @McpTool(name = "listFiles", description = "List all files in the workspace")
    public String listFiles() {
        try (Stream<Path> paths = Files.list(workspaceDir)) {
            List<String> files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
            
            if (files.isEmpty()) {
                return "No files in workspace";
            }
            return "Files in workspace:\n" + String.join("\n", files);
        } catch (IOException e) {
            return "Error listing files: " + e.getMessage();
        }
    }

    @McpTool(name = "deleteFile", description = "Delete a file from the workspace")
    public String deleteFile(String filename) {
        try {
            Path filePath = workspaceDir.resolve(filename);
            if (!Files.exists(filePath)) {
                return String.format("File '%s' does not exist", filename);
            }
            Files.delete(filePath);
            return String.format("Successfully deleted '%s'", filename);
        } catch (IOException e) {
            return String.format("Error deleting file '%s': %s", filename, e.getMessage());
        }
    }

    @McpTool(name = "getFileInfo", description = "Get information about a file (size, last modified)")
    public String getFileInfo(String filename) {
        try {
            Path filePath = workspaceDir.resolve(filename);
            if (!Files.exists(filePath)) {
                return String.format("File '%s' does not exist", filename);
            }
            long size = Files.size(filePath);
            String lastModified = Files.getLastModifiedTime(filePath).toString();
            return String.format("File: %s\nSize: %d bytes\nLast Modified: %s", 
                    filename, size, lastModified);
        } catch (IOException e) {
            return String.format("Error getting file info '%s': %s", filename, e.getMessage());
        }
    }

    @McpTool(name = "getWorkspacePath", description = "Get the workspace directory path")
    public String getWorkspacePath() {
        return "Workspace directory: " + workspaceDir.toAbsolutePath().toString();
    }
}
