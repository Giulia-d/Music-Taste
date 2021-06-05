package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.Album;

public interface ArtistsAlbumsCallback {

    void onResponseArtistAlbums(List<Album> albumList);
    void onFailureArtistAlbums(String msg);
}
