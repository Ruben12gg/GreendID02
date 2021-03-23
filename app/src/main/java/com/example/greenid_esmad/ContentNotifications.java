package com.example.greenid_esmad;

public class ContentNotifications {
    private String authorPfp;
    private String contentUrl;
    private String commentVal;
    private String author;

    public ContentNotifications(String authorPfp, String contentUrl, String commentVal, String author) {
        this.authorPfp = authorPfp;
        this.contentUrl = contentUrl;
        this.commentVal = commentVal;
        this.author = author;
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
}