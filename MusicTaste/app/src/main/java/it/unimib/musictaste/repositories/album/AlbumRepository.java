package it.unimib.musictaste.repositories.album;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.unimib.musictaste.models.Album;
import it.unimib.musictaste.models.Artist;
import it.unimib.musictaste.models.LikedElement;
import it.unimib.musictaste.models.Song;
import it.unimib.musictaste.utils.Utils;

public class AlbumRepository {
    private MutableLiveData<String> currentDetails;
    private MutableLiveData<List<Song>> trackList;
    private MutableLiveData<String> spotifyUri;
    private final MutableLiveData<LikedElement> likedElement;
    private FirebaseFirestore database;
    private final Context context;
    private final SpotifyApi spotifyApi;

    public AlbumRepository(Context context) {
        this.context = context;
        currentDetails = new MutableLiveData<>();
        trackList = new MutableLiveData<>();
        likedElement = new MutableLiveData<>();
        database = FirebaseFirestore.getInstance();
        spotifyUri = new MutableLiveData<>();
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(Utils.CLIENT_ID)
                .setClientSecret(Utils.CLIENT_SECRET)
                .build();
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

                    currentDetails.postValue(description);
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

    public MutableLiveData<LikedElement> checkLikedAlbum(String uid, String idAlbum) {
        database.collection("likedAlbums")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    LikedElement l = new LikedElement(0, null);
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("IDuser").equals(uid) &&
                                        document.get("IDAlbum").equals(idAlbum)) {
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

    public MutableLiveData<LikedElement> deleteLikedAlbum(String documentID) {
        database.collection("likedAlbums").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //boolean liked = true;
                    @Override
                    public void onSuccess(Void aVoid) {
                        likedElement.postValue(new LikedElement(3, null));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        likedElement.postValue(new LikedElement(-1, e.getMessage()));
                    }
                });
        return likedElement;
    }

    public MutableLiveData<LikedElement> addLikedAlbum(String uid, Album currentAlbum) {
        Map<String, Object> likedAlbum = new HashMap<>();
        likedAlbum.put("IDuser", uid);
        likedAlbum.put("IDAlbum", currentAlbum.getId());
        likedAlbum.put("NameArtist", currentAlbum.getArtistName());
        likedAlbum.put("NameAlbum", currentAlbum.getTitle());
        likedAlbum.put("ImageAlbum", currentAlbum.getImage());
        // Add a new document with a generated ID
        database.collection("likedAlbums")
                .add(likedAlbum)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentID = documentReference.getId();
                        likedElement.postValue(new LikedElement(2, documentID));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        likedElement.postValue(new LikedElement(-1, e.getMessage()));
                    }
                });
        return likedElement;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MutableLiveData<String> getLinkSpotify(String title) {
        clientCredential_Async();
        SearchAlbumsRequest searchAlbumsRequest = spotifyApi.searchAlbums(title).limit(1).build();
        try {
            final CompletableFuture<Paging<AlbumSimplified>> pagingFuture = searchAlbumsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final Paging<AlbumSimplified> albumSimplifiedPaging = pagingFuture.join();

            AlbumSimplified[] album = albumSimplifiedPaging.getItems();
            spotifyUri.postValue(album[0].getUri());

        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
            Album albumError = new Album(e.getCause().getMessage(), "error", null, null, null);
            List<Album> listError = new ArrayList<>();
            listError.add(albumError);
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
        return spotifyUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clientCredential_Async() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();
        try {
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

}
