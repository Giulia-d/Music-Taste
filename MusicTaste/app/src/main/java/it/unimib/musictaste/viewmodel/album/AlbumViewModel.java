package it.unimib.musictaste.viewmodel.album;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.musictaste.models.Artist;
import it.unimib.musictaste.models.LikedElement;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.repositories.album.AlbumRepository;


public class AlbumViewModel extends AndroidViewModel {
    private MutableLiveData<LikedElement> likedElement;
    private MutableLiveData<String> currentDescription;
    private MutableLiveData<List<Song>> trackList;
    private MutableLiveData<String> spotifyUri;
    private AlbumRepository albumRepository;
    private String uid;
    private String albumId;
    private String title;

    public AlbumViewModel(@NonNull Application application){super(application);}

    public AlbumViewModel(@NonNull  Application application, String uid, String albumId, String title) {
        super(application);
        albumRepository = new AlbumRepository(application.getApplicationContext());
        this.uid = uid;
        this.albumId = albumId;
        this.title = title;

    }

    public LiveData<String> getDetailsAlbum() {
        if (currentDescription == null) {
            loadDetailsAlbum();
        }
        return currentDescription;
    }

    private void loadDetailsAlbum(){
        currentDescription = albumRepository.getAlbumInfo(albumId);
    }

    public LiveData<List<Song>> getAlbumTracks(String albumId) {
        if(trackList== null){
            trackList = new MutableLiveData<>();
            loadAlbumTracks(albumId);
        }
        return trackList;
    }

    private void loadAlbumTracks(String albumId){
        trackList = albumRepository.getAlbumTracks(albumId);
    }

    public LiveData<LikedElement> getLikedElement() {
        if (likedElement == null) {
            loadLikedElement();
        }
        return likedElement;
    }

    private void loadLikedElement() {
        likedElement = albumRepository.checkLikedAlbum(uid, albumId);
    }



    public LiveData<LikedElement> deleteLikedElement(String documentId) {
        likedElement= albumRepository.deleteLikedAlbum(documentId);
        return likedElement;
    }

        public LiveData<LikedElement> addLikedElement(Artist s) {
        likedElement = albumRepository.addLikedAlbum(uid, s);
        return likedElement;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<String> getLinkSpotify() {
        if (spotifyUri == null) {
            loadLinkSpotify();
        }
        return spotifyUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadLinkSpotify(){
        spotifyUri = albumRepository.getLinkSpotify(title);
    }

}
