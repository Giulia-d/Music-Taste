package it.unimib.musictaste.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

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


import it.unimib.musictaste.models.News;
import it.unimib.musictaste.utils.Utils;

public class NewsRepository {
    private final MutableLiveData<List<News>> mNews;
    private Context context;

    public NewsRepository(Context context) {
        this.context = context;
        mNews = new MutableLiveData<>();
    }

    public MutableLiveData<List<News>> getNews(){
        String url = Utils.NEWS_API_REQUEST;
        List<News> news = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
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
                        news.add( new News(title,description, url, img));
                    }
                    mNews.postValue(news);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                news.add(new News(error.getMessage(), "ErrorResponse", null, null));
                mNews.postValue(news);
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
        return mNews;
    }
}
