package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.dto.response.FollowDTO;
import com.mykyda.talantsocials.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    @GetMapping("/get-follows/{profileId}")
    public List<FollowDTO> getFollowsPageByPostId(@PathVariable("profileId") Long profileId,
                                                  @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return followService.getFollows(profileId, PageRequest.of(page, size));
    }

    @GetMapping("/get-followed-by/{profileId}")
    public List<FollowDTO> getLikesPageByPostId(@PathVariable("profileId") Long profileId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return followService.getFollowedBy(profileId, PageRequest.of(page, size));
    }

    @PostMapping("/follow")
    public ResponseEntity<String> follow(@RequestBody Long followedId,
                                         @AuthenticationPrincipal Jwt jwt) {
        followService.follow(Long.valueOf(jwt.getClaims().get("id").toString()), followedId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<String> unfollow(@RequestBody Long unfollowedId,
                                           @AuthenticationPrincipal Jwt jwt) {
        followService.unfollow(Long.valueOf(jwt.getClaims().get("id").toString()), unfollowedId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-follow/{profileId}")
    public Map<String, Boolean> checkFollow(@PathVariable("profileId") Long profileId,
                                            @AuthenticationPrincipal Jwt jwt) {
        return Map.of("following", followService.isFollowed(Long.valueOf(jwt.getClaims().get("id").toString()), profileId));
    }
}
