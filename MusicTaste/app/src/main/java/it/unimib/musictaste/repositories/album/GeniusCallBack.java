package it.unimib.musictaste.repositories.album;


import it.unimib.musictaste.models.Album;

//credo non si usi mai
public interface GeniusCallBack {
    void onResponseGenius(Album album);
    void onFailureGenius();
}
