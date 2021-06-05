package it.unimib.musictaste;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.repositories.AlbumCallback;
import it.unimib.musictaste.repositories.AlbumTracksCallback;
import it.unimib.musictaste.repositories.ArtistCallback;
import it.unimib.musictaste.repositories.ArtistFBCallback;
import it.unimib.musictaste.repositories.ArtistRepository;
import it.unimib.musictaste.repositories.AlbumRepository;
import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.AlbumRecyclerViewAdapter;

public class AlbumActivity  extends AppCompatActivity implements AlbumCallback, AlbumTracksCallback {

    public static final String SONG = "SONG";
    ImageView imgAlbum;
    //TextView tvAlbumDescription;
    ImageButton mbtnAlbumYt, mbtnAlbumSpotify, mbtnAlbumLike;
    FirebaseFirestore database;
    boolean liked;
    String documentID;
    Toolbar toolbarAlbum;
    CollapsingToolbarLayout collapsingToolbarAlbum;
    Song currentSong;
    Artist currentArtist;
    Album currentAlbum;
    AlbumRepository albumRepository;
    ProgressBar pBLoadingAlbum;
    ExpandableTextView tvExpTextView;
    AlbumRecyclerViewAdapter albumRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        imgAlbum = findViewById(R.id.imgAlbum);
        tvExpTextView = (ExpandableTextView) findViewById(R.id.tvExpandableTextView);
        //tvAlbumDescription = findViewById(R.id.tvAlbumDescription);
        mbtnAlbumYt = findViewById(R.id.btnAlbumYoutube);
        mbtnAlbumSpotify = findViewById(R.id.btnAlbumSpotify);
        mbtnAlbumLike = findViewById(R.id.btnAlbumLike);
        database = FirebaseFirestore.getInstance();
        //liked = false;
        pBLoadingAlbum = findViewById(R.id.pBLoadingAlbum);
        albumRepository = new AlbumRepository(this, this, this);

        Intent intent = getIntent();
        /*
        currentSong = intent.getParcelableExtra(SongActivity.ARTIST);
        currentAlbum = intent.getParcelableExtra(SongActivity.ALBUM);
        if(currentAlbum == null){*/
            currentArtist = intent.getParcelableExtra(ArtistActivity.ARTISTA);
            currentAlbum = intent.getParcelableExtra(ArtistActivity.ALBUMA);
            currentAlbum.setId(intent.getParcelableExtra(ArtistActivity.ID));

            currentArtist = currentSong.getArtist();
            currentSong.setAlbum(currentAlbum);
        Picasso.get().load(currentAlbum.getImage()).transform(new GradientTransformation()).into(imgAlbum);
        //tvAName.setText(currentArtist.getName());
        //tvTitleSong.setText(song.getTitle());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
        setToolbarColor(currentAlbum);


        //getDescription(currentSong);
        //albumRepository.checkLikedAlbum(uid, currentArtist.getId());
        albumRepository.getAlbumInfo(currentAlbum.getId());
        albumRepository.getAlbumTracks(currentAlbum.getId());
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
        /*
        mbtnALike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liked){
                    ArtistRepository.deleteLikedArtist(documentID);
                } else{
                    ArtistRepository.addLikedArtist(uid, currentArtist);
                }

            }
        });*/
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


    public void setToolbarColor(Album album) {
        Picasso.get()
                .load(album.getImage())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Save the bitmap or do something with it here
                        toolbarAlbum = (Toolbar) findViewById(R.id.toolbarAlbum);
                        setSupportActionBar(toolbarAlbum);

                        collapsingToolbarAlbum = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarAlbum);
                        collapsingToolbarAlbum.setTitle(album.getTitle());
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
                                    collapsingToolbarAlbum.setBackgroundColor(backgroundColor);
                                    collapsingToolbarAlbum.setCollapsedTitleTextColor(textColor);
                                    collapsingToolbarAlbum.setStatusBarScrimColor(backgroundColor);
                                    collapsingToolbarAlbum.setContentScrimColor(backgroundColor);

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
    public void onResponse(String description, String date) {
        if (description.equals("?"))
            description = getString(R.string.Description);
        tvExpTextView.setText(description);
        pBLoadingAlbum.setVisibility(View.GONE);
        //currentArtist.setYoutube(youtube);
        //currentArtist.setSpotify(spotify);
        mbtnAlbumYt.setVisibility(View.VISIBLE);
        mbtnAlbumSpotify.setVisibility(View.VISIBLE);
        //mbtnALike.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(AlbumActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseTracks(List<Song> tracks, int nTracks) {
        currentAlbum.setTracks(tracks);
        initRecyclerView();
    }

    @Override
    public void onFailureTracks(String msg) {
        Toast.makeText(AlbumActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.tracks_list);
        albumRecyclerViewAdapter = new AlbumRecyclerViewAdapter(currentAlbum.getTracks(), new AlbumRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response) {
                albumRecyclerViewAdapter.getItemCount();

                Intent intent = new Intent(AlbumActivity.this, SongActivity.class);
                intent.putExtra(SONG, response);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(albumRecyclerViewAdapter);
    }
/*
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

 */


}
