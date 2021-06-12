package it.unimib.musictaste.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.R;
import it.unimib.musictaste.models.News;
import it.unimib.musictaste.ui.adapters.NewsRecyclerViewAdapter;
import it.unimib.musictaste.viewmodel.news.NewsViewModel;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    List<News> news;
    ProgressBar pBLoading_home;
    NewsViewModel newsViewModel;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        news = new ArrayList<>();
        TextView welcome = view.findViewById(R.id.txtWelcome);
        pBLoading_home = view.findViewById(R.id.pBLoading_home);
        recyclerView = view.findViewById(R.id.rv_news);
        user = FirebaseAuth.getInstance().getCurrentUser();
        welcome.setText(getString(R.string.Welcome) + " " + user.getDisplayName());
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        newsViewModel.getNews().observe(getViewLifecycleOwner(), news -> {
            updateUI(news);
        });
        newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(news, new NewsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(News response) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(response.getUrl()));
                getContext().startActivity(webIntent);

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(newsRecyclerViewAdapter);
    }


    public void updateUI(List<News> news) {
        if (!news.get(0).getDescription().equals("ErrorResponse")) {
            this.news.clear();
            this.news.addAll(news);
            pBLoading_home.setVisibility(View.GONE);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    newsRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        } else
            Toast.makeText(this.requireActivity(), news.get(0).getTitle(), Toast.LENGTH_LONG).show();
    }



}