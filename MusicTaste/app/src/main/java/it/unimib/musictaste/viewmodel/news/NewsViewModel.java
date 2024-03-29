package it.unimib.musictaste.viewmodel.news;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.musictaste.models.News;
import it.unimib.musictaste.repositories.NewsRepository;

public class NewsViewModel extends AndroidViewModel {
    private MutableLiveData<List<News>> news;
    private NewsRepository newsRepository;

    public NewsViewModel(@NonNull Application application){
        super(application);
        newsRepository = new NewsRepository(application.getApplicationContext());
    }

    public LiveData<List<News>> getNews(){
        if (news == null){
            news = new MutableLiveData<>();
            loadNews();
        }
        return news;
    }

    private void loadNews(){
        news = newsRepository.getNews();
    }
}
