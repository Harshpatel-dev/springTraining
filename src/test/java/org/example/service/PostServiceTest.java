package org.example.service;

import org.example.entity.Comment;
import org.example.entity.Hashtag;
import org.example.entity.Post;
import org.example.exception.CustomServiceException;
import org.example.repository.CommentRepository;
import org.example.repository.HashtagRepository;
import org.example.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest // Loads only JPA components, repositories, and configures an in-memory database for isolated testing without starting the full application context.
@Import(PostService.class) // Imports PostService, allowing us to test it directly with only necessary beans (PostService and repositories) in the context.
@Transactional // Ensures that each test runs within a transaction, rolling back changes afterward to keep the database clean for the next test.
@Rollback // Explicitly rolls back any changes made in each test, maintaining a consistent initial database state for each test run.
class PostServiceTest {

    // Autowired to access PostRepository, CommentRepository and HashTagRepository for database interactions during the test
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private DataSource dataSource;

    private Post post;
    private List<Comment> comments;
    private List<Hashtag> hashtags;

    @BeforeEach
    void setUp() {
        // Setting up initial data for each test run
        post = new Post();
        comments = Arrays.asList(new Comment(), new Comment());
        hashtags = Arrays.asList(new Hashtag(), new Hashtag());
    }

    @Test
    void createPostWithCommentsAndHashtags_ShouldSavePostWithCommentsAndHashtags() {
        // Act: Calls the method under test to save a post with associated comments and hashtags
        post.setTitle("Valid Title");
        Post result = postService.createPostWithCommentsAndHashtags(post, comments, hashtags);

        // Assert: Ensures the post is saved and associated comments and hashtags are also stored in the database
        assertNotNull(result); // Verifies that the returned post is not null
        assertNotNull(result.getId()); // Verifies that the post has an ID, meaning it's persisted
        assertEquals(2, commentRepository.findAll().size()); // Checks if two comments were saved
        assertEquals(2, hashtagRepository.findAll().size()); // Checks if two hashtags were saved
    }

    @Test
    void createPostWithCommentsAndHashtags_ShouldThrowCustomServiceException_OnDataAccessException() throws Exception {
            post.setTitle("Valid Title");
            // Close the database connection to simulate a failure
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.close();

            // Act & Assert: Expect CustomServiceException when calling the service method
            CustomServiceException exception = assertThrows(CustomServiceException.class, () ->
                    postService.createPostWithCommentsAndHashtags(post, comments, hashtags)
            );

            // Assert: Check the exception's message and error code
            assertTrue(exception.getMessage().contains("Failed to save post to the database"));
            assertEquals("DATABASE_ERROR", exception.getErrorCode());
    }

    @Test
    void createPostWithCommentsAndHashtags_ShouldThrowCustomServiceException_OnUnknownError() {
        // Simulates failure due to null title
        CustomServiceException exception = assertThrows(CustomServiceException.class, () ->
                postService.createPostWithCommentsAndHashtags(post, comments, hashtags)
        );

        // Assert: Verify that the error code matches the "UNKNOWN_ERROR" as defined in the service
        assertEquals("UNKNOWN_ERROR", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("An unexpected error occurred while creating the post"));
    }
}
