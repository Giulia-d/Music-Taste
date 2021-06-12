package it.unimib.musictaste.viewmodel.account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.musictaste.models.Album;
import it.unimib.musictaste.models.Artist;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.repositories.AccountRepository;


public class AccountViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> likedSongs;
    private MutableLiveData<List<Artist>> likedArtists;
    private MutableLiveData<List<Album>> likedAlbums;
    private AccountRepository accountRepository;
    private String uid;

    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    //Custom constructor
    public AccountViewModel(@NonNull Application application, String uid) {
        super(application);
        accountRepository = new AccountRepository(application.getApplicationContext());
        this.uid = uid;
    }

    public LiveData<List<Song>> getLikedSongs(boolean leftFragment) {
        // left fragment is true when the user click on the item to go in the specific activity
        if (likedSongs == null || leftFragment) {
            likedSongs = new MutableLiveData<>();
            loadLikedSongs();
        }
        return likedSongs;
    }

    public LiveData<List<Artist>> getLikedArtists(boolean leftFragment) {
        if (likedArtists == null || leftFragment) {
            likedArtists = new MutableLiveData<>();
            loadLikedArtists();
        }
        return likedArtists;
    }

    public LiveData<List<Album>> getLikedAlbums(boolean leftFragment) {
        if (likedAlbums == null || leftFragment) {
            likedAlbums = new MutableLiveData<>();
            loadLikedAlbums();
        }
        return likedAlbums;
    }

    private void loadLikedSongs() {
        likedSongs = accountRepository.getLikedSongs(uid);
    }

    private void loadLikedArtists() {
        likedArtists = accountRepository.getLikedArtists(uid);
    }

    private void loadLikedAlbums() {
        likedAlbums = accountRepository.getLikedAlbums(uid);
    }

}
