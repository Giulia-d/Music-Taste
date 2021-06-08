package it.unimib.musictaste.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.unimib.musictaste.repositories.UserRepository;
import it.unimib.musictaste.utils.LoginResponse;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<LoginResponse> loginResponseMutableLiveData = new MutableLiveData<>();
    UserRepository userRepository;
    public LiveData<LoginResponse> loginResponseLiveData = loginResponseMutableLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void loginMailPassword(String username, String password) {
        loginResponseMutableLiveData = userRepository.signIn(username, password);
    }

   public void loginGoogle(String idToken) {
        loginResponseMutableLiveData = userRepository.firebaseAuthWithGoogle(idToken);

    }

}
