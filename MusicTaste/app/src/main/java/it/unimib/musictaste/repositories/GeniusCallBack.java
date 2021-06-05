package it.unimib.musictaste.repositories;

import it.unimib.musictaste.utils.Album;

public interface GeniusCallBack {
    void onResponseGenius(Album album);
    void onFailureGenius();
}
