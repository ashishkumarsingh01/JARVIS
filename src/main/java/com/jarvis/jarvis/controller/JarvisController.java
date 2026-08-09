package com.jarvis.jarvis.controller;

import com.jarvis.jarvis.tools.SystemTools;
import com.jarvis.jarvis.tools.PhoneTools;
import com.jarvis.jarvis.tools.WebTools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://jarvis-frontend-mrjd.onrender.com"
})
@RestController
public class JarvisController {

    private final ChatClient chatClient;
    private final SystemTools systemTools;
    private final PhoneTools phoneTools;
    private final WebTools webTools;

    public JarvisController(
            ChatClient.Builder builder,
            SystemTools systemTools,
            PhoneTools phoneTools,
            WebTools webTools) {

        this.chatClient = builder.build();
        this.systemTools = systemTools;
        this.phoneTools = phoneTools;
        this.webTools = webTools;
    }

    @GetMapping("/ai")
    public String askJarvis(@RequestParam String message) {

        return chatClient.prompt()
                .system("""
                        You are JARVIS, my personal AI assistant.

                        Always identify yourself as JARVIS.
                        Never identify yourself as Claude, ChatGPT, Gemini,
                        Qwen, Alibaba Cloud, or another AI.

                        Address me as sir.

                        Be intelligent, calm, concise and professional.

                        Use available tools when they are useful.

                        Available capabilities:
                        - Get the current system time.
                        - Open allowed Android applications.
                        - Search the web.

                        Do not claim that you performed an action
                        unless the corresponding tool successfully
                        performed it.
                        """)
                .user(message)
                .tools(
                        systemTools,
                        phoneTools,
                        webTools
                )
                .call()
                .content();
    }
}