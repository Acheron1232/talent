package com.mykyda.talantsocials.http.controller;

import com.mykyda.talantsocials.dto.create.PostCreationDTO;
import com.mykyda.talantsocials.dto.PostDTO;
import com.mykyda.talantsocials.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/explore")
    private List<PostDTO> getExplorePage() {
        return postService.explore();
    }

    @GetMapping("/{postId}")
    private PostDTO getPostById(@PathVariable("postId") UUID postId) {
        return postService.findById(postId);
    }

    @GetMapping("/{profileId}/")
    public List<PostDTO> getPostsPageByProfileId(@PathVariable("profileId") UUID profileId, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return postService.findByProfileIdPaged(profileId, PageRequest.of(page, size));
    }

    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestBody PostCreationDTO postDTO) {
        postService.post(postDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePost(@RequestParam UUID postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
