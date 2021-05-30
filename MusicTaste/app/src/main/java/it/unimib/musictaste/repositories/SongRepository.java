package it.unimib.musictaste.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.unimib.musictaste.R;
import it.unimib.musictaste.SongActivity;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;

public class SongRepository {
    private final SongCallback songCallback;
    public static SongFBCallback songFBCallback = null;
    private final Context context;
    static FirebaseFirestore database = FirebaseFirestore.getInstance();


    public SongRepository(SongCallback songCallback, SongFBCallback songFBCallback, Context context) {
        this.songCallback = songCallback;
        this.songFBCallback = songFBCallback;
        this.context = context;
    }

    public void checkLikedSongs(String uid, String idSong) {

        database.collection("likedSongs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private boolean liked = false;
                    private String documentID = null;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("IDuser").equals(uid) &&
                                        document.get("IDsong").equals(idSong)) {
                                    liked = true;

                                    documentID = document.getId();
                                    break;
                                }
                            }
                            songFBCallback.onResponseFB(liked, documentID, false);
                        }
                    }

                });
    }


    public static void deleteLikedSong(String documentID) {


        database.collection("likedSongs").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //boolean liked = true;
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("Succes", "DocumentSnapshot successfully deleted!");

                        songFBCallback.onResponseFB(false, null, true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error deleting document", e);
                    }
                });


    }

    public static void addLikedSong(String uid, Song currentSong) {
        Map<String, Object> likedSongs = new HashMap<>();
        likedSongs.put("IDuser", uid);
        likedSongs.put("IDsong", currentSong.getId());
        likedSongs.put("TitleSong", currentSong.getTitle());
        likedSongs.put("ArtistSong", currentSong.getArtist());
        likedSongs.put("ImageSong", currentSong.getImage());

// Add a new document with a generated ID
        database.collection("likedSongs")
                .add(likedSongs)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d("Succes", "DocumentSnapshot added with ID: " + documentReference.getId());
                        //mbtnLike.setImageResource(R.drawable.ic_favorite_full);
                        //liked = true;
                        String documentID = documentReference.getId();
                        songFBCallback.onResponseFB(true, documentID, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    public void getSongInfo(String songId) {
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