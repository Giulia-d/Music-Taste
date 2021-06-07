package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.unimib.musictaste.repositories.SongRepository;
import it.unimib.musictaste.utils.LikedElement;
import it.unimib.musictaste.utils.Song;

public class SongViewModel extends AndroidViewModel {
    private MutableLiveData<LikedElement> likedElement;
    private MutableLiveData<Song> currentSong;
    private SongRepository songRepository;
    private String uid;
    private String songId;


    public SongViewModel(@NonNull Application application) {
        super(application);
    }

    //Custom constructor
    public SongViewModel(@NonNull Application application, String uid, String songId) {
        super(application);
        songRepository = new SongRepository(application.getApplicationContext());
        this.uid = uid;
        this.songId = songId;
    }

    public LiveData<Song> getDetailsSong() {
        if (currentSong == null) {
            loadDetailsSong();
        }
        return currentSong;
    }

    private void loadDetailsSong(){
        currentSong = songRepository.getSongInfo(songId);
    }

    public LiveData<LikedElement> getLikedElement() {
        if (likedElement == null) {
            loadLikedElement();
        }
        return likedElement;
    }

    private void loadLikedElement() {
        likedElement = songRepository.checkLikedSongs(uid, songId);
    }

    public LiveData<LikedElement> deleteLikedElement(String documentId) {
        removeLikedElement(documentId);
        return likedElement;
    }

    private void removeLikedElement(String documentID){
        likedElement = songRepository.deleteLikedSong(documentID);
    }


    public LiveData<LikedElement> addLikedElement(Song s) {
        addLiked(s);
        return likedElement;
    }

    private void addLiked(Song s){
        likedElement = songRepository.addLikedSong(uid, s);
    }
}