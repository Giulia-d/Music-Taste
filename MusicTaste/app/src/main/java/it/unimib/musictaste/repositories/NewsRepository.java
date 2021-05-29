package it.unimib.musictaste.repositories;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import it.unimib.musictaste.utils.News;
import it.unimib.musictaste.utils.Utils;

public class NewsRepository {
    private final NewsCallback newsCallback;
    private Context context;

    public NewsRepository(NewsCallback newsCallback, Context context) {
        this.newsCallback = newsCallback;
        this.context = context;
    }

    public void getNews(){
        String url = Utils.NEWS_API_REQUEST;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<News> news = new ArrayList<>();
                    JSONArray articles = response.getJSONArray("articles");
                    for (int i = 0; i < articles.length(); i++){
                        String title = articles.getJSONObject(i).getString("title");
                        String img = articles.getJSONObject(i).getString("urlToImage");
                        String description = articles.getJSONObject(i).getString("description");
                        String url = articles.getJSONObject(i).getString("url");
                        //Log.d("TITLE", title);


                        news.add( new News(title,description, url, img));


                    }
                    newsCallback.onResponse(news);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                newsCallback.onFailure(error.getMessage());
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
