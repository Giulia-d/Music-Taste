package it.unimib.musictaste.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.musictaste.NewsRecyclerViewAdapter;
import it.unimib.musictaste.R;
import it.unimib.musictaste.SettingActivity;
import it.unimib.musictaste.SongActivity;
import it.unimib.musictaste.SongRecyclerViewAdapter;
import it.unimib.musictaste.utils.News;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    List<News> news;
    TextView prova;
    ProgressBar pBLoading_home;

    Handler handler;

    public static final String NEWS = "NEWS";
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
       // Button btnSetting = root.findViewById(R.id.btnSetting);
       /* btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSetting();
            }
        });*/
        news = new ArrayList<News>();
        getNews(news);
        pBLoading_home = (ProgressBar) root.findViewById(R.id.pBLoading_home);
        return root;
    }

    public void updateSetting(){
        startActivity(new Intent(getActivity(), SettingActivity.class));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_news);




    }

    public void getNews(List<News> news) {
        String url = Utils.NEWS_API_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray articles = response.getJSONArray("articles");
                    for (int i = 0; i < articles.length(); i++){
                        String title = articles.getJSONObject(i).getString("title");
                        String img = articles.getJSONObject(i).getString("urlToImage");
                        String description = articles.getJSONObject(i).getString("description");
                        String url = articles.getJSONObject(i).getString("url");
                        //Log.d("TITLE", title);


                        news.add( new News(title,description, url, img));


                    }
                    newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(news, new NewsRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(News response) {
                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(response.getUrl()));
                            getContext().startActivity(webIntent);

                        }
                    });
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(newsRecyclerViewAdapter);
                    pBLoading_home.setVisibility(View.GONE);

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
                params.put("User-Agent", "Mozilla/5.0");
                return params;
            }
        };





        queue.add(jsonObjectRequest);
    }
}