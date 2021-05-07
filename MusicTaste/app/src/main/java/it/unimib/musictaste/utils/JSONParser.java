package it.unimib.musictaste.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    public static void displayResultAPI(JSONObject res) throws JSONException {
        JSONObject newRes = res.getJSONObject("response");
        JSONArray hits = newRes.getJSONArray("hits");
        for (int i = 0; i < hits.length(); i++){
            String title = hits.getJSONObject(i).getJSONObject("result").getString("full_title");
            Log.d("Titolo", title);
        }
    }
}
