package it.unimib.musictaste.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable{
    private String title;
    private String description;
    private String url;
    private String image;

    public News(String title, String description, String url, String image) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeString(this.url);

    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.description = source.readString();
        this.image = source.readString();
        this.url = source.readString();

    }

    protected News(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.url = in.readString();

    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}

