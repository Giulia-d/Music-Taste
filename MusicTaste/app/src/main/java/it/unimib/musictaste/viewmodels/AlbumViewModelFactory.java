package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AlbumViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String uid;
    private final String albumId;

    public AlbumViewModelFactory(Application application, String uid, String albumId){
        this.application = application;
        this.uid = uid;
        this.albumId = albumId;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AlbumViewModel(application,uid,albumId);
    }
}
