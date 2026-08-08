package com.jarvis.jarvis.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class PhoneTools {

    @Tool(description = "Open an allowed Android application by name. Allowed apps: YouTube, Chrome, Termux.")
    public String openApp(String appName) {

        String app = appName.toLowerCase().trim();

        switch (app) {

            case "youtube":
                return runCommand(
                        "am", "start",
                        "-a", "android.intent.action.VIEW",
                        "-d", "https://www.youtube.com"
                );

            case "chrome":
                return runCommand(
                        "am", "start",
                        "-n", "com.android.chrome/com.google.android.apps.chrome.Main"
                );

            case "termux":
                return runCommand(
                        "am", "start",
                        "-n", "com.termux/.app.TermuxActivity"
                );

            default:
                return "Sorry sir, that application is not currently allowed.";
        }
    }

    private String runCommand(String... command) {

        try {

            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "Application opened successfully.";
            }

            return "I could not open the application.";

        } catch (Exception e) {
            return "Unable to open the application: " + e.getMessage();
        }
    }
}