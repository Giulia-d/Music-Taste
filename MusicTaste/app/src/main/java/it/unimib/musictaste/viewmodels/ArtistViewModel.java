package it.unimib.musictaste.viewmodels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.ArtistActivity;
import it.unimib.musictaste.repositories.ArtistRepository;
import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.LikedElement;
import it.unimib.musictaste.utils.Song;

public class ArtistViewModel extends AndroidViewModel {
    private MutableLiveData<LikedElement> likedElement;
    private MutableLiveData<String> currentDescription;
    private MutableLiveData<List<Album>> albumList;
    private MutableLiveData<String> songId;
    private ArtistRepository artistRepository;
    private MutableLiveData<Album> geniusAlbum;
    private Artist artist;
    private String uid;
    private String artistId;

    public ArtistViewModel(@NonNull Application application){super(application);}

    public ArtistViewModel(@NonNull  Application application, String uid, String artistId) {
        super(application);
        artistRepository = new ArtistRepository(application.getApplicationContext());
        this.uid = uid;
        this.artistId = artistId;
    }

    public LiveData<String> getDetailsArtist() {
        if (currentDescription == null) {
            loadDetailsSong();
        }
        return currentDescription;
    }

    private void loadDetailsSong(){
        currentDescription = artistRepository.getArtistInfo(artistId);
    }

    public LiveData<LikedElement> getLikedElement() {
        if (likedElement == null) {
            loadLikedElement();
        }
        return likedElement;
    }

    private void loadLikedElement() {
        likedElement = artistRepository.checkLikedArtist(uid, artistId);
    }

    public LiveData<LikedElement> deleteLikedElement(String documentId) {
        likedElement= artistRepository.deleteLikedArtist(documentId);
        return likedElement;
    }

    public LiveData<LikedElement> addLikedElement(Artist s) {
        likedElement = artistRepository.addLikedArtist(uid, s);
        return likedElement;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<List<Album>> getListAlbum(Artist artist) throws ParseException, SpotifyWebApiException, IOException {
       if(albumList == null){
           albumList = new MutableLiveData<>();
           loadListAlbum(artist);
       }
       return albumList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadListAlbum(Artist artist) throws ParseException, SpotifyWebApiException, IOException {
        albumList = artistRepository.getArtistAlbums(artist);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<String> getIdSong(Album album){
        songId = artistRepository.getGeniusInfo(album);
        return songId;
    }

    public LiveData<Album> getIdAlbum(Album album, String idSong){
        geniusAlbum = artistRepository.getIdGenius(album, idSong);
        return geniusAlbum;
    }

}
