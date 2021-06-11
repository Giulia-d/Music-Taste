package it.unimib.musictaste.viewmodel.user;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import it.unimib.musictaste.models.LoginResponse;
import it.unimib.musictaste.repositories.user.UserRepository;
import it.unimib.musictaste.utils.Utils;


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

    public MutableLiveData<LoginResponse> createUser(String email, String password, String username) {
        if (!isLogged) {
            register(email, password, username);
        }
        return loginResponseMutableLiveData;
    }

    private void register(String email, String password,String username) {
        loginResponseMutableLiveData = userRepository.register(email, password, username);
    }

    private void sGoogle(String idToken){
        loginResponseMutableLiveData = userRepository.firebaseAuthWithGoogle(idToken);
    }

    public String getAuthenticationToken() {
        SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(Utils.AUTHENTICATION_TOKEN, null);
    }

    public String getUserId() {
        SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(Utils.USER_ID, null);
    }

    public void deleteUserId() {
        SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

}
