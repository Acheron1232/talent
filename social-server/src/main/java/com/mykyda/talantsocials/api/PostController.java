package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.dto.PostDTO;
import com.mykyda.talantsocials.dto.create.PostCreationDTO;
import com.mykyda.talantsocials.service.PostService;
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
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/explore-posts")
    private List<PostDTO> getExplorePage() {
        return postService.explore();
    }

    @GetMapping("/get-post/{postId}")
    private PostDTO getPostById(@PathVariable("postId") UUID postId) {
        return postService.findById(postId);
    }

    @GetMapping("/get-posts/{profileId}")
    public List<PostDTO> getPostsPageByProfileId(@PathVariable("profileId") Long profileId,
                                                 @RequestParam("page") Integer page,
                                                 @RequestParam("size") Integer size) {
        return postService.findByProfileIdPaged(profileId, PageRequest.of(page, size));
    }

    @PostMapping("/create-post")
    public ResponseEntity<String> createPost(@RequestBody PostCreationDTO postDTO,
                                             @AuthenticationPrincipal Jwt jwt) {
        postService.post(Long.valueOf(jwt.getClaims().get("id").toString()), postDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable UUID postId,
                                             @AuthenticationPrincipal Jwt jwt) {
        postService.delete(Long.valueOf(jwt.getClaims().get("id").toString()), postId);
        return ResponseEntity.noContent().build();
    }
}
