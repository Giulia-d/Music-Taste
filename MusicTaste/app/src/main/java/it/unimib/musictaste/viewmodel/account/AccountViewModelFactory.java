package it.unimib.musictaste.viewmodel.account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;



public class AccountViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String uid;

    public AccountViewModelFactory(Application application, String uid){
        this.application = application;
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AccountViewModel(application, uid);
    }
}
