package it.unimib.musictaste.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.unimib.musictaste.fragments.SearchFragment;

public class JSONParser {
    public static ArrayList<String> displayResultAPI(JSONObject res) throws JSONException {
        ArrayList<String> resp = new ArrayList<String>();
        JSONObject newRes = res.getJSONObject("response");
        JSONArray hits = newRes.getJSONArray("hits");
        for (int i = 0; i < hits.length(); i++){
            String title = hits.getJSONObject(i).getJSONObject("result").getString("full_title");
            String img = hits.getJSONObject(i).getJSONObject("result").getString("header_image_url");
            Log.d("Titolo", title);
            resp.add(title);
            /*SearchFragment.suggestions.add(title);
            Log.d("SUGGESTION",SearchFragment.suggestions.toString());*/
        }
        return resp;
    }
}
