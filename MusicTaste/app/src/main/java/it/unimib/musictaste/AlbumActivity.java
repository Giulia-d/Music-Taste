package it.unimib.musictaste;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
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

import java.util.ArrayList;
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
import it.unimib.musictaste.utils.LikedElement;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.AlbumRecyclerViewAdapter;
import it.unimib.musictaste.viewmodels.AlbumViewModel;
import it.unimib.musictaste.viewmodels.AlbumViewModelFactory;
import it.unimib.musictaste.viewmodels.ArtistViewModel;
import it.unimib.musictaste.viewmodels.ArtistViewModelFactory;

public class AlbumActivity  extends AppCompatActivity{

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
    LikedElement likedElement;
    AlbumViewModel albumViewModel;
    List <Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        imgAlbum = findViewById(R.id.imgAlbum);
        tvExpTextView = (ExpandableTextView) findViewById(R.id.tvExpandableTextView);
        //tvAlbumDescription = findViewById(R.id.tvAlbumDescription);
        //iewById(R.id.btnAlbumYoutube);
        mbtnAlbumSpotify = findViewById(R.id.btnAlbumSpotify);
        mbtnAlbumLike = findViewById(R.id.btnAlbumLike);

        database = FirebaseFirestore.getInstance();
        //liked = false;
        pBLoadingAlbum = findViewById(R.id.pBLoadingAlbum);
        likedElement = new LikedElement(0, null);

        Intent intent = getIntent();
        currentAlbum = intent.getParcelableExtra(SongActivity.ALBUM);
        if(currentAlbum == null)
            currentAlbum = intent.getParcelableExtra(ArtistActivity.ALBUM);

        currentArtist = currentAlbum.getArtist();
        songs = new ArrayList<>();
        //songs = currentAlbum.getTracks();
        //currentSong.setAlbum(currentAlbum);
        Picasso.get().load(currentAlbum.getImage()).transform(new GradientTransformation()).into(imgAlbum);
        //tvAName.setText(currentArtist.getName());
        //tvTitleSong.setText(song.getTitle());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
        setToolbarColor(currentAlbum);

        RecyclerView recyclerView = findViewById(R.id.tracks_list);
        albumRecyclerViewAdapter = new AlbumRecyclerViewAdapter(songs, new AlbumRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response) {

                Intent intent = new Intent(AlbumActivity.this, SongActivity.class);
                intent.putExtra(SONG, response);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(albumRecyclerViewAdapter);




        //getDescription(currentSong);
        //albumRepository.checkLikedAlbum(uid, currentArtist.getId());
        //albumRepository.getAlbumInfo(currentAlbum.getId());
        //albumRepository.getAlbumTracks(currentAlbum.getId());

        albumViewModel = new ViewModelProvider(this, new AlbumViewModelFactory(
                getApplication(), uid, currentAlbum.getId())).get(AlbumViewModel.class);
        albumViewModel.getDetailsAlbum().observe(this, desc -> {
            showDescription(desc);
        });
        albumViewModel.getLikedElement().observe(this, le ->{
            updateUILiked(le);
        });
        albumViewModel.getAlbumTracks(currentAlbum.getId()).observe(this, tracks ->{
            updateUI(tracks);
        });





        mbtnAlbumSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentArtist.getSpotify() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentArtist.getSpotify()));
                    try {
                        AlbumActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(AlbumActivity.this, R.string.SpotifyError, Toast.LENGTH_LONG).show();
            }
        });

        mbtnAlbumLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likedElement.getLiked() == 1 || likedElement.getLiked() == 2){
                    //SongRepository.deleteLikedSong(documentID);
                    albumViewModel.deleteLikedElement(likedElement.getDocumentID()).observe(AlbumActivity.this, le -> {
                        updateUILiked(le);
                    });
                } else if (likedElement.getLiked() == 0 || likedElement.getLiked() == 3){
                    //ongRepository.addLikedSong(uid, currentSong);
                    albumViewModel.addLikedElement(currentArtist).observe(AlbumActivity.this, le -> {
                        updateUILiked(le);
                    });
                }

            }
        });
    }

    public void showDescription(String desc){
        if (desc.equals("?"))
            desc = getString(R.string.Description);
        tvExpTextView.setText(desc);
        pBLoadingAlbum.setVisibility(View.GONE);
        mbtnAlbumLike.setVisibility(View.VISIBLE);
        mbtnAlbumSpotify.setVisibility(View.VISIBLE);

    }

    public void updateUI(List<Song> tracklist) {
        if (!tracklist.get(0).getImage().equals("error")) {
            songs.clear();
            songs.addAll(tracklist);
            currentAlbum.setTracks(tracklist);
            //pBLoading_home.setVisibility(View.GONE);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    albumRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        } else
            Toast.makeText(this, tracklist.get(0).getTitle(), Toast.LENGTH_LONG).show();
    }

    public void updateUILiked(LikedElement le) {
        if(le.getLiked() == 1 && le.getDocumentID() != null){
            mbtnAlbumLike.setImageResource(R.drawable.ic_favorite_full);
        }
        else if (le.getLiked() == 2 && le.getDocumentID() != null)
        {
            Toast.makeText(AlbumActivity.this, R.string.likedArtist, Toast.LENGTH_LONG).show();
            mbtnAlbumLike.setImageResource(R.drawable.ic_favorite_full);
        }
        else if (le.getLiked() == 3 && le.getDocumentID() == null){
            Toast.makeText(AlbumActivity.this,R.string.DislikedArtists, Toast.LENGTH_LONG).show();
            mbtnAlbumLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        else if (le.getLiked() == -1)
        {
            Toast.makeText(AlbumActivity.this, le.getDocumentID(), Toast.LENGTH_LONG).show();
        }
        likedElement = new LikedElement(le.getLiked(), le.getDocumentID());

    }



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





}
