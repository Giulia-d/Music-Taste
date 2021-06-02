package it.unimib.musictaste.repositories;

import it.unimib.musictaste.utils.Album;

public interface SongCallback {
    void onResponse(String description, String youtube, String spotify, Album album);
    void onFailure(String msg);
}
