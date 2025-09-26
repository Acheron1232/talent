package com.acheron.projectgeneratorservice.api;

import com.acheron.projectgeneratorservice.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequiredArgsConstructor
public class GeneratorApi {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @GetMapping
    public SseEmitter generate() {
        SseEmitter emitter = new SseEmitter();
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        executor.execute(() -> {
            try {
                restClient.post().uri("http://localhost:11434/api/generate")
                        .body(new AiService.AiRequest("qwen2.5-coder:7b", "write java server"))
                        .exchange((request, response) -> {
                            try (InputStream in = response.getBody()) {
                                byte[] buf = new byte[1024];
                                int len;
                                StringBuilder buffer = new StringBuilder();

                                while ((len = in.read(buf)) != -1) {
                                    buffer.append(new String(buf, 0, len, StandardCharsets.UTF_8));

                                    // Split buffer by newlines
                                    int newlineIndex;
                                    while ((newlineIndex = buffer.indexOf("\n")) != -1) {
                                        String line = buffer.substring(0, newlineIndex);
                                        buffer.delete(0, newlineIndex + 1);

                                        if (!line.isBlank()) {
                                            JsonNode json = objectMapper.readTree(line);
                                            if (json.has("response")) {
                                                String message = json.get("response").asText();
                                                // Split multi-line messages into lines
                                                for (String l : message.split("\n")) {
                                                    emitter.send(SseEmitter.event().data(l + "\n"));
                                                    System.out.print(l + "\n"); // optional console logging
                                                }
                                            }
                                            if (json.has("done") && json.get("done").asBoolean()) {
                                                emitter.complete();
                                            }
                                        }
                                    }
                                }

                                if (buffer.length() > 0) {
                                    JsonNode json = objectMapper.readTree(buffer.toString());
                                    if (json.has("response")) {
                                        String message = json.get("response").asText();
                                        emitter.send(SseEmitter.event().data(message));
                                        System.out.print(message);
                                    }
                                }
                            }
                            return null;
                        });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }


}
