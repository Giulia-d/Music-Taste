package it.unimib.musictaste;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import it.unimib.musictaste.fragments.AccountFragment;
import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.repositories.SongCallback;
import it.unimib.musictaste.repositories.SongFBCallback;
import it.unimib.musictaste.repositories.SongRepository;
import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Song;


public class SongActivity extends AppCompatActivity implements SongCallback, SongFBCallback {

    ImageView imgSong;
    TextView tvArtistSong, tvAlbumSong, tvDescription, tvListen;
    ImageButton mbtnYt, mbtnSpotify, mbtnLike;
    FirebaseFirestore database;
    boolean liked;
    String documentID;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    Song currentSong;
    Album currentAlbum;
    SongRepository songRepository;
    ProgressBar pBLoading;
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
        liked = false;
        pBLoading = findViewById(R.id.pBLoading);
        songRepository = new SongRepository(this, this, this);

        Intent intent = getIntent();
        currentSong = intent.getParcelableExtra(SearchFragment.SONG);
        if (currentSong == null) {
            currentSong = intent.getParcelableExtra(AccountFragment.SONG);
        }


        //int tre = intent.getIntExtra(SearchFragment.SONG, 0);
        Picasso.get().load(currentSong.getImage()).transform(new GradientTransformation()).into(imgSong);
        tvArtistSong.setText(currentSong.getArtist().getName());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
        setToolbarColor(currentSong);


        //getDescription(currentSong);
        songRepository.checkLikedSongs(uid, currentSong.getId());
        songRepository.getSongInfo(currentSong.getId());

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
                Intent intent = new Intent(SongActivity.this, AlbumActivity.class);
                intent.putExtra(ARTIST, currentSong);
                intent.putExtra(ALBUM, currentAlbum);
                startActivity(intent);
            }

        });

        mbtnYt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong.getYoutube() != null) {
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
                if (currentSong.getSpotify() != null) {
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
        //getLyrics(song);


        Log.d("AAAUSER", uid);
        mbtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liked){
                    SongRepository.deleteLikedSong(documentID);
                } else{
                    SongRepository.addLikedSong(uid, currentSong);
                }

            }
        });
    }



    /*
    public void getLyrics(Song song) {
        String url = "https://api.lyrics.ovh/v1/" + song.getArtist() + "/" + song.getTitle();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("lyrics", response.toString());
                    tvDescription.setText(response.getString("lyrics"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }*/


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
                                            R.color.DarkGray);
                                    int textColor = ContextCompat.getColor(getApplicationContext(),
                                            R.color.white);

                                    // Check that the Vibrant swatch is available
                                    if (vibrantSwatch != null) {
                                        if (vibrantSwatch.getRgb() != getResources().getColor(R.color.DarkGray)) {
                                            backgroundColor = vibrantSwatch.getRgb();
                                            textColor = vibrantSwatch.getTitleTextColor();
                                        } else {
                                            backgroundColor = mutedSwatch.getRgb();
                                            textColor = mutedSwatch.getTitleTextColor();
                                        }
                                    }

                                    // Set the toolbar background and text colors
                                    collapsingToolbar.setBackgroundColor(backgroundColor);
                                    collapsingToolbar.setCollapsedTitleTextColor(textColor);
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

    @Override
    public void onResponse(String description, String youtube, String spotify, Album album) {
        if (description.equals("?"))
            description = getString(R.string.Description);
        tvDescription.setText(description);
        currentSong.setYoutube(youtube);
        currentSong.setSpotify(spotify);
        tvArtistSong.setVisibility(View.VISIBLE);
        tvAlbumSong.setVisibility(View.VISIBLE);
        tvListen.setVisibility(View.VISIBLE);
        mbtnYt.setVisibility(View.VISIBLE);
        mbtnSpotify.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        mbtnLike.setVisibility(View.VISIBLE);
        currentSong.setAlbum(album);
        tvAlbumSong.setText(currentSong.getAlbum().getTitle());
        currentAlbum = currentSong.getAlbum();
        pBLoading.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(SongActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseFB(boolean liked, String documentId, boolean firstLike) {
        if(liked && documentId!=null){
            if(firstLike==true)
                Toast.makeText(SongActivity.this, R.string.likedSong, Toast.LENGTH_LONG).show();
            this.liked = liked;
            this.documentID=documentId;
            mbtnLike.setImageResource(R.drawable.ic_favorite_full);
        }else if(!liked && documentId==null){
            if(firstLike==true){
                Toast.makeText(SongActivity.this, R.string.dislikedSong, Toast.LENGTH_LONG).show();
                this.liked = liked;
                mbtnLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    @Override
    public void onFailureFB(String msg) {
        Toast.makeText(SongActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}

