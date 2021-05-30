package it.unimib.musictaste.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String artist;
    private String image;
    private String id;
    private String idArtist;
    private String youtube;
    private String spotify;
    private String artistImg;

    public void setArtistImg(String artistImg) {
        this.artistImg = artistImg;
    }

    public String getArtistImg() {
        return artistImg;
    }

    public Song(String title, String image, String id, String artist, String artistImg) {
        this.title = title;
        this.image = image;
        this.id = id;
        this.artist = artist;
        this.artistImg = artistImg;
    }

    public String getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(String idArtist) {
        this.idArtist = idArtist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
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

    public String getSpotify() {
        return spotify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.image);
        dest.writeString(this.id);
        dest.writeString(this.idArtist);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.artist = source.readString();
        this.image = source.readString();
        this.id = source.readString();
        this.idArtist = source.readString();
    }

    protected Song(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.image = in.readString();
        this.id = in.readString();
        this.idArtist = in.readString();
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
