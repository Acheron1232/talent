package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.dto.LikeDTO;
import com.mykyda.talantsocials.dto.create.LikeCreationDTO;
import com.mykyda.talantsocials.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/get-who-liked/{postId}/")
    public List<LikeDTO> getLikesPageByPostId(@PathVariable("postId") UUID postId,
                                              @RequestParam("page") Integer page,
                                              @RequestParam("size") Integer size) {
        return likeService.getLikesForPostPaged(postId, PageRequest.of(page, size));
    }

    @PostMapping("/like")
    public ResponseEntity<String> like(@RequestBody LikeCreationDTO likeDTO,
                                       @AuthenticationPrincipal Jwt jwt) {
        likeService.createLike(Long.valueOf(jwt.getClaims().get("id").toString()), likeDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<String> unlike(@RequestBody LikeCreationDTO likeDTO,
                                         @AuthenticationPrincipal Jwt jwt) {
        likeService.deleteLike(Long.valueOf(jwt.getClaims().get("id").toString()), likeDTO);
        return ResponseEntity.noContent().build();
    }
}
