package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.dto.CommentDTO;
import com.mykyda.talantsocials.dto.create.CommentCreationDTO;
import com.mykyda.talantsocials.service.CommentService;
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
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/get-comments/{postId}/")
    public List<CommentDTO> getCommentsPageByProfileId(@PathVariable("postId") UUID postId,
                                                       @RequestParam("page") Integer page,
                                                       @RequestParam("size") Integer size) {
        return commentService.getCommentsForPostPaged(postId, PageRequest.of(page, size));
    }

    @GetMapping("/get-replies/{commentId}")
    public List<CommentDTO> getRepliesPageByOriginalCommentId(@PathVariable("commentId") UUID commentId,
                                                              @RequestParam("page") Integer page,
                                                              @RequestParam("size") Integer size) {
        return commentService.getRepliesPaged(commentId, PageRequest.of(page, size));
    }

    @PostMapping("/create-comment")
    public ResponseEntity<String> comment(@RequestBody CommentCreationDTO commentCreationDTO,
                                          @AuthenticationPrincipal Jwt jwt) {
        commentService.createComment(Long.valueOf(jwt.getClaims().get("id").toString()), commentCreationDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId,
                                                @AuthenticationPrincipal Jwt jwt) {
        commentService.deleteComment(Long.valueOf(jwt.getClaims().get("id").toString()), commentId);
        return ResponseEntity.noContent().build();
    }
}
