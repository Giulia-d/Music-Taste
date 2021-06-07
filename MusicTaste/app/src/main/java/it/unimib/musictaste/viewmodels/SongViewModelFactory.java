package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SongViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String uid;
    private final String songId;

    public SongViewModelFactory(Application application, String uid, String songId){
        this.application = application;
        this.uid = uid;
        this.songId = songId;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SongViewModel(application, uid, songId);
    }
}
