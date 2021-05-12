package it.unimib.musictaste.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.musictaste.fragments.SearchFragment;

public class JSONParser {
    public static synchronized void  displayResultAPI(JSONObject res) throws JSONException {
        JSONObject newRes = res.getJSONObject("response");
        JSONArray hits = newRes.getJSONArray("hits");
        for (int i = 0; i < hits.length(); i++){
            String title = hits.getJSONObject(i).getJSONObject("result").getString("full_title");
            Log.d("Titolo", title);
            SearchFragment.suggestions.add(title);
            Log.d("SUGGESTION",SearchFragment.suggestions.toString());
        }
    }
}
