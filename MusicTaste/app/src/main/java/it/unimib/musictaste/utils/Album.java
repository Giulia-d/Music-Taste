package it.unimib.musictaste.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Album implements Parcelable {

    private String title;
    private String artistName;
    private String image;
    private String idGenius;
    private String idSpotify;
    private Artist artist;
    private String urlSpotify;
    private List<Song> tracks;

    public Album (String title, String image, String id) {
        this.title = title;
        this.image = image;
        this.idGenius = id;
    }

    public Album (String title, String image, String id, String url, String artistName){
        this.title = title;
        this.image = image;
        this.idSpotify = id;
        this.urlSpotify = url;
        this.artistName = artistName;
    }



    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return idGenius;
    }

    public String getIdSpotify() {
        return idSpotify;
    }

    public List<Song> getTracks(){
        return tracks;
    }

    public String getArtistName(){
        return artistName;
    }

    public Artist getArtist(){
        return artist;
    }

    public String getUrlSpotify() {
        return urlSpotify;
    }

    public void setUrlSpotify(String urlSpotify) {
        this.urlSpotify = urlSpotify;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.idGenius = id;
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
        this.idGenius = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.idGenius);
        dest.writeString(this.artistName);
        dest.writeString(this.idSpotify);
        dest.writeString(this.urlSpotify);
        dest.writeParcelable(this.artist, flags);
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
