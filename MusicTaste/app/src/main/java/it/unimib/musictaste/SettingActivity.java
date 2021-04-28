package it.unimib.musictaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import android.content.Intent;
public class SettingActivity extends AppCompatActivity {

    Button btnSignOut;
    Button btnName;
    Button btnNameEmailPass;
    //add by Anto
    Button btnAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        btnName = findViewById(R.id.btnName);
        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProviderData();
            }
        });
        btnNameEmailPass = findViewById(R.id.btnNameEmailPass);
        btnNameEmailPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserProfile();
            }
        });

        //add by Anto
        btnAccount = findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });


    }
    //add by Anto
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, AccountActivity.class);
        startActivity(switchActivityIntent);
    }



    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void getProviderData() {
        // [START get_provider_data]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                //String email = profile.getEmail();
                //Uri photoUrl = profile.getPhotoUrl();
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            //String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            // Check if user's email is verified
            //boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            //String uid = user.getUid();
        }
        // [END get_user_profile]
    }
}