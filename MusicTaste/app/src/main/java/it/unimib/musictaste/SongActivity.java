package it.unimib.musictaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Song;

public class SongActivity extends AppCompatActivity {

    ImageView imgSong;
    TextView tvArtistSong;
    TextView tvTitleSong;
    TextView tvLyricsSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);


        imgSong = findViewById(R.id.imgSong);
        tvArtistSong = findViewById(R.id.tvArtistSong);
        tvTitleSong = findViewById(R.id.tvTitleSong);
        tvLyricsSong = findViewById(R.id.tvLyricsSong);

        Intent intent = getIntent();

        Song song = intent.getParcelableExtra(SearchFragment.SONG);
        //int tre = intent.getIntExtra(SearchFragment.SONG, 0);
        Picasso.get().load(song.getImage()).transform(new GradientTransformation()).into(imgSong);
        tvArtistSong.setText(song.getArtist());
        tvTitleSong.setText(song.getTitle());
        tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
    }

}