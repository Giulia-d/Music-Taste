package it.unimib.musictaste.repositories;

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
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;

import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.LikedElement;
import it.unimib.musictaste.utils.Utils;

public class ArtistRepository {
    private MutableLiveData<String> currentDetails;
    private final MutableLiveData<LikedElement> likedElement;
    private MutableLiveData<List<Album>> albumList;
    private MutableLiveData<Album> albumGenius;
    private MutableLiveData<String> songId;
    private final Context context;
    static FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(Utils.CLIENT_ID)
            .setClientSecret(Utils.CLIENT_SECRET)
            .build();

    public ArtistRepository( Context context) {
        this.context = context;
        likedElement = new MutableLiveData<>();
        database = FirebaseFirestore.getInstance();
        currentDetails = new MutableLiveData<>();
        albumList = new MutableLiveData<>();
        albumGenius = new MutableLiveData<>();
        songId = new MutableLiveData<>();
    }
 //db
 public MutableLiveData<LikedElement> checkLikedArtist(String uid, String idArtist) {

     database.collection("likedArtists")
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


    public  MutableLiveData<LikedElement> deleteLikedArtist(String documentID) {

        database.collection("likedArtists").document(documentID)
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

    public MutableLiveData<LikedElement> addLikedArtist(String uid, Artist currentArtist) {
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


//api request

    public MutableLiveData<String> getArtistInfo(String artistId) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MutableLiveData<List<Album>> getArtistAlbums(Artist artist) throws ParseException, SpotifyWebApiException, IOException {
        clientCredentials_Async();
        SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artist.getName()).limit(1).build();

        try {
            CompletableFuture<Paging<com.wrapper.spotify.model_objects.specification.Artist>> pagingFuture = searchArtistsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final Paging<com.wrapper.spotify.model_objects.specification.Artist> artistPaging = pagingFuture.join();

            com.wrapper.spotify.model_objects.specification.Artist[] ar = artistPaging.getItems();
            //set spotify url
            artist.setSpotify(ar[0].getUri());
            String aId = ar[0].getId();
          //  try {

            GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(aId).album_type("album").build();
            final CompletableFuture<Paging<AlbumSimplified>> pagingFutureAlbum = getArtistsAlbumsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            Paging<AlbumSimplified> albumSimplifiedPaging = pagingFutureAlbum.join();

            System.out.println("Total: " + albumSimplifiedPaging.getTotal());
            AlbumSimplified[] albumArray = albumSimplifiedPaging.getItems();
            List<Album> albumList = new ArrayList<Album>();
            if(albumArray != null && albumArray.length != 0) {
                String albumName = albumArray[0].getName();
                String albumId = albumArray[0].getId();
                String albumImage = albumArray[0].getImages()[1].getUrl();
                String albumUri = albumArray[0].getUri();
                albumList.add(new Album(albumName, albumImage, albumId, albumUri, artist.getName()));
                int k = 1;
                for (int i = 1; i < albumArray.length; i++) {
                    albumName = albumArray[i].getName();
                    if (!(albumList.get(k - 1).getTitle().equals(albumName))) {
                        k++;
                        albumId = albumArray[i].getId();
                        albumImage = albumArray[i].getImages()[1].getUrl();
                        albumUri = albumArray[i].getUri();
                        albumList.add(new Album(albumName, albumImage, albumId, albumUri, artist.getName()));
                    }
                }
            }
            else{

            }
            this.albumList.postValue(albumList);
            //artistsAlbumsCallback.onResponseArtistAlbums(albumList);
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
            Album albumError = new Album(e.getCause().getMessage(), "error", null, null, null);
            List<Album> listError = new ArrayList<>();
            listError.add(albumError);
            this.albumList.postValue(listError);
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
   /*       } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }

      try {
            Paging<com.wrapper.spotify.model_objects.specification.Artist> artistPaging = searchArtistsRequest.execute();

            com.wrapper.spotify.model_objects.specification.Artist[] ar = artistPaging.getItems();
            String aId = ar[0].getId();

            GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(aId).album_type("album").build();
            albumSimplifiedPaging = getArtistsAlbumsRequest.execute();


        } catch (IOException | SpotifyWebApiException | ParseException e) {
        }*/
    return this.albumList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clientCredentials_Async() {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  MutableLiveData<String> getGeniusInfo(Album album) {
        clientCredentials_Async();
            try {
                GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(album.getIdSpotify()).limit(1).build();

                final CompletableFuture<Paging<TrackSimplified>> pagingFuture = getAlbumsTracksRequest.executeAsync();
                // Thread free to do other tasks...

                // Example Only. Never block in production code.
                final Paging<TrackSimplified> trackSimplifiedPaging = pagingFuture.join();
                String text = trackSimplifiedPaging.getItems()[0].getName() + ' ' + album.getArtistName();
                //text = text.replace(' ', '&');

                String url = "https://api.genius.com/search/?q=" + text;
                RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject newRes = response.getJSONObject("response");
                            JSONArray hits = newRes.getJSONArray("hits");

                            String id = hits.getJSONObject(0).getJSONObject("result").getString("id");

                            songId.postValue(id);

            /*SearchFragment.suggestions.add(title);
            Log.d("SUGGESTION",SearchFragment.suggestions.toString());*/
                            //searchCallback.onResponse(resp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("tag", "onErrorResponse: " + error.getMessage());
                        //searchCallback.onFailure(error.getMessage());
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", "Bearer " + Utils.ACCESS_TOKEN);
                    return params;
                }
                } ;
               queue.add(jsonObjectRequest);


            } catch (CompletionException e) {
                System.out.println("Error: " + e.getCause().getMessage());
            } catch (CancellationException e) {
                System.out.println("Async operation cancelled.");
            }
            return songId;
        }


    public MutableLiveData<Album> getIdGenius(Album album, String songId)
    {
        String url = "https://api.genius.com/songs/" + songId;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject albumJson = response.getJSONObject("response").getJSONObject("song").getJSONObject("album");
                    String idAlbum = albumJson.getString("id");

                    album.setId(idAlbum);
                    albumGenius.postValue(album);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                //songCallback.onFailure(error.getMessage());
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
       return albumGenius;

    }
    }
