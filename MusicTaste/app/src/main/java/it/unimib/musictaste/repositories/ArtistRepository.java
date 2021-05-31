package it.unimib.musictaste.repositories;

import android.content.Context;
import android.util.Log;

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

import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Utils;

public class ArtistRepository {
    private final ArtistCallback artistCallback;
    public static ArtistFBCallback artistFBCallback = null;
    private final Context context;
    static FirebaseFirestore database = FirebaseFirestore.getInstance();

    public ArtistRepository(ArtistCallback artistCallback, ArtistFBCallback artistFBCallback, Context context) {
        this.artistCallback = artistCallback;
        this.artistFBCallback = artistFBCallback;
        this.context = context;
    }
 //db
 public void checkLikedArtist(String uid, String idArtist) {

     database.collection("likedArtists")
             .get()
             .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 private boolean liked = false;
                 private String documentID = null;

                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             if (document.get("IDuser").equals(uid) &&
                                     document.get("IDartist").equals(idArtist)) {
                                 liked = true;

                                 documentID = document.getId();
                                 break;
                             }
                         }
                         artistFBCallback.onResponseFB(liked, documentID, false);
                     }
                 }

             });
 }


    public static void deleteLikedArtist(String documentID) {

        database.collection("likedArtists").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //boolean liked = true;
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("Succes", "DocumentSnapshot successfully deleted!");

                        artistFBCallback.onResponseFB(false, null, true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error deleting document", e);
                    }
                });


    }

    public static void addLikedArtist(String uid, Artist currentArtist) {
        Map<String, Object> likedArtists = new HashMap<>();
        likedArtists.put("IDuser", uid);
        likedArtists.put("IDartist", currentArtist.getId());
        likedArtists.put("NameArtist", currentArtist.getName());
        likedArtists.put("ImageArtist", currentArtist.getImage());

// Add a new document with a generated ID
        database.collection("likedArtists")
                .add(likedArtists)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d("Succes", "DocumentSnapshot added with ID: " + documentReference.getId());
                        //mbtnLike.setImageResource(R.drawable.ic_favorite_full);
                        //liked = true;
                        String documentID = documentReference.getId();
                        artistFBCallback.onResponseFB(true, documentID, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }


//api request

    public void getArtistInfo(String artistId) {
        String url = "https://api.genius.com/artists/" + artistId;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response").getJSONObject("artist").getJSONObject("description").getJSONObject("dom");
                    JSONArray desc = responseDescription.getJSONArray("children");
                    String description = "";
                    for (int i = 0; i < desc.length(); i++) {
                        if (!(desc.get(i) instanceof String)) {
                            JSONArray children = desc.getJSONObject(i).getJSONArray("children");
                            description = description + digger(children);
                        }
                    }

                    String youtube = "";
                    String spotify = "";
                    //artist non ha media
                    /*
                    // Find youtube and spotify links from response
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
                    */
                    artistCallback.onResponse(description, youtube, spotify);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                artistCallback.onFailure(error.getMessage());
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
