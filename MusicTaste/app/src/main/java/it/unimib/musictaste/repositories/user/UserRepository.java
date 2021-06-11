package it.unimib.musictaste.repositories.user;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;


import it.unimib.musictaste.models.LoginResponse;
import it.unimib.musictaste.utils.Utils;

public class UserRepository {
    //public static GoogleSignInClient mGoogleSignInClient;
    //private static final String TAG = "GoogleActivity";
    //private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private final Application application;
    private MutableLiveData<LoginResponse> loginResultMutableLiveData;
    //private final DatabaseReference mDatabase;

    public UserRepository(Application application) {
        this.application = application;
        mAuth = FirebaseAuth.getInstance();
        //mDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DB).getReference();
    }

    public MutableLiveData<LoginResponse> register(String email, String password, String username){
        loginResultMutableLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(ContextCompat.getMainExecutor(application), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginResponse loginResponse = new LoginResponse();
                        if (task.isSuccessful()) {
                            // Sign in success, update with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });
                            setAuthenticationToken(user.getIdToken(false).getResult().getToken());
                            setUserId(user.getUid());
                            loginResponse.setSuccess(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            loginResponse.setSuccess(false);
                        }
                        loginResultMutableLiveData.postValue(loginResponse);
                    }
                });
        // [END create_user_with_email]

        return loginResultMutableLiveData;
    }


    //sign in with google
    public MutableLiveData<LoginResponse> firebaseAuthWithGoogle(String idToken) {
        loginResultMutableLiveData = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ContextCompat.getMainExecutor(application), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginResponse loginResponse = new LoginResponse();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            setAuthenticationToken(user.getIdToken(false).getResult().getToken());
                            setUserId(user.getUid());
                            loginResponse.setSuccess(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            loginResponse.setSuccess(false);
                        }
                        loginResultMutableLiveData.postValue(loginResponse);
                    }
                });
        return loginResultMutableLiveData;
    }

    //sign in with mail and password
    public MutableLiveData<LoginResponse> signIn(String email, String password) {
        // [START sign_in_with_email]
        loginResultMutableLiveData = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(ContextCompat.getMainExecutor(application), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginResponse loginResponse = new LoginResponse();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            setAuthenticationToken(user.getIdToken(false).getResult().getToken());
                            setUserId(user.getUid());
                            loginResponse.setSuccess(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            loginResponse.setSuccess(false);
                        }
                        loginResultMutableLiveData.postValue(loginResponse);
                    }
                });
        // [END sign_in_with_email]
        return loginResultMutableLiveData;
    }

    private void setAuthenticationToken(String token) {
        SharedPreferences sharedPref = application.getSharedPreferences(
                Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Utils.AUTHENTICATION_TOKEN, token);
        editor.apply();
    }

    private void setUserId(String userId) {
        SharedPreferences sharedPref = application.getSharedPreferences(
                Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Utils.USER_ID, userId);
        editor.apply();
    }
}

