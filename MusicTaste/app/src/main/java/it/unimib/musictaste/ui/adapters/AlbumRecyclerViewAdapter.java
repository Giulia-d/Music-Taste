package it.unimib.musictaste.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.models.Album;


public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.AlbumViewHolder>{

    private List<Album> likedAlbums;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Album album);
    }

    public AlbumRecyclerViewAdapter(List<Album> likedAlbums, OnItemClickListener onItemClickListener) {
        this.likedAlbums = likedAlbums;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.bind(likedAlbums.get(position));
    }

    @Override
    public int getItemCount() {
        return likedAlbums.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final ImageView imageAlbum;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvLikedItemTitle);
            artistTextView = itemView.findViewById(R.id.tvLikedItemArtist);
            imageAlbum = itemView.findViewById(R.id.imgLikedItem);
        }

        public void bind(Album album) {
            titleTextView.setText(album.getTitle());
            artistTextView.setText(album.getArtist().getName());
            Picasso.get().load(album.getImage()).into(imageAlbum);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(album);
                }
            });
        }
    }
}
