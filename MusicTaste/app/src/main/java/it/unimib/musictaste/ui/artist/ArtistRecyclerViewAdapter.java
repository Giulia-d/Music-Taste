package it.unimib.musictaste.ui.artist;

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
import it.unimib.musictaste.models.Artist;


public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ArtistViewHolder>{
    private List<Artist> artistList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Artist response);
    }

    public ArtistRecyclerViewAdapter(List<Artist> responseList, OnItemClickListener onItemClickListener) {
        this.artistList = responseList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.bind(artistList.get(position));
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final ImageView imageArtist;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvLikedArtistItem);
            imageArtist = itemView.findViewById(R.id.imgLikedArtistItem);
        }

        public void bind(Artist response) {
            titleTextView.setText(response.getName());
            Picasso.get().load(response.getImage()).into(imageArtist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }
    }
}
