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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.unimib.musictaste.fragments.AccountFragment;
import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.repositories.ArtistCallback;
import it.unimib.musictaste.repositories.ArtistFBCallback;
import it.unimib.musictaste.repositories.ArtistRepository;
import it.unimib.musictaste.repositories.SongRepository;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Artist;

import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;


public class ArtistActivity extends AppCompatActivity implements ArtistCallback, ArtistFBCallback {
    ImageView imgA;
    TextView tvAName;
    String titleSong;
    TextView tvADescription;
    ImageButton mbtnAYt, mbtnASpotify, mbtnALike;
    FirebaseFirestore database;
    boolean liked;
    String documentID;
    Toolbar toolbarA;
    CollapsingToolbarLayout collapsingToolbarA;
    Song currentSong;
    Artist currentArtist;
    ArtistRepository artistRepository;
    ProgressBar pBLoadingA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        imgA = findViewById(R.id.imgArtist);
        tvAName = findViewById(R.id.tvArtistName);
        tvADescription = findViewById(R.id.tvArtistDescription);
        mbtnAYt = findViewById(R.id.btnArtistYoutube);
        mbtnASpotify = findViewById(R.id.btnArtistSpotify);
        mbtnALike = findViewById(R.id.btnArtistLike);
        database = FirebaseFirestore.getInstance();
        //liked = false;
        pBLoadingA = findViewById(R.id.pBLoadingArtist);
        artistRepository = new ArtistRepository(this, this, this);
        Intent intent = getIntent();
        currentSong = intent.getParcelableExtra(SearchFragment.SONG);
        if (currentSong == null) {
            currentSong = intent.getParcelableExtra(SongActivity.ARTIST);
        }
        currentArtist = currentSong.getArtist();

        Picasso.get().load(currentArtist.getImage()).transform(new GradientTransformation()).into(imgA);
        tvAName.setText(currentArtist.getName());
        //tvTitleSong.setText(song.getTitle());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
        setToolbarColor(currentArtist);





        //getDescription(currentSong);
        artistRepository.checkLikedArtist(uid, currentArtist.getId());
        artistRepository.getArtistInfo(currentArtist.getId());

        /*
        mbtnAYt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentArtist.getYoutube() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentArtist.getYoutube()));
                    try {
                        ArtistActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(ArtistActivity.this, R.string.YoutubeError, Toast.LENGTH_LONG).show();
            }
        });

        mbtnSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentArtist.getSpotify() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentArtist.getSpotify()));
                    try {
                        ArtistActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(ArtistActivity.this, R.string.SpotifyError, Toast.LENGTH_LONG).show();
            }
        });
         */
        //getLyrics(song);


        Log.d("AAAUSER", uid);
        mbtnALike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liked){
                    ArtistRepository.deleteLikedArtist(documentID);
                } else{
                    ArtistRepository.addLikedArtist(uid, currentArtist);
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


    public void setToolbarColor(Artist artist) {
        Picasso.get()
                .load(artist.getImage())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Save the bitmap or do something with it here
                        toolbarA = (Toolbar) findViewById(R.id.toolbarArtist);
                        setSupportActionBar(toolbarA);

                        collapsingToolbarA = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarArtist);
                        collapsingToolbarA.setTitle(artist.getName());
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
                                    collapsingToolbarA.setBackgroundColor(backgroundColor);
                                    collapsingToolbarA.setCollapsedTitleTextColor(textColor);
                                    collapsingToolbarA.setStatusBarScrimColor(backgroundColor);
                                    collapsingToolbarA.setContentScrimColor(backgroundColor);

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
    public void onResponse(String description, String youtube, String spotify) {
        if (description.equals("?"))
            description = getString(R.string.Description);
        tvADescription.setText(description);
        pBLoadingA.setVisibility(View.GONE);
        //currentArtist.setYoutube(youtube);
       //currentArtist.setSpotify(spotify);
        //mbtnAYt.setVisibility(View.VISIBLE);
        //mbtnASpotify.setVisibility(View.VISIBLE);
        mbtnALike.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(ArtistActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseFB(boolean liked, String documentId, boolean firstLike) {
        if(liked && documentId!=null){
            if(firstLike==true)
                Toast.makeText(ArtistActivity.this, R.string.likedSong, Toast.LENGTH_LONG).show();
            this.liked= liked;
            this.documentID=documentId;
            mbtnALike.setImageResource(R.drawable.ic_favorite_full);
        }else if(!liked && documentId==null){
            if(firstLike==true) {
                Toast.makeText(ArtistActivity.this, R.string.dislikedSong, Toast.LENGTH_LONG).show();
                this.liked=liked;
                mbtnALike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }

    }

    @Override
    public void onFailureFB(String msg) {
        Toast.makeText(ArtistActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}

