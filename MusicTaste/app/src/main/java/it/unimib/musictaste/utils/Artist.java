package it.unimib.musictaste.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {
    private String name;
    private String image;
    private String id;

    public Artist(String name, String image, String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.id);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.image = source.readString();
        this.id = source.readString();
    }

    protected Artist(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
