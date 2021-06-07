package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.musictaste.repositories.AccountRepository;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Song;

public class AccountViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> likedSongs;
    private MutableLiveData<List<Artist>> likedArtists;
    private AccountRepository accountRepository;
    private String uid;

    public AccountViewModel(@NonNull Application application){ super(application);}

    //Custom constructor
    public AccountViewModel(@NonNull Application application, String uid) {
        super(application);
        accountRepository = new AccountRepository(application.getApplicationContext());
        this.uid = uid;
    }

    public LiveData<List<Song>> getLikedSongs(boolean leftFragment){
        if(likedSongs == null || leftFragment){
            likedSongs = new MutableLiveData<>();
            loadLikedSongs();
        }
        return likedSongs;
    }

    public LiveData<List<Artist>> getLikedArtists(boolean leftFragment){
        if(likedArtists == null || leftFragment){
            likedArtists = new MutableLiveData<>();
            loadLikedArtists();
        }
        return likedArtists;
    }

    private void loadLikedSongs(){
        likedSongs = accountRepository.getLikedSongs(uid);
    }
    private void loadLikedArtists(){
        likedArtists = accountRepository.getLikedArtists(uid);
    }

}
