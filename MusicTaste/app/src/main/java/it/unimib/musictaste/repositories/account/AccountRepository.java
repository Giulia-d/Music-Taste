package it.unimib.musictaste.repositories.account;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimib.musictaste.models.Album;
import it.unimib.musictaste.models.Artist;
import it.unimib.musictaste.models.Song;

public class AccountRepository {
    private final MutableLiveData<List<Song>> mLikedSongs;
    private final MutableLiveData<List<Artist>> mLikedArtists;
    private final MutableLiveData<List<Album>> mLikedAlbums;
    private Context context;
    FirebaseFirestore database = FirebaseFirestore.getInstance();


    public AccountRepository(Context context) {
        this.context = context;
        mLikedSongs = new MutableLiveData<>();
        mLikedArtists = new MutableLiveData<>();
        mLikedAlbums = new MutableLiveData<>();
    }


    public MutableLiveData<List<Artist>> getLikedArtists(String uid) {
        List<Artist> likedArtist = new ArrayList<>();
        database.collection("likedArtists")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("IDuser").equals(uid)) {
                                    likedArtist.add(new Artist(document.getString("NameArtist"),
                                            document.getString("ImageArtist"),
                                            document.getString("IDartist")));
                                }
                            }
                        }
                        mLikedArtists.postValue(likedArtist);
                    }

                });
        return mLikedArtists;
    }

    public MutableLiveData<List<Album>> getLikedAlbums(String uid) {
        List<Album> likedAlbums = new ArrayList<>();
        database.collection("likedAlbums")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("IDuser").equals(uid)) {
                                    likedAlbums.add(new Album(document.getString("NameAlbum"),
                                            document.getString("ImageAlbum"),
                                            document.getString("IDAlbum"),
                                            document.getString("NameArtist")));
                                }
                            }
                        }
                        mLikedAlbums.postValue(likedAlbums);
                    }

                });
        return mLikedAlbums;
    }

    public MutableLiveData<List<Song>> getLikedSongs(String uid) {
        List<Song> likedSongs = new ArrayList<>();
        database.collection("likedSongs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("IDuser").equals(uid)) {
                                    Map<String, Object> data = document.getData();
                                    Map<String, Object> artistMap = (Map<String, Object>) data.get("Artist");
                                    String id = (String) artistMap.get("id");
                                    String image = (String) artistMap.get("image");
                                    String name = (String) artistMap.get("name");
                                    Log.d("AAAAAAAAAAAA", document.getString("TitleSong"));
                                    Song s = new Song(document.getString("TitleSong"), document.getString("ImageSong"),
                                            document.getString("IDsong"), new Artist(name, image, id));
                                    likedSongs.add(s);
                                }
                            }
                        }
                        mLikedSongs.postValue(likedSongs);
                    }
                });
        return mLikedSongs;
    }
}
