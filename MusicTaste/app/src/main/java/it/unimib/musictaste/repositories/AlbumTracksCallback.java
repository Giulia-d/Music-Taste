package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.Song;

public interface AlbumTracksCallback {
    void onResponseTracks(List<Song> tracks, int nTracks);
    void onFailureTracks(String msg);
}
