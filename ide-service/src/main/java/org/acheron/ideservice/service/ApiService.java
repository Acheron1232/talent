package org.acheron.ideservice.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ApiService {
    @Value("${x-rapidapi-key}")
    private String key;
    @Value("${x-rapidapi-host}")
    private String host;

    public String execute(String code,String stdin){
        RestClient restClient = RestClient.create();
        String codeBase64 = Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8));
        String stdinBase64 = Base64.getEncoder().encodeToString(stdin.getBytes(StandardCharsets.UTF_8));

        TokenDto tokenResponse = restClient
                .post()
                .uri("https://ce.judge0.com/submissions/?base64_encoded=true&wait=true")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RequestDto(codeBase64, 50, stdinBase64))
                .header("x-rapidapi-key", key)
                .header("x-rapidapi-host", host)
                .retrieve()
                .toEntity(TokenDto.class)
                .getBody();
        if (tokenResponse == null) {
            throw new RuntimeException("token response is null");
        }
        ResponseDto resultResponse = restClient
                .get()
                .uri(
                        String
                                .format("https://ce.judge0.com/submissions/%s?base64_encoded=true&fields=stdout,status_id,language_id",
                                        tokenResponse.token))
                .header("x-rapidapi-key", "3549719eebmshf1bd4ecf94649bap18e11djsn5ebb49a4af38")
                .header("x-rapidapi-host", "judge0-ce.p.rapidapi.com")
                .retrieve()
                .toEntity(ResponseDto.class)
                .getBody();

        if (resultResponse == null) {
            throw new RuntimeException("result response is null");
        }

        String stdoutDecoded = null;
        if (resultResponse.stdout != null) {
            stdoutDecoded = new String(Base64.getDecoder().decode(resultResponse.stdout), StandardCharsets.UTF_8);
        }

        return stdoutDecoded != null ? stdoutDecoded : "No output";
    }

    public record RequestDto(
            @JsonProperty("source_code") String sourceCode,
            @JsonProperty("language_id") Integer languageId,
            String stdin
    ) {
    }

    public record TokenDto(
            String token
    ) {
    }

    public record ResponseDto(

            @JsonProperty("stdout") String stdout,
            Integer status_id,
            Integer language_id
    ) {
    }
}
