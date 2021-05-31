package it.unimib.musictaste.repositories;

public interface ArtistFBCallback {
    void onResponseFB(boolean liked, String documentId, boolean firstLike);
    void onFailureFB(String msg);
}
