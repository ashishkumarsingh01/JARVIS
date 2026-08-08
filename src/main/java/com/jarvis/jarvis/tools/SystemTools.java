package com.jarvis.jarvis.tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class SystemTools {

    @Tool(description = "Get the current date and time.")
    public String getCurrentTime() {

        LocalDateTime now = LocalDateTime.now();

        return now.format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a")
        );
    }
}