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
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.unimib.musictaste.fragments.AccountFragment;
import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Target;


public class SongActivity extends AppCompatActivity {

    ImageView imgSong;
    TextView tvArtistSong;
    //TextView tvTitleSong;
    String titleSong;
    //TextView tvLyricsSong;
    TextView tvDescription;
    ImageButton mbtnYt, mbtnSpotify, mbtnLike;
    FirebaseFirestore database;
    boolean liked;
    String documentID;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    Song song;
    ProgressBar pBLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        imgSong = findViewById(R.id.imgSong);
        tvArtistSong = findViewById(R.id.tvArtistSong);
        //tvTitleSong = findViewById(R.id.tvTitleSong);
        //tvLyricsSong = findViewById(R.id.tvLyricsSong);
        tvDescription = findViewById(R.id.tvDescription);
        mbtnYt = findViewById(R.id.btnYoutube);
        mbtnSpotify = findViewById(R.id.btnSpotify);
        mbtnLike = findViewById(R.id.btnLike);
        database = FirebaseFirestore.getInstance();
        liked = false;
        pBLoading = findViewById(R.id.pBLoading);

        Intent intent = getIntent();
        song = intent.getParcelableExtra(SearchFragment.SONG);
        if (song == null) {
            song = intent.getParcelableExtra(AccountFragment.SONG);
        }


        //int tre = intent.getIntExtra(SearchFragment.SONG, 0);
        Picasso.get().load(song.getImage()).transform(new GradientTransformation()).into(imgSong);
        tvArtistSong.setText(song.getArtist());
        //tvTitleSong.setText(song.getTitle());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
        setToolbarColor(song);



        database.collection("likedSongs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("IDuser").equals(uid) &&
                                        document.get("IDsong").equals(song.getId())) {
                                    liked = true;
                                    mbtnLike.setImageResource(R.drawable.ic_favorite_full);
                                    documentID = document.getId();
                                    break;
                                }
                            }
                        }
                    }

                });

        getDescription(song);

        mbtnYt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.getYoutube() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(song.getYoutube()));
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
                if (song.getSpotify() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(song.getSpotify()));
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
                if (liked) {
                    database.collection("likedSongs").document(documentID)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SongActivity.this, R.string.dislikedSong, Toast.LENGTH_LONG).show();
                                    Log.d("Succes", "DocumentSnapshot successfully deleted!");
                                    mbtnLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                    liked = false;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Error", "Error deleting document", e);
                                }
                            });

                } else {
                    Map<String, Object> likedSongs = new HashMap<>();
                    likedSongs.put("IDuser", uid);
                    likedSongs.put("IDsong", song.getId());
                    likedSongs.put("TitleSong", song.getTitle());
                    likedSongs.put("ArtistSong", song.getArtist());
                    likedSongs.put("ImageSong", song.getImage());

// Add a new document with a generated ID
                    database.collection("likedSongs")
                            .add(likedSongs)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(SongActivity.this, R.string.likedSong, Toast.LENGTH_LONG).show();
                                    Log.d("Succes", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    mbtnLike.setImageResource(R.drawable.ic_favorite_full);
                                    liked = true;
                                    documentID = documentReference.getId();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Error", "Error adding document", e);
                                }
                            });
                }

            }
        });
    }


    public String digger(JSONArray children) throws JSONException {
        String description = "";
        for (int j = 0; j < children.length(); j++) {
            if (children.get(j) instanceof String)
                description = description + children.get(j);
            else if (children.getJSONObject(j).has("children"))
                description = description + digger(children.getJSONObject(j).getJSONArray("children"));
        }
        return description;
    }

    public void getDescription(Song song) {
        String url = "https://api.genius.com/songs/" + song.getId();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response").getJSONObject("song").getJSONObject("description").getJSONObject("dom");
                    JSONArray desc = responseDescription.getJSONArray("children");
                    String description = "";
                    for (int i = 0; i < desc.length(); i++) {
                        if (!(desc.get(i) instanceof String)) {
                            JSONArray children = desc.getJSONObject(i).getJSONArray("children");
                            description = description + digger(children);
                        }
                    }
                    if (description.equals("?"))
                        description = getString(R.string.Description);
                    tvDescription.setText(description);
                    pBLoading.setVisibility(View.GONE);
                    //Find youtube and spotify links from response
                    JSONArray media = response.getJSONObject("response").getJSONObject("song").getJSONArray("media");
                    //Log.d("media", media.toString());
                    if (media != null) {
                        for (int k = 0; k < media.length(); k++) {
                            if (media.getJSONObject(k).getString("provider").equals("youtube"))
                                song.setYoutube(media.getJSONObject(k).getString("url"));
                            else if (media.getJSONObject(k).getString("provider").equals("spotify"))
                                song.setSpotify(media.getJSONObject(k).getString("url"));
                        }
                    }
                    mbtnYt.setVisibility(View.VISIBLE);
                    mbtnSpotify.setVisibility(View.VISIBLE);
                    mbtnLike.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + Utils.ACCESS_TOKEN);
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

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
    }


    public void setToolbarColor(Song song) {
        Picasso.get()
                .load(song.getImage())
                .into(new Target() {

                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                        /* Save the bitmap or do something with it here */
                         toolbar = (Toolbar) findViewById(R.id.toolbar);
                         setSupportActionBar(toolbar);

                         collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                         collapsingToolbar.setTitle(song.getTitle());
                         if(bitmap != null) {
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
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}


});

}}

