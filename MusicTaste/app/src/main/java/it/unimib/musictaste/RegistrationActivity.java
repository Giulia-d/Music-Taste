package it.unimib.musictaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    EditText mRegName;
    EditText mRegEmail;
    EditText mRegPassword;
    Button btnRegSignIn;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        mRegEmail = findViewById(R.id.etRegEmail);
        mRegPassword = findViewById(R.id.etRegPassword);
        mRegName = findViewById(R.id.etRegName);

        btnRegSignIn = findViewById(R.id.btnRegSignIn);
        btnRegSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mRegEmail.getText().toString();
                String password = mRegPassword.getText().toString();
                String name =mRegName.getText().toString();
                if(!(email.matches("") || password.matches("") || password.length() < 6 || !email.contains("@") || name.matches(""))) {
                    createAccount(email, password);
                    //updateProfile(name);
                }
                else
                   emptyField(email, password, name);
            }
        });

    }
/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
*/
    private void emptyField(String email, String password, String name){
        if((email.matches("") || password.matches("") || name.matches("")))
            Toast.makeText(this, R.string.EmptyField,Toast.LENGTH_LONG).show();
        else if(password.length() < 6)
            Toast.makeText(this, R.string.PasLength,Toast.LENGTH_LONG).show();
            else if(!email.contains("@"))
            Toast.makeText(this, R.string.EmailNotValid,Toast.LENGTH_LONG).show();
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            /*UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });*/
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, R.string.AutFail,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }



public void updateUI(FirebaseUser account){

        if(account != null){
            Toast.makeText(this, R.string.AutSucc,Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,MainActivity.class));

        }else {
            Toast.makeText(this, R.string.AutFail,Toast.LENGTH_LONG).show();
        }

    }

    public void updateProfile(String name) {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(this, "Primo",Toast.LENGTH_SHORT).show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))

        Toast.makeText(this, "Secondo",Toast.LENGTH_SHORT).show();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        updateUI(user);
        // [END update_profile]
    }
}

