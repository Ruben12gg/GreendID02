package com.example.greenid_esmad;

public class ContentSearch {
    private String profileName;
    private String pfp;


    public ContentSearch(String profileName, String pfp) {
        this.profileName = profileName;
        this.pfp = pfp;
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
}

