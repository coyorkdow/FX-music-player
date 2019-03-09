package Player;

import javafx.scene.image.Image;

public class MediaInfo {

    private String title;
    private String artist;
    private String album;
    private Image cover;
    private double time;
    private String url;

    public MediaInfo() {
        title = null;
        artist = null;
        album = null;
        cover = null;
        time = 0;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Image getCover() {
        return cover;
    }

    public double getTime() {
        return time;
    }

    public String getUrl(){
        return url;
    }
}
