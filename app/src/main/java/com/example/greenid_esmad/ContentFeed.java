package com.example.greenid_esmad;

public class ContentFeed {
    private String authorPfp;
    private String contentUrl;
    private String author;
    private String date;
    private String likeVal;
    private String commentVal;
    private String location;
    private String description;
    private String postId;
    private String userId;
    private String authorId;
    private String ecoIdea;
    private String eventDate;
    private String eventTime;
    private String impactful;
    private String postType;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getEcoIdea() {
        return ecoIdea;
    }

    public void setEcoIdea(String ecoIdea) {
        this.ecoIdea = ecoIdea;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getImpactful() {
        return impactful;
    }

    public void setImpactful(String impactful) {
        this.impactful = impactful;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public ContentFeed(String authorPfp, String contentUrl, String author, String date, String likeVal, String commentVal, String location, String description, String postId, String userId, String authorId, String ecoIdea, String eventDate, String eventTime, String impactful, String postType) {
        this.authorPfp = authorPfp;
        this.contentUrl = contentUrl;
        this.author = author;
        this.date = date;
        this.likeVal = likeVal;
        this.commentVal = commentVal;
        this.location = location;
        this.description = description;
        this.postId = postId;
        this.userId = userId;
        this.authorId = authorId;
        this.ecoIdea = ecoIdea;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.impactful = impactful;
        this.postType = postType;
    }
}

