package com.example.greenid_esmad;

public class ContentFollowing {
    private String profileName;
    private String pfp;
    private String id;

    public ContentFollowing(String profileName, String pfp, String id) {
        this.profileName = profileName;
        this.pfp = pfp;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

