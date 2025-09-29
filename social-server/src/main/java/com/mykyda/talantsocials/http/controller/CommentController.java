package com.mykyda.talantsocials.http.controller;

import com.mykyda.talantsocials.dto.CommentDTO;
import com.mykyda.talantsocials.dto.create.CommentCreationDTO;
import com.mykyda.talantsocials.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}/")
    public List<CommentDTO> getCommentsPageByProfileId(@PathVariable("postId") UUID postId, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return commentService.getCommentsForPostPaged(postId, PageRequest.of(page, size));
    }

    @GetMapping("/replies/{commentId}")
    public List<CommentDTO> getRepliesPageByOriginalCommentId(@PathVariable("commentId") UUID commentId, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return commentService.getRepliesPaged(commentId, PageRequest.of(page, size));
    }

    @PostMapping("/create")
    public ResponseEntity<String> comment(@RequestBody CommentCreationDTO commentCreationDTO) {
        commentService.createComment(commentCreationDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteComment(@RequestBody CommentCreationDTO commentCreationDTO) {
        commentService.deleteComment(commentCreationDTO);
        return ResponseEntity.noContent().build();
    }
}
