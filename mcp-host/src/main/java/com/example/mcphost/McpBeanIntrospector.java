
package com.example.mcphost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Introspects Spring beans to find MCP-related beans
 */
@Component
public class McpBeanIntrospector implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(McpBeanIntrospector.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private io.modelcontextprotocol.client.McpSyncClient mcpSyncClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("========== Inspecting MCP-related beans in Host ==========");
        logger.info("Directly injected McpSyncClient: {}", mcpSyncClient);

        String[] allBeans = applicationContext.getBeanDefinitionNames();

        logger.info("Looking for MCP-related beans...");
        Arrays.stream(allBeans)
                .filter(name -> name.toLowerCase().contains("mcp") ||
                               name.toLowerCase().contains("sync") ||
                               name.toLowerCase().contains("client"))
                .forEach(name -> {
                    try {
                        Object bean = applicationContext.getBean(name);
                        logger.info("Found bean: {} -> {}", name, bean.getClass().getName());

                        // If it's a list, show its size
                        if (bean instanceof java.util.List) {
                            logger.info("  └─ List size: {}", ((java.util.List<?>) bean).size());
                        }
                    } catch (Exception e) {
                        logger.warn("Could not get bean: {}", name);
                    }
                });

        logger.info("========== End of MCP bean inspection ==========");
    }
}
