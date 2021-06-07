package it.unimib.musictaste.repositories;

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

import it.unimib.musictaste.utils.Artist;
import it.unimib.musictaste.utils.Song;

public class AccountRepository {
    private final MutableLiveData<List<Song>> mLikedSongs;
    private final MutableLiveData<List<Artist>> mLikedArtists;
    private Context context;
    FirebaseFirestore database = FirebaseFirestore.getInstance();


    public AccountRepository(Context context) {
        this.context = context;
        mLikedSongs = new MutableLiveData<>();
        mLikedArtists = new MutableLiveData<>();
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
                                    String name = document.getString("NameArtist");
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
