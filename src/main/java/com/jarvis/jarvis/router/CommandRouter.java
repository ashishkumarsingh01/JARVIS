package com.jarvis.jarvis.router;

import com.jarvis.jarvis.tools.PhoneTools;
import com.jarvis.jarvis.tools.SystemTools;
import com.jarvis.jarvis.memory.MemoryTools;
import org.springframework.stereotype.Component;

@Component
public class CommandRouter {

    private final SystemTools systemTools;
    private final PhoneTools phoneTools;
    private final MemoryTools memoryTools;

    public CommandRouter(
            SystemTools systemTools,
            PhoneTools phoneTools,
            MemoryTools memoryTools) {

        this.systemTools = systemTools;
        this.phoneTools = phoneTools;
        this.memoryTools = memoryTools;
    }

    public String route(String message) {

        if (message == null || message.isBlank()) {
            return "How may I assist you, sir?";
        }

        String command = message.trim().toLowerCase();

        // =========================
        // GREETINGS
        // =========================

        if (command.matches(
                "^(hi|hello|hey|hello jarvis|hi jarvis|hey jarvis)[.! ]*$")) {

            return "Hello, sir. JARVIS is online. How may I assist you?";
        }

        // =========================
        // TIME
        // =========================

        if (command.matches(
                "^(what time is it|what's the time|current time|tell me the time)[?!. ]*$")) {

            return "The current time is "
                    + systemTools.getCurrentTime()
                    + ", sir.";
        }

        // =========================
        // CHECK BATTERY + REMEMBER (must come before generic BATTERY rule)
        // =========================

        if (command.matches(".*battery.*remember.*") ||
            command.matches(".*remember.*battery.*")) {

            String battery = systemTools.getBatteryStatus();
            memoryTools.remember("Checked battery status: " + battery);
            return battery + " I've noted that you checked it, sir.";
        }

        // =========================
        // BATTERY
        // =========================

        if (command.matches(
                ".*\\b(battery|charging|charge)\\b.*")) {

            return systemTools.getBatteryStatus();
        }

        // =========================
        // OPEN YOUTUBE
        // =========================

        if (command.matches(
                "^(open youtube|start youtube|launch youtube)[?!. ]*$")) {

            return phoneTools.openApp("youtube");
        }

        // =========================
        // OPEN CHROME
        // =========================

        if (command.matches(
                "^(open chrome|start chrome|launch chrome)[?!. ]*$")) {

            return phoneTools.openApp("chrome");
        }

        // =========================
        // OPEN TERMUX
        // =========================

        if (command.matches(
                "^(open termux|start termux|launch termux)[?!. ]*$")) {

            return phoneTools.openApp("termux");
        }

        // Not a fast command.
        return null;
    }
}