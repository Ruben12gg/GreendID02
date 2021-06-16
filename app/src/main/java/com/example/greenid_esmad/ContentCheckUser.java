package com.example.greenid_esmad;

public class ContentCheckUser {
    private String contentUrl;
    private String userId;
    private String postId;
    private String postType;

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public ContentCheckUser(String contentUrl, String userId, String postId, String postType) {
        this.contentUrl = contentUrl;
        this.userId = userId;
        this.postId = postId;
        this.postType = postType;
    }
}

