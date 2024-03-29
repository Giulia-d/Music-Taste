package it.unimib.musictaste.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import it.unimib.musictaste.models.Album;
import it.unimib.musictaste.models.LikedElement;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.utils.Utils;

public class SongRepository {
    private final Context context;
    private FirebaseFirestore database;
    private final MutableLiveData<LikedElement> likedElement;
    private final MutableLiveData<Song> currentSong;

    public SongRepository(Context context) {
        this.context = context;
        likedElement = new MutableLiveData<>();
        database = FirebaseFirestore.getInstance();
        currentSong = new MutableLiveData<>();
    }

    public MutableLiveData<LikedElement> checkLikedSongs(String uid, String idSong) {
        database.collection("likedSongs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    LikedElement l = new LikedElement(0, null);
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("IDuser").equals(uid) &&
                                        document.get("IDsong").equals(idSong)) {

                                    String documentID = document.getId();
                                    LikedElement l = new LikedElement(1, documentID);
                                    likedElement.postValue(l);
                                    break;
                                }
                            }
                        }
                    }

                });
        return likedElement;
    }


    public MutableLiveData<LikedElement> deleteLikedSong(String documentID) {
        database.collection("likedSongs").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("Succes", "DocumentSnapshot successfully deleted!");
                        likedElement.postValue(new LikedElement(3, null));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("Error", "Error deleting document", e);
                        likedElement.postValue(new LikedElement(-1, e.getMessage()));
                    }
                });
        return likedElement;
    }

    public MutableLiveData<LikedElement> addLikedSong(String uid, Song currentSong) {
        Map<String, Object> artist = new HashMap<>();
        artist.put("id", currentSong.getArtist().getId());
        artist.put("image", currentSong.getArtist().getImage());
        artist.put("name", currentSong.getArtist().getName());
        Map<String, Object> likedSongs = new HashMap<>();
        likedSongs.put("IDuser", uid);
        likedSongs.put("IDsong", currentSong.getId());
        likedSongs.put("TitleSong", currentSong.getTitle());
        likedSongs.put("ImageSong", currentSong.getImage());
        likedSongs.put("Artist", artist);


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
                        //songFBCallback.onResponseFB(true, documentID, true);
                        likedElement.postValue(new LikedElement(2, documentID));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
                        likedElement.postValue(new LikedElement(-1, e.getMessage()));
                    }
                });
        return likedElement;
    }

    public MutableLiveData<Song> getSongInfo(String songId) {
        String url = "https://api.genius.com/songs/" + songId;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Song currentS = new Song();
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

                    Album al = null;
                    if(!response.getJSONObject("response").getJSONObject("song").isNull("album")){
                        JSONObject album = response.getJSONObject("response").getJSONObject("song").getJSONObject("album");

                        String idAlbum  = album.getString("id");
                        String albumImg  = album.getString("cover_art_url");
                        String albumTitle  = album.getString("name");

                        al = new Album(albumTitle, albumImg, idAlbum);
                    }

                    currentS.setTitle("OnComplete");
                    currentS.setDescription(description);
                    currentS.setYoutube(youtube);
                    currentS.setSpotify(spotify);
                    currentS.setAlbum(al);

                    currentSong.postValue(currentS);

                    //songCallback.onResponse(description, youtube, spotify, al);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(e.toString().equals("org.json.JSONException: No value for children")){
                        currentS.setDescription("?");
                        currentS.setTitle("ErrorResponse");
                        currentS.setImage("error");
                        currentSong.postValue(currentS);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                currentSong.postValue(new Song("ErrorResponse", error.getMessage(), null, null));
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
        return currentSong;
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
