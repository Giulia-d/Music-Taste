package it.unimib.musictaste.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import it.unimib.musictaste.repositories.UserRepository;
import it.unimib.musictaste.utils.LoginResponse;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<LoginResponse> loginResponseMutableLiveData;
    UserRepository userRepository;
    private boolean isLogged;
    //public LiveData<LoginResponse> loginResponseLiveData = loginResponseMutableLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        isLogged = false;
    }

    public MutableLiveData<LoginResponse> signInEmail(String email, String password){
        if(!isLogged)
            sEmail(email, password);
        return loginResponseMutableLiveData;
    }

    private void sEmail(String email, String password){
        loginResponseMutableLiveData = userRepository.signIn(email, password);
    }

    public MutableLiveData<LoginResponse> signInGoogle(String idToken){
        if(!isLogged)
            sGoogle(idToken);
        return loginResponseMutableLiveData;
    }

    private void sGoogle(String idToken){
        loginResponseMutableLiveData = userRepository.firebaseAuthWithGoogle(idToken);
    }

}
