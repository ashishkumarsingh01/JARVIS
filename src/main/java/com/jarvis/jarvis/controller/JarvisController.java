package com.jarvis.jarvis.controller;

import com.jarvis.jarvis.router.CommandRouter;
import com.jarvis.jarvis.tools.SystemTools;
import com.jarvis.jarvis.tools.PhoneTools;
import com.jarvis.jarvis.tools.WebTools;
import com.jarvis.jarvis.memory.MemoryTools;
import com.jarvis.jarvis.memory.MemoryRepository;

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
    private final CommandRouter commandRouter;
    private final SystemTools systemTools;
    private final PhoneTools phoneTools;
    private final WebTools webTools;
    private final MemoryTools memoryTools;
    private final MemoryRepository memoryRepository;

    public JarvisController(
            ChatClient.Builder builder,
            CommandRouter commandRouter,
            SystemTools systemTools,
            PhoneTools phoneTools,
            WebTools webTools,
            MemoryTools memoryTools,
            MemoryRepository memoryRepository) {

        this.chatClient = builder.build();
        this.commandRouter = commandRouter;
        this.systemTools = systemTools;
        this.phoneTools = phoneTools;
        this.webTools = webTools;
        this.memoryTools = memoryTools;
        this.memoryRepository = memoryRepository;
    }

    @GetMapping("/ai")
    public String askJarvis(@RequestParam String message) {

        String fastResponse = commandRouter.route(message);

        if (fastResponse != null) {
            return fastResponse;
        }

        StringBuilder memoryBlock = new StringBuilder();
        memoryRepository.findTop20ByOrderByCreatedAtDesc()
                .forEach(m -> memoryBlock.append("- ")
                        .append(m.getContent())
                        .append("\n"));

        String systemPrompt = """
                You are JARVIS, my personal AI assistant.

                Always identify yourself as JARVIS.
                Never identify yourself as Claude, ChatGPT, Gemini,
                Qwen, Alibaba Cloud, or another AI.

                 Address me as sir.

Respond only in plain conversational text. Never use XML tags,
markdown formatting, or any wrapper tags like <sir> around your
response. Just speak naturally as JARVIS would.
          

                Be intelligent, calm, concise and professional.

                Known facts about the user (from memory):
                """
                + memoryBlock
                + """

                CRITICAL RULE: You do NOT have built-in knowledge of current
                events, news, or real-time information. You MUST call the
                webSearch tool whenever the user asks about news, current
                events, prices, weather, or anything time-sensitive. Never
                say you cannot access live information — you have a tool
                for that. Always use it instead of refusing.

                If the user tells you something worth remembering
                (a fact, preference, appointment, or instruction),
                call the remember tool to save it.

                Available capabilities:
                - Get the current system time.
                - Open allowed Android applications.
                - Search the web (ALWAYS use this for anything current/live).
                - Remember important facts about the user.

                Do not claim that you performed an action
                unless the corresponding tool successfully
                performed it.
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .tools(
                        systemTools,
                        phoneTools,
                        webTools,
                        memoryTools
                )
                .call()
                .content();
    }
}