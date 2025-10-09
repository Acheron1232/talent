package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.database.entity.Short;
import com.mykyda.talantsocials.dto.create.ShortCreationDto;
import com.mykyda.talantsocials.service.ShortService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shorts")
public class ShortController {
    private final ShortService shortService;

    @GetMapping
    public List<Short> getShorts(
            @RequestParam(defaultValue = "5") Integer shorts_size,
            @RequestParam(name = "exclude", required = false) List<UUID> exclude,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return shortService.findAllExcluding(shorts_size,(Long) jwt.getClaims().get("id"), exclude);
    }

    @PostMapping
    public void createShort(@AuthenticationPrincipal Jwt jwt, @RequestBody ShortCreationDto shortCreationDto) {
        shortService.save(shortCreationDto,(Long) jwt.getClaims().get("id"));
    }
}
