package it.unimib.musictaste.repositories;

public interface SongCallback {
    void onResponse(String description, String youtube, String spotify);
    void onFailure(String msg);
}
