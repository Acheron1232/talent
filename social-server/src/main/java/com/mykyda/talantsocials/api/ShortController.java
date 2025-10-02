package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.database.entity.Short;
import com.mykyda.talantsocials.dto.create.ShortCreationDto;
import com.mykyda.talantsocials.service.ShortService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shorts")
public class ShortController {
    private final ShortService shortService;

    @GetMapping
    public List<Short> getShorts(
            @RequestParam(defaultValue = "5") Integer shorts_size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return shortService.findAll(shorts_size,(Long) jwt.getClaims().get("id"));
    }

    @PostMapping
    public void createShort(@AuthenticationPrincipal Jwt jwt, @RequestBody ShortCreationDto  shortCreationDto) {
        shortService.save(shortCreationDto,(Long) jwt.getClaims().get("id"));
    }


}
