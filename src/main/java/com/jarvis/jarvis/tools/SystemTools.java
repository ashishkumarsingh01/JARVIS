package com.jarvis.jarvis.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

    @Tool(description = "Get the current Android battery percentage and charging status.")
    public String getBatteryStatus() {

        try {

            Process process = new ProcessBuilder(
                    "/data/data/com.termux/files/usr/bin/termux-battery-status"
            )
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            StringBuilder output = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            process.waitFor();

            String json = output.toString();

            if (json.isBlank()) {
                return "Unable to read battery status.";
            }

            // Extract battery percentage
            String percentage = extractJsonValue(
                    json,
                    "percentage"
            );

            // Extract charging status
            String status = extractJsonValue(
                    json,
                    "status"
            );

            // Extract plugged state
            String plugged = extractJsonValue(
                    json,
                    "plugged"
            );

            if (percentage == null) {
                return "Unable to determine the battery percentage.";
            }

            return "Battery is at "
                    + percentage
                    + "% and is currently "
                    + status.toLowerCase()
                    + ". Plugged status: "
                    + plugged.toLowerCase()
                    + ".";

        } catch (Exception e) {

            return "Unable to read battery status: "
                    + e.getMessage();
        }
    }

    private String extractJsonValue(
            String json,
            String key) {

        String search = "\"" + key + "\":";

        int start = json.indexOf(search);

        if (start == -1) {
            return null;
        }

        start += search.length();

        while (
                start < json.length()
                        && Character.isWhitespace(json.charAt(start))
        ) {
            start++;
        }

        if (
                start < json.length()
                        && json.charAt(start) == '"'
        ) {

            start++;

            int end = json.indexOf(
                    '"',
                    start
            );

            if (end == -1) {
                return null;
            }

            return json.substring(
                    start,
                    end
            );
        }

        int end = start;

        while (
                end < json.length()
                        && json.charAt(end) != ','
                        && json.charAt(end) != '}'
        ) {
            end++;
        }

        return json.substring(
                start,
                end
        ).trim();
    }
}