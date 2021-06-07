package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.musictaste.repositories.SearchRepository;
import it.unimib.musictaste.utils.Song;

public class SearchViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> results;
    private SearchRepository searchRepository;
    private String textToSearch;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        searchRepository = new SearchRepository(application.getApplicationContext());
        textToSearch = "";
    }

    public LiveData<List<Song>> getResults(String text){
        if (!textToSearch.equals(text)){
            results = new MutableLiveData<>();
            search(text);
        }
        return results;
    }

    public void search(String textToSearch){
        results = searchRepository.searchSong(textToSearch);
    }
}
