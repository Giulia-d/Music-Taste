package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.Song;

public interface SearchCallback {
    void onResponse(List<Song> songs);
    void onFailure(String msg);
}
