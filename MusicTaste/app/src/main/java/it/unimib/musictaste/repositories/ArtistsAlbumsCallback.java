package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.Album;

public interface ArtistsAlbumsCallback {

    void onResponseAA(List<Album> albumList);
    void onFailureAA(String msg);
}