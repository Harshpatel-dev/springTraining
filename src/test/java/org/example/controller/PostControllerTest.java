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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
        // Arrange: Mock the service to return the post when called
        when(postService.createPostWithCommentsAndHashtags(any(Post.class), any(List.class), any(List.class))).thenReturn(post);

        // Act: Perform a POST request to /posts/create
        ResultActions result = mockMvc.perform(post("/posts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"post\": { \"title\": \"Test Post\" }, \"comments\": [{ \"text\": \"Test Comment\" }], \"hashtags\": [{ \"tag\": \"#Test\" }] }"));

        // Assert: Verify that the response is as expected
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.id").value(1L));
    }

//    @Test
//    void helloEndpoint_ShouldReturnHelloWorldMessage() throws Exception {
//        // Act: Perform a GET request to /posts/hello
//        mockMvc.perform(get("/posts/hello"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Hello, World 12345!"));
//    }
}
