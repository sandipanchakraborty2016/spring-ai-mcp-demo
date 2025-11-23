package com.example.mcpserver;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/**
 * In-memory key-value storage service.
 * Methods annotated with @McpTool are automatically exposed as MCP tools.
 */
@Service
public class DataStorageService {

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @McpTool(name = "store", description = "Store a value with the given key in memory")
    public String store(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        
        storage.put(key, value);
        return String.format("Stored value under key '%s'", key);
    }

    @McpTool(name = "retrieve", description = "Retrieve a value by key from memory storage")
    public String retrieve(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        
        String value = storage.get(key);
        if (value == null) {
            return String.format("No value found for key '%s'", key);
        }
        return value;
    }

    @McpTool(name = "delete", description = "Delete a value by key from memory storage")
    public String delete(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        
        String removed = storage.remove(key);
        if (removed == null) {
            return String.format("No value found for key '%s'", key);
        }
        return String.format("Deleted value for key '%s'", key);
    }

    @McpTool(name = "listKeys", description = "List all stored keys in memory")
    public String listKeys() {
        Set<String> keys = storage.keySet();
        if (keys.isEmpty()) {
            return "No keys stored";
        }
        return "Stored keys: " + String.join(", ", keys);
    }

    @McpTool(name = "clear", description = "Clear all stored data from memory")
    public String clear() {
        int size = storage.size();
        storage.clear();
        return String.format("Cleared %d entries from storage", size);
    }

    @McpTool(name = "count", description = "Get the count of stored entries in memory")
    public String count() {
        return String.format("Storage contains %d entries", storage.size());
    }
}
