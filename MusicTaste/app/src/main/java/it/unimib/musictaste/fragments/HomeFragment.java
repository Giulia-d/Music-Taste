package it.unimib.musictaste.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.musictaste.NewsRecyclerViewAdapter;
import it.unimib.musictaste.R;
import it.unimib.musictaste.utils.News;
import it.unimib.musictaste.viewmodels.NewsViewModel;

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
    /*
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    */

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
        pBLoading_home = view.findViewById(R.id.pBLoading_home);
        recyclerView = view.findViewById(R.id.rv_news);

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