package it.unimib.musictaste.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Album implements Parcelable {

    private String title;
    private String image;
    private String id;
    private Artist artist;
    private List<Song> tracks;

    public Album (String title, String image, String id){
        this.title = title;
        this.image = image;
        this.id = id;
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

    public List<Song> getTracks(){
        return tracks;
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

    public void setTracks(List<Song> tracks){
        this.tracks = tracks;
    }

    public void setArtist(Artist artist){
        this.artist = artist;
    }

    protected Album(Parcel in) {
        this.title = in.readString();
        this.image = in.readString();
        this.id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
