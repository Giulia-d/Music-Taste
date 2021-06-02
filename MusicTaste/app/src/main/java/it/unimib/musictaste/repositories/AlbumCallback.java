package it.unimib.musictaste.repositories;

import it.unimib.musictaste.utils.Song;

public interface AlbumCallback {

    void onResponse(String description, String date);
    void onFailure(String msg);
}
