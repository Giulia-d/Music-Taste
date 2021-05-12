package it.unimib.musictaste;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResponseRecyclerViewAdapter extends RecyclerView.Adapter<ResponseRecyclerViewAdapter.ResponseViewHolder>{

    private List<String> responseList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String response);
    }

    public ResponseRecyclerViewAdapter(List<String> responseList, OnItemClickListener onItemClickListener) {
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

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvtitle);
        }

        public void bind(String response) {
            titleTextView.setText(response);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(response);
                }
            });
        }
    }
}
