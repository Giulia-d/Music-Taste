package it.unimib.musictaste.ui.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.models.Album;
import it.unimib.musictaste.models.Artist;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.ui.adapters.AlbumRecyclerViewAdapter;
import it.unimib.musictaste.ui.AlbumActivity;
import it.unimib.musictaste.ui.ArtistActivity;
import it.unimib.musictaste.ui.adapters.ArtistRecyclerViewAdapter;
import it.unimib.musictaste.ui.LoginActivity;
import it.unimib.musictaste.ui.SongActivity;
import it.unimib.musictaste.ui.adapters.SongRecyclerViewAdapter;
import it.unimib.musictaste.viewmodel.account.AccountViewModel;
import it.unimib.musictaste.viewmodel.account.AccountViewModelFactory;
import it.unimib.musictaste.viewmodel.user.UserViewModel;



public class AccountFragment extends Fragment {

    public static final String SONG = "SONGLIKED";
    public static final String ARTIST = "ARTISTLIKED";
    public static final String ALBUM = "ALBUMLIKED";
    boolean leftFragment = false;
    ImageButton btnLogout;
    TextView txtMusicTaste,txtNoSongs, txtNoArtist, txtNoAlbum;
    FirebaseFirestore database;
    String uid;
    SongRecyclerViewAdapter songRecyclerViewAdapter;
    ArtistRecyclerViewAdapter artistRecyclerViewAdapter;
    AlbumRecyclerViewAdapter albumRecyclerViewAdapter;
    List<Artist> likedArtists;
    List<Song> likedSongs;
    List<Album> likedAlbums;
    RecyclerView recyclerViewSong, recyclerViewArtist, recyclerViewAlbum;
    FirebaseUser user;
    AccountViewModel accountViewModel;
    UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        accountViewModel.getLikedSongs(leftFragment).observe(getViewLifecycleOwner(), lSongs ->{
            updateUISong(lSongs);
            leftFragment = false;
        });
        accountViewModel.getLikedArtists(leftFragment).observe(getViewLifecycleOwner(), lArtists ->{
            updateUIArtist(lArtists);
            leftFragment = false;
        });
        accountViewModel.getLikedAlbums(leftFragment).observe(getViewLifecycleOwner(), lAlbums ->{
            updateUIAlbum(lAlbums);
            leftFragment = false;
        });
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = view.findViewById(R.id.btnLogout);
        recyclerViewSong = view.findViewById(R.id.likedSongs);
        recyclerViewArtist = view.findViewById(R.id.likedArtists);
        recyclerViewAlbum = view.findViewById(R.id.likedAlbums);
        txtNoSongs = view.findViewById(R.id.tvEmptySong);
        txtNoArtist = view.findViewById(R.id.tvEmptyArtist);
        txtNoAlbum = view.findViewById(R.id.tvEmptyAlbum);
        txtMusicTaste = view.findViewById(R.id.txtMusicTaste);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        txtMusicTaste.setText(getString(R.string.MusicTaste) + ", " + user.getDisplayName());
        likedSongs = new ArrayList<>();
        likedArtists = new ArrayList<>();
        likedAlbums = new ArrayList<>();
        database = FirebaseFirestore.getInstance();

        //Creation of view model using parameters
        accountViewModel = new ViewModelProvider(this, new AccountViewModelFactory(
                requireActivity().getApplication(), uid)).get(AccountViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //Set recycler view for liked songs
        songRecyclerViewAdapter = new SongRecyclerViewAdapter(likedSongs, new SongRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response) {
                leftFragment = true;
                Intent intent = new Intent(getActivity(), SongActivity.class);
                intent.putExtra(SONG, response);
                startActivity(intent);
            }
        });
        LinearLayoutManager lmSong = new LinearLayoutManager(getContext());
        lmSong.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewSong.setLayoutManager(lmSong);
        recyclerViewSong.setAdapter(songRecyclerViewAdapter);

        //Set recycler view for liked artists
        artistRecyclerViewAdapter = new ArtistRecyclerViewAdapter(likedArtists, new ArtistRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Artist response) {
                leftFragment = true;
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
                intent.putExtra(ARTIST, response);
                startActivity(intent);
            }
        });
        LinearLayoutManager lmArtis = new LinearLayoutManager(getContext());
        lmArtis.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewArtist.setLayoutManager(lmArtis);
        recyclerViewArtist.setAdapter(artistRecyclerViewAdapter);

        //Set recycler view for liked albums
        albumRecyclerViewAdapter = new AlbumRecyclerViewAdapter(likedAlbums, new AlbumRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album album) {
                leftFragment = true;
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra(ALBUM, album);
                startActivity(intent);
            }
        });
        LinearLayoutManager lmAlbum = new LinearLayoutManager(getContext());
        lmAlbum.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewAlbum.setLayoutManager(lmAlbum);
        recyclerViewAlbum.setAdapter(albumRecyclerViewAdapter);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }

        });
    }

    private void updateUISong(List<Song> likedSongs) {
        changeStatus(likedSongs, recyclerViewSong, txtNoSongs);
        this.likedSongs.clear();
        this.likedSongs.addAll(likedSongs);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
    private void updateUIArtist(List<Artist> likedArtists){
        changeStatus(likedArtists, recyclerViewArtist, txtNoArtist);
        this.likedArtists.clear();
        this.likedArtists.addAll(likedArtists);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                artistRecyclerViewAdapter.notifyDataSetChanged();

            }
        });
    }

    private void updateUIAlbum(List<Album> likedAlbums){
        changeStatus(likedAlbums, recyclerViewAlbum, txtNoAlbum);
        this.likedAlbums.clear();
        this.likedAlbums.addAll(likedAlbums);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                albumRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void changeStatus(List l,RecyclerView r, TextView t){
        if(l.isEmpty()){
            r.setVisibility(View.GONE);
            t.setVisibility(View.VISIBLE);
        }
        else {
            r.setVisibility(View.VISIBLE);
            t.setVisibility(View.GONE);
        }
    }
    private void signOut() {
        userViewModel.deleteUserId();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}