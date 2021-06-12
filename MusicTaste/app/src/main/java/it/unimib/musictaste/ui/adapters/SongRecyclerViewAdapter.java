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
import it.unimib.musictaste.models.Song;


public class SongRecyclerViewAdapter extends RecyclerView.Adapter<SongRecyclerViewAdapter.SongViewHolder>{

    private List<Song> responseList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Song response);
    }

    public SongRecyclerViewAdapter(List<Song> responseList, OnItemClickListener onItemClickListener) {
        this.responseList = responseList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(responseList.get(position));
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final ImageView imageSong;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvLikedItemTitle);
            artistTextView = itemView.findViewById(R.id.tvLikedItemArtist);
            imageSong = itemView.findViewById(R.id.imgLikedItem);
        }

        public void bind(Song response) {
            titleTextView.setText(response.getTitle());
            artistTextView.setText(response.getArtist().getName());
            Picasso.get().load(response.getImage()).into(imageSong);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }
    }
}
