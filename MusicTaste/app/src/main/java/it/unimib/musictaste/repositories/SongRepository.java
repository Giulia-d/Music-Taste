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

import java.util.HashMap;
import java.util.Map;

import it.unimib.musictaste.utils.Utils;

public class SongRepository {
    private final SongCallback songCallback;
    private final Context context;

    public SongRepository(SongCallback songCallback, Context context){
        this.songCallback = songCallback;
        this.context = context;
    }

    public void getSongInfo(String songId){
        String url = "https://api.genius.com/songs/" + songId;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response").getJSONObject("song").getJSONObject("description").getJSONObject("dom");
                    JSONArray desc = responseDescription.getJSONArray("children");
                    String description = "";
                    for (int i = 0; i < desc.length(); i++) {
                        if (!(desc.get(i) instanceof String)) {
                            JSONArray children = desc.getJSONObject(i).getJSONArray("children");
                            description = description + digger(children);
                        }
                    }



                    //Find youtube and spotify links from response
                    JSONArray media = response.getJSONObject("response").getJSONObject("song").getJSONArray("media");
                    //Log.d("media", media.toString());
                    String youtube = "";
                    String spotify = "";
                    if (media != null) {
                        for (int k = 0; k < media.length(); k++) {
                            if (media.getJSONObject(k).getString("provider").equals("youtube"))
                                youtube = (media.getJSONObject(k).getString("url"));
                            else if (media.getJSONObject(k).getString("provider").equals("spotify"))
                                spotify = (media.getJSONObject(k).getString("url"));
                        }
                    }

                    songCallback.onResponse(description, youtube, spotify);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                songCallback.onFailure(error.getMessage());
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

    private String digger(JSONArray children) throws JSONException {
        String description = "";
        for (int j = 0; j < children.length(); j++) {
            if (children.get(j) instanceof String)
                description = description + children.get(j);
            else if (children.getJSONObject(j).has("children"))
                description = description + digger(children.getJSONObject(j).getJSONArray("children"));
        }
        return description;
    }
}
