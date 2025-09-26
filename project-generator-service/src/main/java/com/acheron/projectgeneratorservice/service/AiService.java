package com.acheron.projectgeneratorservice.service;

import com.acheron.projectgeneratorservice.header.AiSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class AiService implements AiSender {

    public String request(String prompt) {
//        restClient.post().uri("http://localhost:11434/api/generate")
//                .body(new AiRequest("qwen2.5-coder:7b","write java server"))
//                .exchange((request,response)->{
//                    try (InputStream in = response.getBody()) {
//                        byte[] buf = new byte[1024];
//                        int len;
//                        StringBuilder buffer = new StringBuilder();
//
//                        while ((len = in.read(buf)) != -1) {
//                            buffer.append(new String(buf, 0, len));
//
//                            int newline;
//                            while ((newline = buffer.indexOf("\n")) != -1) {
//                                String chunk = buffer.substring(0, newline).trim();
//                                buffer.delete(0, newline + 1);
//
//                                if (!chunk.isEmpty()) {
//                                    JsonNode json = objectMapper.readTree(chunk);
//                                    if (json.has("response")) {
//                                        System.out.print(json.get("response").asText());
//                                    }
//                                    if (json.has("done") && json.get("done").asBoolean()) {
//                                        System.out.println("\n--- done ---");
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    return null;
//                });
        return  null;
    }
    public record AiRequest(
            String model,
            String prompt
    ){}
    public record AiResponse(
            String model,
            LocalDateTime created_at,
            String response,
            String done
    ){}
}