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
import java.util.List;
import java.util.Map;

import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;

public class AlbumRepository {
    private final AlbumCallback albumCallback;
    private final AlbumTracksCallback albumTracksCallback;
    private final Context context;

    public AlbumRepository(AlbumCallback albumCallback, Context context, AlbumTracksCallback albumTracksCallback) {
        this.albumCallback = albumCallback;
        this.albumTracksCallback = albumTracksCallback;
        this.context = context;
    }

    public void getAlbumInfo(String albumId) {
        String url = "https://api.genius.com/albums/" + albumId;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response").getJSONObject("album").getJSONObject("description_annotation"); //getJSONObject("annotations").getJSONObject("0").getJSONObject("body").getJSONObject("dom");
                    JSONArray jsonArray = responseDescription.getJSONArray("annotations");
                    responseDescription = jsonArray.getJSONObject(0).getJSONObject("body").getJSONObject("dom");
                    JSONArray desc = responseDescription.getJSONArray("children");
                    String description = "";
                    for (int i = 0; i < desc.length(); i++) {
                        if (!(desc.get(i) instanceof String)) {
                            JSONArray children = desc.getJSONObject(i).getJSONArray("children");
                            description = description + digger(children);
                        }
                    }

                    JSONObject album = response.getJSONObject("response").getJSONObject("album").getJSONObject("release_date_components");
                    String date =  album.getString("day") + "/" + album.getString("month") + "/" + album.getString("year");


                    albumCallback.onResponse(description, date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                albumCallback.onFailure(error.getMessage());
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

    public void getAlbumTracks(String albumId) {
        String url = "https://api.genius.com/albums/" + albumId + "/tracks";
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response");
                    JSONArray desc = responseDescription.getJSONArray("tracks");
                    List<Song> tracks = new ArrayList<Song>();
                    for (int i = 0; i < desc.length(); i++) {
                        JSONObject s = desc.getJSONObject(i).getJSONObject("song");
                        String sTitle = s.getString("title");
                        String sId = s.getString("id");
                        String sImage = s.getString("song_art_image_url");
                        String aName = s.getJSONObject("primary_artist").getString("name");
                        String aImage = s.getJSONObject("primary_artist").getString("image_url");
                        String aId = s.getJSONObject("primary_artist").getString("id");
                        Artist a = new Artist(aName, aImage, aId);
                        tracks.add(new Song(sTitle, sImage, sId, a));
                    }
                    int nTracks = tracks.size();

                    albumTracksCallback.onResponseTracks(tracks, nTracks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                albumTracksCallback.onFailureTracks(error.getMessage());
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
