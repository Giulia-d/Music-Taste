package it.unimib.musictaste.repositories;

public interface SongFBCallback {
    void onResponseFB(boolean liked, String documentId, boolean firstLike);
    void onFailureFB(String msg);
}
