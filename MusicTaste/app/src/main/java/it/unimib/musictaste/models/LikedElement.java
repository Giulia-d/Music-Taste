package it.unimib.musictaste.models;

public class LikedElement {
    private int liked;
    private String documentID;

    public LikedElement(int liked, String documentID) {
        this.liked = liked;
        this.documentID = documentID;
    }

    public int getLiked() {
        return liked;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
