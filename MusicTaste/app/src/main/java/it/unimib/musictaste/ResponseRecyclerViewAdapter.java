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

public class ResponseRecyclerViewAdapter extends RecyclerView.Adapter<ResponseRecyclerViewAdapter.ResponseViewHolder> {

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
    public ResponseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_item, parent, false);
        return new ResponseViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder holder, int position) {
        if (position == 0) {
            holder.bindFirst(responseList.get(position));
        } else if(position==1) {
            holder.bind(responseList.get(0));
        } else{
            holder.bind(responseList.get(position-1));
        }


    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    public class ResponseViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final ImageView imageSong;
        private final ImageView imageArtist;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tvtitle);
            artistTextView = itemView.findViewById(R.id.tvartist);
            imageSong = itemView.findViewById(R.id.imageSong);
            imageArtist = itemView.findViewById(R.id.imageArtist);
        }

        public void bind(Song response) {
            titleTextView.setText(response.getTitle());
            artistTextView.setText(response.getArtist());
            Picasso.get().load(response.getImage()).into(imageSong);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }

        public void bindFirst(Song response) {
            titleTextView.setText(response.getArtist());
            artistTextView.setText("Top Artist");
            imageSong.setVisibility(View.GONE);
            imageArtist.setVisibility(View.VISIBLE);
            Picasso.get().load(response.getArtistImg()).into(imageArtist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }
    }


}
