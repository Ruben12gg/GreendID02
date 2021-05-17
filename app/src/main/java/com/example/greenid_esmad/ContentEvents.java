package com.example.greenid_esmad;

public class ContentEvents {
    private String authorPfp;
    private String contentUrl;
    private String author;
    private String date;
    private String likeVal;
    private String commentId;
    private String commentVal;
    private String location;
    private String description;
    private String postId;
    private String userId;
    private String authorId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorPfp() {
        return authorPfp;
    }

    public void setAuthorPfp(String authorPfp) {
        this.authorPfp = authorPfp;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLikeVal() {
        return likeVal;
    }

    public void setLikeVal(String likeVal) {
        this.likeVal = likeVal;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentVal() {
        return commentVal;
    }

    public void setCommentVal(String commentVal) {
        this.commentVal = commentVal;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public ContentEvents(String authorPfp, String location, String contentUrl, String author, String date, String likeVal, String commentVal, String description, String postId, String userId, String authorId) {
        this.authorPfp = authorPfp;
        this.contentUrl = contentUrl;
        this.author = author;
        this.date = date;
        this.likeVal = likeVal;
        this.commentId = commentId;
        this.commentVal = commentVal;
        this.location = location;
        this.description = description;
        this.postId = postId;
        this.userId = userId;
        this.authorId = authorId;
    }
}

