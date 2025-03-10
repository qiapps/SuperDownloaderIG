package com.qiapps.superdownloaderig.Model;

public class InstagramResource {

    private String title;
    private int id;
    private int isVideo;//1 - é video; 0 - não é video
    private String url;//thumb
    private String video_url;//post url
    private String img_profile;
    private String username;
    private String filepath;
    private String instagramPath;
    private int duration;

    public InstagramResource() {
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstagramPath() {
        return instagramPath;
    }

    public void setInstagramPath(String instagramPath) {
        this.instagramPath = instagramPath;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(int isVideo) {
        this.isVideo = isVideo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isVideo(){
        return isVideo == 1;
    }

    @Override
    public String toString() {
        return "InstagramResource{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", isVideo=" + isVideo +
                ", url='" + url + '\'' +
                ", video_url='" + video_url + '\'' +
                ", img_profile='" + img_profile + '\'' +
                ", username='" + username + '\'' +
                ", filepath='" + filepath + '\'' +
                ", instagramPath='" + instagramPath + '\'' +
                ", duration=" + duration +
                '}';
    }
}
