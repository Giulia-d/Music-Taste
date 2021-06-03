package it.unimib.musictaste.repositories;

public interface ArtistsAlbumsCallback {

    void onResponseAA(String description, String youtube, String spotify);
    void onFailureAA(String msg);
}
