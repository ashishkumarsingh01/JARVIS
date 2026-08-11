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
                return runCommand("am", "start", "-a", "android.intent.action.VIEW", "-d", "https://www.youtube.com");
            case "chrome":
                return runCommand("am", "start", "-n", "com.android.chrome/com.google.android.apps.chrome.Main");
            case "termux":
                return runCommand("am", "start", "-n", "com.termux/.app.TermuxActivity");
            default:
                return "Sorry sir, that application is not currently allowed.";
        }
    }

    @Tool(description = "Read the current text content of the phone's clipboard.")
    public String getClipboard() {
        return runCommandOutput("termux-clipboard-get");
    }

    @Tool(description = "Copy the given text to the phone's clipboard.")
    public String setClipboard(String text) {
        try {
            Process process = new ProcessBuilder("termux-clipboard-set", text)
                    .redirectErrorStream(true)
                    .start();
            process.waitFor();
            return "Copied to clipboard, sir.";
        } catch (Exception e) {
            return "Unable to set clipboard: " + e.getMessage();
        }
    }

    @Tool(description = "Vibrate the phone briefly, useful for alerts or confirmations.")
    public String vibrate() {
        return runCommand("termux-vibrate", "-d", "300");
    }

    @Tool(description = "Turn the phone's flashlight/torch on or off. Pass 'on' or 'off'.")
    public String toggleFlashlight(String state) {
        String value = state.toLowerCase().trim().equals("on") ? "on" : "off";
        return runCommand("termux-torch", value);
    }

    @Tool(description = "Get the phone's current GPS location (latitude, longitude).")
    public String getLocation() {
        return runCommandOutput("termux-location", "-p", "gps", "-r", "once");
    }

    @Tool(description = "Send a short Android notification with the given title and message.")
    public String sendNotification(String title, String message) {
        try {
            Process process = new ProcessBuilder(
                    "termux-notification",
                    "--title", title,
                    "--content", message)
                    .redirectErrorStream(true)
                    .start();
            process.waitFor();
            return "Notification sent, sir.";
        } catch (Exception e) {
            return "Unable to send notification: " + e.getMessage();
        }
    }

    private String runCommand(String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            int exitCode = process.waitFor();
            return exitCode == 0 ? "Done, sir." : "I could not complete that action.";
        } catch (Exception e) {
            return "Unable to run command: " + e.getMessage();
        }
    }

    private String runCommandOutput(String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            String output = new String(process.getInputStream().readAllBytes());
            process.waitFor();

            return output.isBlank() ? "No data returned." : output.trim();
        } catch (Exception e) {
            return "Unable to run command: " + e.getMessage();
        }
    }
}