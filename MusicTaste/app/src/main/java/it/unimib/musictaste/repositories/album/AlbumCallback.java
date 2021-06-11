package it.unimib.musictaste.repositories.album;

public interface AlbumCallback {

    void onResponse(String description, String date);
    void onFailure(String msg);
}
