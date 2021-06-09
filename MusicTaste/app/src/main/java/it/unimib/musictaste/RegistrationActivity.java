package it.unimib.musictaste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import it.unimib.musictaste.utils.LoginResponse;
import it.unimib.musictaste.viewmodels.UserViewModel;

public class RegistrationActivity extends AppCompatActivity {

    EditText mRegName;
    EditText mRegEmail;
    EditText mRegPassword;
    Button btnRegSignIn;
    private UserViewModel userViewModel;
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

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnRegSignIn = findViewById(R.id.btnRegSignIn);
        btnRegSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mRegEmail.getText().toString();
                String password = mRegPassword.getText().toString();
                String username =mRegName.getText().toString();
                if(!(email.matches("") || password.matches("") || password.length() < 6 || !email.contains("@") || username.matches(""))) {
                    createAccount(email, password, username);
                    //updateProfile(name);
                }
                else
                   emptyField(email, password, username);
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
    private void createAccount(String email, String password, String username) {
        userViewModel.createUser(email,password,username).observe(this, lr -> {
            updateUI(lr);
        });
    }



public void updateUI(LoginResponse lr){
    if(lr.isSuccess()) {
        Toast.makeText(this, R.string.AutSucc, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    else
        Toast.makeText(this, R.string.AutFail,Toast.LENGTH_LONG).show();
    }

    /*
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
     */
}

