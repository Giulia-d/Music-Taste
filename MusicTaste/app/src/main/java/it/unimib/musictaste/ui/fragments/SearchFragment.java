package it.unimib.musictaste.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.ui.adapters.ResponseRecyclerViewAdapter;
import it.unimib.musictaste.ui.ArtistActivity;
import it.unimib.musictaste.ui.SongActivity;
import it.unimib.musictaste.utils.MyTouchListener;
import it.unimib.musictaste.viewmodel.search.SearchViewModel;

public class SearchFragment extends Fragment{
    public static final String SONG = "SONG";
    public static final String ARTIST = "ARTIST";
    // TODO: Something to say about this
    static List<Song> results = new ArrayList<>();
    SearchViewModel searchViewModel;
    ResponseRecyclerViewAdapter responseRecyclerViewAdapter;

    /*TO DELETE
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText mSearch = view.findViewById(R.id.etSearch);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.result_list);
        responseRecyclerViewAdapter = new ResponseRecyclerViewAdapter(results, new ResponseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response, int position) {
                Intent intent;
                if(position==0){
                    intent = new Intent(getActivity(), ArtistActivity.class);
                    intent.putExtra(ARTIST, response.getArtist());
                }else {
                    intent = new Intent(getActivity(), SongActivity.class);
                    intent.putExtra(SONG, response);
                }
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(responseRecyclerViewAdapter);
        mSearch.setOnTouchListener(new MyTouchListener(mSearch));
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchViewModel.getResults(mSearch.getText().toString()).observe(getViewLifecycleOwner(), results ->{
                    updateUI(results);
                });
            }
        });
    }

    public void updateUI(List<Song> results) {
        if(results.size()>0){
            if(!results.get(0).getImage().equals("ErrorResponse")){
                this.results.clear();
                this.results.addAll(results);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }else
                Toast.makeText(getContext(), results.get(0).getTitle(), Toast.LENGTH_LONG).show();
        }
    }
}