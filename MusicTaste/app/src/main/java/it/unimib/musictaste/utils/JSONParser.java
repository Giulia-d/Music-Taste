package it.unimib.musictaste.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
    public static ArrayList<Song> displayResultAPI(JSONObject res) throws JSONException {
        ArrayList<Song> resp = new ArrayList<Song>();
        JSONObject newRes = res.getJSONObject("response");
        JSONArray hits = newRes.getJSONArray("hits");
        for (int i = 0; i < hits.length(); i++){
            String title = hits.getJSONObject(i).getJSONObject("result").getString("title");
            String img = hits.getJSONObject(i).getJSONObject("result").getString("header_image_url");
            String id = hits.getJSONObject(i).getJSONObject("result").getString("id");
            String artist = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("name");
            String artistId = hits.getJSONObject(i).getJSONObject("result").getJSONObject("primary_artist").getString("id");
            Log.d("Titolo", title);
            resp.add(new Song(title, img, id,artist, artistId));
            /*SearchFragment.suggestions.add(title);
            Log.d("SUGGESTION",SearchFragment.suggestions.toString());*/
        }
        return resp;
    }


}
