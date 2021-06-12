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


import it.unimib.musictaste.models.Artist;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.utils.Utils;

public class SearchRepository {
    private final MutableLiveData<List<Song>> mResults;
    private final Context context;

    public SearchRepository(Context context) {
        this.context = context;
        mResults = new MutableLiveData<>();
    }

    public MutableLiveData<List<Song>> searchSong(String text){
        String url = "https://api.genius.com/search/?q=" + text;
        ArrayList<Song> results = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject newRes = response.getJSONObject("response");
                    JSONArray hits = newRes.getJSONArray("hits");
                    for (int i = 0; i < hits.length(); i++){
                        String title = hits.getJSONObject(i).getJSONObject("result").getString("title");
                        String img = hits.getJSONObject(i).getJSONObject("result").getString("header_image_url");
                        String id = hits.getJSONObject(i).getJSONObject("result").getString("id");
                        String artist = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("name");
                        String artistImg = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("image_url");
                        String idArtist = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("id");
                        Artist a = new Artist(artist, artistImg, idArtist);
                        results.add(new Song(title, img, id, a));
                    }
                   mResults.postValue(results);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                results.add(new Song(error.getMessage(), "ErrorResponse",null,null));
                mResults.postValue(results);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + Utils.ACCESS_TOKEN);
                return params;
            }
        };
        queue.add(jsonObjectRequest);
        return mResults;
    }
}
