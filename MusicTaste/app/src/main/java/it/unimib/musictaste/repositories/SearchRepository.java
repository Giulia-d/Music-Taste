package it.unimib.musictaste.repositories;

import android.content.Context;
import android.util.Log;

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
import java.util.Map;

import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;

public class SearchRepository {
    private final SearchCallback searchCallback;
    private final Context context;

    public SearchRepository(SearchCallback searchCallback, Context context) {
        this.searchCallback = searchCallback;
        this.context = context;
    }

    public void searchSong(String text){
        String url = "https://api.genius.com/search/?q=" + text;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<Song> resp = new ArrayList<Song>();
                    JSONObject newRes = response.getJSONObject("response");
                    JSONArray hits = newRes.getJSONArray("hits");
                    for (int i = 0; i < hits.length(); i++){
                        String title = hits.getJSONObject(i).getJSONObject("result").getString("title");
                        String img = hits.getJSONObject(i).getJSONObject("result").getString("header_image_url");
                        String id = hits.getJSONObject(i).getJSONObject("result").getString("id");
                        String artist = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("name");
                        String artistImg = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("image_url");
                        Log.d("Titolo", title);
                        resp.add(new Song(title, img, id,artist, artistImg));
                        Log.d("RESP", newRes.toString());
            /*SearchFragment.suggestions.add(title);
            Log.d("SUGGESTION",SearchFragment.suggestions.toString());*/
                    }
                   searchCallback.onResponse(resp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                searchCallback.onFailure(error.getMessage());
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
    }
}
