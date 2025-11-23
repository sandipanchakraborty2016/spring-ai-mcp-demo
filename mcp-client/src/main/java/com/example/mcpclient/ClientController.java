package com.example.mcpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller demonstrating MCP client capabilities.
 * Provides endpoints to interact with all MCP server tools.
 */
@RestController
@RequestMapping("/api")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired(required = false)
    private McpClientService mcpClientService;

    /**
     * Get client status.
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "mcp-client");
        response.put("status", "UP");
        response.put("message", "MCP Client is running with Spring AI 1.1.0-M2");
        return response;
    }

    /**
     * Example endpoint - demonstrates client capability.
     */
    @GetMapping("/demo")
    public Map<String, Object> demo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "MCP Client Demo");
        response.put("description", "Connect to MCP Server at http://localhost:8080");
        response.put("server_endpoint", "http://localhost:8080/mcp/message");
        return response;
    }

    /**
     * List available MCP tools
     */
    @GetMapping("/tools")
    public Map<String, Object> listTools() {
        return mcpClientService.listTools();
    }

    // ==================== TIME SERVICE ====================

    @GetMapping("/time")
    public Map<String, Object> getCurrentTime() {
        return mcpClientService.getCurrentTime();
    }

    // ==================== CALCULATOR SERVICE ====================

    @GetMapping("/calculator/add")
    public Map<String, Object> add(@RequestParam double a, @RequestParam double b) {
        return mcpClientService.add(a, b);
    }

    @GetMapping("/calculator/subtract")
    public Map<String, Object> subtract(@RequestParam double a, @RequestParam double b) {
        return mcpClientService.subtract(a, b);
    }

    @GetMapping("/calculator/multiply")
    public Map<String, Object> multiply(@RequestParam double a, @RequestParam double b) {
        return mcpClientService.multiply(a, b);
    }

    @GetMapping("/calculator/divide")
    public Map<String, Object> divide(@RequestParam double a, @RequestParam double b) {
        return mcpClientService.divide(a, b);
    }

    @GetMapping("/calculator/power")
    public Map<String, Object> power(@RequestParam double base, @RequestParam double exponent) {
        return mcpClientService.power(base, exponent);
    }

    @GetMapping("/calculator/sqrt")
    public Map<String, Object> sqrt(@RequestParam double number) {
        return mcpClientService.sqrt(number);
    }

    // ==================== FILE SERVICE ====================

    @GetMapping("/files/list")
    public Map<String, Object> listFiles() {
        return mcpClientService.listFiles();
    }

    @PostMapping("/files/write")
    public Map<String, Object> writeFile(@RequestParam String filename, @RequestParam String content) {
        return mcpClientService.writeFile(filename, content);
    }

    @GetMapping("/files/read")
    public Map<String, Object> readFile(@RequestParam String filename) {
        return mcpClientService.readFile(filename);
    }

    @PostMapping("/files/append")
    public Map<String, Object> appendToFile(@RequestParam String filename, @RequestParam String content) {
        return mcpClientService.appendToFile(filename, content);
    }

    @GetMapping("/files/info")
    public Map<String, Object> getFileInfo(@RequestParam String filename) {
        return mcpClientService.getFileInfo(filename);
    }

    @DeleteMapping("/files/delete")
    public Map<String, Object> deleteFile(@RequestParam String filename) {
        return mcpClientService.deleteFile(filename);
    }

    @GetMapping("/files/workspace")
    public Map<String, Object> getWorkspacePath() {
        return mcpClientService.getWorkspacePath();
    }

    // ==================== DATA STORAGE SERVICE ====================

    @PostMapping("/storage/store")
    public Map<String, Object> storeData(@RequestParam String key, @RequestParam String value) {
        return mcpClientService.storeData(key, value);
    }

    @GetMapping("/storage/retrieve")
    public Map<String, Object> retrieveData(@RequestParam String key) {
        return mcpClientService.retrieveData(key);
    }

    @GetMapping("/storage/list")
    public Map<String, Object> listKeys() {
        return mcpClientService.listKeys();
    }

    @DeleteMapping("/storage/delete")
    public Map<String, Object> deleteData(@RequestParam String key) {
        return mcpClientService.deleteData(key);
    }

    @DeleteMapping("/storage/clear")
    public Map<String, Object> clearStorage() {
        return mcpClientService.clearStorage();
    }

    @GetMapping("/storage/count")
    public Map<String, Object> countEntries() {
        return mcpClientService.countEntries();
    }
}
