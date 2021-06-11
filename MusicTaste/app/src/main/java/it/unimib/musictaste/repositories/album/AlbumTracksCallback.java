package it.unimib.musictaste.repositories.album;

import java.util.List;

import it.unimib.musictaste.models.Song;


public interface AlbumTracksCallback {
    void onResponseTracks(List<Song> tracks, int nTracks);
    void onFailureTracks(String msg);
}
