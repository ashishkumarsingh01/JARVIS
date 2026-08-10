package com.jarvis.jarvis.memory;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class MemoryTools {

    private final MemoryRepository repository;

    public MemoryTools(MemoryRepository repository) {
        this.repository = repository;
    }

    @Tool(description = "Save an important fact, preference, or reminder about the user for future reference.")
    public String remember(String fact) {
        repository.save(new Memory(fact));
        return "Noted, sir. I'll remember that.";
    }
}