package it.unimib.musictaste.viewmodel.artist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class ArtistViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String uid;
    private final String artistId;

    public ArtistViewModelFactory(Application application, String uid, String artistId){
        this.application = application;
        this.uid = uid;
        this.artistId = artistId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ArtistViewModel(application,uid,artistId);
    }
}
