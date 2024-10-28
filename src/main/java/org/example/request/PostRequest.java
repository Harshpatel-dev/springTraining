package org.example.request;

import org.example.entity.Comment;
import org.example.entity.Hashtag;
import org.example.entity.Post;

import java.util.List;

public class PostRequest {
    private Post post;
    private List<Comment> comments;
    private List<Hashtag> hashtags;

    // Getters and Setters

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }
}
