package com.jarvis.jarvis.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class WebTools {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Tool(description = "Search the web for information using DuckDuckGo.")
    public String webSearch(String query) {

        try {
            String encodedQuery = URLEncoder.encode(
                    query,
                    StandardCharsets.UTF_8
            );

            String url = "https://html.duckduckgo.com/html/?q=" + encodedQuery;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                return "Web search failed. HTTP status: "
                        + response.statusCode();
            }

            return extractResults(response.body());

        } catch (Exception e) {
            return "Unable to search the web: " + e.getMessage();
        }
    }

    private String extractResults(String html) {

        StringBuilder results = new StringBuilder();

        String[] parts = html.split("result__a");

        int count = 0;

        for (String part : parts) {

            if (count >= 5) {
                break;
            }

            int titleStart = part.indexOf(">");

            if (titleStart == -1) {
                continue;
            }

            int titleEnd = part.indexOf("</a>");

            if (titleEnd == -1) {
                continue;
            }

            String title = part.substring(
                    titleStart + 1,
                    titleEnd
            );

            title = title.replaceAll("<[^>]*>", "")
                    .replace("&amp;", "&")
                    .replace("&quot;", "\"")
                    .trim();

            if (!title.isEmpty()) {
                results.append(count + 1)
                        .append(". ")
                        .append(title)
                        .append("\n");

                count++;
            }
        }

        if (results.isEmpty()) {
            return "No search results found.";
        }

        return results.toString();
    }
}