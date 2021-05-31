package it.unimib.musictaste.repositories;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

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
    private final AccountFBCallback accountFBCallback;
    private Context context;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    public AccountRepository(AccountFBCallback accountFBCallback, Context context) {
        this.accountFBCallback = accountFBCallback;
        this.context = context;
    }

    public void getLikedSongs(String uid){
        List<Song> likedSongs = new ArrayList<>();
        database.collection("likedSongs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("IDuser").equals(uid)) {
                                    Map <String, Object> data = document.getData();
                                    Map <String, Object> artistMap = (Map<String, Object>) data.get("Artist");
                                    String id = (String) artistMap.get("id");
                                    String image = (String) artistMap.get("image");
                                    String name = (String) artistMap.get("name");
                                    Log.d("AAAAAAAAAAAA", document.getString("TitleSong"));
                                    Song s = new Song(document.getString("TitleSong"), document.getString("ImageSong"),
                                            document.getString("IDsong"),new Artist(name, image, id));
                                    likedSongs.add(s);

                                }
                            }
                            accountFBCallback.onResponse(likedSongs);

                        }
                    }

                });
    }
}
