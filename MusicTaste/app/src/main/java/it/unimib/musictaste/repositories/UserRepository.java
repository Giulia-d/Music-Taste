package it.unimib.musictaste.repositories;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unimib.musictaste.LoginActivity;
import it.unimib.musictaste.MainActivity;
import it.unimib.musictaste.R;
import it.unimib.musictaste.RegistrationActivity;
import it.unimib.musictaste.utils.LoginResponse;

public class UserRepository {
    //public static GoogleSignInClient mGoogleSignInClient;
    //private static final String TAG = "GoogleActivity";
    //private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private final Application application;
    private MutableLiveData<LoginResponse> loginResultMutableLiveData;
    private FirebaseFirestore database;
    //private final DatabaseReference mDatabase;

    public UserRepository(Application application) {
        this.application = application;
        mAuth = FirebaseAuth.getInstance();
        //mDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DB).getReference();
        this.database = FirebaseFirestore.getInstance();
    }


    //sign in with google
    public MutableLiveData<LoginResponse>  firebaseAuthWithGoogle(String idToken) {
        loginResultMutableLiveData = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ContextCompat.getMainExecutor(application), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginResponse loginResponse = new LoginResponse();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loginResponse.setSuccess(true);
                            FirebaseUser user = mAuth.getCurrentUser();
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
                            // Sign in success, update UI with the signed-in user's information
                            loginResponse.setSuccess(true);
                            FirebaseUser user = mAuth.getCurrentUser();
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
}

