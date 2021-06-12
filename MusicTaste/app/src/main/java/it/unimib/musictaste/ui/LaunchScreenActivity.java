package it.unimib.musictaste.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.musictaste.R;
import it.unimib.musictaste.viewmodel.user.UserViewModel;


public class LaunchScreenActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userViewModel.getAuthenticationToken() != null){
                    Intent intent = new Intent(LaunchScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(LaunchScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);



    }
}