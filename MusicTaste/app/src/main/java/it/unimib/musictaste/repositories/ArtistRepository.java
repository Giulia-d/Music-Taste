package it.unimib.musictaste.repositories;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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
import com.wrapper.spotify.SpotifyApiThreading;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Song;
import it.unimib.musictaste.utils.Utils;
import it.unimib.musictaste.repositories.ArtistsAlbumsCallback;

public class ArtistRepository {
    private final ArtistCallback artistCallback;
    private final GeniusCallBack geniusCallback;
    public static ArtistFBCallback artistFBCallback = null;
    public static ArtistsAlbumsCallback artistsAlbumsCallback;
    private final Context context;
    static FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(Utils.CLIENT_ID)
            .setClientSecret(Utils.CLIENT_SECRET)
            .build();

    public ArtistRepository(ArtistCallback artistCallback, ArtistFBCallback artistFBCallback, ArtistsAlbumsCallback artistsAlbumsCallback, GeniusCallBack geniusCallBack, Context context) {
        this.artistCallback = artistCallback;
        this.artistFBCallback = artistFBCallback;
        this.artistsAlbumsCallback = artistsAlbumsCallback;
        this.geniusCallback = geniusCallBack;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getArtistAlbums(String artistName) throws ParseException, SpotifyWebApiException, IOException {
        clientCredentials_Async();
        SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artistName).limit(1).build();

        try {
            CompletableFuture<Paging<com.wrapper.spotify.model_objects.specification.Artist>> pagingFuture = searchArtistsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final Paging<com.wrapper.spotify.model_objects.specification.Artist> artistPaging = pagingFuture.join();

            com.wrapper.spotify.model_objects.specification.Artist[] ar = artistPaging.getItems();
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
                albumList.add(new Album(albumName, albumImage, albumId, albumUri, artistName));
                int k = 1;
                for (int i = 1; i < albumArray.length; i++) {
                    albumName = albumArray[i].getName();
                    if (!(albumList.get(k - 1).getTitle().equals(albumName))) {
                        k++;
                        albumId = albumArray[i].getId();
                        albumImage = albumArray[i].getImages()[1].getUrl();
                        albumUri = albumArray[i].getUri();
                        albumList.add(new Album(albumName, albumImage, albumId, albumUri, artistName));
                    }
                }
            }
            else{

            }
            artistsAlbumsCallback.onResponseArtistAlbums(albumList);
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
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

    }

    public void clientCredentials_Sync() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
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
    public void getGeniusInfo(Album album) {
        clientCredentials_Async();
            try {
                GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(album.getIdSpotify()).limit(1).build();

                final CompletableFuture<Paging<TrackSimplified>> pagingFuture = getAlbumsTracksRequest.executeAsync();
                // Thread free to do other tasks...

                // Example Only. Never block in production code.
                final Paging<TrackSimplified> trackSimplifiedPaging = pagingFuture.join();
                String text = trackSimplifiedPaging.getItems()[0].getName() + ' ' + album.getArtistName();
                text = text.replace(' ', '&');

                String url = "https://api.genius.com/search/?q=" + text;
                RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject newRes = response.getJSONObject("response");
                            JSONArray hits = newRes.getJSONArray("hits");

                            String songId = hits.getJSONObject(0).getJSONObject("result").getString("id");

                            getIdGenius(album, songId);





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
        }


    public void getIdGenius(Album album, String songId)
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
                    geniusCallback.onResponseGenius(album);

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

    }}