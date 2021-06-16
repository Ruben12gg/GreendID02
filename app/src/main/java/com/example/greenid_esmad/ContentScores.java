package com.example.greenid_esmad;

public class ContentScores {
    private String profileName;
    private String pfp;
    private String id;
    private String score;
    private String place;

    public ContentScores(String profileName, String pfp, String id, String score, String place) {
        this.profileName = profileName;
        this.pfp = pfp;
        this.id = id;
        this.score = score;
        this.place = place;

    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}

