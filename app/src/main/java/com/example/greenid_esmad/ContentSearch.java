package com.example.greenid_esmad;

public class ContentSearch {
    private String profileName;
    private String pfp;
    private String bio;
    private String following;
    private String followers;
    private String id;

    public ContentSearch(String profileName, String pfp, String bio, String following, String followers, String id) {
        this.profileName = profileName;
        this.pfp = pfp;
        this.bio = bio;
        this.following = following;
        this.followers = followers;
        this.id = id;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

