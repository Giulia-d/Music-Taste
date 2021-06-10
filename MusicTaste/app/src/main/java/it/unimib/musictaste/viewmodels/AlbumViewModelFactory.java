package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AlbumViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String uid;
    private final String albumId;
    private final String title;

    public AlbumViewModelFactory(Application application, String uid, String albumId, String title){
        this.application = application;
        this.uid = uid;
        this.albumId = albumId;
        this.title = title;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AlbumViewModel(application,uid,albumId,title);
    }
}
