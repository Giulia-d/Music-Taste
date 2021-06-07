package it.unimib.musictaste.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private Album album;
    private String image;
    private String id;
    private String youtube;
    private String spotify;
    private String description;
    private Artist artist;

    public Song(String title, String image, String id, Artist artist) {
        this.title = title;
        this.image = image;
        this.id = id;
        this.artist = artist;
    }
    public Song(){

    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public Album getAlbum(){
        return album;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public void setSpotify(String spotify) {
        this.spotify = spotify;
    }

    public void setAlbum(Album album){
        this.album = album;
    }

    public String getSpotify() {
        return spotify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.id);
        dest.writeParcelable(this.artist, flags);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.image = source.readString();
        this.id = source.readString();
        this.artist = source.readParcelable(Artist.class.getClassLoader());
    }

    protected Song(Parcel in) {
        this.title = in.readString();
        this.image = in.readString();
        this.id = in.readString();
        this.artist = in.readParcelable(Artist.class.getClassLoader());
        this.album = in.readParcelable(Album.class.getClassLoader());
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
