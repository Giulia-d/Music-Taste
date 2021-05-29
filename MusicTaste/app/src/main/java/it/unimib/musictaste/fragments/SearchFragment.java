package it.unimib.musictaste.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.ResponseRecyclerViewAdapter;
import it.unimib.musictaste.SongActivity;
import it.unimib.musictaste.repositories.SearchCallback;
import it.unimib.musictaste.repositories.SearchRepository;
import it.unimib.musictaste.utils.ApiCall;
import it.unimib.musictaste.utils.JSONParser;
import it.unimib.musictaste.utils.MyTouchListener;
import it.unimib.musictaste.utils.Song;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SearchCallback {
    public static final String SONG = "SONG";
    public static boolean flagAPI = false;
    static List<Song> suggestions = new ArrayList<>();
    private SearchRepository searchRepository;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    ResponseRecyclerViewAdapter responseRecyclerViewAdapter;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //final TextView textView = view.findViewById(R.id.text);
        searchRepository= new SearchRepository(this, getContext());
        EditText mSearch = view.findViewById(R.id.etSearch);
        RecyclerView recyclerView = view.findViewById(R.id.result_list);
        responseRecyclerViewAdapter = new ResponseRecyclerViewAdapter(suggestions, new ResponseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song response) {
                //Log.d("Lista", response);
                Intent intent = new Intent(getActivity(), SongActivity.class);
                intent.putExtra(SONG, response);
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
                //handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                //handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        //AUTO_COMPLETE_DELAY);
                searchRepository.searchSong(mSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(mSearch.getText())) {
                        makeApiCall(mSearch.getText().toString());
                    }
                }
                return false;
            }
        });*/
    }

    @Override
    public void onResponse(List<Song> songs) {
        suggestions.clear();
        suggestions.addAll(songs);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseRecyclerViewAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    /*private void makeApiCall(String text) {
        ApiCall.make(getContext(), text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.d("Your Array Response", response);
                    try {
                        JSONObject res = new JSONObject(response);
                        suggestions.clear();
                        suggestions.addAll(JSONParser.displayResultAPI(res));
                        Log.d("Suggestion", suggestions.toString());
                        responseRecyclerViewAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }*/
}