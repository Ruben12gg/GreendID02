package com.example.greenid_esmad;

public class ContentFeed {
    private String authorPfp;
    private String contentUrl;
    private String author;
    private String date;
    private String likeVal;
    private String commentVal;
    private String location;

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

    public ContentFeed(String authorPfp, String location, String contentUrl, String author, String date, String likeVal, String commentVal) {
        this.authorPfp = authorPfp;
        this.contentUrl = contentUrl;
        this.author = author;
        this.date = date;
        this.likeVal = likeVal;
        this.commentVal = commentVal;
        this.location = location;
    }
}

