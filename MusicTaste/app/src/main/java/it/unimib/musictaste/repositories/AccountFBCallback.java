package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Song;

public interface AccountFBCallback {
    void onResponse(List<Song> likedSongs, List<Artist> likedArtists);
    void onFailure(String msg);
}
