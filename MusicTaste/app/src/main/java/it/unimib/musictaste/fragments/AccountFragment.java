package it.unimib.musictaste.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.SettingActivity;
import it.unimib.musictaste.SongActivity;
import it.unimib.musictaste.SongRecyclerViewAdapter;
import it.unimib.musictaste.repositories.AccountFBCallback;
import it.unimib.musictaste.repositories.AccountRepository;
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
    TextView txtName,txtNoSongs;
    ImageView imgAccount;
    FirebaseFirestore database;
    String uid;
    SongRecyclerViewAdapter songRecyclerViewAdapter;
    List<Song> likedSongs;
    RecyclerView recyclerView;
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
        recyclerView = view.findViewById(R.id.liked_songs);
        txtNoSongs = view.findViewById(R.id.tvEmptySong);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        likedSongs = new ArrayList<Song>();
        database = FirebaseFirestore.getInstance();
        //dbCall();
        songRecyclerViewAdapter = new SongRecyclerViewAdapter(likedSongs, new SongRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response) {
                Intent intent = new Intent(getActivity(), SongActivity.class);
                intent.putExtra(SONG, response);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songRecyclerViewAdapter);

        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });


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
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(getActivity(), SettingActivity.class);
        startActivity(switchActivityIntent);
    }


    @Override
    public void onResponse(List<Song> likedSongs) {

        if (likedSongs.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            txtNoSongs.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            txtNoSongs.setVisibility(View.GONE);
        }
        this.likedSongs.clear();
        this.likedSongs.addAll(likedSongs);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songRecyclerViewAdapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public void onFailure(String msg) {

    }
}