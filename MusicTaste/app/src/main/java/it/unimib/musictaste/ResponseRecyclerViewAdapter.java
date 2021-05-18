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

import it.unimib.musictaste.utils.Song;

public class ResponseRecyclerViewAdapter extends RecyclerView.Adapter<ResponseRecyclerViewAdapter.ResponseViewHolder>{

    private List<Song> responseList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Song response);
    }

    public ResponseRecyclerViewAdapter(List<Song> responseList, OnItemClickListener onItemClickListener) {
        this.responseList = responseList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_item, parent, false);
        return new ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder holder, int position) {
        holder.bind(responseList.get(position));
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }

    public class ResponseViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final ImageView imageSong;
        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvtitle);
            artistTextView = itemView.findViewById(R.id.tvartist);
            imageSong = itemView.findViewById(R.id.imageSong);
        }

        public void bind(Song response) {
            titleTextView.setText(response.getTitle());
            artistTextView.setText(response.getArtist());
            Picasso.get().load(response.getImage()).into(imageSong);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }
    }
}
