package it.unimib.musictaste.repositories.artist;

import java.util.List;

import it.unimib.musictaste.models.Album;


public interface ArtistsAlbumsCallback {

    void onResponseArtistAlbums(List<Album> albumList);
    void onFailureArtistAlbums(String msg);
}
