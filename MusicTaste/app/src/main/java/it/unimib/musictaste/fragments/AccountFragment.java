package it.unimib.musictaste.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.ArtistActivity;
import it.unimib.musictaste.ArtistRecyclerViewAdapter;
import it.unimib.musictaste.R;
import it.unimib.musictaste.SettingActivity;
import it.unimib.musictaste.SongActivity;
import it.unimib.musictaste.SongRecyclerViewAdapter;
import it.unimib.musictaste.repositories.AccountFBCallback;
import it.unimib.musictaste.repositories.AccountRepository;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Song;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements AccountFBCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SONG = "SONGLIKED";
    public static final String ARTIST = "ARTISTLIKED";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageButton btnControl;
    TextView txtMusicTaste,txtNoSongs, txtNoArtist;
    FirebaseFirestore database;
    String uid;
    SongRecyclerViewAdapter songRecyclerViewAdapter;
    ArtistRecyclerViewAdapter artistRecyclerViewAdapter;
    List<Artist> likedArtists;
    List<Song> likedSongs;
    RecyclerView recyclerViewSong, recyclerViewArtist;
    AccountRepository accountRepository;
    FirebaseUser user;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        accountRepository = new AccountRepository(this, getContext());
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        accountRepository.getLikedSongs(uid);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnControl = view.findViewById(R.id.btnControl);
        recyclerViewSong = view.findViewById(R.id.likedSongs);
        recyclerViewArtist = view.findViewById(R.id.likedArtists);
        txtNoSongs = view.findViewById(R.id.tvEmptySong);
        txtNoArtist = view.findViewById(R.id.tvEmptyArtist);
        txtMusicTaste = view.findViewById(R.id.txtMusicTaste);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        txtMusicTaste.setText(getString(R.string.MusicTaste) + ", " + user.getDisplayName());
        likedSongs = new ArrayList<>();
        likedArtists = new ArrayList<>();
        database = FirebaseFirestore.getInstance();

        //Set recycler view for liked songs
        songRecyclerViewAdapter = new SongRecyclerViewAdapter(likedSongs, new SongRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response) {
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
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
                intent.putExtra(ARTIST, response);
                startActivity(intent);
            }
        });
        LinearLayoutManager lmArtis = new LinearLayoutManager(getContext());
        lmArtis.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewArtist.setLayoutManager(lmArtis);
        recyclerViewArtist.setAdapter(artistRecyclerViewAdapter);


        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });

        /*
        if (user != null) {
            // User is signed in
            Log.d("user", "email:" + user.getEmail());
            Log.d("user", "Photo:" + user.getPhotoUrl());
            txtName = view.findViewById(R.id.txtName);
            txtName.setText(user.getDisplayName());
            imgAccount = view.findViewById(R.id.imgAccount);
            if (user.getPhotoUrl() != null) {
                Picasso.get().load(user.getPhotoUrl()).into(imgAccount);
            }


        } else {
            // No user is signed in

        }
        */
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(getActivity(), SettingActivity.class);
        startActivity(switchActivityIntent);
    }

    public void changeStatus(List l,RecyclerView r, TextView t){
        if(l.isEmpty()){
            r.setVisibility(View.GONE);
            t.setVisibility(View.VISIBLE);
        }
        else {
            r.setVisibility(View.VISIBLE);
            t.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(List<Song> likedSongs, List<Artist> likedArtists) {
        /*
        if (likedSongs.isEmpty()){
            recyclerViewSong.setVisibility(View.GONE);
            txtNoSongs.setVisibility(View.VISIBLE);
        }
        else{
            recyclerViewSong.setVisibility(View.VISIBLE);
            txtNoSongs.setVisibility(View.GONE);
        }*/
        changeStatus(likedSongs, recyclerViewSong, txtNoSongs);
        this.likedSongs.clear();
        this.likedSongs.addAll(likedSongs);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songRecyclerViewAdapter.notifyDataSetChanged();

            }
        });

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

    @Override
    public void onFailure(String msg) {

    }
}