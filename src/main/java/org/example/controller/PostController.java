package org.example.controller;

import org.example.entity.Comment;
import org.example.entity.Hashtag;
import org.example.entity.Post;
import org.example.request.PostRequest;
import org.example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody PostRequest postRequest) {
        ResponseEntity<Post> response;
        Post createdPost = postService.createPostWithCommentsAndHashtags(postRequest.getPost(), postRequest.getComments(), postRequest.getHashtags());
        response = new ResponseEntity<>(createdPost, HttpStatus.OK);
        return response;
    }
    
    //If you just return the POJO (Post), Spring will automatically return a 200 OK response with the object in the body, 
    // but we won’t have control over the status or headers unless you use ResponseEntity
    
    //response entity vs POJO return

    // READ: Get a Post by ID
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if(post != null) {
            return new ResponseEntity<>(post,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // READ: Get all Posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        Post post = postRequest.getPost();
        List<Hashtag> hashtag = postRequest.getHashtags();
        Post updatePost = postService.updatePost(id, postRequest.getPost(), postRequest.getHashtags());
        
        if(updatePost != null) {
            return new ResponseEntity<>(updatePost, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> deletePost(@PathVariable Long id) {
        boolean deleted = postService.deletePost(id);
        Map<String, String> response = new HashMap<>();
        if(deleted) {
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Post not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
