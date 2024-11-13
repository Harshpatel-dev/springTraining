package org.example.config;

import org.example.entity.Comment;
import org.example.entity.Hashtag;
import org.example.entity.Post;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class AsyncPostIntegrationConfig {

    // Define a ThreadPoolTaskExecutor for asynchronous processing
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); //minimum number of threads to keep active
        executor.setMaxPoolSize(10); //maximum number of threads to allow in the pool
        executor.setQueueCapacity(25); //maximum number of tasks that can wait in the queue when all threads are busy
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize(); // Initialize the executor to apply the configuration settings
        return executor;
    }

    // Synchronous post creation channel
    @Bean
    public MessageChannel postChannel() {
        return new DirectChannel();
    }

    // Asynchronous comment validation channel
    @Bean
    public MessageChannel commentValidationChannel() {
        return new ExecutorChannel(taskExecutor());
    }

    // Asynchronous hashtag extraction channel
    @Bean
    public MessageChannel hashtagExtractionChannel() {
        return new ExecutorChannel(taskExecutor());
    }

    // Final channel for storage operations
    @Bean
    public MessageChannel storageChannel() {
        return new ExecutorChannel(taskExecutor());
    }

    // Step 1: Process new post synchronously
    @ServiceActivator(inputChannel = "postChannel", outputChannel = "commentValidationChannel")
    public Post processNewPost(Post post) {
        System.out.println(Thread.currentThread().getName() + " - Processing new post: " + post.getTitle());
        return post;
    }

    // Step 2: Validate comments asynchronously
    @ServiceActivator(inputChannel = "commentValidationChannel", outputChannel = "hashtagExtractionChannel")
    public Post validateComments(Post post) {
        for (Comment comment : post.getComments()) {
            comment.setValid(comment.getText().length() > 5); // Simple validation
            System.out.println(Thread.currentThread().getName() + " - Comment validation: "
                    + comment.getText() + " isValid: " + comment.isValid());
        }
        return post;
    }

    // Step 3: Extract hashtags asynchronously
    @ServiceActivator(inputChannel = "hashtagExtractionChannel", outputChannel = "storageChannel")
    public Post extractHashtags(Post post) {
        System.out.println(Thread.currentThread().getName() + " - Extracting hashtags from post: " + post.getTitle());
        return post;
    }

    // Step 4: Save post, comments, and hashtags asynchronously
    @ServiceActivator(inputChannel = "storageChannel")
    public void saveEntities(Post post) {
        System.out.println(Thread.currentThread().getName() + " - Saving post: " + post.getTitle());
        for (Comment comment : post.getComments()) {
            System.out.println(Thread.currentThread().getName() + " - Saving comment: "
                    + comment.getText() + " isValid: " + comment.isValid());
        }
    }
}

//Uni-directional (inbound or outbound)
//A Channel Adapter is used to connect Spring Integration channels to external systems (e.g., databases, files, or APIs) without expecting a response.
//Inbound Channel Adapter: Polls or listens to an external source and sends the data to a Spring Integration channel.
//Outbound Channel Adapter: Receives messages from a Spring Integration channel and sends them to an external system.
//Example: A file inbound channel adapter reads data from a file and sends it to a channel, while a JDBC outbound channel adapter writes data from a channel to a database.

//A Service Activator is used to process messages from a channel and send a response to another channel.
//A Message Router is used to route messages from one channel to another based on a set of conditions.
//Main Frame