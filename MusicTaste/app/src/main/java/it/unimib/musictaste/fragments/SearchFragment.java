package it.unimib.musictaste.fragments;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.musictaste.R;
import it.unimib.musictaste.ResponseRecyclerViewAdapter;
import it.unimib.musictaste.utils.ApiCall;
import it.unimib.musictaste.utils.HandleAPICalls;
import it.unimib.musictaste.utils.JSONParser;
import it.unimib.musictaste.utils.Semaphore;
import it.unimib.musictaste.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    public static boolean flagAPI = false;
    static List<String> suggestions = new ArrayList<>();

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
        final TextView textView = view.findViewById(R.id.text);
        EditText mSearch = view.findViewById(R.id.etSearch);
        RecyclerView recyclerView = view.findViewById(R.id.result_list);
        responseRecyclerViewAdapter = new ResponseRecyclerViewAdapter(suggestions, new ResponseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String response) {
                Log.d("Lista", response);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(responseRecyclerViewAdapter);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //this will call your method every time the user stops typing, if you want to call it for each letter, call it in onTextChanged
                /*
                Log.d("Search", s.toString());
                JSONObject resAPI = HandleAPICalls.querySearch(s.toString(), getContext());
                try {
                    if(flagAPI==true) {
                        suggestions.clear();
                        suggestions.addAll(JSONParser.displayResultAPI(resAPI));
                        Log.d("Suggestion", suggestions.toString());
                        responseRecyclerViewAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(mSearch.getText())) {
                        makeApiCall(mSearch.getText().toString());
                    }
                }
                return false;
            }
        });
    }

    private void makeApiCall(String text) {
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
    }
}