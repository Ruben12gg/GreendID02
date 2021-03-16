package com.example.greenid_esmad;

public class ContentFeed {
    private static String authorPfp;
    private static String contentUrl;
    private String author;
    private String date;
    private String likeVal;
    private String commentVal;

    public ContentFeed() {
    }

    public ContentFeed(String author, String authorPfp, String date, String contentUrl, String likeVal, String commentVal) {
        this.author = author;
        this.authorPfp = authorPfp;
        this.date = date;
        this.contentUrl = contentUrl;
        this.likeVal = likeVal;
        this.commentVal = commentVal;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static String getAuthorPfp() {
        return authorPfp;
    }

    public void setAuthorPfp(String authorPfp) {
        this.authorPfp = authorPfp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
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


}

