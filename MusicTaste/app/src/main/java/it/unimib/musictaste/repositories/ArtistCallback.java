package it.unimib.musictaste.repositories;

public interface ArtistCallback {

    void onResponse(String description, String youtube, String spotify);
    void onFailure(String msg);
}
