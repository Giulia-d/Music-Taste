package it.unimib.musictaste.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.unimib.musictaste.fragments.SearchFragment;

public class HandleAPICalls {
    static JSONObject res;

    public static synchronized JSONObject querySearch(String searchParam, Context context){
        String url ="https://api.genius.com/search/?q=" + searchParam;

        RequestQueue queue = Volley.newRequestQueue(context);
        //SearchFragment.flagAPI=false;
// Request a string response from the provided URL.
         StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.d("Your Array Response", response);
                    try {
                        res = new JSONObject(response);
                        SearchFragment.flagAPI=true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + Utils.ACCESS_TOKEN);
                return params;
            }

            //Pass Your Parameters here
            /*@Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User", UserName);
                params.put("Pass", PassWord);
                return params;
            }*/
        };

        queue.add(request);
        return res;


    }

}
