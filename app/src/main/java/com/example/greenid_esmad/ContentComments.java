package com.example.greenid_esmad;

public class ContentComments {
    private String author;
    private String authorPfp;
    private String authorId;
    private String commentId;
    private String commentVal;

    public ContentComments(String author, String authorPfp, String authorId, String commentId, String commentVal) {
        this.author = author;
        this.authorPfp = authorPfp;
        this.authorId = authorId;
        this.commentId = commentId;
        this.commentVal = commentVal;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorPfp() {
        return authorPfp;
    }

    public void setAuthorPfp(String authorPfp) {
        this.authorPfp = authorPfp;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
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
}
