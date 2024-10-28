package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Comment;
import org.example.entity.Hashtag;
import org.example.entity.Post;
import org.example.exception.CustomServiceException;
import org.example.repository.CommentRepository;
import org.example.repository.HashtagRepository;
import org.example.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service // Marks this class as a Service for business logic
@RequiredArgsConstructor // Generates a constructor for final fields, like postRepository, commentRepository, and hashtagRepository
public class PostService {

    private final PostRepository postRepository; // Automatically injected by Spring due to @RequiredArgsConstructor so // @Autowired - not required.
    
    private final CommentRepository commentRepository;
    
    private final HashtagRepository hashtagRepository;

    // Note: Make sure saveComments and saveHashtags are also transactional so they commit their own changes.
    @Async
    @Transactional
    public CompletableFuture<Void> saveComments(Post post, List<Comment> comments) {
        post.setComments(comments);
        comments.forEach(comment -> comment.setPost(post));
        commentRepository.saveAll(comments);
        return CompletableFuture.completedFuture(null);
    }

    @Async // Makes the method run in a separate thread
    @Transactional
    public CompletableFuture<Void> saveHashtags(Post post, List<Hashtag> hashtags) {
        post.getHashtags().addAll(hashtags);
        hashtagRepository.saveAll(hashtags);
        return CompletableFuture.completedFuture(null);
    }
    
    public Post getPostById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE) // Ensures all database actions within this method are rolled back if something fails
    public Post createPostWithCommentsAndHashtags(Post post, List<Comment> comments, List<Hashtag> hashtags) {
        try {
            // 1. Save post first
            Post savedPost = postRepository.save(post);

            // 2. Save comments and hashtags asynchronously to speed up processing
            CompletableFuture<Void> commentsFuture = this.saveComments(savedPost, comments);
            CompletableFuture<Void> hashtagsFuture = this.saveHashtags(savedPost, hashtags);

            // 3. Wait for both asynchronous tasks to complete before returning the post
            CompletableFuture.allOf(commentsFuture, hashtagsFuture).join();
            
            // 4. Return the saved post after comments and hashtags have been saved
            return savedPost;
        } catch (DataAccessException e) {
            // Log the specific database exception
            throw new CustomServiceException("Failed to save post to the database", "DATABASE_ERROR", e);
        } 
        catch (Exception e) {
            // Log any other exceptions
            throw new CustomServiceException("An unexpected error occurred while creating the post", "UNKNOWN_ERROR", e);
        }
       
    }

    // Update a Post by ID
    public Post updatePost(Long id, Post post, List<Hashtag> hashtags) {
        Post existingPost = getPostById(id);
        if(existingPost != null) {
            if (existingPost.getTitle() != null) {
                existingPost.setTitle(post.getTitle());
            }
            if(existingPost.getContent() != null) {
                existingPost.setContent(post.getContent());
            }
           
            if (existingPost.getHashtags() != null) {
                existingPost.setHashtags(hashtags);
            }
            return postRepository.save(existingPost);
        }
        return null;
    }
    
    // Delete a Post by ID
    public boolean deletePost(Long id) {
        Post existingPost = getPostById(id);
        if(existingPost != null) {
            postRepository.delete(existingPost);
            return true;
        }
       return false; // Post not found
    }
}

//    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
//    public Post createPostWithCommentsAndHashtags(Post post, List<Comment> comments, List<Hashtag> hashtags) {
//        // Save post
//        Post savedPost = postRepository.save(post);
//
//        comments.forEach(comment -> comment.setPost(savedPost));
//        commentRepository.saveAll(comments);
//
//        // Every individual save in a loop creates a separate transaction, which adds significant overhead. Using saveAll handles everything within a single transaction.
//
//        //in spring how to return formatted relavent error responses to client.
//
//        // Add hashtags to post and save them
//        for (Hashtag hashtag : hashtags) {
//            savedPost.getHashtags().add(hashtag);
//            hashtagRepository.save(hashtag);
//        }
//
//        return postRepository.save(savedPost);
//    }
