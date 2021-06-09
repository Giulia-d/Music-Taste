package it.unimib.musictaste;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.fragments.AccountFragment;
import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.repositories.ArtistCallback;
import it.unimib.musictaste.repositories.ArtistFBCallback;
import it.unimib.musictaste.repositories.ArtistRepository;
import it.unimib.musictaste.repositories.ArtistsAlbumsCallback;
import it.unimib.musictaste.repositories.GeniusCallBack;
import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.LikedElement;
import it.unimib.musictaste.utils.News;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.viewmodels.ArtistViewModel;
import it.unimib.musictaste.viewmodels.ArtistViewModelFactory;


public class ArtistActivity extends AppCompatActivity  {
    public static final String ALBUM = "ALBUM";

    ImageView imgA;
    String titleSong;
    TextView tvADescription;
    ExpandableTextView tvExpTextView;
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
    ArtistAlbumsRecyclerViewAdapter artistAlbumsRecyclerViewAdapter;
    List<Album> album_list;
    ArtistViewModel artistViewModel;
    LikedElement likedElement;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        imgA = findViewById(R.id.imgArtist);
        tvADescription = findViewById(R.id.expandable_text);
        tvExpTextView = (ExpandableTextView) findViewById(R.id.tvArtistExpandableTextView);
        //mbtnAYt = findViewById(R.id.btnArtistYoutube);
        mbtnASpotify = findViewById(R.id.btnArtistSpotify);
        mbtnALike = findViewById(R.id.btnArtistLike);
        database = FirebaseFirestore.getInstance();
        //liked = false;
        pBLoadingA = findViewById(R.id.pBLoadingArtist);
        likedElement = new LikedElement(0, null);
        album_list = new ArrayList<>();
        Intent intent = getIntent();

        RecyclerView recyclerView = findViewById(R.id.album_list);
        artistAlbumsRecyclerViewAdapter = new ArtistAlbumsRecyclerViewAdapter(album_list, new ArtistAlbumsRecyclerViewAdapter.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(Album response) {
                //artistRepository.getGeniusInfo(response);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(artistAlbumsRecyclerViewAdapter);

        /*
        currentSong = intent.getParcelableExtra(SearchFragment.SONG);
        if (currentSong == null) {
            currentSong = intent.getParcelableExtra(SongActivity.ARTIST);
        }
        currentArtist = currentSong.getArtist();*/


        currentArtist = intent.getParcelableExtra(SearchFragment.ARTIST);
        if (currentArtist == null){
            currentArtist = intent.getParcelableExtra(SongActivity.ARTIST);
            if(currentArtist == null){
                currentArtist = intent.getParcelableExtra(AccountFragment.ARTIST);
            }
        }

        Picasso.get().load(currentArtist.getImage()).transform(new GradientTransformation()).into(imgA);
        setToolbarColor(currentArtist);



        artistViewModel = new ViewModelProvider(this, new ArtistViewModelFactory(
                getApplication(), uid, currentArtist.getId())).get(ArtistViewModel.class);


        //getDescription(currentSong);
       artistViewModel.getDetailsArtist().observe(this, desc -> {
          showDescription(desc);
       });

       artistViewModel.getLikedElement().observe(this, le ->{
           updateUILiked(le);
       });





        mbtnASpotify.setOnClickListener(new View.OnClickListener() {
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




        Log.d("AAAUSER", uid);
        mbtnALike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likedElement.getLiked() == 1 || likedElement.getLiked() == 2){
                    //SongRepository.deleteLikedSong(documentID);
                    artistViewModel.deleteLikedElement(likedElement.getDocumentID()).observe(ArtistActivity.this, le -> {
                        updateUILiked(le);
                    });
                } else if (likedElement.getLiked() == 0 || likedElement.getLiked() == 3){
                    //ongRepository.addLikedSong(uid, currentSong);
                    artistViewModel.addLikedElement(currentArtist).observe(ArtistActivity.this, le -> {
                        updateUILiked(le);
                    });
                }

            }
        });

        try {
            artistViewModel.getListAlbum(currentArtist).observe(this, listAlbum ->{

                updateUI(listAlbum);
            });
            //artistRepository.getArtistAlbums(currentArtist.getName());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SpotifyWebApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void updateUI(List<Album> albums) {
        if (!albums.get(0).getImage().equals("error")) {
            this.album_list.clear();
            this.album_list.addAll(albums);
            //pBLoading_home.setVisibility(View.GONE);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    artistAlbumsRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        } else
            Toast.makeText(this, albums.get(0).getTitle(), Toast.LENGTH_LONG).show();
    }

    public void showDescription(String desc){
        if (desc.equals("?"))
            desc = getString(R.string.Description);
        tvExpTextView.setText(desc);
        pBLoadingA.setVisibility(View.GONE);
        mbtnALike.setVisibility(View.VISIBLE);

    }

    public void updateUILiked(LikedElement le) {
        if(le.getLiked() == 1 && le.getDocumentID() != null){
            mbtnALike.setImageResource(R.drawable.ic_favorite_full);
        }
        else if (le.getLiked() == 2 && le.getDocumentID() != null)
        {
            Toast.makeText(ArtistActivity.this, R.string.likedArtist, Toast.LENGTH_LONG).show();
            mbtnALike.setImageResource(R.drawable.ic_favorite_full);
        }
        else if (le.getLiked() == 3 && le.getDocumentID() == null){
            Toast.makeText(ArtistActivity.this,R.string.DislikedArtists, Toast.LENGTH_LONG).show();
            mbtnALike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        else if (le.getLiked() == -1)
        {
            Toast.makeText(ArtistActivity.this, le.getDocumentID(), Toast.LENGTH_LONG).show();
        }
        likedElement = new LikedElement(le.getLiked(), le.getDocumentID());

    }





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


    public void initRecyclerView(){

    }




}

