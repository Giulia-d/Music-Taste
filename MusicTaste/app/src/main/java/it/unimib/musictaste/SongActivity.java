package it.unimib.musictaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.musictaste.fragments.SearchFragment;
import it.unimib.musictaste.utils.GradientTransformation;
import it.unimib.musictaste.utils.Song;


public class SongActivity extends AppCompatActivity {

    ImageView imgSong;
    TextView tvArtistSong;
    TextView tvTitleSong;
    TextView tvLyricsSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);


        imgSong = findViewById(R.id.imgSong);
        tvArtistSong = findViewById(R.id.tvArtistSong);
        tvTitleSong = findViewById(R.id.tvTitleSong);
        tvLyricsSong = findViewById(R.id.tvLyricsSong);

        Intent intent = getIntent();

        Song song = intent.getParcelableExtra(SearchFragment.SONG);
        //int tre = intent.getIntExtra(SearchFragment.SONG, 0);
        Picasso.get().load(song.getImage()).transform(new GradientTransformation()).into(imgSong);
        tvArtistSong.setText(song.getArtist());
        tvTitleSong.setText(song.getTitle());
        //tvLyricsSong.setText(song.getId());
        //Log.d("user", "Photo:" + tre);
        getLyrics(song);
    }

    public void getLyrics(Song song){
        /*RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.lyrics.ovh/v1/" + song.getArtist() + "/" + song.getTitle();

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        tvLyricsSong.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvLyricsSong.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);*/
        String url ="https://api.lyrics.ovh/v1/" + song.getArtist() + "/" + song.getTitle();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {
                    Log.d("lyrics", response.toString());
                    tvLyricsSong.setText(response.getString("lyrics"));
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