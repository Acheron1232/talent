package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.database.repository.ShortRepository;
import com.mykyda.talantsocials.service.ShortService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shorts")
public class ShortApi {
    private final ShortService shortService;

    @GetMapping
    public ResponseEntity<List<Short>> getShorts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name", name = "sort_by") String sortBy,
            @RequestParam(defaultValue = "asc", name = "sort_dir") String sortDir,
            @AuthenticationPrincipal Jwt jwt
    ) {
        PageRequest pageable = ApiUtil.pageable(page, size, sortBy, sortDir);
        return shortService.findAll(pageable,jwt.getClaims().get("id"));
    }


}
