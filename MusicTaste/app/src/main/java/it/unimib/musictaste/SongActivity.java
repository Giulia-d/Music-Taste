package it.unimib.musictaste;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;


public class SongActivity extends AppCompatActivity {

    ImageView imgSong;
    TextView tvArtistSong;
    TextView tvTitleSong;
    TextView tvLyricsSong;
    TextView tvDescription;
    ImageButton mbtnYt, mbtnSpotify, mbtnLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);


        imgSong = findViewById(R.id.imgSong);
        tvArtistSong = findViewById(R.id.tvArtistSong);
        tvTitleSong = findViewById(R.id.tvTitleSong);
        //tvLyricsSong = findViewById(R.id.tvLyricsSong);
        tvDescription = findViewById(R.id.tvDescription);
        mbtnYt = findViewById(R.id.btnYoutube);
        mbtnSpotify = findViewById(R.id.btnSpotify);
        mbtnLike = findViewById(R.id.btnLike);

        Intent intent = getIntent();

        Song song = intent.getParcelableExtra(SearchFragment.SONG);
        //int tre = intent.getIntExtra(SearchFragment.SONG, 0);
        Picasso.get().load(song.getImage()).transform(new GradientTransformation()).into(imgSong);
        tvArtistSong.setText(song.getArtist());
        tvTitleSong.setText(song.getTitle());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);

        getDescription(song);

        mbtnYt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.getYoutube() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(song.getYoutube()));
                    try {
                        SongActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(SongActivity.this, R.string.YoutubeError, Toast.LENGTH_LONG).show();
            }
        });

        mbtnSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.getSpotify() != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(song.getSpotify()));
                    try {
                        SongActivity.this.startActivity(webIntent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else
                    Toast.makeText(SongActivity.this, R.string.SpotifyError, Toast.LENGTH_LONG).show();
            }
        });
        //getLyrics(song);
    }

    public String digger(JSONArray children) throws JSONException {
        String description = "";
        for (int j = 0; j < children.length(); j++) {
            if (children.get(j) instanceof String)
                description = description + children.get(j);
            else if (children.getJSONObject(j).has("children"))
                description = description + digger(children.getJSONObject(j).getJSONArray("children"));
        }
        return description;
    }

    public void getDescription(Song song) {
        String url = "https://api.genius.com/songs/" + song.getId();
        RequestQueue queue = Volley.newRequestQueue(this);
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
                    if (description.equals("?"))
                        description = getString(R.string.Description);

                    tvDescription.setText(description);

                    //Find youtube and spotify links from response
                    JSONArray media = response.getJSONObject("response").getJSONObject("song").getJSONArray("media");
                    //Log.d("media", media.toString());
                    if (media != null) {
                        for (int k = 0; k < media.length(); k++) {
                            if (media.getJSONObject(k).getString("provider").equals("youtube"))
                                song.setYoutube(media.getJSONObject(k).getString("url"));
                            else if (media.getJSONObject(k).getString("provider").equals("spotify"))
                                song.setSpotify(media.getJSONObject(k).getString("url"));
                        }
                    }
                    mbtnYt.setVisibility(View.VISIBLE);
                    mbtnSpotify.setVisibility(View.VISIBLE);
                    mbtnLike.setVisibility(View.VISIBLE);
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
                params.put("Authorization", "Bearer " + Utils.ACCESS_TOKEN);
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void getLyrics(Song song) {
        String url = "https://api.lyrics.ovh/v1/" + song.getArtist() + "/" + song.getTitle();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("lyrics", response.toString());
                    tvDescription.setText(response.getString("lyrics"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }


}