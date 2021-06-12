package it.unimib.musictaste.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import it.unimib.musictaste.R;

import it.unimib.musictaste.models.Album;
import it.unimib.musictaste.models.LikedElement;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.ui.fragments.AccountFragment;
import it.unimib.musictaste.ui.fragments.SearchFragment;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.viewmodel.song.SongViewModel;
import it.unimib.musictaste.viewmodel.song.SongViewModelFactory;


public class SongActivity extends AppCompatActivity {

    ImageView imgSong;
    TextView tvArtistSong, tvAlbumSong, tvDescription, tvListen;
    ImageButton mbtnYt, mbtnSpotify, mbtnLike;
    FirebaseFirestore database;
    //boolean liked;
    //String documentID;
    LikedElement likedElement;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    Song currentSong;
    Album currentAlbum;
    ProgressBar pBLoading;
    SongViewModel songViewModel;
    public static final String ARTIST = "ARTIST";
    public static final String ALBUM = "ALBUM";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        imgSong = findViewById(R.id.imgSong);
        tvArtistSong = findViewById(R.id.tvArtistSong);
        tvAlbumSong = findViewById(R.id.tvAlbumSong);
        tvListen = findViewById(R.id.tvListen);
        tvDescription = findViewById(R.id.tvDescription);
        mbtnYt = findViewById(R.id.btnYoutube);
        mbtnSpotify = findViewById(R.id.btnSpotify);
        mbtnLike = findViewById(R.id.btnLike);
        database = FirebaseFirestore.getInstance();
        //liked = false;
        pBLoading = findViewById(R.id.pBLoading);

        likedElement = new LikedElement(0, null);

        Intent intent = getIntent();
        currentSong = intent.getParcelableExtra(SearchFragment.SONG);
        if (currentSong == null) {
            currentSong = intent.getParcelableExtra(AccountFragment.SONG);
        }

        Picasso.get().load(currentSong.getImage()).transform(new GradientTransformation()).into(imgSong);
        tvArtistSong.setText(currentSong.getArtist().getName());
        setToolbarColor(currentSong);

        //Creation of view model using parameters
        songViewModel = new ViewModelProvider(this, new SongViewModelFactory(
                getApplication(), uid, currentSong.getId())).get(SongViewModel.class);

        //Check if the user likes the song
        //checkedLikedBegin();
        songViewModel.getLikedElement().observe(this, le ->{
            updateUILiked(le);
        });

        songViewModel.getDetailsSong().observe(this, cs ->{
            updateUISong(cs);
        });


        tvArtistSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongActivity.this, ArtistActivity.class);
                intent.putExtra(ARTIST, currentSong.getArtist());
                startActivity(intent);
            }
        });

        tvAlbumSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAlbum.setArtist(currentSong.getArtist());
                Intent intent = new Intent(SongActivity.this, AlbumActivity.class);
                intent.putExtra(ALBUM, currentAlbum);
                startActivity(intent);
            }

        });

        mbtnYt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong.getYoutube() != "") {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentSong.getYoutube()));
                    try {
                        SongActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(SongActivity.this, R.string.YoutubeError, Toast.LENGTH_LONG).show();
            }
        });

        mbtnSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong.getSpotify() != "") {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentSong.getSpotify()));
                    try {
                        SongActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(SongActivity.this, R.string.SpotifyError, Toast.LENGTH_LONG).show();
            }
        });

        mbtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(likedElement.getLiked() == 1 || likedElement.getLiked() == 2){
                    songViewModel.deleteLikedElement(likedElement.getDocumentID());

                } else if (likedElement.getLiked() == 0 || likedElement.getLiked() == 3){
                    songViewModel.addLikedElement(currentSong);


                }
            }
        });
    }


    public void setToolbarColor(Song song) {
        Picasso.get()
                .load(song.getImage())
                .into(new Target() {

                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        /* Save the bitmap or do something with it here */
                        toolbar = (Toolbar) findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);

                        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                        collapsingToolbar.setTitle(song.getTitle());
                        if (bitmap != null) {
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    // Use generated instance
                                    Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
                                    Palette.Swatch mutedSwatch = p.getMutedSwatch();
                                    int backgroundColor = ContextCompat.getColor(getApplicationContext(),
                                            R.color.Orange);
                                    int textColor = ContextCompat.getColor(getApplicationContext(),
                                            R.color.white);

                                    // Check that the Vibrant swatch is available
                                    if (vibrantSwatch != null) {
                                        if (vibrantSwatch.getRgb() != getResources().getColor(R.color.Orange)) {
                                            backgroundColor = vibrantSwatch.getRgb();
                                            textColor = vibrantSwatch.getTitleTextColor();
                                        } else {
                                            backgroundColor = mutedSwatch.getRgb();
                                            textColor = mutedSwatch.getTitleTextColor();
                                        }
                                    }

                                    // Set the toolbar background and text colors
                                    collapsingToolbar.setBackgroundColor(backgroundColor);
                                    //collapsingToolbar.setCollapsedTitleTextColor(textColor);
                                    collapsingToolbar.setStatusBarScrimColor(backgroundColor);
                                    collapsingToolbar.setContentScrimColor(backgroundColor);

                                }
                            });

                        }

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }


                });

    }

    public void updateUISong(Song s) {
        if(s.getTitle().equals("ErrorResponse")){
            Toast.makeText(SongActivity.this, s.getImage(), Toast.LENGTH_LONG).show();
        }else{
            if (s.getDescription().equals("?"))
                s.setDescription(getString(R.string.Description));
            tvDescription.setText(s.getDescription());
            currentSong.setYoutube(s.getYoutube());
            currentSong.setSpotify(s.getSpotify());
            tvArtistSong.setVisibility(View.VISIBLE);
            tvAlbumSong.setVisibility(View.VISIBLE);
            if(s.getAlbum() != null){
                tvAlbumSong.setText(s.getAlbum().getTitle());
            }
            tvListen.setVisibility(View.VISIBLE);
            mbtnYt.setVisibility(View.VISIBLE);
            mbtnSpotify.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
            mbtnLike.setVisibility(View.VISIBLE);
            currentSong.setAlbum(s.getAlbum());
            currentAlbum = currentSong.getAlbum();
            pBLoading.setVisibility(View.GONE);

        }

    }

    public void updateUILiked(LikedElement le) {
        likedElement = new LikedElement(le.getLiked(), le.getDocumentID());
        if(le.getLiked() == 1 && le.getDocumentID() != null){
            mbtnLike.setImageResource(R.drawable.ic_favorite_full);
        }
        else if (le.getLiked() == 2 && le.getDocumentID() != null)
        {
            Toast.makeText(SongActivity.this, R.string.likedSong, Toast.LENGTH_SHORT).show();
            mbtnLike.setImageResource(R.drawable.ic_favorite_full);
        }
        else if (le.getLiked() == 3 && le.getDocumentID() == null){
            Toast.makeText(SongActivity.this, R.string.dislikedSong, Toast.LENGTH_SHORT).show();
            mbtnLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        else if (le.getLiked() == -1)
        {
            Toast.makeText(SongActivity.this, le.getDocumentID(), Toast.LENGTH_SHORT).show();
        }
    }
}


