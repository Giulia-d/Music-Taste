package it.unimib.musictaste;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.musictaste.utils.Album;
import it.unimib.musictaste.utils.Song;

public class ArtistAlbumsRecyclerViewAdapter extends RecyclerView.Adapter<ArtistAlbumsRecyclerViewAdapter.ArtistAlbumsViewHolder> {
    private List<Album> albums;
    private ArtistAlbumsRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(Album response);
    }

    public ArtistAlbumsRecyclerViewAdapter(List<Album> albums, ArtistAlbumsRecyclerViewAdapter.OnItemClickListener onItemClickListener){
        this.albums = albums;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ArtistAlbumsRecyclerViewAdapter.ArtistAlbumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_item, parent, false);
        return new ArtistAlbumsRecyclerViewAdapter.ArtistAlbumsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ArtistAlbumsRecyclerViewAdapter.ArtistAlbumsViewHolder holder, int position) {
        holder.bind(albums.get(position));
    }

    @Override
    public int getItemCount() {
        if(albums != null) {
            return albums.size();
        }
        else return 0;
    }

    public class ArtistAlbumsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumImage;
        private final TextView albumTitle;


        public ArtistAlbumsViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.imageSong);
            albumTitle = itemView.findViewById(R.id.tvtitle);


        }

        public void bind(Album response) {
            if (response != null) {
                Picasso.get().load(response.getImage()).into(albumImage);
                albumTitle.setText(response.getTitle());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(response);
                    }
                });
            }
        }



    }
}
