package it.unimib.musictaste;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {
    ImageButton btnControl;
    TextView txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btnControl = findViewById(R.id.btnControl);
        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("user", "email:" + user.getEmail());
            Log.d("user", "Photo:" + user.getPhotoUrl());
            txtName = (TextView)findViewById(R.id.txtName);
            txtName.setText(user.getDisplayName());



        } else {
            // No user is signed in

        }




    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, SettingActivity.class);
        startActivity(switchActivityIntent);

    }
}