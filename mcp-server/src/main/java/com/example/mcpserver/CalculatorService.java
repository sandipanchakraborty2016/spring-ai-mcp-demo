package com.example.mcpserver;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

/**
 * Calculator service providing basic mathematical operations.
 * Methods annotated with @McpTool are automatically exposed as MCP tools.
 */
@Service
public class CalculatorService {

    @McpTool(name = "add", description = "Add two numbers together")
    public double add(double a, double b) {
        return a + b;
    }

    @McpTool(name = "subtract", description = "Subtract b from a")
    public double subtract(double a, double b) {
        return a - b;
    }

    @McpTool(name = "multiply", description = "Multiply two numbers")
    public double multiply(double a, double b) {
        return a * b;
    }

    @McpTool(name = "divide", description = "Divide a by b. Returns error if b is zero")
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    @McpTool(name = "power", description = "Calculate base raised to the power of exponent")
    public double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    @McpTool(name = "sqrt", description = "Calculate the square root of a number. Returns error if number is negative")
    public double sqrt(double number) {
        if (number < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number");
        }
        return Math.sqrt(number);
    }
}
