package com.example.greenid_esmad;

public class ContentNotifications {
    private String authorPfp;
    private String contentUrl;
    private String commentVal;
    private String author;
    private String notifId;
    private String userId;
    private String authorId;

    public ContentNotifications(String authorPfp, String contentUrl, String commentVal, String author, String notifId, String userId, String authorId) {
        this.authorPfp = authorPfp;
        this.contentUrl = contentUrl;
        this.commentVal = commentVal;
        this.author = author;
        this.notifId = notifId;
        this.userId = userId;
        this.authorId = authorId;
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

    public String getCommentVal() {
        return commentVal;
    }

    public void setCommentVal(String commentVal) {
        this.commentVal = commentVal;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNotifId() {
        return notifId;
    }

    public void setNotifId(String notifId) {
        this.notifId = notifId;
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
}