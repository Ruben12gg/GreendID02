package com.example.greenid_esmad;

public class ContentComments {
    private String author;
    private String auhtorPfp;
    private String authorId;
    private String commentId;
    private String commentVal;

    public ContentComments(String author, String auhtorPfp, String authorId, String commentId, String commentVal) {
        this.author = author;
        this.auhtorPfp = auhtorPfp;
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

    public String getAuhtorPfp() {
        return auhtorPfp;
    }

    public void setAuhtorPfp(String auhtorPfp) {
        this.auhtorPfp = auhtorPfp;
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
