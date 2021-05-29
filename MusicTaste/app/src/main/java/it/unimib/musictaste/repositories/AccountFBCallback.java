package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.Song;

public interface AccountFBCallback {
    void onResponse(List<Song> likedSongs);
    void onFailure(String msg);
}
