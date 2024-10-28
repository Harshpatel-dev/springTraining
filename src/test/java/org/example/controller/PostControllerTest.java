package org.example.controller;

import org.example.controller.PostController;
import org.example.entity.Comment;
import org.example.entity.Hashtag;
import org.example.entity.Post;
import org.example.request.PostRequest;
import org.example.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private Post post;
    private PostRequest postRequest;

    @BeforeEach
    void setup() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");

        Comment comment = new Comment();
        comment.setText("Test Comment");

        Hashtag hashtag = new Hashtag();
        hashtag.setName("#Test");

        postRequest = new PostRequest();
        postRequest.setPost(post);
        postRequest.setComments(Collections.singletonList(comment));
        postRequest.setHashtags(Collections.singletonList(hashtag));
    }

    @Test
    void createPost_ShouldReturnCreatedPost() throws Exception {
        
        when(postService.createPostWithCommentsAndHashtags(any(Post.class), any(List.class), any(List.class))).thenReturn(post);
        
        ResultActions result = mockMvc.perform(post("/posts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"post\": { \"title\": \"Test Post\" }, \"comments\": [{ \"text\": \"Test Comment\" }], \"hashtags\": [{ \"tag\": \"#Test\" }] }"));
        
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPostById_ShouldReturnPost_WhenPostExists() throws Exception {
        
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Sample Title");
        post.setContent("Sample Content");

        when(postService.getPostById(1L)).thenReturn(post);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));
    }

    @Test
    void getPostById_ShouldReturnNotFound_WhenPostDoesNotExist() throws Exception {
        
        when(postService.getPostById(anyLong())).thenReturn(null);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPosts_ShouldReturnListOfPosts_WhenPostsExist() throws Exception {
        // Arrange: Create sample Post objects and configure the mock service to return them
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("First Post");
        post1.setContent("Content of the first post");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Second Post");
        post2.setContent("Content of the second post");

        List<Post> posts = Arrays.asList(post1, post2);
        when(postService.getAllPosts()).thenReturn(posts);

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(posts.size()))
                .andExpect(jsonPath("$[0].id").value(post1.getId()))
                .andExpect(jsonPath("$[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(post1.getContent()))
                .andExpect(jsonPath("$[1].id").value(post2.getId()))
                .andExpect(jsonPath("$[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$[1].content").value(post2.getContent()));
    }

    @Test
    void getAllPosts_ShouldReturnEmptyList_WhenNoPostsExist() throws Exception {
        // Arrange: Configure the mock service to return an empty list
        when(postService.getAllPosts()).thenReturn(Arrays.asList());

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void updatePost_ShouldReturnUpdatedPost_WhenPostExists() throws Exception {
       
        Post post = new Post();
        post.setTitle("Updated Title");
        post.setContent("Updated content");

        PostRequest postRequest = new PostRequest();
        postRequest.setPost(post);
        
        Hashtag hashtag = new Hashtag();
        hashtag.setName("UpdatedHashtag");
        
        postRequest.setHashtags(Arrays.asList(hashtag));
        
        when(postService.updatePost(1L, postRequest.getPost(), postRequest.getHashtags())).thenReturn(post);
        
        mockMvc.perform(MockMvcRequestBuilders.put("/posts/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"post\": { \"title\": \"Updated Title\", \"content\": \"Updated content\" }, \"hashtags\": [ { \"name\": \"UpdatedHashtag\" } ] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void deletePost_ShouldReturnTrue_WhenPostExists() throws Exception {
        Long postId = 1L;
        
        when(postService.deletePost(postId)).thenReturn(true);

        mockMvc.perform(delete("/posts/delete/{id}", postId))
                .andExpect(status().isOk());

        verify(postService).deletePost(postId);
    }
}
