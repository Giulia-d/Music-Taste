package it.unimib.musictaste.ui.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.models.Song;


public class AlbumSongRecyclerViewAdapter extends RecyclerView.Adapter<AlbumSongRecyclerViewAdapter.AlbumViewHolder>{

    private List<Song>tracks;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(Song response);
    }

    public AlbumSongRecyclerViewAdapter(List<Song> tracks, OnItemClickListener onItemClickListener){
        this.tracks = tracks;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new AlbumViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
            holder.bind(tracks.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(tracks != null)
            return tracks.size();
        else return 0;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView numberTextView;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tvTrackTitle);
            numberTextView = itemView.findViewById(R.id.tvnumber);
        }

        public void bind(Song response, int position) {
            titleTextView.setText(response.getTitle());
            numberTextView.setText((position + 1) + ".");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }


    }

}
