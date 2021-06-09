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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.LikedElement;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;

public class AlbumRepository {
    private MutableLiveData<String> currentDetails;
    private MutableLiveData<List<Song>> trackList;
    private final MutableLiveData<LikedElement> likedElement;
    static FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final Context context;

    public AlbumRepository( Context context) {
        this.context = context;
        currentDetails = new MutableLiveData<>();
        trackList = new MutableLiveData<>();
        likedElement = new MutableLiveData<>();
        database = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<String> getAlbumInfo(String albumId) {
        String url = "https://api.genius.com/albums/" + albumId;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response").getJSONObject("album").getJSONObject("description_annotation"); //getJSONObject("annotations").getJSONObject("0").getJSONObject("body").getJSONObject("dom");
                    JSONArray jsonArray = responseDescription.getJSONArray("annotations");
                    responseDescription = jsonArray.getJSONObject(0).getJSONObject("body").getJSONObject("dom");
                    JSONArray desc = responseDescription.getJSONArray("children");
                    String description = "";
                    for (int i = 0; i < desc.length(); i++) {
                        if (!(desc.get(i) instanceof String)) {
                            JSONArray children = desc.getJSONObject(i).getJSONArray("children");
                            description = description + digger(children);
                        }
                    }

                    JSONObject album = response.getJSONObject("response").getJSONObject("album").getJSONObject("release_date_components");
                    //String date =  album.getString("day") + "/" + album.getString("month") + "/" + album.getString("year");

                    currentDetails.postValue(description);
                    //albumCallback.onResponse(description, date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                currentDetails.postValue("error");
                //albumCallback.onFailure(error.getMessage());
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
        return currentDetails;
    }

    public MutableLiveData<List<Song>> getAlbumTracks(String albumId) {
        String url = "https://api.genius.com/albums/" + albumId + "/tracks";
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject responseDescription = response.getJSONObject("response");
                    JSONArray desc = responseDescription.getJSONArray("tracks");
                    List<Song> tracks = new ArrayList<Song>();
                    for (int i = 0; i < desc.length(); i++) {
                        JSONObject s = desc.getJSONObject(i).getJSONObject("song");
                        String sTitle = s.getString("title");
                        String sId = s.getString("id");
                        String sImage = s.getString("song_art_image_url");
                        String aName = s.getJSONObject("primary_artist").getString("name");
                        String aImage = s.getJSONObject("primary_artist").getString("image_url");
                        String aId = s.getJSONObject("primary_artist").getString("id");
                        Artist a = new Artist(aName, aImage, aId);
                        tracks.add(new Song(sTitle, sImage, sId, a));
                    }
                    //int nTracks = tracks.size();

                    trackList.postValue(tracks);
                    //albumTracksCallback.onResponseTracks(tracks, nTracks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                Song trackError = new Song(error.getMessage(), "error", null, null);
                List<Song> listError = new ArrayList<>();
                listError.add(trackError);
                trackList.postValue(listError);
                //albumTracksCallback.onFailureTracks(error.getMessage());
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
        return trackList;
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

    public MutableLiveData<LikedElement> checkLikedAlbum(String uid, String idArtist) {

        database.collection("likedAlbums")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    LikedElement l = new LikedElement(0, null);

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("IDuser").equals(uid) &&
                                        document.get("IDartist").equals(idArtist)) {


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


    public  MutableLiveData<LikedElement> deleteLikedAlbum(String documentID) {

        database.collection("likedAlbums").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //boolean liked = true;
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("Succes", "DocumentSnapshot successfully deleted!");

                        likedElement.postValue(new LikedElement(3, null));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error deleting document", e);
                        likedElement.postValue(new LikedElement(-1, e.getMessage()));
                    }
                });
        return likedElement;

    }

    public MutableLiveData<LikedElement> addLikedAlbum(String uid, Artist currentArtist) {
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


}
